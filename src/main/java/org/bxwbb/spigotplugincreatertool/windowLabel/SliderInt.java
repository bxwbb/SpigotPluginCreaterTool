package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SliderInt extends BaseLabel {

    static final Logger logger = LoggerFactory.getLogger(SliderInt.class);

    public int value;
    public String name;
    public List<String> lore;
    public boolean leftBorder;
    public int leftValue;
    public boolean rightBorder;
    public int rightValue;
    public int delta;

    private Text title;
    private final Rectangle background;
    private Rectangle SelectedBackground;
    private final Rectangle mask;
    private Text leftAdd;
    private Text rightAdd;
    private Text valueText;
    private IntegerTextField valueTextField;
    private double rMouseX;
    private boolean isDragging;
    private final Group baseGroup;

    public SliderInt(double startX, double startY, double endX, double endY, int value, String name, List<String> lore, boolean leftBorder, boolean rightBorder, int leftValue, int rightValue, int delta) {
        this.base = new Group();
        this.baseGroup = new Group();
        this.value = value;
        this.name = name;
        this.lore = lore;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.delta = delta;
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
            this.rMouseX = event.getX();
            if (this.visible) {
                if (!(this.leftBorder && this.rightBorder)) {
                    this.leftAdd.setVisible(true);
                    this.rightAdd.setVisible(true);
                    if (event.getX() <= this.startX + 5 + this.leftAdd.getLayoutBounds().getWidth()) {
                        this.background.setCursor(Cursor.DEFAULT);
                    } else if (event.getX() >= this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth()) {
                        this.background.setCursor(Cursor.DEFAULT);
                    } else {
                        this.background.setCursor(Cursor.H_RESIZE);
                    }
                } else {
                    this.background.setCursor(Cursor.H_RESIZE);
                }
                this.background.setFill(HelloApplication.HOVER_COLOR);
            }
        });
        this.background.setOnMouseExited(event -> {
            this.rMouseX = event.getX();
            this.background.setFill(HelloApplication.UNSELECTED_COLOR);
            this.leftAdd.setVisible(false);
            this.rightAdd.setVisible(false);
        });
        this.background.setOnMouseClicked(event -> {
            this.isDragging = false;
            this.rMouseX = event.getX();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!(this.leftBorder && this.rightBorder)) {
                    if (event.getX() <= this.startX + 5 + this.leftAdd.getLayoutBounds().getWidth()) {
                        if (this.leftBorder && (this.value - this.delta) >= this.leftValue) {
                            this.value -= this.delta;
                            this.valueText.setText(String.valueOf(this.value));
                            this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
                            this.valueTextField.setText(String.valueOf(this.value));
                        } else if (!this.leftBorder) {
                            this.value -= this.delta;
                            this.valueText.setText(String.valueOf(this.value));
                            this.valueTextField.setText(String.valueOf(this.value));
                        }
                        this.isDragging = true;
                    }
                    if (event.getX() >= this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth()) {
                        if (this.rightBorder && (this.value + this.delta) <= this.rightValue) {
                            this.value += this.delta;
                            this.valueText.setText(String.valueOf(this.value));
                            this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
                            this.valueTextField.setText(String.valueOf(this.value));
                        } else if (!this.rightBorder) {
                            this.value += this.delta;
                            this.valueText.setText(String.valueOf(this.value));
                            this.valueTextField.setText(String.valueOf(this.value));
                        }
                        this.isDragging = true;
                    }
                }
            }
        });
        this.background.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.isDragging = true;
                if (((event.getX() - this.rMouseX) + 1) > 0) {
                    if (this.rightBorder && (this.value + this.delta <= this.rightValue)) {
                        this.value += this.delta;
                    } else if (!this.rightBorder) {
                        this.value += this.delta;
                    }
                } else {
                    if (this.leftBorder && (this.value - this.delta >= this.leftValue)) {
                        this.value -= this.delta;
                    } else if (!this.leftBorder) {
                        this.value -= this.delta;
                    }
                }
                this.valueText.setText(String.valueOf(this.value));
                this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
                this.valueTextField.setText(String.valueOf(this.value));
                this.rMouseX = event.getX();
                if (this.leftBorder && this.rightBorder) {
                    this.SelectedBackground.setWidth((double) (this.value - this.leftValue) / (Math.abs(this.rightValue - this.leftValue)) * (this.endX - this.startX));
                }
            }
        });
        this.background.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!this.isDragging && !(event.getX() <= this.startX + 5 + this.leftAdd.getLayoutBounds().getWidth()) && !(event.getX() >= this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth())) {
                    this.leftAdd.setVisible(false);
                    this.rightAdd.setVisible(false);
                    this.title.setVisible(false);
                    this.valueText.setVisible(false);
                    this.valueTextField.setVisible(true);
                    this.valueTextField.requestFocus();
                }
            }
        });
        this.leftAdd = new Text("<");
        this.leftAdd.setFont(HelloApplication.TEXT_FONT);
        this.leftAdd.setFill(HelloApplication.FONT_COLOR);
        this.leftAdd.setX(this.startX + 5);
        this.leftAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.leftAdd.setVisible(false);
        this.leftAdd.setMouseTransparent(true);
        this.title = new Text(this.name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + this.leftAdd.getLayoutBounds().getWidth() + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.title.setMouseTransparent(true);
        this.rightAdd = new Text(">");
        this.rightAdd.setFont(HelloApplication.TEXT_FONT);
        this.rightAdd.setFill(HelloApplication.FONT_COLOR);
        this.rightAdd.setX(this.endX - 12);
        this.rightAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.rightAdd.setVisible(false);
        this.rightAdd.setMouseTransparent(true);
        this.valueText = new Text(String.valueOf(this.value));
        this.valueText.setFont(HelloApplication.TEXT_FONT);
        this.valueText.setFill(HelloApplication.FONT_COLOR);
        this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setMouseTransparent(true);
        this.SelectedBackground = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.SelectedBackground.setArcWidth(HelloApplication.ROUNDNESS);
        this.SelectedBackground.setArcHeight(HelloApplication.ROUNDNESS);
        this.SelectedBackground.setStrokeWidth(0.0f);
        this.SelectedBackground.setFill(HelloApplication.SELECTED_COLOR);
        this.SelectedBackground.setWidth((double) (this.value - this.leftValue) / (Math.abs(this.rightValue - this.leftValue)) * (this.endX - this.startX));
        this.SelectedBackground.setMouseTransparent(true);
        this.mask = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.mask.setArcWidth(HelloApplication.ROUNDNESS);
        this.mask.setArcHeight(HelloApplication.ROUNDNESS);
        this.baseGroup.setClip(mask);
        this.valueTextField = new IntegerTextField();
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
        try {
            this.value = Integer.parseInt(this.valueTextField.getText());
            if (this.leftBorder) {
                if (this.value < this.leftValue) {
                    this.value = this.leftValue;
                }
            }
            if (this.rightBorder) {
                if (this.value > this.rightValue) {
                    this.value = this.rightValue;
                }
            }
            if (this.leftBorder && this.rightBorder)
                this.SelectedBackground.setWidth((double) (this.value - this.leftValue) / (Math.abs(this.rightValue - this.leftValue)) * (this.endX - this.startX));
            this.valueText.setText(String.valueOf(this.value));
            this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
            this.title.setVisible(true);
            this.valueText.setVisible(true);
            this.valueTextField.setVisible(false);
            this.valueTextField.setText(String.valueOf(this.value));
        } catch (NumberFormatException e) {
            logger.error("输入的数字太大");
        }
    }

    public void resetPos(double x, double y) {
        this.endX = endX - startX + x;
        this.endY = endY - startY + y;
        this.startX = x;
        this.startY = y;
        this.background.setX(startX);
        this.background.setY(startY);
        this.leftAdd.setX(this.startX + 5);
        this.leftAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.title.setX(this.startX + this.leftAdd.getLayoutBounds().getWidth() + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.rightAdd.setX(this.endX - 12);
        this.rightAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.SelectedBackground.setX(startX);
        this.SelectedBackground.setY(startY);
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
        this.leftAdd.setFill(HelloApplication.FONT_COLOR);
        this.leftAdd.setX(this.startX + 5);
        this.leftAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.title.setX(this.startX + this.leftAdd.getLayoutBounds().getWidth() + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.rightAdd.setX(this.endX - 12);
        this.rightAdd.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.SelectedBackground.setWidth(this.endX - this.startX);
        this.SelectedBackground.setHeight(this.endY - this.startY);
        this.SelectedBackground.setWidth((double) (this.value - this.leftValue) / (Math.abs(this.rightValue - this.leftValue)) * (this.endX - this.startX));
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
        this.baseGroup.getChildren().add(this.SelectedBackground);
        this.baseGroup.getChildren().add(this.leftAdd);
        this.baseGroup.getChildren().add(this.rightAdd);
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
        this.title.setX(this.startX + this.leftAdd.getLayoutBounds().getWidth() + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
    }

    @Override
    public double getWidth() {
        return this.leftAdd.getLayoutBounds().getWidth() + this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + this.rightAdd.getLayoutBounds().getWidth() + 40;
    }

    @Override
    public double getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void autoWidth() {
        this.resetSize((float) (this.leftAdd.getLayoutBounds().getWidth() + this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + this.rightAdd.getLayoutBounds().getWidth() + 40), (float) (this.endY - this.startY));
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
        this.value = (int) data;
        if (this.leftBorder) {
            if (this.value < this.leftValue) {
                this.value = this.leftValue;
            }
        }
        if (this.rightBorder) {
            if (this.value > this.rightValue) {
                this.value = this.rightValue;
            }
        }
        if (this.leftBorder && this.rightBorder)
            this.SelectedBackground.setWidth((double) (this.value - this.leftValue) / (Math.abs(this.rightValue - this.leftValue)) * (this.endX - this.startX));
        this.valueText.setText(String.valueOf(this.value));
        this.valueText.setX(this.endX - 12 - this.rightAdd.getLayoutBounds().getWidth() - this.valueText.getLayoutBounds().getWidth());
        this.title.setVisible(true);
        this.valueText.setVisible(true);
        this.valueTextField.setVisible(false);
        this.valueTextField.setText(String.valueOf(this.value));
    }

    @Override
    public BaseLabel createNew() {
        return new SliderInt(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.value,
                this.name,
                new ArrayList<>(this.lore),
                this.leftBorder,
                this.rightBorder,
                this.leftValue,
                this.rightValue,
                this.delta
        );
    }

    @Override
    public Node.VarType getVarType() {
        return Node.VarType.INT;
    }

}
