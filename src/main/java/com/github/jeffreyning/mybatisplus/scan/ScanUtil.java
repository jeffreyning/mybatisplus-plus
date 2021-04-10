package com.github.jeffreyning.mybatisplus.scan;

import com.github.jeffreyning.mybatisplus.anno.AutoMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScanUtil.class);
    public static Set<Class> getClasses(String packagePath) {
        Set<Class> res = new HashSet();
        String path = packagePath.replace(".", "/");
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            logger.error(packagePath + " is not exit");
            return res;
        }
        String protocol = url.getProtocol();
        if ("jar".equalsIgnoreCase(protocol)) {
            try {
                res.addAll(getJarClasses(url, packagePath));
            } catch (IOException e) {
                logger.error("scan error",e);
                return res;
            }
        } else if ("file".equalsIgnoreCase(protocol)) {
            res.addAll(getFileClasses(url, packagePath));
        }
        return res;
    }

    private static Set<Class> getFileClasses(URL url, String packagePath) {
        Set<Class> res = new HashSet();
        String filePath = url.getFile().replace("%20"," ");
        File dir = new File(filePath);
        String[] list = dir.list();
        if (list == null) return res;
        for (String classPath : list) {
            if (classPath.endsWith(".class")) {
                classPath = classPath.replace(".class", "");
                try {
                    Class<?> aClass = Class.forName(packagePath + "." + classPath);
                    res.add(aClass);
/*                    Annotation[] anns=aClass.getAnnotations();
                    if(anns!=null) {
                        for (Annotation an : anns) {
                            if (an.toString().contains(AutoMap.class.getName())) {
                                res.add(aClass);
                            }
                        }
                    }*/
                } catch (ClassNotFoundException e) {
                    logger.error("scan error",e);
                }
            } else {
                res.addAll(getClasses(packagePath + "." + classPath));
            }
        }
        return res;
    }

    private static Set<Class> getJarClasses(URL url, String packagePath) throws IOException {
        Set<Class> res = new HashSet();
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        if (conn != null) {
            JarFile jarFile = conn.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.contains(".class") && name.replaceAll("/", ".").startsWith(packagePath)) {
                    String className = name.substring(0, name.lastIndexOf(".")).replace("/", ".");
                    try {
                        Class clazz = Class.forName(className);
                        res.add(clazz);
/*                        Annotation[] anns=clazz.getAnnotations();
                        if(anns!=null) {
                            for (Annotation an : anns) {
                                if (an.toString().contains(AutoMap.class.getName())) {
                                    res.add(clazz);
                                }
                            }
                        }*/
                    } catch (ClassNotFoundException e) {
                        logger.error("scan error",e);
                    }
                }
            }
        }
        return res;
    }
}
