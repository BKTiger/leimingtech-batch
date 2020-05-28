package com.leimingtech.batch.job;

import com.leimingtech.batch.entity.BrandFavPO;
import com.leimingtech.batch.entity.Goods;
import com.leimingtech.batch.listener.MongoToMysqlJobListener;
import com.leimingtech.batch.listener.MongoToMysqlProcessListener;
import com.leimingtech.batch.listener.MongoToMysqlReadeListener;
import com.leimingtech.batch.listener.MongoToMysqlWriterListener;
import com.leimingtech.batch.process.MongoToMysqlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhangtai
 * @date 2020/5/8 16:44
 * @Description:
 */
@Component
public class MongoToMysqlDemo {

    private static final Logger log = LoggerFactory.getLogger(MongoToMysqlDemo.class);


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Resource(name = "mysql1DataSource")
    private DataSource dataSource;

    @Autowired
    private MongoOperations mongoTemplate;

    @Autowired
    private MongoToMysqlProcess mongoToMysqlProcess;

    @Autowired
    private MongoToMysqlWriterListener mongoToMysqlWriterListener;

    @Autowired
    private MongoToMysqlJobListener mongoToMysqlJobListener;

    @Autowired
    private MongoToMysqlProcessListener mongoToMysqlProcessListener;

    @Autowired
    private MongoToMysqlReadeListener mongoToMysqlReadeListener;

    @Bean
    public Job mongoToMysqlJob(Step mongToMysqlStep) throws Exception {
        return jobBuilderFactory.get("mongToMysqlJob")
                .incrementer(new RunIdIncrementer())// 允许任务重复执行
                .listener(mongoToMysqlJobListener) // 任务执行的监听器
                .start(mongToMysqlStep)
                .build();
    }

    @Bean
    public Step mongToMysqlStep(ItemReader<Goods> readFromMongo) throws Exception {
        return stepBuilderFactory.get("mongToMysqlStep")
                .<Goods, Goods>chunk(1) // 每批次处理的数据条数
                .reader(readFromMongo) // 读取数据
                //.listener(mongoToMysqlReadeListener) //读取数据的监听器
                .processor(mongoToMysqlProcess) //数据处理
                //.listener(mongoToMysqlProcessListener) // 数据处理的监听器
                .writer(writer()) // 写入数据
                //.listener(mongoToMysqlWriterListener) // 写入数据的监听器
                .faultTolerant() // 打开容错,这样才可以跳过异常
                .skip(Exception.class) // 异常跳过,可以指定其他自定义异常类型
                .skipLimit(3) //设置异常跳过次数限制
                .build();
    }

    public ItemWriter<Goods> writer() throws Exception {
        JdbcBatchItemWriter<Goods> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource); // 设置数据源

        String sql = "insert into goods(id,name,price,color_id,color_name,size_id,size_name) values (:id,:name,:price,:colorId,:colorName,:sizeId,:sizeName)";
        writer.setSql(sql); // 设置插入sql脚本

        // 映射TestData对象属性到占位符中的属性
        BeanPropertyItemSqlParameterSourceProvider<Goods> provider = new BeanPropertyItemSqlParameterSourceProvider<>();
        writer.setItemSqlParameterSourceProvider(provider);

        writer.afterPropertiesSet(); // 设置一些额外属性
        return writer;
    }

    @Bean
    @StepScope
    public ItemReader<Goods> readFromMongo(@Value("#{jobParameters[name]}") String name) throws Exception {
        List<Object> param = new ArrayList<>();

        String jsonQuery = "";
        if(StringUtils.isEmpty(name)){
            jsonQuery = "{}";
        }else {
            log.info("mongo to mysql 的入参name:{}",name);
            jsonQuery = "{name: ?0}";
            param.add(name);
        }
        return new MongoItemReaderBuilder<Goods>()
                .name("tweetsItemReader")
                .targetType(Goods.class)
                .jsonQuery(jsonQuery)
                .parameterValues(param)
                .pageSize(1)
                .collection("goods")
                .sorts(Collections.singletonMap("created_at", Sort.Direction.ASC))
                .template(mongoTemplate)
                .build();
    }

}
