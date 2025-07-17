package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;

import java.util.Objects;

public class Button extends BaseLabel {

    public final Rectangle background;
    public final ImageView image;
    private boolean isDown;

    public Button(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Group();
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(0.0);
        this.background.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> this.background.setFill(HelloApplication.HOVER_COLOR));
        this.background.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            if (!this.isDown) {
                this.background.setFill(HelloApplication.UNSELECTED_COLOR);
            }
        });
        this.image = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Run.png"
                )))
        );
        this.image.setMouseTransparent(true);
        this.image.setX(startX + 1);
        this.image.setY(startY + 1);
        this.image.setPreserveRatio(false);
        this.image.setFitWidth(endX - startX - 2);
        this.image.setFitHeight(endY - startY - 2);
        this.isDown = false;
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.background.setX(startX);
        this.background.setY(startY - 10);
        this.image.setX(startX + 1);
        this.image.setY(startY + 1 - 10);
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.background.setWidth(width);
        this.background.setHeight(height);
        this.image.setFitWidth(width - 2);
        this.image.setFitHeight(height - 2);
    }

    @Override
    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.image);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {}

    @Override
    public double getWidth() {
        return this.endX - this.startX;
    }

    @Override
    public double getHeight() {
        return this.endY - this.startY;
    }

    @Override
    public void autoWidth() {}

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Object getData() {
        return this.isDown;
    }

    @Override
    public void setData(Object data) {
        this.isDown = (boolean) data;
        if (this.isDown) {
            this.background.setFill(HelloApplication.SELECTED_COLOR);
        } else {
            this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        }
    }

    @Override
    public BaseLabel createNew() {
        return new Button(this.startX, this.startY, this.endX, this.endY);
    }
}
