package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes;

import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeCtr;
import org.bxwbb.spigotplugincreatertool.windowLabel.ObjectInput;
import org.bxwbb.spigotplugincreatertool.windowLabel.SliderLong;
import org.bxwbb.spigotplugincreatertool.windowLabel.StringInput;

import java.util.List;
import java.util.Random;

@SuppressWarnings("ALL")
public enum InputNodes {

    MAIN(NodeTopBarColor.START_NODE_TITLE, new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(0, 0, new Node(
                        0,
                        0,
                        NodeTopBarColor.empty,
                        NodeTopBarColor.START_NODE_TITLE,
                        List.of(
                                "若运行这个文件,则执行的第一个指令是这个函数"
                        ),
                        List.of(),
                        List.of(
                                new Node.NodeCardNode("Args(执行时外部参数)", List.of(
                                        "在外部(如cmd)执行java运行命令时,这个命令后的参数"
                                ),
                                        Node.VarType.LIST,
                                        false,
                                        List.of(
                                                Node.VarType.STRING
                                        ), List.of(
                                        new StringInput(0, 0, 0, 0, "bxwbb", "args", List.of())
                                ))
                        ),
                        NodeTopBarColor.INPUT_TOP_BAR_COLOR,
                        false,
                        true
                ),
                        List.of(
                                inputs -> List.of(
                                        "bxwbb",
                                        "bilibili:1814140675",
                                        "QQ:3754934636"
                                )
                        ),
                        "main"
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate()),
    PRINT("打印输出(System.out.println)", new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(
                        0,
                        0,
                        new Node(
                                0,
                                0,
                                NodeTopBarColor.empty,
                                "打印输出(System.out.println)",
                                List.of(
                                        "需要输出的值"
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "值",
                                                List.of(
                                                        "打印的值"
                                                ),
                                                Node.VarType.STRING,
                                                true,
                                                List.of(),
                                                "输出"
                                        )
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "值",
                                                List.of(
                                                        "输入的值"
                                                ),
                                                Node.VarType.STRING,
                                                false,
                                                List.of(),
                                                "输出"
                                        )
                                ),
                                NodeTopBarColor.INPUT_TOP_BAR_COLOR,
                                true,
                                true
                        ),
                        List.of(
                                inputs -> {
                                    System.out.println(inputs.getFirst().value());
                                    return inputs.getFirst().value();
                                }
                        ),
                        "java.io.PrintStream.println(java.lang.Object)"
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate()),
    LIST_GET("获取列表元素", new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(
                        0,
                        0,
                        new Node(
                                0,
                                0,
                                NodeTopBarColor.empty,
                                "获取列表元素",
                                List.of(
                                        "元素"
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "索引",
                                                List.of(
                                                        "列表的索引"
                                                ),
                                                Node.VarType.INT,
                                                true,
                                                List.of(),
                                                0
                                        ),
                                        new Node.NodeCardNode(
                                                "列表",
                                                List.of(
                                                        "列表的索引"
                                                ),
                                                Node.VarType.LIST,
                                                true,
                                                List.of(
                                                        Node.VarType.OBJECT
                                                ),
                                                List.of(
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表")),
                                                        new ObjectInput(0, 0, 0, 0, null, "列表", List.of("列表"))
                                                )
                                        )
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "值",
                                                List.of(
                                                        "该列表中索引位置的值"
                                                ),
                                                Node.VarType.OBJECT,
                                                false,
                                                List.of(),
                                                null
                                        )
                                ),
                                NodeTopBarColor.INPUT_TOP_BAR_COLOR,
                                true,
                                true
                        ),
                        List.of(
                                inputs -> {
                                    int index = (int) inputs.get(0).value();
                                    List<Object> ol = (List<Object>) inputs.get(1).value();
                                    return ol.get(index);
                                }
                        ),
                        "java.util.List.get"
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate()),
    INT_TO_STRING("转换为字符串", new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(
                        0,
                        0,
                        new Node(
                                0,
                                0,
                                NodeTopBarColor.empty,
                                "整型变字符串",
                                List.of(
                                        "数据"
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "数据",
                                                List.of(
                                                        "数据"
                                                ),
                                                Node.VarType.INT,
                                                true,
                                                List.of(),
                                                0
                                        )
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "字符串",
                                                List.of(
                                                        "字符串"
                                                ),
                                                Node.VarType.STRING,
                                                false,
                                                List.of(),
                                                null
                                        )
                                ),
                                NodeTopBarColor.INPUT_TOP_BAR_COLOR,
                                true,
                                true
                        ),
                        List.of(
                                inputs -> {
                                    return String.valueOf(inputs.get(0).value());
                                }
                        ),
                        "java.util.List.get"
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate()),
    RANDOM("内部测试用随机数", new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(
                        0,
                        0,
                        new Node(
                                0,
                                0,
                                NodeTopBarColor.empty,
                                "内部测试用随机数",
                                List.of(
                                        "内部测试用随机数"
                                ),
                                List.of(),
                                List.of(
                                        new Node.NodeCardNode(
                                                "随机数",
                                                List.of(
                                                        "随机数"
                                                ),
                                                Node.VarType.LONG,
                                                false,
                                                List.of(),
                                                0L
                                        )
                                ),
                                NodeTopBarColor.INPUT_TOP_BAR_COLOR,
                                true,
                                true
                        ),
                        List.of(
                                inputs -> {
                                    Random random = new Random();
                                    return random.nextLong(1000);
                                }
                        ),
                        ""
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate()),
    ADD("标量相加", new Object() {
        NodeCtr evaluate() {
            try {
                return new NodeCtr(
                        0,
                        0,
                        new Node(
                                0,
                                0,
                                NodeTopBarColor.empty,
                                "标量相加",
                                List.of(
                                        "输入的所有值"
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "输入的所有值",
                                                List.of(
                                                        "输入的所有值"
                                                ),
                                                Node.VarType.SELF_ADAPTION_LIST,
                                                true,
                                                List.of(
                                                        Node.VarType.LONG
                                                ),
                                                List.of(
                                                        new SliderLong(0, 0, 0, 0, 0L, "long", List.of("参数描述-Parameter description"), false, false, 0L, 0L, 1L)
                                                )
                                        )
                                ),
                                List.of(
                                        new Node.NodeCardNode(
                                                "和",
                                                List.of(
                                                        "所有输入之和"
                                                ),
                                                Node.VarType.LONG,
                                                false,
                                                List.of(),
                                                0L
                                        )
                                ),
                                NodeTopBarColor.MATH_TOP_BAR_COLOR,
                                true,
                                true
                        ),
                        List.of(
                                inputs -> {
                                    long sum = 0;
                                    List<Long> is = (List<Long>) inputs.getFirst().value();
                                    for (Long i : is) {
                                        sum += i;
                                    }
                                    return sum;
                                }
                        ),
                        "java.io.PrintStream.println(java.lang.Object)"
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }.evaluate());

    public final String name;
    public final NodeCtr nodeCtr;

    InputNodes(String name, NodeCtr nodeCtr) {
        this.name = name;
        this.nodeCtr = nodeCtr;
    }

}
