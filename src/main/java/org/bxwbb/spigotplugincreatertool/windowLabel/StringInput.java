package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;

import java.util.ArrayList;
import java.util.List;

public class StringInput extends BaseLabel {

    public String data;
    public String name;
    public List<String> lore;
    public Rectangle background;
    public Text title;
    public Text dataText;
    public TextField textField;

    public StringInput(double startX, double startY, double endX, double endY, String name, List<String> lore, String data) {
        this.base = new Group();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.name = name;
        this.lore = lore;
        this.data = data;
        this.title = new Text(name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + this.title.getLayoutBounds().getHeight() * 0.5 + 5);
        this.background = new Rectangle(this.startX, this.startY + this.title.getLayoutBounds().getHeight() + 5, this.endX - this.startX, this.endY - this.startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(1.0);
        this.background.setStroke(HelloApplication.UNSELECTED_BORDER_COLOR);
        this.background.setOnMouseEntered(event -> {
            if (this.visible) {
                this.background.setFill(HelloApplication.HOVER_COLOR);
            }
        });
        this.background.setOnMouseExited(event -> {
            if (this.visible) {
                this.background.setFill(HelloApplication.UNSELECTED_COLOR);
            }
        });
        this.background.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (this.visible) {
                    this.dataText.setVisible(false);
                    this.textField.setVisible(true);
                    this.textField.requestFocus();
                }
            }
        });
        this.dataText = new Text();
        dataText.setText(this.data);
        dataText.setFont(HelloApplication.TEXT_FONT);
        dataText.setFill(HelloApplication.FONT_COLOR);
        this.dataText.setX(this.startX + 5);
        this.dataText.setY(this.startY + this.title.getLayoutBounds().getHeight() + 16);
        this.dataText.setMouseTransparent(true);
        this.textField = new TextField();
        this.textField.setLayoutX(this.startX + 5);
        this.textField.setLayoutY(this.startY + this.title.getLayoutBounds().getHeight() + 16);
        this.textField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: text;" +
                        "-fx-alignment: center;" +
                        "-fx-text-fill: " + HelloApplication.toHexString(HelloApplication.FONT_COLOR) + ";"
        );
        this.textField.setVisible(false);
        this.textField.setText(this.data);
        this.textField.setOnAction(event -> inputDown());
        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                inputDown();
            }
        });
    }

    private void inputDown() {
        this.dataText.setVisible(true);
        this.textField.setVisible(false);
        this.dataText.setText(this.textField.getText());
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.title.setX(this.startX);
        this.title.setY(this.startY + this.title.getLayoutBounds().getHeight() * 0.5 + 5);
        this.background.setX(this.startX);
        this.background.setY(this.startY + this.title.getLayoutBounds().getHeight() + 5);
        this.dataText.setX(this.startX + 5);
        this.dataText.setY(this.startY + this.title.getLayoutBounds().getHeight() + 20);
        this.textField.setLayoutX(this.startX);
        this.textField.setLayoutY(this.startY + this.title.getLayoutBounds().getHeight() + 5);
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.background.setWidth(this.endX - this.startX);
        this.background.setHeight(this.endY - this.startY + 15);
        this.textField.setPrefWidth(this.endX - this.startX);
        this.textField.setPrefHeight(this.endY - this.startY + 15);
    }

    @Override
    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        if (!this.base.getChildren().contains(this.background)) this.base.getChildren().add(this.background);
        if (!this.base.getChildren().contains(this.title)) this.base.getChildren().add(this.title);
        if (!this.base.getChildren().contains(this.dataText)) this.base.getChildren().add(this.dataText);
        if (!this.base.getChildren().contains(this.textField)) this.base.getChildren().add(this.textField);
        if (!this.root.getChildren().contains(this.base)) this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.title.setText(name);
    }

    @Override
    public double getWidth() {
        return this.background.getWidth();
    }

    @Override
    public double getHeight() {
        return this.background.getHeight() + this.title.getLayoutBounds().getHeight();
    }

    @Override
    public void autoWidth() {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = (String) data;
        this.dataText.setText(this.data);
        this.textField.setText(this.data);
    }

    @Override
    public BaseLabel createNew() {
        return new StringInput(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.name,
                new ArrayList<>(this.lore),
                this.data
        );
    }
}
