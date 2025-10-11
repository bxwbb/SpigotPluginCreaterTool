package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.InputNodes;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.NodeTopBarColor;
import org.bxwbb.spigotplugincreatertool.windowLabel.ConnectingLine;
import org.bxwbb.spigotplugincreatertool.windowLabel.SelfAdaptionListSlider;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class NodeCtr implements Serializable {

    public record NodeInput(Object value, Node.VarType type) {
    }

    @FunctionalInterface
    public interface NodeGetOutput extends Function<List<NodeInput>, Object> {
    }

    public List<Object> outputs = new ArrayList<>();

    public Node node;
    public List<Node.VarType> inputTypes;
    public List<Node.VarType> outputTypes;
    public String name;
    public double startX;
    public double startY;
    public List<NodeGetOutput> getOutputs;
    // 代表的方法路径
    public String methodPath;
    public UUID uuid;

    public NodeCtr(double startX, double startY, Node node, List<NodeGetOutput> getOutputs, String methodPath) {
        this.methodPath = methodPath;
        this.startX = startX;
        this.startY = startY;
        this.getOutputs = getOutputs;
        this.node = node;
        this.uuid = node.uuid;
        this.inputTypes = new ArrayList<>();
        this.outputTypes = new ArrayList<>();
        for (Node.NodeCardNode nodeCardNode : node.leftCardNodes) {
            this.inputTypes.add(nodeCardNode.varType);
        }
        for (Node.NodeCardNode nodeCardNode : node.rightCardNodes) {
            this.outputTypes.add(nodeCardNode.varType);
            this.outputs.add(0);
        }
    }

    public NodeCtr createNew(double x, double y, Group root) throws ClassNotFoundException {
        return new NodeCtr(x, y, this.node.createNew(x, y, root), this.getOutputs, this.methodPath);
    }

    public void runCard() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                try {
                    this.node.updateInput();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        List<NodeInput> inp = new ArrayList<>();
        for (int i = 0; i < this.inputTypes.size(); i++) {
            inp.add(new NodeInput(this.node.leftCardNodes.get(i).edit.getData(), this.inputTypes.get(i)));
            int finalI = i;
            Platform.runLater(() -> {
                if (!this.inputTypes.get(finalI).equals(Node.VarType.SELF_ADAPTION_LIST)) {
                    if (this.node.leftDataLines.get(finalI) != null)
                        this.node.leftDataLines.get(finalI).bezierCurve.startSignalAnimation(NodeEditor.EXECUTE_LINE_COLOR, NodeEditor.EXECUTE_LINE_DURATION);
                } else {
                    SelfAdaptionListSlider slider = (SelfAdaptionListSlider) this.node.leftCardNodes.get(finalI).edit;
                    for (ConnectingLine dataLine : slider.dataLines) {
                        dataLine.bezierCurve.startSignalAnimation(NodeEditor.EXECUTE_LINE_COLOR, NodeEditor.EXECUTE_LINE_DURATION);
                    }
                }
            });
        }
        for (int i = 0; i < this.outputTypes.size(); i++) {
            this.outputs.set(i, this.getOutputs.get(i).apply(inp));
        }
        for (int i = 0; i < this.outputTypes.size(); i++) {
            int finalI = i;
            Platform.runLater(() -> {
                try {
                    this.node.rightCardNodes.get(finalI).edit.setData(this.outputs.get(finalI));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Platform.runLater(() -> this.node.resetPos((float) this.node.startX, (float) this.node.startY));
    }

    public Object getOutput(int index) {
        Platform.runLater(() -> {
            try {
                this.node.updateInput();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        List<NodeInput> inp = new ArrayList<>();
        for (int i = 0; i < this.inputTypes.size(); i++) {
            inp.add(new NodeInput(this.node.leftCardNodes.get(i).edit.getData(), this.inputTypes.get(i)));
        }
        Object result = this.getOutputs.get(index).apply(inp);
        Platform.runLater(() -> {
            try {
                this.node.rightCardNodes.get(index).edit.setData(result);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    public void setRunningColorToNext() {
        this.node.backgroundBorder.setFill(Node.CARD_BORDER_COLOR);
        if (node.nextNode != null)
            this.node.nextNode.backgroundBorder.setFill(Node.RUNNING_COLOR);
    }

    public void resetPos(float x, float y) {
        this.node.resetPos(x, y);
    }

    public void resetSize(float width, float height) {
        this.node.resetSize(width, height);
    }

    public void delete() {
        this.node.delete();
    }

}
