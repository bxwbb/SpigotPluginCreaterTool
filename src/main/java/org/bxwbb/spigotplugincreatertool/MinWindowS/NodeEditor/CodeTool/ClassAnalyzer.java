package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClassAnalyzer {

    /**
     * 分析指定类的信息，支持多路径和类名变体查找
     */
    public static ClassInfo analyzeClass(String fullClassName, List<String> classPaths) throws Exception {
        List<String> possibleClassNames = generateClassNamesWithDollar(fullClassName);

        Class<?> clazz = null;

        loop:
        for (String className : possibleClassNames) {
            for (String path : classPaths) {
                try {
                    clazz = loadClassFromPath(className, path);
                    System.out.println("成功加载类 - 类名: " + className + ", 路径: " + path);
                    break loop;
                } catch (ClassNotFoundException e) {
                    System.out.println("加载失败 - 类名: " + className + ", 路径: " + path);
                } catch (IllegalArgumentException e) {
                    System.out.println("路径无效 - 路径: " + path + ", 原因: " + e.getMessage());
                }
            }
        }

        if (clazz == null) {
            throw new ClassNotFoundException("所有类名变体在所有路径中均未找到类。尝试过的类名: " +
                    Arrays.toString(possibleClassNames.toArray()));
        }

        return extractClassInfo(clazz);
    }

    /**
     * 生成所有可能的类名变体（逐步将点替换为$）
     */
    private static List<String> generateClassNamesWithDollar(String fullClassName) {
        List<String> result = new ArrayList<>();
        List<Integer> dotPositions = new ArrayList<>();

        for (int i = 0; i < fullClassName.length(); i++) {
            if (fullClassName.charAt(i) == '.') {
                dotPositions.add(i);
            }
        }

        if (dotPositions.isEmpty()) {
            result.add(fullClassName);
            return result;
        }

        int dotCount = dotPositions.size();
        for (int mask = 0; mask < (1 << dotCount); mask++) {
            StringBuilder sb = new StringBuilder(fullClassName);

            for (int i = 0; i < dotCount; i++) {
                if ((mask & (1 << i)) != 0) {
                    int position = dotPositions.get(i);
                    sb.setCharAt(position, '$');
                }
            }

            result.add(sb.toString());
        }

        return result;
    }

    /**
     * 从指定路径加载类
     */
    private static Class<?> loadClassFromPath(String className, String path) throws Exception {
        File classPath = new File(path);
        if (!classPath.exists()) {
            throw new IllegalArgumentException("路径不存在");
        }
        if (!classPath.isDirectory() && !path.endsWith(".jar") && !path.endsWith(".zip")) {
            throw new IllegalArgumentException("路径不是有效的目录或JAR/ZIP文件");
        }

        List<URL> urlList = new ArrayList<>();
        collectClasspathUrls(classPath, urlList);

        try (URLClassLoader classLoader = new URLClassLoader(
                urlList.toArray(new URL[0]),
                ClassLoader.getSystemClassLoader())) {
            return classLoader.loadClass(className);
        }
    }

    /**
     * 收集指定路径下所有可能的类路径资源
     */
    private static void collectClasspathUrls(File file, List<URL> urls) throws IOException {
        if (file.isDirectory()) {
            urls.add(file.toURI().toURL());
            for (File child : Objects.requireNonNull(file.listFiles())) {
                collectClasspathUrls(child, urls);
            }
        } else if (file.getName().toLowerCase().endsWith(".jar") ||
                file.getName().toLowerCase().endsWith(".zip")) {
            urls.add(file.toURI().toURL());
        }
    }

    /**
     * 从Class对象中提取完整的类信息
     */
    private static ClassInfo extractClassInfo(Class<?> clazz) {
        ClassInfo info = new ClassInfo();
        info.fullClassName = clazz.getName();
        info.simpleName = clazz.getSimpleName();
        info.modifiers = clazz.getModifiers();
        info.isInterface = clazz.isInterface();
        info.isEnum = clazz.isEnum();
        info.isAnnotation = clazz.isAnnotation();
        info.isPrimitive = clazz.isPrimitive();

        info.superclassName = clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : null;

        info.interfaceNames = Arrays.stream(clazz.getInterfaces())
                .map(Class::getName)
                .toArray(String[]::new);

        info.fields = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.name = field.getName();
                    fieldInfo.type = field.getType().getName();
                    fieldInfo.modifiers = field.getModifiers();
                    return fieldInfo;
                })
                .toArray(FieldInfo[]::new);

        // 方法信息（包含参数名）
        info.methods = Arrays.stream(clazz.getDeclaredMethods())
                .map(method -> {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.name = method.getName();
                    methodInfo.returnType = method.getReturnType().getName();
                    methodInfo.modifiers = method.getModifiers();

                    // 提取参数类型
                    methodInfo.parameterTypes = Arrays.stream(method.getParameterTypes())
                            .map(Class::getName)
                            .toArray(String[]::new);

                    // 提取参数名（使用索引作为后备）
                    java.lang.reflect.Parameter[] params = method.getParameters();
                    methodInfo.parameterNames = new String[params.length];
                    for (int i = 0; i < params.length; i++) {
                        methodInfo.parameterNames[i] = params[i].isNamePresent() ?
                                params[i].getName() : "arg" + i;
                    }

                    methodInfo.exceptionTypes = Arrays.stream(method.getExceptionTypes())
                            .map(Class::getName)
                            .toArray(String[]::new);

                    return methodInfo;
                })
                .toArray(MethodInfo[]::new);

        // 构造函数信息（包含参数名）
        info.constructors = Arrays.stream(clazz.getDeclaredConstructors())
                .map(constructor -> {
                    ConstructorInfo constructorInfo = new ConstructorInfo();
                    constructorInfo.modifiers = constructor.getModifiers();

                    // 提取参数类型
                    constructorInfo.parameterTypes = Arrays.stream(constructor.getParameterTypes())
                            .map(Class::getName)
                            .toArray(String[]::new);

                    // 提取参数名（使用索引作为后备）
                    java.lang.reflect.Parameter[] params = constructor.getParameters();
                    constructorInfo.parameterNames = new String[params.length];
                    for (int i = 0; i < params.length; i++) {
                        constructorInfo.parameterNames[i] = params[i].isNamePresent() ?
                                params[i].getName() : "arg" + i;
                    }

                    constructorInfo.exceptionTypes = Arrays.stream(constructor.getExceptionTypes())
                            .map(Class::getName)
                            .toArray(String[]::new);

                    return constructorInfo;
                })
                .toArray(ConstructorInfo[]::new);

        // 内部类信息（递归处理）
        info.innerClasses = Arrays.stream(clazz.getDeclaredClasses())
                .map(ClassAnalyzer::extractClassInfo)
                .toArray(ClassInfo[]::new);

        return info;
    }

    /**
     * 打印ClassInfo对象的完整信息
     */
    public static void printClassInfo(ClassInfo info) {
        printClassInfo(info, 0);
    }

    /**
     * 带缩进的递归打印方法
     */
    private static void printClassInfo(ClassInfo info, int indent) {
        String indentStr = "  ".repeat(indent);
        System.out.println(indentStr + "=== 类信息 ===");
        System.out.println(indentStr + "全类名: " + info.fullClassName);
        System.out.println(indentStr + "简单名: " + info.simpleName);
        System.out.println(indentStr + "修饰符: " + Modifier.toString(info.modifiers));
        System.out.println(indentStr + "类型: " + getClassType(info));

        if (info.superclassName != null) {
            System.out.println(indentStr + "父类: " + info.superclassName);
        }

        if (info.interfaceNames.length > 0) {
            System.out.println(indentStr + "实现的接口: " + String.join(", ", info.interfaceNames));
        }

        if (info.fields.length > 0) {
            System.out.println(indentStr + "字段列表:");
            for (FieldInfo field : info.fields) {
                System.out.println(indentStr + "  " +
                        Modifier.toString(field.modifiers) + " " +
                        field.type + " " +
                        field.name);
            }
        }

        if (info.constructors.length > 0) {
            System.out.println(indentStr + "构造函数列表:");
            for (ConstructorInfo constructor : info.constructors) {
                StringBuilder paramsBuilder = new StringBuilder();
                for (int i = 0; i < constructor.parameterTypes.length; i++) {
                    if (i > 0) paramsBuilder.append(", ");
                    paramsBuilder.append(constructor.parameterTypes[i])
                            .append(" ")
                            .append(constructor.parameterNames[i]);
                }
                String params = paramsBuilder.toString();

                String exceptions = constructor.exceptionTypes.length > 0 ?
                        " throws " + String.join(", ", constructor.exceptionTypes) : "";

                System.out.println(indentStr + "  " +
                        Modifier.toString(constructor.modifiers) + " " +
                        info.simpleName + "(" + params + ")" + exceptions);
            }
        }

        if (info.methods.length > 0) {
            System.out.println(indentStr + "方法列表:");
            for (MethodInfo method : info.methods) {
                StringBuilder paramsBuilder = new StringBuilder();
                for (int i = 0; i < method.parameterTypes.length; i++) {
                    if (i > 0) paramsBuilder.append(", ");
                    paramsBuilder.append(method.parameterTypes[i])
                            .append(" ")
                            .append(method.parameterNames[i]);
                }
                String params = paramsBuilder.toString();

                String exceptions = method.exceptionTypes.length > 0 ?
                        " throws " + String.join(", ", method.exceptionTypes) : "";

                System.out.println(indentStr + "  " +
                        Modifier.toString(method.modifiers) + " " +
                        method.returnType + " " +
                        method.name + "(" + params + ")" + exceptions);
            }
        }

        if (info.innerClasses.length > 0) {
            System.out.println(indentStr + "内部类列表:");
            for (ClassInfo innerClass : info.innerClasses) {
                printClassInfo(innerClass, indent + 2);
            }
        }
        System.out.println(indentStr + "==============");
    }

    private static String getClassType(ClassInfo info) {
        if (info.isInterface) return "接口";
        if (info.isEnum) return "枚举";
        if (info.isAnnotation) return "注解";
        if (info.isPrimitive) return "原生类型";
        return "类";
    }

    // 内部信息类定义
    public static class ClassInfo {
        public String fullClassName;
        public String simpleName;
        public int modifiers;
        public boolean isInterface;
        public boolean isEnum;
        public boolean isAnnotation;
        public boolean isPrimitive;
        public String superclassName;
        public String[] interfaceNames;
        public FieldInfo[] fields;
        public MethodInfo[] methods;
        public ConstructorInfo[] constructors;
        public ClassInfo[] innerClasses;
    }

    public static class FieldInfo {
        public String name;
        public String type;
        public int modifiers;
    }

    public static class MethodInfo {
        public String name;
        public String returnType;
        public int modifiers;
        public String[] parameterTypes;  // 参数类型
        public String[] parameterNames;  // 参数名
        public String[] exceptionTypes;
    }

    public static class ConstructorInfo {
        public int modifiers;
        public String[] parameterTypes;  // 参数类型
        public String[] parameterNames;  // 参数名
        public String[] exceptionTypes;
    }

    /**
     * 主方法用于测试
     */
    public static void main(String[] args) {
        try {
            // 创建多路径列表
            List<String> classPaths = new ArrayList<>();
            classPaths.add(System.getProperty("user.dir") + "/src/main/java");
            classPaths.add(System.getProperty("user.dir") + "/target/classes");
            classPaths.add(System.getProperty("user.dir") + "/lib");

            // 测试顶级类
            String topLevelClassName = "org.bxwbb.spigotplugincreatertool.HelloApplication";
            System.out.println("===== 分析顶级类 =====");
            ClassInfo topLevelInfo = analyzeClass(topLevelClassName, classPaths);
            printClassInfo(topLevelInfo);

            // 测试内部类
            System.out.println("\n===== 分析内部类 =====");
            String innerClassName = "org.bxwbb.spigotplugincreatertool.OuterClass.InnerClass";
            ClassInfo innerClassInfo = analyzeClass(innerClassName, classPaths);
            printClassInfo(innerClassInfo);
        } catch (Exception ignored) {
        }
    }
}