package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindow;

import java.util.List;

public class Node {

    // 节点卡片背景色
    public static Color CARD_BG_COLOR = Color.color(0.2, 0.2, 0.2);
    // 节点卡片顶部颜色
    public static Color CARD_TOP_COLOR = Color.color(0.1, 0.1, 0.1);
    // 节点卡片边框颜色
    public static Color CARD_BORDER_COLOR = Color.BLACK;
    // 节点卡片边框宽度
    public static float CARD_BORDER_WIDTH = 1.5f;
    // 卡片阴影效果半径
    public static float CARD_SHADOW_RADIUS = 30.0f;
    // 执行顺序连接节点颜色
    public static Color EXECUTE_ORDER_COLOR = Color.rgb(153, 210, 143);

    public double startX;
    public double startY;
    public double endX;
    public double endY;
    public Group root;
    public Group base;
    public Group nodeGroup;
    public Rectangle mask;
    public String name;
    public Text title;
    public Text hideTitle;
    public List<String> lore;
    public Group leftGroup;
    public Group rightGroup;
    public Rectangle leftMask;
    public Rectangle rightMask;
    public List<NodeCardNode> leftCardNodes;
    public List<NodeCardNode> rightCardNodes;
    public Rectangle background;
    public Rectangle backgroundBorder;
    public Line topBar;
    public DropShadow cardDropShadow;
    public Polygon leftRunLineInput;
    public Polygon rightRunLineOutput;

    private double rMouseX, rMouseY;

    Node(double startX, double startY, Group root, String name, List<String> lore, List<NodeCardNode> leftCardNodes, List<NodeCardNode> rightCardNodes) {
        this.startX = startX;
        this.startY = startY;
        this.endX = startX;
        this.endY = startY + 60;
        this.root = root;
        this.name = name;
        this.lore = lore;
        this.leftGroup = new Group();
        this.rightGroup = new Group();
        this.leftCardNodes = leftCardNodes;
        this.rightCardNodes = rightCardNodes;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.endY += nextHeight;
            this.endX = Math.max(this.endX, startX + this.rightCardNodes.get(i).getWidth());
        }
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + i * nextHeight));
            this.rightCardNodes.get(i).addTo(rightGroup);
        }
        this.rightMask = new Rectangle(startX, startY, endX - startX + 20, endY - startY);
        this.rightGroup.setClip(rightMask);
        this.nodeGroup = new Group();
        this.base = new Group();
        this.base.setClip(getBaseMask());
        this.backgroundBorder = new Rectangle(startX - CARD_BORDER_WIDTH, startY - CARD_BORDER_WIDTH, endX - startX + CARD_BORDER_WIDTH * 2, endY - startY + CARD_BORDER_WIDTH * 2);
        this.backgroundBorder.setStrokeWidth(0.0f);
        this.backgroundBorder.setArcWidth(HelloApplication.ROUNDNESS);
        this.backgroundBorder.setArcHeight(HelloApplication.ROUNDNESS);
        this.backgroundBorder.setFill(CARD_BORDER_COLOR);
        this.cardDropShadow = new DropShadow();
        this.cardDropShadow.setColor(CARD_BORDER_COLOR);
        this.cardDropShadow.setBlurType(BlurType.GAUSSIAN);
        this.cardDropShadow.setRadius(CARD_SHADOW_RADIUS);
        this.backgroundBorder.setEffect(this.cardDropShadow);
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(CARD_BG_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(0.0f);
        this.background.setStroke(CARD_BORDER_COLOR);
        this.background.setOnMouseMoved(event -> {
            if (Math.abs(event.getX() - this.startX) <= MinWindow.TEST_PADDING && event.getY() <= (this.endY - MinWindow.TEST_PADDING)) {
                this.background.setCursor(Cursor.H_RESIZE);
            } else if (Math.abs(event.getX() - this.endX) <= MinWindow.TEST_PADDING && event.getY() <= (this.endY - MinWindow.TEST_PADDING)) {
                this.background.setCursor(Cursor.H_RESIZE);
            } else {
                this.background.setCursor(Cursor.DEFAULT);
            }
        });
        this.background.setOnMouseDragged(event -> {
            if (Math.abs(event.getX() - this.startX) <= MinWindow.TEST_PADDING && event.getY() <= (this.endY - MinWindow.TEST_PADDING)) {
                if (this.endX - event.getX() > 60) {
                    this.background.setCursor(Cursor.H_RESIZE);
                    this.resetSize((float) (this.endX - event.getX()), (float) (this.endY - this.startY));
                    this.resetPos((float) event.getX(), (float) this.startY);
                }
            } else if (Math.abs(event.getX() - this.endX) <= MinWindow.TEST_PADDING && event.getY() <= (this.endY - MinWindow.TEST_PADDING)) {
                this.resetSize(Math.max(60, (float) (event.getX() - this.startX)), (float) (this.endY - this.startY));
            }
        });
        this.topBar = new Line(startX, startY, endX, startY);
        this.topBar.setStrokeWidth(60.0);
        this.topBar.setStroke(CARD_TOP_COLOR);
        this.topBar.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                this.rMouseX = event.getX();
                this.rMouseY = event.getY();
            }
        });
        this.topBar.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                this.topBar.setCursor(Cursor.MOVE);
                this.resetPos((float) (this.startX + (event.getX() - this.rMouseX)), (float) (this.startY + (event.getY() - this.rMouseY)));
                this.rMouseX = event.getX();
                this.rMouseY = event.getY();
            }
        });
        this.topBar.setOnMouseReleased(event -> this.topBar.setCursor(Cursor.DEFAULT));
        this.leftRunLineInput = new Polygon();
        this.leftRunLineInput.getPoints().addAll(
                0.0 + this.startX + 5, 0.0 + this.startY + 5,
                0.0 + this.startX + 5, 20.0 + this.startY + 5,
                15.0 + this.startX + 5, 10.0 + this.startY + 5);
        this.leftRunLineInput.setFill(EXECUTE_ORDER_COLOR);
        this.leftRunLineInput.setStrokeWidth(1.5);
        this.leftRunLineInput.setStroke(CARD_BORDER_COLOR);
        this.rightRunLineOutput = new Polygon();
        this.rightRunLineOutput.getPoints().addAll(
                0.0 + this.endX - 20, 0.0 + this.startY + 5,
                0.0 + this.endX - 20, 20.0 + this.startY + 5,
                15.0 + this.endX - 20, 10.0 + this.startY + 5);
        this.rightRunLineOutput.setFill(EXECUTE_ORDER_COLOR);
        this.rightRunLineOutput.setStrokeWidth(1.5);
        this.rightRunLineOutput.setStroke(CARD_BORDER_COLOR);
        this.hideTitle = new Text();
        this.hideTitle.setFont(HelloApplication.TEXT_FONT);
        this.hideTitle.setFill(HelloApplication.FONT_COLOR);
        this.hideTitle.setText("▼  ");
        this.hideTitle.setX(this.startX + 25);
        this.hideTitle.setY(this.startY + 20);
        this.title = new Text();
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setText(this.name);
        this.title.setX(this.startX + 25 + this.hideTitle.getLayoutBounds().getWidth());
        this.title.setY(this.startY + 20);
        this.title.setMouseTransparent(true);
        this.nodeGroup.getChildren().add(this.backgroundBorder);
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.topBar);
        this.base.getChildren().add(this.leftRunLineInput);
        this.base.getChildren().add(this.rightRunLineOutput);
        this.base.getChildren().add(this.hideTitle);
        this.base.getChildren().add(this.title);
        this.nodeGroup.getChildren().add(this.base);
        this.nodeGroup.getChildren().add(this.rightGroup);
        this.nodeGroup.getChildren().add(this.leftGroup);
        this.root.getChildren().add(this.nodeGroup);
    }

    public void delete() {
        for (NodeCardNode nc : this.leftCardNodes) {
            nc.delete();
        }
        for (NodeCardNode nc : this.rightCardNodes) {
            nc.delete();
        }
        this.base.getChildren().clear();
        this.leftGroup.getChildren().clear();
        this.rightGroup.getChildren().clear();
        this.nodeGroup.getChildren().clear();
        this.root.getChildren().remove(this.nodeGroup);
    }

    public void resetPos(float x, float y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.backgroundBorder.setX(startX - CARD_BORDER_WIDTH);
        this.backgroundBorder.setY(startY - CARD_BORDER_WIDTH);
        this.background.setX(startX);
        this.background.setY(startY);
        this.topBar.setStartX(startX);
        this.topBar.setStartY(startY);
        this.topBar.setEndX(endX);
        this.topBar.setEndY(startY);
        this.leftRunLineInput.getPoints().clear();
        this.leftRunLineInput.getPoints().addAll(
                0.0 + this.startX + 5, 0.0 + this.startY + 5,
                0.0 + this.startX + 5, 20.0 + this.startY + 5,
                15.0 + this.startX + 5, 10.0 + this.startY + 5);
        this.rightRunLineOutput.getPoints().clear();
        this.rightRunLineOutput.getPoints().addAll(
                0.0 + this.endX - 20, 0.0 + this.startY + 5,
                0.0 + this.endX - 20, 20.0 + this.startY + 5,
                15.0 + this.endX - 20, 10.0 + this.startY + 5);
        this.hideTitle.setX(this.startX + 25);
        this.hideTitle.setY(this.startY + 20);
        this.title.setX(this.startX + 25 + this.hideTitle.getLayoutBounds().getWidth());
        this.title.setY(this.startY + 20);
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + i * nextHeight));
        }
        this.rightMask.setX(startX);
        this.rightMask.setY(startY);
        this.base.setClip(getBaseMask());
    }

    public void resetSize(float width, float height) {
        this.endX = startX + width;
        this.endY = startY + height;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + i * nextHeight));
        }
        this.backgroundBorder.setWidth(endX - startX + CARD_BORDER_WIDTH * 2);
        this.backgroundBorder.setHeight(endY - startY + CARD_BORDER_WIDTH * 2);
        this.background.setWidth(width);
        this.background.setHeight(height);
        this.topBar.setEndX(endX);
        this.leftRunLineInput.getPoints().clear();
        this.leftRunLineInput.getPoints().addAll(
                0.0 + this.startX + 5, 0.0 + this.startY + 5,
                0.0 + this.startX + 5, 20.0 + this.startY + 5,
                15.0 + this.startX + 5, 10.0 + this.startY + 5);
        this.rightRunLineOutput.getPoints().clear();
        this.rightRunLineOutput.getPoints().addAll(
                0.0 + this.endX - 20, 0.0 + this.startY + 5,
                0.0 + this.endX - 20, 20.0 + this.startY + 5,
                15.0 + this.endX - 20, 10.0 + this.startY + 5);
        this.rightMask.setWidth(endX - startX + 20);
        this.rightMask.setHeight(endY - startY);
        this.base.setClip(getBaseMask());
    }

    private Rectangle getBaseMask() {
        Rectangle baseMask = new Rectangle(this.startX, this.startY, this.endX - this.startX, this.endY - this.startY);
        baseMask.setArcWidth(HelloApplication.ROUNDNESS);
        baseMask.setArcHeight(HelloApplication.ROUNDNESS);
        return baseMask;
    }

    public static class NodeCardNode {

        public Group point;
        public Group edit;
        public Text title;
        public String name;
        public List<String> lore;
        public Group root;
        public boolean rightText;
        private final VarType varType;

        NodeCardNode(String name, List<String> lore, VarType varType, boolean rightText) {
            this.point = varType.getShape1();
            this.edit = varType.getShape2();
            this.name = name;
            this.lore = lore;
            this.title = new Text(name);
            this.root = null;
            this.rightText = rightText;
            this.title.setFont(HelloApplication.TEXT_FONT);
            this.title.setFill(HelloApplication.FONT_COLOR);
            this.varType = varType;
        }

        public void addTo(Group root) {
            this.root = root;
            this.root.getChildren().add(this.point);
            this.root.getChildren().add(this.edit);
            this.root.getChildren().add(this.title);
        }

        public void resetPos(float x, float y) {
            for (javafx.scene.Node node : this.point.getChildren()) {
                if (node instanceof Circle) {
                    node.setLayoutX(x);
                    node.setLayoutY(y);
                } else {
                    node.setLayoutX(x - 7);
                    node.setLayoutY(y - 7);
                }
            }
            for (javafx.scene.Node node : this.edit.getChildren()) {
                node.setLayoutX(x - 7);
                node.setLayoutY(y - 7);
            }
            if (this.rightText) {
                this.title.setLayoutX(x + 10);
                this.title.setLayoutY(y + 4);
            } else {
                this.title.setLayoutX(x - this.title.getLayoutBounds().getWidth() - 12);
                this.title.setLayoutY(y + 4);
            }
        }

        public void resetSelection(float s) {
            // 设置缩放
            this.point.setScaleX(s);
            this.point.setScaleY(s);
            this.edit.setScaleX(s);
            this.edit.setScaleY(s);
            // TODO 缩放
            this.title.setScaleX(s);
            this.title.setScaleY(s);
            this.title.setLayoutX(this.title.getLayoutX() + 5 * s);
            this.title.setLayoutY(this.title.getLayoutY() + 5 * s);
        }

        public double getWidth() {
            return this.point.getLayoutBounds().getWidth() + this.title.getLayoutBounds().getWidth();
        }

        public double getNextHeight() {
            return switch (this.varType) {
                case BYTE,SHORT,INT,LONG,FLOAT,DOUBLE,BOOLEAN,CHAR -> 30;
            };
        }

        public void delete() {
            this.point.getChildren().clear();
            this.edit.getChildren().clear();
            this.root.getChildren().remove(this.point);
            this.root.getChildren().remove(this.edit);
            this.root.getChildren().remove(this.title);
        }

    }

    public enum VarType {
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR;

        public Group getShape1() {
            Group group = new Group();
            Shape shape;
            switch (this) {
                case BYTE:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(60, 180, 75));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case SHORT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(100, 149, 237));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case INT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(0, 135, 81));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case LONG:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(0, 0, 255));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case FLOAT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 191, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case DOUBLE:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 140, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case BOOLEAN:
                    shape = new Rectangle(0.0, 0.0, 14.0, 14.0);
                    shape.setFill(Color.rgb(153, 102, 255));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
                case CHAR:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 69, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    group.getChildren().add(shape);
                    break;
            }
            return group;
        }

        public Group getShape2() {
            Group group = new Group();
            switch (this) {
                case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR:
                    break;
            }
            return group;
        }

    }

}
