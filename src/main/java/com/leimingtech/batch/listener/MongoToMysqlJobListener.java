package com.leimingtech.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author zhangtai
 * @date 2020/5/9 14:28
 * @Description:
 */
@Component
public class MongoToMysqlJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(MongoToMysqlJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("任务:数据从mongo转存到mysql任务开始");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("任务:数据从mongo转存到mysql任务结束");
    }
}
