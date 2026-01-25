package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

public class Stage extends BaseLabel {
    private boolean hasMask = true;
    private final Rectangle mask;

    public Stage(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.BG_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.mask = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.base.setClip(this.mask);
    }

    @Override
    public void update() {
        this.background.setX(startX);
        this.background.setY(startY);
        this.background.setWidth(endX - startX);
        this.background.setHeight(endY - startY);
        this.mask.setX(startX);
        this.mask.setY(startY);
        this.mask.setWidth(endX - startX);
        this.mask.setHeight(endY - startY);
        this.base.setClip(this.mask);
    }

    public Rectangle getBackground() {
        return background;
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.background.setVisible(visible);
        for (BaseLabel child : this.getChildren()) {
            child.setDisplayVisible(visible);
        }
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new Stage(this.startX, this.startY, this.endX, this.endY);
    }

    public boolean isHasMask() {
        return hasMask;
    }

    public void setHasMask(boolean hasMask) {
        this.hasMask = hasMask;
        if (hasMask) {
            this.background.setClip(this.mask);
        } else {
            this.background.setClip(null);
        }
    }

}
