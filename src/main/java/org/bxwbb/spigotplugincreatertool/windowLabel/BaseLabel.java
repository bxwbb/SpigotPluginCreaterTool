package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

public abstract class BaseLabel {
    public double startX = 0;
    public double startY = 0;
    public double endX = 0;
    public double endY = 0;
    public Group root;
    public Group base;
    public boolean visible = true;

    public abstract void resetPos(double x, double y);
    public abstract void resetSize(double width, double height);
    public abstract void delete();
    public abstract void addTo(Group root);
    public abstract void setName(String name);
    public abstract double getWidth();
    public abstract double getHeight();
    public abstract void autoWidth();
    public abstract void setVisible(boolean visible);
    public abstract Object getData();
    public abstract void setData(Object data) throws ClassNotFoundException;
    public abstract BaseLabel createNew() throws ClassNotFoundException;
    public abstract Node.VarType getVarType();

    public void update() {
        this.resetPos(this.startX, this.startY);
    }

}
