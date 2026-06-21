package com.yexiang.travelPlanning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 */
@SpringBootApplication
@MapperScan("com.yexiang.travelPlanning.mapper")
public class TravelApplication {
    public static void main(String[] args) {
        new SpringApplication().run(TravelApplication.class, args);
    }
}
