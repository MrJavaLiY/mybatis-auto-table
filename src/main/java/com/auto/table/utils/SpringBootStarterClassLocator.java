package com.auto.table.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;

@Component
@Slf4j
public class SpringBootStarterClassLocator {
    private final ApplicationContext applicationContext;

    @Autowired
    public SpringBootStarterClassLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getApplicationPath() {
        SpringBootStarterClassLocator locator = new SpringBootStarterClassLocator(applicationContext);
        try {
            URL location = locator.getStarterClassLocation();
            System.out.println("启动类所在的位置: " + location);
            return location.toString();
        } catch (Exception e) {
            log.error("获取启动类位置失败", e);
            return "";
        }
    }

    /**
     * 获取Spring Boot启动类所在的地址
     *
     * @return 启动类所在的URL
     */
    public URL getStarterClassLocation() {
        Class<?> starterClass = applicationContext.getClass();
        while (starterClass != null && !starterClass.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class)) {
            starterClass = starterClass.getSuperclass();
        }
        if (starterClass == null) {
            throw new IllegalStateException("未找到Spring Boot启动类");
        }
        return getClassLocation(starterClass);
    }

    /**
     * 获取类所在的URL
     *
     * @param clazz 类
     * @return 类所在的URL
     */
    private URL getClassLocation(Class<?> clazz) {
        String className = clazz.getName().replace(".", "/") + ".class";
        URL url = getClass().getClassLoader().getResource(className);
        if (url == null) {
            throw new IllegalStateException("无法找到类 " + clazz.getName() + " 的位置");
        }
        return url;
    }

}
