package top.dreamcenter.bkpg.util;

import top.dreamcenter.bkpg.protocal.MiniApp;
import top.dreamcenter.bkpg.protocal.MiniAppGroup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 应用注册器
 */
public class MiniAppRegister {
    /**
     * 注册 ext 文件夹下的扩展应用
     * @return
     */
    public static HashMap<String, MiniApp> registerApps() {
        HashMap<String, MiniApp> miniAppContainer = new HashMap<>();

        Class<MiniApp> miniAppClass = MiniApp.class;
        Class<MiniAppGroup> miniAppGroupClass = MiniAppGroup.class;

        // 找到ext 文件夹 并且初始化ext路径类加载器
        String extDirectory = "ext";
        File file = new File(extDirectory);

        // 解析ext 目录下的 Jar文件
        File[] files = file.listFiles(new JarFileFilter());
        if (files == null) return new HashMap<>();

        // 遍历jar文件
        for (File tmp : files) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile("/" + tmp.getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 解析jar包
            if (jarFile != null) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()){
                    JarEntry jarEntry = entries.nextElement();
                    // 解析class文件
                    if (jarEntry.getName().endsWith(".class") && !jarEntry.getName().contains("$")){
                        String classRoute = jarEntry.getName().replaceAll("/", ".").replace(".class", "");

                        try {
                            // 装载类
                            URLClassLoader loader = new URLClassLoader(new URL[]{tmp.toURI().toURL()});
                            Class<?> clazz = loader.loadClass(classRoute);

                            // 校验是否符合协议
                            if (miniAppClass.isAssignableFrom(clazz)) {             // 单应用
                                // 注册进容器
                                Object obj = clazz.getConstructor().newInstance();
                                miniAppContainer.put(classRoute, (MiniApp) obj);
                            } else if(miniAppGroupClass.isAssignableFrom(clazz)){   // 组合应用
                                // 注册进容器
                                Object obj = clazz.getConstructor().newInstance();
                                MiniAppGroup group = (MiniAppGroup) obj;
                                List<MiniApp> miniApps = group.getMiniApps();

                                // 检查返回值是否为空，如果为空则不处理
                                if (miniApps != null) {
                                    for (int i = 0; i < miniApps.size(); i++) {
                                        MiniApp app = miniApps.get(i);
                                        miniAppContainer.put(classRoute + "-" + app.getName(), app);
                                    }
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        // 返回结果
        return miniAppContainer;
    }

}
