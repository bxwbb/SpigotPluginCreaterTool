package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.List;

public class StringInput extends BaseLabel {

    public String value;
    public String name;
    public List<String> lore;

    private final Text title;
    private final Rectangle background;
    private final Rectangle mask;
    private Text valueText;
    private TextField valueTextField;
    private final Group baseGroup;

    public StringInput(double startX, double startY, double endX, double endY, String value, String name, List<String> lore) {
        this.base = new Group();
        this.baseGroup = new Group();
        this.value = value;
        this.name = name;
        this.lore = lore;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setStrokeWidth(1);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setOnMouseMoved(event -> {
            if (this.visible) {
                this.background.setFill(HelloApplication.HOVER_COLOR);
            }
        });
        this.background.setOnMouseExited(event -> this.background.setFill(HelloApplication.UNSELECTED_COLOR));
        this.background.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.valueText.setText(String.valueOf(this.value));
                this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
                this.valueTextField.setText(String.valueOf(this.value));
            }
        });
        this.background.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.valueText.setVisible(false);
                this.valueTextField.setVisible(true);
                this.valueTextField.requestFocus();
            }
        });
        this.title = new Text(this.name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.title.setMouseTransparent(true);
        this.valueText = new Text(String.valueOf(this.value));
        this.valueText.setFont(HelloApplication.TEXT_FONT);
        this.valueText.setFill(HelloApplication.FONT_COLOR);
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setMouseTransparent(true);
        this.mask = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.mask.setArcWidth(HelloApplication.ROUNDNESS);
        this.mask.setArcHeight(HelloApplication.ROUNDNESS);
        this.baseGroup.setClip(mask);
        this.valueTextField = new TextField();
        this.valueTextField.setLayoutX(this.startX + 10);
        this.valueTextField.setLayoutY(this.startY);
        this.valueTextField.setPrefWidth(endX - startX - 22);
        this.valueTextField.setPrefHeight(endY - startY);
        this.valueTextField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: text;" +
                        "-fx-alignment: center;" +
                        "-fx-text-fill: " + HelloApplication.toHexString(HelloApplication.FONT_COLOR) + ";"
        );
        this.valueTextField.setVisible(false);
        this.valueTextField.setOnAction(event -> inputDown());
        this.valueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                inputDown();
            }
        });
        this.valueTextField.setText(String.valueOf(this.value));
    }

    private void inputDown() {
        this.value = this.valueTextField.getText();
        this.valueText.setText(String.valueOf(this.value));
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.title.setVisible(true);
        this.valueText.setVisible(true);
        this.valueTextField.setVisible(false);
        this.valueTextField.setText(String.valueOf(this.value));
    }

    public void resetPos(double x, double y) {
        this.endX = endX - startX + x;
        this.endY = endY - startY + y;
        this.startX = x;
        this.startY = y;
        this.background.setX(startX);
        this.background.setY(startY);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.mask.setX(startX);
        this.mask.setY(startY);
        this.valueTextField.setLayoutX(this.startX + 10);
        this.valueTextField.setLayoutY(this.startY);
    }

    public void resetSize(double width, double height) {
        this.endX = startX + width;
        this.endY = startY + height;
        this.background.setWidth(endX - startX);
        this.background.setHeight(endY - startY);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.mask.setWidth(this.endX - this.startX);
        this.mask.setHeight(this.endY - this.startY);
        this.valueTextField.setPrefWidth(endX - startX - 22);
        this.valueTextField.setPrefHeight(endY - startY);
    }

    public void delete() {
        this.baseGroup.getChildren().clear();
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
        this.root.getChildren().remove(this.valueTextField);
    }

    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.baseGroup.getChildren().add(this.valueText);
        this.baseGroup.getChildren().add(this.title);
        this.base.getChildren().add(this.baseGroup);
        this.root.getChildren().add(this.base);
        this.root.getChildren().add(this.valueTextField);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.title.setText(name);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
    }

    @Override
    public double getWidth() {
        return this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + 40;
    }

    @Override
    public double getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void autoWidth() {
        this.resetSize((float) (this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + 40), (float) (this.endY - this.startY));
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.background.setMouseTransparent(!visible);
        this.background.setFill(visible ? HelloApplication.UNSELECTED_COLOR : HelloApplication.DISABLED_COLOR);
        this.background.setStroke(!visible ? HelloApplication.UNSELECTED_BORDER_COLOR : HelloApplication.BORDER_COLOR);
    }

    @Override
    public Object getData() {
        return this.value;
    }

    @Override
    public void setData(Object data) {
        this.value = (String) data;
        this.valueText.setText(String.valueOf(this.value));
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.title.setVisible(true);
        this.valueText.setVisible(true);
        this.valueTextField.setVisible(false);
        this.valueTextField.setText(String.valueOf(this.value));
    }

    @Override
    public BaseLabel createNew() {
        return new StringInput(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.value,
                this.name,
                new ArrayList<>(this.lore)
        );
    }

    @Override
    public Node.VarType getVarType() {
        return Node.VarType.STRING;
    }

}
