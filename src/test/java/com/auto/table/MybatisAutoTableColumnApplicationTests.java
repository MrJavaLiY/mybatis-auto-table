package com.auto.table;

import com.auto.table.auto.create.Create;
import com.auto.table.tes.TestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MybatisAutoTableColumnApplicationTests {
    @Resource
    private Create create;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() throws NoSuchFieldException {
        create.execute(TestEntity.class);
    }

}
