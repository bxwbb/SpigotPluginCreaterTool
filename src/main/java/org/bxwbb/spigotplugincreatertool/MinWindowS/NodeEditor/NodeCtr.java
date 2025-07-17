package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NodeCtr {

    public record NodeInput(Object value, Node.VarType type) {
    }

    @FunctionalInterface
    public interface NodeGetOutput extends Function<List<NodeInput>, Object> {
    }

    public Node node;
    public List<Node.VarType> inputTypes;
    public List<Node.VarType> outputTypes;
    public String name;
    public double startX;
    public double startY;
    public List<NodeGetOutput> getOutputs;

    public NodeCtr(double startX, double startY, Node node, List<NodeGetOutput> getOutputs) {
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
        return new NodeCtr(x, y, this.node.createNew(x, y, root), this.getOutputs);
    }

    public void runCard() {
        List<NodeInput> inp = new ArrayList<>();
        for (int i = 0; i < this.inputTypes.size(); i++) {
            inp.add(new NodeInput(this.node.leftCardNodes.get(i).edit.getData(), this.inputTypes.get(i)));
        }
        for (int i = 0; i < this.outputTypes.size(); i++) {
            this.node.rightCardNodes.get(i).edit.setData(this.getOutputs.get(i).apply(inp));
        }
        this.node.resetPos((float) this.node.startX, (float) this.node.startY);
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
