package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.function.Function;

public class InputBox extends BaseLabel {

    public Rectangle background;
    public TextField textField;
    public Function<String[], Void> onInput;

    private final Group baseGroup;

    public InputBox(double x, double y, double endX, double endY, String promptText) {
        this.base = new Group();
        this.baseGroup = new Group();
        this.startX = x;
        this.startY = y;
        this.endX = endX;
        this.endY = endY;
        this.background = new Rectangle(x, y, endX - x, endY - y);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setStrokeWidth(1);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
        this.background.setOnMouseEntered(event -> this.background.setStroke(HelloApplication.HOVER_COLOR));
        this.background.setOnMouseExited(event -> this.background.setStroke(HelloApplication.UNSELECTED_COLOR));
        this.background.setOnMouseClicked(event -> this.textField.requestFocus());
        this.textField = new TextField();
        this.textField.setLayoutX(x + 5);
        this.textField.setLayoutY(y);
        this.textField.setPrefWidth(endX - x - 10);
        this.textField.setPrefHeight(endY - startY);
        this.textField.setPromptText(promptText);
        this.textField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: text;" +
                        "-fx-text-fill: " + HelloApplication.toHexString(HelloApplication.FONT_COLOR) + ";"
        );
        this.textField.setOnAction(event -> inputDown());
        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                inputDown();
            }
        });
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.onInput != null) this.onInput.apply(new String[]{oldValue, newValue});
        });
        this.textField.setMouseTransparent(true);
        this.baseGroup.setClip(getMask());
    }

    public void inputDown() {
        HelloApplication.loseFocus();
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.background.setX(startX);
        this.background.setY(startY);
        this.textField.setLayoutX(startX + 5);
        this.textField.setLayoutY(startY);
        this.baseGroup.setClip(getMask());
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.background.setWidth(this.endX - this.startX);
        this.background.setHeight(this.endY - this.startY);
        this.textField.setPrefWidth(this.endX - this.startX - 10);
        this.textField.setPrefHeight(this.endY - this.startY);
        this.baseGroup.setClip(getMask());
    }

    @Override
    public void delete() {
        this.baseGroup.getChildren().clear();
        this.base.getChildren().clear();
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.baseGroup.getChildren().add(this.textField);
        this.base.getChildren().add(this.baseGroup);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return this.background.getWidth();
    }

    @Override
    public double getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.textField.setVisible(visible);
        this.background.setFill(visible ? HelloApplication.UNSELECTED_COLOR : HelloApplication.DISABLED_COLOR);
        this.background.setStroke(!visible ? HelloApplication.UNSELECTED_BORDER_COLOR : HelloApplication.BORDER_COLOR);
    }

    @Override
    public Object getData() {
        return this.textField.getText();
    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {
        this.textField.setText((String) data);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new InputBox(this.startX, this.startY, this.endX, this.endY, (String) this.getData());
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }

    private Rectangle getMask() {
        return new Rectangle(startX, startY, endX, endY);
    }

}
