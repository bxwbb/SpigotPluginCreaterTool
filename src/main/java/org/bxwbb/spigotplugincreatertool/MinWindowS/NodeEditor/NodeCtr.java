package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import javafx.application.Platform;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class NodeCtr {

    public record NodeInput(Object value, Node.VarType type) {}

    @FunctionalInterface
    public interface NodeGetOutput extends Function<List<NodeInput>, Object> {}

    public Node node;
    public List<Node.VarType> inputTypes;
    public List<Node.VarType> outputTypes;
    public String name;
    public double startX;
    public double startY;
    public List<NodeGetOutput> getOutputs;
    // 代表的方法路径
    public String methodPath;

    public NodeCtr(double startX, double startY, Node node, List<NodeGetOutput> getOutputs, String methodPath) {
        this.methodPath = methodPath;
        this.startX = startX;
        this.startY = startY;
        this.getOutputs = getOutputs;
        this.node = node;
        this.inputTypes = new ArrayList<>();
        this.outputTypes = new ArrayList<>();
        for (Node.NodeCardNode nodeCardNode : node.leftCardNodes) {
            this.inputTypes.add(nodeCardNode.varType);
        }
        for (Node.NodeCardNode nodeCardNode : node.rightCardNodes) {
            this.outputTypes.add(nodeCardNode.varType);
        }
    }

    public NodeCtr createNew(double x, double y, Group root) {
        return new NodeCtr(x, y, this.node.createNew(x, y, root), this.getOutputs, this.methodPath);
    }

    public void runCard() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                this.node.updateInput();
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
        }
        for (int i = 0; i < this.outputTypes.size(); i++) {
            int finalI = i;
            Platform.runLater(() -> this.node.rightCardNodes.get(finalI).edit.setData(this.getOutputs.get(finalI).apply(inp)));
        }
        Platform.runLater(() -> this.node.resetPos((float) this.node.startX, (float) this.node.startY));
    }

    public Object getOutput(int index) {
        Platform.runLater(() -> this.node.updateInput());
        List<NodeInput> inp = new ArrayList<>();
        for (int i = 0; i < this.inputTypes.size(); i++) {
            inp.add(new NodeInput(this.node.leftCardNodes.get(i).edit.getData(), this.inputTypes.get(i)));
        }
        Object result = this.getOutputs.get(index).apply(inp);
        Platform.runLater(() -> this.node.rightCardNodes.get(index).edit.setData(result));
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
