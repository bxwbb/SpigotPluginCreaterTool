package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.Group;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

public class Text extends BaseLabel {

    private final javafx.scene.text.Text textLabel;

    public Text(double startX, double startY, double endX, double endY, String text) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.textLabel = new javafx.scene.text.Text(text);
        this.textLabel.setLayoutX(startX);
        this.textLabel.setLayoutY(startY);
        this.textLabel.setSmooth(false);
        this.base = new Group();
        update();
    }

    public Text(String text) {
        this.startX = 0;
        this.startY = 0;
        this.endX = 0;
        this.endY = 0;
        this.textLabel = new javafx.scene.text.Text(text);
        this.textLabel.setLayoutX(startX);
        this.textLabel.setLayoutY(startY);
        this.textLabel.setSmooth(false);
        this.base = new Group();
    }

    @Override
    public void update() {
        this.textLabel.setLayoutX(startX);
        this.textLabel.setLayoutY(startY + getHeight());
        this.endX = this.startX + this.getTextLabel().getLayoutBounds().getWidth();
        this.endY = this.startY + this.getTextLabel().getLayoutBounds().getHeight();
    }

    @Override
    public void delete() {
        if (root == null) return;
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        if (!this.base.getChildren().contains(this.textLabel)) this.base.getChildren().add(this.textLabel);
        if (!this.root.getChildren().contains(this.base)) this.root.getChildren().add(this.base);
    }

    @Override
    public double getWidth() {
        return this.getTextLabel().getLayoutBounds().getWidth();
    }

    @Override
    public double getHeight() {
        return this.getTextLabel().getLayoutBounds().getHeight();
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.textLabel.setVisible(visible);
    }

    @Override
    public Object getData() {
        return this.textLabel.getText();
    }

    @Override
    public void setData(Object data) {
        this.textLabel.setText((String) data);
    }

    @Override
    public BaseLabel createNew() {
        return new Text(this.startX, this.startY, this.endX, this.endY, this.textLabel.getText());
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        super.setMouseTransparent(transparent);
        this.textLabel.setMouseTransparent(this.isMouseTransparent());
    }

    public javafx.scene.text.Text getTextLabel() {
        return textLabel;
    }
    public String getText() {
        return textLabel.getText();
    }

    public void setText(String textLabel) {
        this.textLabel.setText(textLabel);
        update();
    }
}
