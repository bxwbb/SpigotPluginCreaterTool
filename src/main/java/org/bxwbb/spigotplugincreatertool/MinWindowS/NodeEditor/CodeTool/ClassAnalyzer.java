package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * 获取指定类中的指定方法对象
     */
    public static Method getMethod(String fullClassName, String methodName,
                                   Class<?>[] parameterTypes, List<String> classPaths) throws Exception {
        List<String> possibleClassNames = generateClassNamesWithDollar(fullClassName);

        Class<?> clazz = null;
        loop:
        for (String className : possibleClassNames) {
            for (String path : classPaths) {
                try {
                    clazz = loadClassFromPath(className, path);
                    break loop;
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        if (clazz == null) {
            throw new ClassNotFoundException("找不到类: " + fullClassName);
        }

        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null) {
                try {
                    return superClass.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    superClass = superClass.getSuperclass();
                }
            }
            throw new NoSuchMethodException("在类 " + fullClassName + " 及其父类中找不到方法: " + methodName);
        }
    }

    /**
     * 将Method对象转换为Function<List<Object>, Object>
     */
    public static Function<List<Object>, Object> getMethodFunction(Method method, Object instance) {
        method.setAccessible(true);

        return params -> {
            try {
                if (params.size() != method.getParameterCount()) {
                    throw new IllegalArgumentException("参数数量不匹配，预期: " +
                            method.getParameterCount() + ", 实际: " + params.size());
                }

                Object[] paramsArray = params.toArray(new Object[0]);
                return method.invoke(instance, paramsArray);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("方法调用失败: " + e.getMessage(), e);
            }
        };
    }

    /**
     * 将Constructor对象转换为Function<List<Object>, Object>
     * 该函数接收参数列表，返回通过构造函数创建的类实例
     */
    public static Function<List<Object>, Object> getConstructorFunction(Constructor<?> constructor) {
        constructor.setAccessible(true);

        return params -> {
            try {
                if (params.size() != constructor.getParameterCount()) {
                    throw new IllegalArgumentException("参数数量不匹配，预期: " +
                            constructor.getParameterCount() + ", 实际: " + params.size());
                }

                Object[] paramsArray = params.toArray(new Object[0]);
                return constructor.newInstance(paramsArray);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("构造函数调用失败: " + e.getMessage(), e);
            }
        };
    }

    /**
     * 通过ConstructorInfo获取Function对象
     * 该函数接收参数列表，返回通过构造函数创建的类实例
     */
    public static Function<List<Object>, Object> getFunctionFromConstructorInfo(
            ConstructorInfo constructorInfo,
            ClassInfo classInfo,
            List<String> classPaths) throws Exception {
        // 1. 加载类
        Class<?> clazz = getClassFromString(classInfo.fullClassName, classPaths);

        // 2. 解析参数类型
        Class<?>[] parameterTypes = new Class[constructorInfo.parameterTypes.length];
        for (int i = 0; i < constructorInfo.parameterTypes.length; i++) {
            parameterTypes[i] = getClassFromString(constructorInfo.parameterTypes[i], classPaths);
        }

        // 3. 查找构造函数
        Constructor<?> constructor = findConstructor(clazz, parameterTypes);

        // 4. 转换为Function并返回
        return getConstructorFunction(constructor);
    }

    /**
     * 查找类中的构造函数（包括私有构造函数）
     */
    private static Constructor<?> findConstructor(Class<?> clazz, Class<?>[] parameterTypes) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("在类 " + clazz.getName() + " 中找不到匹配的构造函数");
        }
    }

    /**
     * 联动方法：直接通过类名、方法名、参数类型和类路径获取Function对象
     */
    public static Function<List<Object>, Object> getFunctionFromMethodInfo(
            String fullClassName,
            String methodName,
            Class<?>[] parameterTypes,
            List<String> classPaths,
            Object instance) throws Exception {
        Method method = getMethod(fullClassName, methodName, parameterTypes, classPaths);
        return getMethodFunction(method, instance);
    }

    /**
     * 通过MethodInfo和ClassInfo获取Function对象
     */
    public static Function<List<Object>, Object> getFunctionFromInfoObjects(
            MethodInfo methodInfo,
            ClassInfo classInfo,
            List<String> classPaths,
            Object instance) throws Exception {
        String fullClassName = classInfo.fullClassName;
        String methodName = methodInfo.name;

        Class<?>[] parameterTypes = new Class[methodInfo.parameterTypes.length];
        for (int i = 0; i < methodInfo.parameterTypes.length; i++) {
            parameterTypes[i] = getClassFromString(methodInfo.parameterTypes[i], classPaths);
        }

        return getFunctionFromMethodInfo(fullClassName, methodName, parameterTypes, classPaths, instance);
    }

    /**
     * 新增：获取字段的地址引用（通过Field对象实现）
     * 传入ClassInfo和FieldInfo，返回能获取字段引用的Supplier
     * @param classInfo 类信息对象
     * @param fieldInfo 字段信息对象
     * @param classPaths 类路径列表
     * @param instance 实例对象（静态字段可传null）
     * @return Supplier<Field> 提供字段引用的函数式接口
     * @throws Exception 当类或字段找不到时抛出
     */
    public static Supplier<Field> getFieldAddress(
            ClassInfo classInfo,
            FieldInfo fieldInfo,
            List<String> classPaths,
            Object instance) throws Exception {
        // 1. 从ClassInfo获取类名并加载类
        Class<?> clazz = getClassFromString(classInfo.fullClassName, classPaths);

        // 2. 从FieldInfo获取字段名并查找字段
        Field field = findField(clazz, fieldInfo.name);

        // 3. 检查字段是否为静态，验证实例对象是否符合要求
        if (Modifier.isStatic(field.getModifiers())) {
            if (instance != null) {
                throw new IllegalArgumentException("静态字段不需要实例对象，应传入null");
            }
        } else {
            if (instance == null) {
                throw new IllegalArgumentException("实例字段必须传入有效的实例对象");
            }
            if (!clazz.isInstance(instance)) {
                throw new ClassCastException("实例对象类型与类信息不匹配");
            }
        }

        // 4. 设置字段可访问（包括私有字段）
        field.setAccessible(true);

        // 5. 返回提供字段引用的Supplier
        return () -> field;
    }

    /**
     * 查找类及其父类中的字段
     */
    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return findField(superClass, fieldName);
            }
            throw new NoSuchFieldException("在类 " + clazz.getName() + " 及其父类中找不到字段: " + fieldName);
        }
    }

    /**
     * 将类名字符串转换为Class对象
     */
    private static Class<?> getClassFromString(String className, List<String> classPaths) throws Exception {
        // 处理基本类型
        if ("int".equals(className)) return int.class;
        if ("long".equals(className)) return long.class;
        if ("float".equals(className)) return float.class;
        if ("double".equals(className)) return double.class;
        if ("boolean".equals(className)) return boolean.class;
        if ("char".equals(className)) return char.class;
        if ("byte".equals(className)) return byte.class;
        if ("short".equals(className)) return short.class;
        if ("void".equals(className)) return void.class;

        // 处理引用类型
        List<String> possibleClassNames = generateClassNamesWithDollar(className);
        for (String name : possibleClassNames) {
            for (String path : classPaths) {
                try {
                    return loadClassFromPath(name, path);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        // 如果所有路径都找不到，尝试使用系统类加载器
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("找不到类: " + className);
        }
    }

    /**
     * 生成所有可能的类名变体
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
                    Parameter[] params = method.getParameters();
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
                    Parameter[] params = constructor.getParameters();
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
            classPaths.add(System.getProperty("java.home") + "/lib/rt.jar"); // JDK内置类路径

            // 测试字段地址获取功能
            System.out.println("===== 测试字段地址获取 =====");

            // 1. 分析String类获取ClassInfo
            ClassInfo stringClassInfo = analyzeClass("java.lang.String", classPaths);

            // 2. 查找value字段（String类中存储字符的私有字段）
            FieldInfo valueFieldInfo = null;
            for (FieldInfo field : stringClassInfo.fields) {
                if ("value".equals(field.name) && "char[]".equals(field.type)) {
                    valueFieldInfo = field;
                    break;
                }
            }

            if (valueFieldInfo != null) {
                // 3. 创建测试字符串实例
                String testStr = "Hello";

                // 4. 获取字段地址引用
                Supplier<Field> fieldSupplier = getFieldAddress(
                        stringClassInfo,
                        valueFieldInfo,
                        classPaths,
                        testStr
                );

                // 5. 通过字段引用操作字段
                Field valueField = fieldSupplier.get();
                System.out.println("获取到的字段: " + valueField);
                System.out.println("字段类型: " + valueField.getType());
                System.out.println("字段修饰符: " + Modifier.toString(valueField.getModifiers()));

                // 6. 读取字段值（字符数组）
                char[] value = (char[]) valueField.get(testStr);
                System.out.println("字段值: " + new String(value)); // 输出 "Hello"
            }

            // 测试静态字段
            System.out.println("\n===== 测试静态字段地址获取 =====");

            // 1. 分析Integer类
            ClassInfo integerClassInfo = analyzeClass("java.lang.Integer", classPaths);

            // 2. 查找MAX_VALUE静态字段
            FieldInfo maxValueFieldInfo = null;
            for (FieldInfo field : integerClassInfo.fields) {
                if ("MAX_VALUE".equals(field.name) && "int".equals(field.type)) {
                    maxValueFieldInfo = field;
                    break;
                }
            }

            if (maxValueFieldInfo != null) {
                // 3. 获取静态字段地址引用（实例传null）
                Supplier<Field> staticFieldSupplier = getFieldAddress(
                        integerClassInfo,
                        maxValueFieldInfo,
                        classPaths,
                        null
                );

                // 4. 操作静态字段
                Field maxValueField = staticFieldSupplier.get();
                System.out.println("获取到的静态字段: " + maxValueField);
                int maxValue = (int) maxValueField.get(null); // 静态字段get参数传null
                System.out.println("Integer.MAX_VALUE = " + maxValue); // 输出2147483647
            }

            // 测试构造函数Function
            System.out.println("\n===== 测试构造函数Function =====");

            // 1. 分析String类获取ClassInfo（已在前面获取）
            // 2. 获取String类的一个构造函数信息（例如: String(char[] value)）
            ConstructorInfo stringConstructorInfo = null;
            for (ConstructorInfo constructor : stringClassInfo.constructors) {
                if (constructor.parameterTypes.length == 1 && "char[]".equals(constructor.parameterTypes[0])) {
                    stringConstructorInfo = constructor;
                    break;
                }
            }

            if (stringConstructorInfo != null) {
                // 3. 获取构造函数的Function对象
                Function<List<Object>, Object> constructorFunction = getFunctionFromConstructorInfo(
                        stringConstructorInfo,
                        stringClassInfo,
                        classPaths
                );

                // 4. 使用Function创建实例
                char[] chars = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
                List<Object> params = new ArrayList<>();
                params.add(chars);

                String newString = (String) constructorFunction.apply(params);
                System.out.println("通过构造函数创建的字符串: " + newString); // 输出 "Hello World"
            }

        } catch (Exception ignored) {
        }
    }
}
