package org.bxwbb.spigotplugincreatertool.WindowLabel;

import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

public class HorizontalLayout extends BaseLabel {
    // 子元素间隔
    private double spacing = 5;

    public HorizontalLayout(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void update() {
        double dx = 0;
        for (BaseLabel child : this.getChildren()) {
            child.resetPos(this.getX() + dx, this.getY());
            dx += child.getWidth() + spacing;
        }
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        for (BaseLabel child : this.getChildren()) {
            child.setDisplayVisible(visible);
        }
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new HorizontalLayout(startX, startY, endX, endY);
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

}
