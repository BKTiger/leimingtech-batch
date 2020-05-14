package com.leimingtech.batch.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/9 16:12
 * @Description:
 */
public class ElasticSearchItemReader extends AbstractPaginatedDataItemReader implements InitializingBean {

    protected RestHighLevelClient restHighLevelClient;

    protected String indexName;

    protected QueryBuilder queryBuilder;

    protected Map<String, String> sortFileds;


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

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Map<String, String> getSortFileds() {
        return sortFileds;
    }

    public void setSortFileds(Map<String, String> sortFileds) {
        this.sortFileds = sortFileds;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Iterator<Map> doPageRead() {
        Integer pageStart = page * pageSize;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                // 设置查询关键词
                .query(queryBuilder)
                // 设置查询数据的位置,分页用
                .from(pageStart)
                // 设置查询结果集的最大条数
                .size(pageSize)
                // 不展示分析逻辑
                .explain(false);
        for (String sortKey : sortFileds.keySet()) {
            if ("asc".equalsIgnoreCase(sortFileds.get(sortKey))) {
                searchSourceBuilder.sort(SortBuilders.fieldSort(sortKey).order(SortOrder.ASC));
            } else if ("desc".equalsIgnoreCase(sortFileds.get(sortKey))) {
                searchSourceBuilder.sort(SortBuilders.fieldSort(sortKey).order(SortOrder.DESC));
            }
        }

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 设置查询类型
        // 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
        // 2.SearchType.SCAN = 扫描查询,无序
        // 3.SearchType.COUNT = 不设置的话,这个为默认值,
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();

            SearchHit[] hits = searchHits.getHits();
            List<Map> result = new ArrayList<>();
            for (SearchHit searchHit : hits) {
                Map sourceAsMap = searchHit.getSourceAsMap();
                result.add(sourceAsMap);
            }
            return result.iterator();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(restHighLevelClient != null, "An implementation of ElasticSearchOperations is required.");
        Assert.state(indexName != null, "A indexName is required.");
        Assert.state(queryBuilder != null, "A queryBuilder is required.");
        //Assert.state( !CollectionUtils.isEmpty(sortFileds), "A sort is required.");
    }
}
