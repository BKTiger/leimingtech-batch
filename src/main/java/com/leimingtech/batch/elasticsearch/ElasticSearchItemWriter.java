package com.leimingtech.batch.elasticsearch;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/11 09:17
 * @Description:
 */
public class ElasticSearchItemWriter<T> implements ItemWriter<T>, InitializingBean {

    protected RestHighLevelClient restHighLevelClient;

    protected String indexName;

    private final Object bufferKey;

    public ElasticSearchItemWriter() {
        super();
        this.bufferKey = new Object();
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        if (!transactionActive()) {
            doWrite(items);
            return;
        }

        List<T> bufferedItems = getCurrentBuffer();
        bufferedItems.addAll(items);
    }

    protected void doWrite(List<? extends T> items) throws Exception {
        if (!CollectionUtils.isEmpty(items)) {
            BulkRequest bulkRequest = new BulkRequest();
            items.forEach(data -> {
                String json = JSON.toJSONString(data);
                Map map = JSON.parseObject(json, Map.class);
                IndexRequest indexRequest = new IndexRequest(indexName).source(json, XContentType.JSON).id(map.get("id").toString());
                bulkRequest.add(indexRequest);
            });

            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        }
    }

    @SuppressWarnings("unchecked")
    private List<T> getCurrentBuffer(){
        if (!TransactionSynchronizationManager.hasResource(bufferKey)) {
            TransactionSynchronizationManager.bindResource(bufferKey, new ArrayList<T>());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    List<T> items = (List<T>) TransactionSynchronizationManager.getResource(bufferKey);

                    if (!CollectionUtils.isEmpty(items)) {
                        if (!readOnly) {
                            try {
                                doWrite(items);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronizationManager.hasResource(bufferKey)) {
                        TransactionSynchronizationManager.unbindResource(bufferKey);
                    }
                }
            });
        }

        return (List<T>) TransactionSynchronizationManager.getResource(bufferKey);
    }

    private boolean transactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(restHighLevelClient != null, "A ElasticSearchOperations implementation is required.");
    }
}
