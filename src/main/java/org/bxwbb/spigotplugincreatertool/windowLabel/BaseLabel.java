package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.UUID;

public abstract class BaseLabel {
    public double startX = 0;
    public double startY = 0;
    public double endX = 0;
    public double endY = 0;
    transient public Group root;
    transient public Group base = new Group();
    public boolean visible = true;
    public UUID uuid = UUID.randomUUID();
    private String name = uuid.toString();
    public boolean mouseTransparent = false;

    public static final BaseLabel NULL_LABEL = new NullLabel();

    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
    }

    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
    }

    public void setX(double x) {
        this.resetPos(x, this.startY);
    }

    public void setY(double y) {
        this.resetPos(this.startX, y);
    }

    public abstract void delete();

    public abstract void addTo(Group root);

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract void autoWidth();

    public abstract void setDisplayVisible(boolean visible);

    public Object getData() {
        return null;
    }

    public abstract void setData(Object data) throws ClassNotFoundException;

    public abstract BaseLabel createNew() throws ClassNotFoundException;

//    public abstract void setMouseTransparent(boolean transparent);

    public void resetPosOfCenter(double x, double y) {
        this.resetPos(x - this.getWidth() / 2, y - this.getHeight() / 2);
    }

    public Node.VarType getVarType() {
        return null;
    }

    public void update() {
        this.resetPos(this.startX, this.startY);
        this.resetSize(this.getWidth(), this.getHeight());
    }

    public void resetSizePos(double x, double y) {
        this.endX = x;
        this.endY = y;
        this.resetSize(this.endX - this.startX, this.endY - this.startY);
    }

    private static class NullLabel extends BaseLabel {

        @Override
        public void delete() {

        }

        @Override
        public void addTo(Group root) {

        }

        @Override
        public void setName(String name) {

        }

        @Override
        public double getWidth() {
            return 0;
        }

        @Override
        public double getHeight() {
            return 0;
        }

        @Override
        public void autoWidth() {

        }

        @Override
        public void setDisplayVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public void setData(Object data) throws ClassNotFoundException {

        }

        @Override
        public BaseLabel createNew() throws ClassNotFoundException {
            return null;
        }

        public void setMouseTransparent(boolean transparent) {
            this.mouseTransparent = transparent;
        }
    }

    public static void shutdownExecutor() {
    }

}
