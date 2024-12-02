package com.auto.table.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Component
public class ClassPathScanner implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        Class<?> applicationClass = applicationContext.getClass();
        while (applicationClass != null && !applicationClass.isAnnotationPresent(Scan.class)) {
            applicationClass = applicationClass.getSuperclass();
        }
        if (applicationClass == null) {
            throw new IllegalStateException("未找到带有 @Scan 注解的启动类");
        }

        Set<String> classFilePaths = getAllClassFilePaths(applicationClass);
        for (String filePath : classFilePaths) {
            System.out.println("扫描到的类文件: " + filePath);
        }
    }

    /**
     * 获取指定注解配置的包路径下所有类文件的路径
     * @param applicationClass 启动类
     * @return 类文件路径集合
     */
    public Set<String> getAllClassFilePaths(Class<?> applicationClass) {
        Set<String> classFilePaths = new HashSet<>();
        Scan scanAnnotation = applicationClass.getAnnotation(Scan.class);
        if (scanAnnotation != null) {
            String[] packages = scanAnnotation.value();
            for (String packageName : packages) {
                classFilePaths.addAll(getClassFilePathsInPackage(packageName));
            }
        }
        return classFilePaths;
    }

    /**
     * 获取指定包及其子包下所有类文件的路径
     * @param packageName 包名
     * @return 类文件路径集合
     */
    private Set<String> getClassFilePathsInPackage(String packageName) {
        Set<String> classFilePaths = new HashSet<>();
        String path = packageName.replace('.', '/');
        try {
            // 获取包路径下所有的文件
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                if (file.isDirectory()) {
                    findClassFilesByFile(file, classFilePaths);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包 " + packageName + " 时出错", e);
        }
        return classFilePaths;
    }

    /**
     * 通过文件系统递归查找类文件
     * @param file 文件或目录
     * @param classFilePaths 类文件路径集合
     */
    private void findClassFilesByFile(File file, Set<String> classFilePaths) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    findClassFilesByFile(f, classFilePaths);
                } else if (f.getName().endsWith(".class")) {
                    classFilePaths.add(f.getAbsolutePath());
                }
            }
        }
    }
}
