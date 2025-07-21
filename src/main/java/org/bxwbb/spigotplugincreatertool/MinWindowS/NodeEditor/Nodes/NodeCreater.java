package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes;

import javafx.scene.paint.Color;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.ClassAnalyzer;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeCtr;

import java.util.ArrayList;
import java.util.List;

public class NodeCreater {

    private NodeCreater() {
    }

    public static NodeCtr createNode(double x, double y, ClassAnalyzer.ClassInfo records2, ClassAnalyzer.MethodInfo methodInfo) {

        List<Node.NodeCardNode> leftCardNodes = new ArrayList<>();
        for (int i = 0; i < methodInfo.parameterTypes.length; i++) {
            leftCardNodes.add(
                    new Node.NodeCardNode(
                            methodInfo.parameterNames[i],
                            List.of(
                                    methodInfo.parameterNames[i]
                            ),
                            switch (methodInfo.parameterTypes[i]) {
                                case "boolean":
                                    yield Node.VarType.BOOLEAN;
                                case "byte":
                                    yield Node.VarType.BYTE;
                                case "short":
                                    yield Node.VarType.SHORT;
                                case "int":
                                    yield Node.VarType.INT;
                                case "long":
                                    yield Node.VarType.LONG;
                                case "float":
                                    yield Node.VarType.FLOAT;
                                case "double":
                                    yield Node.VarType.DOUBLE;
                                case "char":
                                    yield Node.VarType.CHAR;
                                case "java.lang.String":
                                    yield Node.VarType.STRING;
                                default:
                                    yield Node.VarType.OBJECT;
                            },
                            true,
                            List.of(),
                            switch (methodInfo.parameterTypes[i]) {
                                case "boolean":
                                    yield true;
                                case "byte":
                                    yield (byte) 0;
                                case "short":
                                    yield (short) 0;
                                case "int":
                                    yield 0;
                                case "long":
                                    yield 0L;
                                case "float":
                                    yield 0.0f;
                                case "double":
                                    yield 0.0;
                                case "char":
                                    yield 'A';
                                case "java.lang.String":
                                    yield "";
                                default:
                                    yield null;
                            }
                    )
            );
        }
        List<Node.NodeCardNode> rightCardNodes = new ArrayList<>();
        rightCardNodes.add(
                new Node.NodeCardNode(
                        "返回值",
                        List.of(
                                "返回值"
                        ),
                        Node.VarType.OBJECT,
                        true,
                        List.of(),
                        null
                )
        );
        return new NodeCtr(
                x,
                y,
                new Node(
                        x,
                        y,
                        NodeTopBarColor.empty,
                        "来自内部方法:" + records2.fullClassName + "->" + methodInfo.name,
                        List.of(
                                "来自内部方法:" + records2.fullClassName + "->" + methodInfo.name
                        ),
                        leftCardNodes,
                        rightCardNodes,
                        Color.rgb(211, 47, 47),
                        true,
                        true
                ),
                List.of(
                ),
                records2.fullClassName + "." + methodInfo.name
        );
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
