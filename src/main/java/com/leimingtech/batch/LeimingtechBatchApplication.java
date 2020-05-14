package com.leimingtech.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class LeimingtechBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeimingtechBatchApplication.class, args);
    }

}
