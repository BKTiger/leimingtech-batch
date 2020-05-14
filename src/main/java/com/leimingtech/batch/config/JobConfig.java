package com.leimingtech.batch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangtai
 * @date 2020/5/11 18:12
 * @Description:
 */
@Configuration
public class JobConfig {

    /**
     * 注册JobRegistryBeanPostProcessor bean
     * 用于将任务名称和实际的任务关联起来
     */
    @Bean
    public JobRegistryBeanPostProcessor processor(JobRegistry jobRegistry, ApplicationContext applicationContext) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        postProcessor.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        return postProcessor;
    }
}
