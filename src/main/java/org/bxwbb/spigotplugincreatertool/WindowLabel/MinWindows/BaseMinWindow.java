package org.bxwbb.spigotplugincreatertool.WindowLabel.MinWindows;

import javafx.scene.paint.Color;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Stage;

public abstract class BaseMinWindow extends BaseLabel {

    // 基底颜色
    public static Color BASE_COLOR = Color.web("#2B2D30");

    private Stage stage;

    public void init() {
        this.stage = new Stage(this.getX(), getY(), getEndX(), getEndY());
        this.stage.getBackground().setFill(BASE_COLOR);
        this.stage.getBackground().setArcWidth(HelloApplication.ROUNDNESS);
        this.stage.getBackground().setArcHeight(HelloApplication.ROUNDNESS);
        this.addChild(this.stage);
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.stage.setDisplayVisible(visible);
    }

    @Override
    public abstract BaseLabel createNew() throws ClassNotFoundException;

    @Override
    public void update() {
        this.stage.resetPos(this.getX(), this.getY());
        this.stage.resetSize(this.getWidth(), this.getHeight());
    }

    public Stage getStage() {
        return stage;
    }
}
