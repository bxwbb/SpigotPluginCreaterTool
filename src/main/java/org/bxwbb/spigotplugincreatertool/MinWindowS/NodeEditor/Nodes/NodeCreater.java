package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes;

import javafx.scene.paint.Color;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.ClassAnalyzer;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeCtr;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class NodeCreater {

    private NodeCreater() {
    }

    private static Node.VarType getVarType(String type) {
        return switch (type) {
            case "boolean" -> Node.VarType.BOOLEAN;
            case "byte" -> Node.VarType.BYTE;
            case "short" -> Node.VarType.SHORT;
            case "int" -> Node.VarType.INT;
            case "long" -> Node.VarType.LONG;
            case "float" -> Node.VarType.FLOAT;
            case "double" -> Node.VarType.DOUBLE;
            case "char" -> Node.VarType.CHAR;
            case "java.lang.String" -> Node.VarType.STRING;
            case "java.lang.Object", "void" -> Node.VarType.OBJECT;
            default -> Node.VarType.__DEFAULT__;
        };
    }

    private static Object getData(String type) {
        return switch (type) {
            case "boolean" -> true;
            case "byte" -> (byte) 0;
            case "short" -> (short) 0;
            case "int" -> 0;
            case "long" -> 0L;
            case "float" -> 0.0f;
            case "double" -> 0.0;
            case "char" -> 'A';
            case "java.lang.String" -> "字符串";
            default -> null;
        };
    }

    private static ClassAnalyzer.ClassInfo getClassInfo(String className) throws Exception {
        if (getVarType(className).equals(Node.VarType.__DEFAULT__)) {
            return ClassAnalyzer.analyzeClass(className, HelloApplication.paths);
        } else {
            return null;
        }
    }

    public static NodeCtr createNode(double x, double y, ClassAnalyzer.ClassInfo records2, ClassAnalyzer.MethodInfo methodInfo) throws Exception {

        List<Node.NodeCardNode> leftCardNodes = new ArrayList<>();
        ClassAnalyzer.ClassInfo classinfo;
        for (int i = 0; i < methodInfo.parameterTypes.length; i++) {
            classinfo = getClassInfo(methodInfo.parameterTypes[i]);
            leftCardNodes.add(
                    new Node.NodeCardNode(
                            methodInfo.parameterNames[i],
                            List.of(
                                    methodInfo.parameterNames[i]
                            ),
                            getVarType(methodInfo.parameterTypes[i]),
                            true,
                            List.of(),
                            getData(methodInfo.parameterTypes[i]),
                            classinfo
                    )
            );
        }
        if (!Modifier.isStatic(methodInfo.modifiers)) {
            classinfo = getClassInfo(records2.fullClassName);
            leftCardNodes.add(
                    new Node.NodeCardNode(
                            "操作对象",
                            List.of(
                                    "操作对象"
                            ),
                            getVarType(records2.fullClassName),
                            true,
                            List.of(),
                            getData(records2.fullClassName),
                            classinfo
                    )
            );
        }
        List<Node.NodeCardNode> rightCardNodes = new ArrayList<>();
        classinfo = getClassInfo(methodInfo.returnType);
        rightCardNodes.add(
                new Node.NodeCardNode(
                        "返回值",
                        List.of(
                                "返回值"
                        ),
                        getVarType(methodInfo.returnType),
                        true,
                        List.of(),
                        getData(methodInfo.returnType),
                        classinfo
                )
        );
        List<NodeCtr.NodeGetOutput> getOutputs = getNodeGetOutputs(records2, methodInfo);
        for (Node.NodeCardNode rightCardNode : rightCardNodes) {
            rightCardNode.edit.setVisible(false);
        }
        return new NodeCtr(
                x,
                y,
                new Node(
                        x,
                        y,
                        NodeTopBarColor.empty,
                        "来自内部方法:" + records2.fullClassName + "::" + methodInfo.name,
                        List.of(
                                "来自内部方法:" + records2.fullClassName + "::" + methodInfo.name
                        ),
                        leftCardNodes,
                        rightCardNodes,
                        Color.rgb(211, 47, 47),
                        true,
                        true
                ),
                getOutputs,
                records2.fullClassName + "." + methodInfo.name
        );
    }

    public static NodeCtr createNode(double x, double y, ClassAnalyzer.ClassInfo records2, ClassAnalyzer.FieldInfo fieldInfo) throws Exception {

        List<Node.NodeCardNode> leftCardNodes = new ArrayList<>();
        ClassAnalyzer.ClassInfo classinfo;
        if (!Modifier.isStatic(fieldInfo.modifiers)) {
            classinfo = getClassInfo(records2.fullClassName);
            leftCardNodes.add(
                    new Node.NodeCardNode(
                            "操作对象",
                            List.of(
                                    "操作对象"
                            ),
                            getVarType(records2.fullClassName),
                            true,
                            List.of(),
                            getData(records2.fullClassName),
                            classinfo
                    )
            );
        }
        List<Node.NodeCardNode> rightCardNodes = new ArrayList<>();
        classinfo = getClassInfo(fieldInfo.type);
        rightCardNodes.add(
                new Node.NodeCardNode(
                        fieldInfo.name,
                        List.of(
                                fieldInfo.name
                        ),
                        getVarType(fieldInfo.type),
                        true,
                        List.of(),
                        getData(fieldInfo.type),
                        classinfo
                )
        );
        List<NodeCtr.NodeGetOutput> getOutputs = getNodeGetOutputs(records2, fieldInfo);
        for (Node.NodeCardNode rightCardNode : rightCardNodes) {
            rightCardNode.edit.setVisible(false);
        }
        return new NodeCtr(
                x,
                y,
                new Node(
                        x,
                        y,
                        NodeTopBarColor.empty,
                        "来自内部字段:" + records2.fullClassName + "." + fieldInfo.name,
                        List.of(
                                "来自内部字段:" + records2.fullClassName + "." + fieldInfo.name
                        ),
                        leftCardNodes,
                        rightCardNodes,
                        Color.rgb(211, 47, 47),
                        true,
                        true
                ),
                getOutputs,
                records2.fullClassName + "." + fieldInfo.name
        );
    }

    @NotNull
    private static List<NodeCtr.NodeGetOutput> getNodeGetOutputs(ClassAnalyzer.ClassInfo records2, ClassAnalyzer.MethodInfo methodInfo) {
        List<NodeCtr.NodeGetOutput> getOutputs = new ArrayList<>();
        if (Modifier.isStatic(methodInfo.modifiers)) {
            getOutputs.add(
                    inputs -> {
                        try {
                            Function<List<Object>, Object> func = ClassAnalyzer.getFunctionFromInfoObjects(
                                    methodInfo,
                                    records2,
                                    HelloApplication.paths,
                                    null
                            );
                            List<Object> i = new ArrayList<>();
                            for (NodeCtr.NodeInput input : inputs) {
                                i.add(input.value());
                            }
                            return func.apply(i);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } else {
            getOutputs.add(
                    inputs -> {
                        try {
                            Function<List<Object>, Object> func = ClassAnalyzer.getFunctionFromInfoObjects(
                                    methodInfo,
                                    records2,
                                    HelloApplication.paths,
                                    inputs.getLast().value()
                            );
                            List<NodeCtr.NodeInput> inputs1 = inputs.subList(0, inputs.size() - 1);
                            List<Object> i = new ArrayList<>();
                            inputs1.forEach(input -> i.add(input.value()));
                            return func.apply(i);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
        return getOutputs;
    }

    private static List<NodeCtr.NodeGetOutput> getNodeGetOutputs(ClassAnalyzer.ClassInfo records2, ClassAnalyzer.FieldInfo fieldInfo) {
        List<NodeCtr.NodeGetOutput> getOutputs = new ArrayList<>();
        if (Modifier.isStatic(fieldInfo.modifiers)) {
            getOutputs.add(
                    inputs -> {
                        try {
                            Supplier<Field> func = ClassAnalyzer.getFieldAddress(
                                    records2,
                                    fieldInfo,
                                    HelloApplication.paths,
                                    null
                            );
                            return func.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } else {
            getOutputs.add(
                    inputs -> {
                        try {
                            Supplier<Field> func = ClassAnalyzer.getFieldAddress(
                                    records2,
                                    fieldInfo,
                                    HelloApplication.paths,
                                    inputs.getFirst().value()
                            );
                            return func.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
        return getOutputs;
    }

//    PRINT("打印输出(System.out.println)", new NodeCtr(
//            0,
//            0,
//                  new Node(
//                  0,
//                    0,
//          NodeTopBarColor.empty,
//                    "打印输出(System.out.println)",
//          List.of(
//                  "需要输出的值"
//    ),
//                    List.of(
//                            new Node.NodeCardNode(
//                            "值",
//                            List.of(
//                            "打印的值"
//                            ),
//    Node.VarType.STRING,
//            true,
//            List.of(),
//            "输出"
//            )
//            ),
//            List.of(
//            new Node.NodeCardNode(
//            "值",
//            List.of(
//            "输入的值"
//            ),
//    Node.VarType.STRING,
//            false,
//            List.of(),
//            "输出"
//            )
//            ),
//    NodeTopBarColor.INPUT_TOP_BAR_COLOR
//            ),
//                    List.of(
//    inputs -> {
//        System.out.println(inputs.getFirst().value());
//        return inputs.getFirst().value();
//    }
//            ),
//                    "java.io.PrintStream.println(java.lang.Object)"
//                    )),

}
