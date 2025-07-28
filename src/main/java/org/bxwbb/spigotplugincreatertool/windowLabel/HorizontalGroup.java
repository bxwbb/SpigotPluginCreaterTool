package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.List;

public class HorizontalGroup extends BaseLabel {

    public Rectangle mask;
    public List<BaseLabel> labels;

    public HorizontalGroup(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.mask = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.base = new Group();
        this.base.setClip(mask);
        double rW = startX;
        for (BaseLabel label : this.labels) {
            label.resetPos(rW, startY);
            rW += label.getWidth() + 5;
            label.addTo(this.base);
        }
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.mask.setX(startX);
        this.mask.setY(startY);
        double rW = startX;
        for (BaseLabel label : this.labels) {
            label.resetPos(rW, startY);
            rW += label.getWidth() + 5;
        }
    }

    public void addLabel(BaseLabel label) {
        this.labels.add(label);
        label.addTo(this.base);
        this.update();
    }

    public void removeLabel(BaseLabel label) {
        label.delete();
        this.labels.remove(label);
        this.update();
    }

    public void insertLabel(BaseLabel label, int index) {
        this.labels.add(index, label);
        label.addTo(this.base);
        this.update();
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.mask.setWidth(this.endX - this.startX);
        this.mask.setHeight(this.endY - this.startY);
        double rW = startX;
        for (BaseLabel label : this.labels) {
            label.resetPos(rW, startY);
            rW += label.getWidth() + 5;
        }
    }

    @Override
    public void delete() {
        for (BaseLabel label : labels) {
            label.delete();
        }
        this.labels.clear();
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return this.mask.getWidth();
    }

    @Override
    public double getHeight() {
        return this.mask.getHeight();
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {

    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        HorizontalGroup horizontalGroup = new HorizontalGroup(this.startX, this.startY, this.endX, this.endY);
        List<BaseLabel> r = new ArrayList<>();
        for (BaseLabel label : this.labels) {
            r.add(label.createNew());
        }
        horizontalGroup.labels = r;
        return horizontalGroup;
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }
}
