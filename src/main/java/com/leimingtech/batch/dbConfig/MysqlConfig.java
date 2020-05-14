package com.leimingtech.batch.dbConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author zhangtai
 * @date 2020/5/9 10:52
 * @Description:
 */
@Configuration
public class MysqlConfig {

    @ConfigurationProperties(prefix = "spring.one.datasource")
    @Bean
    @Primary
    public DataSource mysql1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @ConfigurationProperties(prefix = "spring.two.datasource")
    @Bean

    public DataSource mysql2DataSource() {
        return DataSourceBuilder.create().build();
    }
}
