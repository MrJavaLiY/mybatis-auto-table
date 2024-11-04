package com.auto.table;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.auto.table.dao")
public class MybatisAutoTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisAutoTableApplication.class, args);
    }

}
