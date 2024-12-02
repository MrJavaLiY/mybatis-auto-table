package com.auto.table.utils;

import com.auto.table.AutoTable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassUtil {
    /**
     * 扫描指定包下的所有类
     * @param packageName 包名
     * @return 类集合
     */
    public static Set<Class<?>> scanClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String path = packageName.replace('.', '/');
        try {
            // 获取包路径下所有的文件
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                if (file.isDirectory()) {
                    findClassesByFile(file, packageName, classes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包 " + packageName + " 时出错", e);
        }
        return classes;
    }

    /**
     * 通过文件系统递归查找类
     * @param file 文件或目录
     * @param packageName 包名
     * @param classes 类集合
     */
    private static void findClassesByFile(File file, String packageName, Set<Class<?>> classes) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    findClassesByFile(f, packageName + "." + f.getName(), classes);
                } else if (f.getName().endsWith(".class")) {
                    try {
                        // 去掉 .class 后缀
                        String className = packageName + '.' + f.getName().substring(0, f.getName().length() - 6);
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("加载类 " + f.getName() + " 时出错", e);
                    }
                }
            }
        }
    }

}
