package com.auto.table;

import com.auto.table.auto.create.Create;
import com.auto.table.tes.TestEntity;
import com.auto.table.utils.ClassUtil;
import com.auto.table.utils.SpringBootStarterClassLocator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.Set;

@SpringBootTest
class MybatisAutoTableColumnApplicationTests {
    @Resource
    private Create create;
    @Resource
    SpringBootStarterClassLocator springBootStarterClassLocator;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() throws NoSuchFieldException {
        String path = springBootStarterClassLocator.getApplicationPath();
        Set<Class<?>> classes = ClassUtil.scanClasses(path);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(AutoTable.class)) {
                create.execute(clazz);
            }
        }
    }

}
