package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceScannerFixed {

    static final Logger logger = LoggerFactory.getLogger(JavaSourceScannerFixed.class);

    /**
     * 类类型枚举（外部类6种 + 内部类6种）
     */
    public enum ClassType {
        // 外部类
        TOP_LEVEL_INTERFACE, TOP_LEVEL_ABSTRACT_CLASS, TOP_LEVEL_ENUM,
        TOP_LEVEL_RECORD, TOP_LEVEL_ANNOTATION, TOP_LEVEL_CLASS,
        // 内部类
        INNER_INTERFACE, INNER_ABSTRACT_CLASS, INNER_ENUM,
        INNER_RECORD, INNER_ANNOTATION, INNER_CLASS
    }

    /**
     * 类信息容器（包含全类名和正确的外部类归属）
     *
     * @param fullClassName      全类名（包名.外部类$内部类）
     * @param simpleClassName    简单类名（不含包和外部类）
     * @param filePath           源文件路径
     * @param type               类类型
     * @param outerFullClassName 外部类全类名（内部类专属）
     */
        public record ClassInfo(String fullClassName, String simpleClassName, String filePath, ClassType type,
                                String outerFullClassName) {

        @Override
            public String toString() {
                if (outerFullClassName == null) {
                    return String.format("[%s] %s（文件：%s）", type, fullClassName, filePath);
                } else {
                    return String.format("[%s] %s（外部类：%s，文件：%s）", type, fullClassName, outerFullClassName, filePath);
                }
            }
        }

    /**
     * 扫描指定目录下的Java源文件，返回正确归属的类信息
     */
    public static List<ClassInfo> scanJavaSources(String path) {
        List<ClassInfo> result = new ArrayList<>();
        File root = new File(path);

        if (!root.exists() || !root.isDirectory()) {
            logger.error("路径不存在或不是目录: {}", path);
            return result;
        }

        scanDirectory(root, result);
        return result;
    }

    /**
     * 递归扫描目录下的所有.java文件
     */
    private static void scanDirectory(File directory, List<ClassInfo> result) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, result);
            } else if (file.getName().endsWith(".java")) {
                try {
                    processJavaFile(file, result);
                } catch (IOException e) {
                    logger.error("处理文件失败: {} - {}", file.getAbsolutePath(), e.getMessage());
                }
            }
        }
    }

    /**
     * 处理单个.java文件：先提取所有类声明，再按顺序识别外部类和内部类
     */
    private static void processJavaFile(File file, List<ClassInfo> result) throws IOException {
        String content = Files.readString(file.toPath());
        String codeWithoutComments = removeComments(content); // 彻底移除注释
        String packageName = extractPackageName(codeWithoutComments); // 提取包名
        String packagePrefix = packageName.isEmpty() ? "" : (packageName + ".");

        // 1. 先提取文件中所有的类声明（按出现顺序）
        List<ClassDeclaration> allDeclarations = extractAllClassDeclarations(codeWithoutComments);

        // 2. 第一次遍历：识别并记录所有外部类（顶层类）
        List<ClassDeclaration> topLevelDeclarations = new ArrayList<>();
        for (ClassDeclaration decl : allDeclarations) {
            if (isTopLevelDeclaration(decl, codeWithoutComments)) {
                topLevelDeclarations.add(decl);
            }
        }

        // 3. 第二次遍历：处理所有类（外部类+内部类），确保内部类能关联到正确的外部类
        for (ClassDeclaration decl : allDeclarations) {
            boolean isTopLevel = topLevelDeclarations.contains(decl);
            String simpleClassName = decl.className;
            String typeKeyword = decl.typeKeyword;
            String abstractModifier = decl.abstractModifier;

            // 确定外部类归属（内部类专用）
            String outerFullClassName = null;
            if (!isTopLevel) {
                // 从已识别的外部类中找最近的一个（按出现顺序）
                outerFullClassName = findNearestOuterClass(decl, topLevelDeclarations, packagePrefix);
            }

            // 生成全类名
            String fullClassName;
            if (isTopLevel) {
                fullClassName = packagePrefix + simpleClassName;
            } else {
                fullClassName = outerFullClassName + "$" + simpleClassName;
            }
            logger.trace("尝试查找类 {}", fullClassName);

            // 确定类类型
            ClassType classType = determineClassType(typeKeyword, abstractModifier, isTopLevel);

            // 添加到结果
            result.add(new ClassInfo(
                    fullClassName,
                    simpleClassName,
                    file.getAbsolutePath(),
                    classType,
                    isTopLevel ? null : outerFullClassName
            ));
            logger.trace("成功的查找 {}", fullClassName);
        }
    }

    /**
     * 提取文件中所有的类声明（按出现顺序）
     */
    private static List<ClassDeclaration> extractAllClassDeclarations(String code) {
        List<ClassDeclaration> declarations = new ArrayList<>();
        // 匹配所有类/接口/枚举/记录/注解声明
        Pattern pattern = Pattern.compile(
                "(?<whitespace>\\s+|^)" +
                        "(?<access>public\\s+|protected\\s+|private\\s+)?" +
                        "(?<static>static\\s+)?" +
                        "(?<abstract>abstract\\s+)?" +
                        "(?<keyword>class|interface|enum|record|@interface)\\s+" +
                        "(?<name>[A-Za-z_$][A-Za-z0-9_$]*)",
                Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            ClassDeclaration decl = new ClassDeclaration();
            decl.startIndex = matcher.start();
            decl.whitespace = matcher.group("whitespace");
            decl.accessModifier = matcher.group("access");
            decl.staticModifier = matcher.group("static");
            decl.abstractModifier = matcher.group("abstract");
            decl.typeKeyword = matcher.group("keyword");
            decl.className = matcher.group("name");
            declarations.add(decl);
        }
        return declarations;
    }

    /**
     * 判断是否为外部类（顶层类）
     */
    private static boolean isTopLevelDeclaration(ClassDeclaration decl, String code) {
        // 外部类的判断：声明前的花括号数量平衡（{和}数量相等）
        String prefix = code.substring(0, decl.startIndex);
        int braceCount = countBraceBalance(prefix);
        return braceCount == 0; // 花括号平衡（不在任何类内部）
    }

    /**
     * 统计花括号平衡数（{ +1，} -1）
     */
    private static int countBraceBalance(String code) {
        int count = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') count++;
            else if (c == '}') count--;
        }
        return count;
    }

    /**
     * 为内部类找最近的外部类（按出现顺序，且在内部类之前声明）
     */
    private static String findNearestOuterClass(ClassDeclaration innerDecl, List<ClassDeclaration> topLevelDeclarations, String packagePrefix) {
        // 遍历外部类，找声明位置在内部类之前的最后一个
        ClassDeclaration nearestOuter = null;
        for (ClassDeclaration outerDecl : topLevelDeclarations) {
            if (outerDecl.startIndex < innerDecl.startIndex) {
                nearestOuter = outerDecl;
            }
        }
        // 如果找不到外部类（理论上Java不允许），则用文件中第一个类作为默认
        if (nearestOuter == null) {
            nearestOuter = topLevelDeclarations.isEmpty() ?
                    new ClassDeclaration("DefaultOuter", "", "", "", "", 0) :
                    topLevelDeclarations.getFirst();
        }
        return packagePrefix + nearestOuter.className;
    }

    /**
     * 确定类的具体类型
     */
    private static ClassType determineClassType(String typeKeyword, String abstractModifier, boolean isTopLevel) {
        switch (typeKeyword) {
            case "interface":
                return isTopLevel ? ClassType.TOP_LEVEL_INTERFACE : ClassType.INNER_INTERFACE;
            case "enum":
                return isTopLevel ? ClassType.TOP_LEVEL_ENUM : ClassType.INNER_ENUM;
            case "record":
                return isTopLevel ? ClassType.TOP_LEVEL_RECORD : ClassType.INNER_RECORD;
            case "@interface":
                return isTopLevel ? ClassType.TOP_LEVEL_ANNOTATION : ClassType.INNER_ANNOTATION;
            case "class":
                if (abstractModifier != null) {
                    return isTopLevel ? ClassType.TOP_LEVEL_ABSTRACT_CLASS : ClassType.INNER_ABSTRACT_CLASS;
                } else {
                    return isTopLevel ? ClassType.TOP_LEVEL_CLASS : ClassType.INNER_CLASS;
                }
            default:
                return ClassType.TOP_LEVEL_CLASS;
        }
    }

    /**
     * 提取Java文件的包名
     */
    private static String extractPackageName(String code) {
        Pattern packagePattern = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;", Pattern.MULTILINE);
        Matcher matcher = packagePattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * 移除代码中的所有注释（单行、多行、JavaDoc）
     */
    private static String removeComments(String code) {
        // 移除单行注释
        code = code.replaceAll("//.*$", "");
        // 移除多行注释和JavaDoc（非贪婪匹配）
        code = code.replaceAll("/\\*.*?\\*/", "");
        return code;
    }

    /**
     * 内部类：存储类声明的信息
     */
    private static class ClassDeclaration {
        String className;        // 类名
        String typeKeyword;      // 类型关键字（class/interface等）
        String accessModifier;   // 访问修饰符
        String staticModifier;   // static修饰符
        String abstractModifier; // abstract修饰符
        String whitespace;       // 声明前的空白
        int startIndex;          // 在代码中的起始位置（用于判断顺序）

        public ClassDeclaration() {}

        public ClassDeclaration(String className, String typeKeyword, String accessModifier, String staticModifier, String abstractModifier, int startIndex) {
            this.className = className;
            this.typeKeyword = typeKeyword;
            this.accessModifier = accessModifier;
            this.staticModifier = staticModifier;
            this.abstractModifier = abstractModifier;
            this.startIndex = startIndex;
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {

        List<ClassInfo> classes = scanJavaSources("F:\\McServer\\Plugin\\SpigotPluginCreaterTool\\src\\main\\java\\org\\bxwbb\\spigotplugincreatertool");
        System.out.println("扫描结果（共 " + classes.size() + " 个类）：");
        for (ClassInfo info : classes) {
            System.out.println(info);
        }
    }
}