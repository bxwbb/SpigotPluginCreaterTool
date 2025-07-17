package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes;

import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeCtr;
import org.bxwbb.spigotplugincreatertool.windowLabel.StringInput;

import java.util.List;

public enum InputNodes {

    MAIN("开始程序(程序入口点main函数)", new NodeCtr(0,0, new Node(
            0,
            0,
            NodeTopBarColor.empty,
            "开始程序(程序入口点main函数)",
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
                            ),List.of(
                            new StringInput(0,0,0,0,"args",List.of(), "bxwbb")
                    ))
            ),
            NodeTopBarColor.INPUT_TOP_BAR_COLOR
    ),
            List.of(
                    inputs -> List.of(
                            "bxwbb",
                            "bilibili:1814140675",
                            "QQ:3754934636"
                    )
            )
            ));

    public final String name;
    public final NodeCtr nodeCtr;

    InputNodes(String name, NodeCtr nodeCtr) {
        this.name = name;
        this.nodeCtr = nodeCtr;
    }

}
