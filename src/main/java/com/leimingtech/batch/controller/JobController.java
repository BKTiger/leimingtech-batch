package com.leimingtech.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangtai
 * @date 2020/5/11 13:56
 * @Description:
 */
@RestController
public class JobController {

    @Autowired
    private Job mysqlToElasticSearchJob;

    @Autowired
    private Job mongoToMysqlJob;

    @Autowired
    private Job elasticSearchToMongoJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobOperator jobOperator;

    @GetMapping("job/mysqlToElasticSearchJob")
    public void jobStart(@RequestParam(required = false,defaultValue = "") String id) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("id", id)
                .addString("data", System.currentTimeMillis() + "")
                .toJobParameters();
        // 将参数传递给任务
        try {
            jobLauncher.run(mysqlToElasticSearchJob, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("job/mongoToMysqlJob")
    public void mongoToMysqlJob(@RequestParam(required = false,defaultValue = "") String name) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("name",name)
                .addString("data", System.currentTimeMillis() + "")
                .toJobParameters();
        // 将参数传递给任务
        try {
            jobLauncher.run(mongoToMysqlJob, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("job/elasticSearchToMongoJob")
    public void elasticSearchToMongoJob() {
        JobParameters parameters = new JobParametersBuilder()
                .addString("data", System.currentTimeMillis() + "")
                .toJobParameters();
        // 将参数传递给任务
        try {
            jobLauncher.run(elasticSearchToMongoJob, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("job/elasticSearchToMongoJobByJobOperator")
    public void elasticSearchToMongoJobByJobOperator() throws Exception{
        // 传递任务名称，参数使用 kv方式
        jobOperator.start("elasticSearchToMongoJob", "date=" + System.currentTimeMillis());
    }

}
