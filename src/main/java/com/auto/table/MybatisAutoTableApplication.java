package com.auto.table;

import com.auto.table.auto.create.Create;
import com.auto.table.utils.ClassUtil;
import com.auto.table.utils.Scan;
import com.auto.table.utils.SpringBootStarterClassLocator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootApplication
@MapperScan("com.auto.table.dao")
@Scan("com.auto")

public class MybatisAutoTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisAutoTableApplication.class, args);
    }



}
