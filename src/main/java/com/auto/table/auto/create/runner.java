package com.auto.table.auto.create;

import com.auto.table.AutoTable;
import com.auto.table.utils.ClassUtil;
import com.auto.table.utils.SpringBootStarterClassLocator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

//@Component
public class runner implements ApplicationRunner {
    @Resource
    private Create create;
    @Resource
    SpringBootStarterClassLocator springBootStarterClassLocator;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = springBootStarterClassLocator.getApplicationPath();
            Set<Class<?>> classes = ClassUtil.scanClasses(path);
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(AutoTable.class)) {
                    create.execute(clazz);
                }
            }
    }
}
