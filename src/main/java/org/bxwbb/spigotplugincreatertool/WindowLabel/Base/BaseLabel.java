package org.bxwbb.spigotplugincreatertool.WindowLabel.Base;

import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseLabel {

    public static Group stageRoot;

    public double startX = 0;
    public double startY = 0;
    public double endX = 0;
    public double endY = 0;
    public Rectangle background;
    transient public Group root;
    transient public Group base = new Group();
    public boolean visible = true;
    public UUID uuid = UUID.randomUUID();
    private String name = uuid.toString();
    private boolean mouseTransparent = false;
    protected BaseLabel parent;
    protected final List<BaseLabel> children = new ArrayList<>();

    public static final BaseLabel NULL_LABEL = new NullLabel();

    /**
     * 重置组件位置（核心优化：空指针保护、性能优化、逻辑简化）
     * @param x 新的起始X坐标
     * @param y 新的起始Y坐标
     */
    public void resetPos(double x, double y) {
        if (Double.isNaN(x) || Double.isInfinite(x) || Double.isNaN(y) || Double.isInfinite(y)) {
            return;
        }
        double xOffset = x - this.startX;
        double yOffset = y - this.startY;
        this.startX = x;
        this.startY = y;
        this.endX += xOffset;
        this.endY += yOffset;

        if (getChildren() != null && !getChildren().isEmpty()) {
            for (BaseLabel child : new ArrayList<>(getChildren())) {
                if (child != null) {
                    child.resetPos(child.startX + xOffset, child.startY + yOffset);
                }
            }
        }
        this.update();
    }

    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.update();
    }

    public void setX(double x) {
        this.resetPos(x, this.startY);
    }

    public void setY(double y) {
        this.resetPos(this.startX, y);
    }

    public double getX() {
        return this.startX;
    }

    public double getY() {
        return this.startY;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setStartX(double startX) {
        this.startX = startX;
        update();
    }

    public void setStartY(double startY) {
        this.startY = startY;
        update();
    }

    public void setEndX(double endX) {
        this.endX = endX;
        update();
    }

    public void setEndY(double endY) {
        this.endY = endY;
        update();
    }

    public void setWidth(double width) {
        this.resetSize(width, this.getHeight());
    }

    public void setHeight(double height) {
        this.resetSize(this.getWidth(), height);
    }

    public void delete() {
        for (BaseLabel child : this.getChildren()) {
            child.delete();
        }
        this.setParent(null);
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
    }

    public void addTo(Group root) {
        this.root = root;
        if (this.background != null && !this.base.getChildren().contains(this.background)) this.base.getChildren().addFirst(this.background);
        for (BaseLabel child : this.getChildren()) {
            child.addTo(base);
        }
        if (!root.getChildren().contains(this.base)) root.getChildren().add(this.base);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public double getWidth() {
        return this.endX - this.startX;
    }

    public double getHeight() {
        return this.endY - this.startY;
    }

    public void autoSize() {
        autoSize(this.parent.getWidth(), this.parent.getHeight());
        for (BaseLabel child : this.getChildren()) {
            child.autoSize();
        }
    }

    protected void autoSize(double w, double h) {
        // 宽高比
        double ratio = this.getWidth() / this.getHeight();
        if (w < h) {
            this.resetSize(w, w / ratio);
        } else {
            this.resetSize(h * ratio, h);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public abstract void setDisplayVisible(boolean visible);

    public Object getData() {
        return null;
    }

    public void setData(Object data) throws ClassNotFoundException {
    }

    public abstract BaseLabel createNew() throws ClassNotFoundException;

    public boolean isMouseTransparent() {
        return mouseTransparent;
    }

    public void setMouseTransparent(boolean transparent) {
        this.mouseTransparent = transparent;
    }

    public void resetPosOfCenter(double x, double y) {
        this.resetPos(x - this.getWidth() / 2, y + this.getHeight() / 2);
    }

    public abstract void update();

    public void resetSizePos(double x, double y) {
        this.endX = x;
        this.endY = y;
        this.resetSize(this.endX - this.startX, this.endY - this.startY);
        this.update();
    }

    public BaseLabel getParent() {
        return parent;
    }

    public void setParent(BaseLabel parent) {
        if (this.parent == parent || this.parent == this) return;
        if (this.parent != null) this.parent.removeChild(this);
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
            this.addTo(parent.base);
        }
    }

    public List<BaseLabel> getChildren() {
        return children;
    }

    public void addChild(BaseLabel child) {
        if (child == this) return;
        this.children.add(child);
        child.setParent(this);
        child.addTo(this.base);
        update();
    }

    public void removeChild(BaseLabel child) {
        child.parent = null;
        this.children.remove(child);
    }

    public void getTestBoxing() {
        Rectangle rect = new Rectangle(this.getStartX(), this.getStartY(), this.getWidth(), this.getHeight());
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.RED);
        rect.setStrokeWidth(3);
        rect.setMouseTransparent(true);
        stageRoot.getChildren().add(rect);
    }

    private static class NullLabel extends BaseLabel {

        @Override
        public void setDisplayVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public BaseLabel createNew() {
            return null;
        }

        @Override
        public void update() {

        }

    }

    public static void shutdownExecutor() {
    }

    public record LabelEvent(Event event, BaseLabel baseLabel) {
    }

    public static void setStageRoot(Group stageRoot) {
        BaseLabel.stageRoot = stageRoot;
    }
}
