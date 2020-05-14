package com.leimingtech.batch.job;

import com.leimingtech.batch.elasticsearch.ElasticSearchItemWriter;
import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import com.leimingtech.batch.process.ElasticSearchToMongoProcess;
import com.leimingtech.batch.process.MysqlToElsticSearchProcess;
import javafx.beans.binding.ObjectExpression;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.batch.runtime.JobExecution;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/11 10:39
 * @Description:
 */
@Component
public class MysqlToElasticSearchDemo {

    private static final Logger log = LoggerFactory.getLogger(MysqlToElasticSearchDemo.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MysqlToElsticSearchProcess mysqlToElsticSearchProcess;

    @Resource(name = "mysql1DataSource")
    private DataSource dataSource;

    @Resource(name = "highLevelClient")
    private RestHighLevelClient restHighLevelClient;



    @Bean
    public Job mysqlToElasticSearchJob(Step mysqlToElasticSearchStep) throws Exception {
        return jobBuilderFactory.get("mysqlToElasticSearchJob")
                .incrementer(new RunIdIncrementer())// 允许任务重复执行
                .start(mysqlToElasticSearchStep)
                .build();
    }

    @Bean
    public Step mysqlToElasticSearchStep(ItemReader<Goods> GoodsItemReader) throws Exception {
        return stepBuilderFactory.get("mysqlToElasticSearchStep")
                .<Goods, Goods>chunk(1000) // 每批次处理的数据条数
                .reader(GoodsItemReader) // 读取数据
                .processor(mysqlToElsticSearchProcess)
                .writer(writer()) // 写入数据
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Goods> GoodsItemReader(@Value("#{jobParameters[id]}") String id) throws Exception{
        JdbcPagingItemReader<Goods> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource); // 设置数据源
        reader.setFetchSize(1000); // 每次取多少条记录
        reader.setPageSize(1000); // 设置每页数据量
        // 指定sql查询语句 select id,field1,field2,field3 from TEST
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("id,name,price,color_id colorId,color_name colorName,size_id sizeId,size_name sizeName"); //设置查询字段
        provider.setFromClause("from goods"); // 设置从哪张表查询
       if(!StringUtils.isEmpty(id)){
           log.info("mysql传入es的入参id为:{}",id);
           Map<String, Object> param = new HashMap<>();
           param.put("id",id);
           provider.setWhereClause("where id = :id");
           reader.setParameterValues(param);
       }

        // 将读取到的数据转换为TestData对象
        reader.setRowMapper((resultSet, rowNum) -> {
            Goods data = new Goods();
            data.setId(String.valueOf(resultSet.getLong(1)));
            data.setName(resultSet.getString(2));
            data.setPrice(resultSet.getBigDecimal(3).toString());
            data.setColorId(String.valueOf(resultSet.getLong(4)));
            data.setColorName(resultSet.getString(5));
            data.setSizeId(String.valueOf(resultSet.getLong(6)));
            data.setSizeName(resultSet.getString(7));
            return data;
        });

        Map<String, Order> sort = new HashMap<>(1);
        sort.put("id", Order.ASCENDING);
        provider.setSortKeys(sort); // 设置排序,通过id 升序

        reader.setQueryProvider(provider);

        // 设置namedParameterJdbcTemplate等属性
        reader.afterPropertiesSet();
        return reader;
    }

    public ItemWriter<Goods> writer() throws Exception{
        ElasticSearchItemWriter<Goods> brandFavPOElasticSearchItemWriter = new ElasticSearchItemWriter<>();
        brandFavPOElasticSearchItemWriter.setIndexName("goods");
        brandFavPOElasticSearchItemWriter.setRestHighLevelClient(restHighLevelClient);
        brandFavPOElasticSearchItemWriter.afterPropertiesSet();
        return brandFavPOElasticSearchItemWriter;
    }
}
