package com.leimingtech.batch.job;

import com.leimingtech.batch.elasticsearch.ElasticSearchItemReader;
import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import com.leimingtech.batch.mongodb.MongoItemBulkWriter;
import com.leimingtech.batch.process.ElasticSearchToMongoProcess;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/9 16:44
 * @Description:
 */
@Component
public class ElasticSearchToMongoDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoOperations mongoTemplate;

    @Resource(name = "highLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticSearchToMongoProcess elasticSearchToMongoProcess;

    @Bean
    public Job elasticSearchToMongoJob() throws Exception {
        return jobBuilderFactory.get("elasticSearchToMongoJob")
                .incrementer(new RunIdIncrementer())// 允许任务重复执行
                .start(step())
                .build();
    }

    public Step step() throws Exception {
        return stepBuilderFactory.get("elasticSearchToMongoStep")
                .<Map, Goods>chunk(1000) // 每批次处理的数据条数
                .reader(reader()) // 读取数据
                .processor(elasticSearchToMongoProcess)
                .writer(writer()) // 写入数据
                .build();
    }

    public ItemReader<Map> reader() {
        ElasticSearchItemReader elasticSearchItemReader = new ElasticSearchItemReader();
        elasticSearchItemReader.setIndexName("goods");
        elasticSearchItemReader.setPageSize(1000);
        Map<String, String> sort = new HashMap<>();
        //sort.put("id", "asc");
        elasticSearchItemReader.setSortFileds(sort);
        elasticSearchItemReader.setQueryBuilder(QueryBuilders.boolQuery());
        elasticSearchItemReader.setRestHighLevelClient(restHighLevelClient);
        elasticSearchItemReader.setName("elasticsearchreader");
        return elasticSearchItemReader;
    }

    public ItemWriter<Goods> writer() throws Exception {
        MongoItemBulkWriter<Goods> brandFavPOMongoItemWriter = new MongoItemBulkWriter<>();
        brandFavPOMongoItemWriter.setCollection("goods");
        brandFavPOMongoItemWriter.setTemplate(mongoTemplate);
        brandFavPOMongoItemWriter.afterPropertiesSet();
        return brandFavPOMongoItemWriter;
    }

}
