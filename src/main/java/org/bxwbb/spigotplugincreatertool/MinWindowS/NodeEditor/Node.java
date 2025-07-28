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
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.ClassAnalyzer;
import org.bxwbb.spigotplugincreatertool.MinWindowType;
import org.bxwbb.spigotplugincreatertool.windowLabel.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class Node {

    // 节点卡片背景色
    public static Color CARD_BG_COLOR = Color.color(0.1, 0.1, 0.1);
    // 节点卡片顶部颜色
    public static Color CARD_TOP_COLOR = Color.color(0.1, 0.1, 0.1);
    // 节点卡片边框颜色
    public static Color CARD_BORDER_COLOR = Color.BLACK;
    // 节点卡片边框宽度
    public static float CARD_BORDER_WIDTH = 1.5f;
    // 卡片阴影效果半径
    public static float CARD_SHADOW_RADIUS = 30.0f;
    // 执行顺序连接节点颜色
    public static Color EXECUTE_ORDER_COLOR = Color.rgb(118, 255, 3);
    // 运行时颜色(偏绿)
    public static Color RUNNING_COLOR = Color.rgb(118, 255, 118);

    public double startX;
    public double startY;
    public double endX;
    public double endY;
    public Group root;
    public Group base;
    public Group nodeGroup;
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
    public Color topBarColor;
    // 左顺序节点连接线
    public ConnectingLine leftRunLine;
    // 右顺序节点连接线
    public ConnectingLine rightRunLine;
    public Node nextNode;
    public Node lastNode;
    public List<ConnectingLine> leftDataLines = new ArrayList<>();
    public List<DataLinePoint> leftDataPointList = new ArrayList<>();
    public List<List<ConnectingLine>> rightDataLines = new ArrayList<>();
    public boolean rightRunLineB;
    public boolean leftRunLineB;

    private double rMouseX, rMouseY;

    public Node(double startX, double startY, Group root, String name, List<String> lore, List<NodeCardNode> leftCardNodes, List<NodeCardNode> rightCardNodes, Color topBarColor, boolean leftRunLineB, boolean rightRunLineB) {
        this.startX = startX;
        this.startY = startY;
        this.endX = startX + 40 + (new Text(name).getLayoutBounds().getWidth() + 50);
        this.endY = startY + 45;
        this.root = root;
        this.name = name;
        this.lore = lore;
        this.leftRunLineB = leftRunLineB;
        this.rightRunLineB = rightRunLineB;
        this.leftGroup = new Group();
        this.rightGroup = new Group();
        this.leftCardNodes = leftCardNodes;
        this.rightCardNodes = rightCardNodes;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            if (this.leftCardNodes.isEmpty() && this.rightCardNodes.size() == 1) {
                this.endY += this.rightCardNodes.getLast().getNextHeight();
            }
            this.endY += nextHeight;
            this.endX = Math.max(this.endX, startX + this.rightCardNodes.get(i).getWidth());
        }
        double rNextHeight = 0;
        for (int i = 0; i < this.leftCardNodes.size(); i++) {
            rNextHeight += i == 0 ? (this.rightCardNodes.isEmpty() ? 0 : this.rightCardNodes.getLast().getNextHeight()) : this.leftCardNodes.get(i - 1).getNextHeight();
            this.leftCardNodes.get(i).resetPos((float) startX - 1, (float) ((float) endY + rNextHeight), startX, endX - startX);
            if (this.leftCardNodes.get(i).edit != null)
                this.leftCardNodes.get(i).edit.autoWidth();
            for (javafx.scene.Node shape : this.leftCardNodes.get(i).point.getChildren()) {
                int finalI = i;
                int finalI1 = i;
                shape.setOnMousePressed(event -> {
                    this.addDataLine((Color) ((Circle) this.leftCardNodes.get(finalI).point.getChildren().getFirst()).getFill(), this.leftCardNodes.get(finalI1).point.getChildren().getFirst().getLayoutX(), this.leftCardNodes.get(finalI1).point.getChildren().getFirst().getLayoutY(), true, this.leftCardNodes.get(finalI));
                });
            }
            this.leftDataLines.add(null);
            this.leftDataPointList.add(null);
            this.leftCardNodes.get(i).addTo(leftGroup);
        }
        for (int i = 0; i < this.leftCardNodes.size(); i++) {
            double nextHeight = i == 0 ? (this.rightCardNodes.isEmpty() ? 0 : this.rightCardNodes.getLast().getNextHeight()) : this.leftCardNodes.get(i - 1).getNextHeight();
            this.endY += nextHeight;
            this.endX = Math.max(this.endX, startX + this.leftCardNodes.get(i).getWidth());
        }
        this.endY += this.leftCardNodes.isEmpty() ? 0 : this.leftCardNodes.getLast().getNextHeight();
        rNextHeight = 0;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            rNextHeight += i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + rNextHeight), startX, endX - startX);
            for (javafx.scene.Node shape : this.rightCardNodes.get(i).point.getChildren()) {
                int finalI = i;
                int finalI1 = i;
                shape.setOnMousePressed(event -> {
                    this.addDataLine((Color) ((Circle) this.rightCardNodes.get(finalI).point.getChildren().getFirst()).getFill(), this.rightCardNodes.get(finalI1).point.getChildren().getFirst().getLayoutX(), this.rightCardNodes.get(finalI1).point.getChildren().getFirst().getLayoutY(), false, this.rightCardNodes.get(finalI));
                });
            }
            this.rightDataLines.add(new ArrayList<>());
            this.rightCardNodes.get(i).addTo(rightGroup);
        }
        this.leftMask = new Rectangle(startX - 20, startY, endX - startX + 20, endY - startY);
        this.leftGroup.setClip(leftMask);
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
        this.topBarColor = topBarColor;
        this.topBar.setStroke(this.topBarColor);
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
        this.leftRunLineInput.setOnMousePressed(event -> {
            if (HelloApplication.cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                NodeEditor nodeEditor = (NodeEditor) HelloApplication.cancelWindow.minWindowType;
                if (!nodeEditor.userConnectingLine) {
                    nodeEditor.userConnectingLine = true;
                    nodeEditor.focusNode = this;
                    HelloApplication.isConnectingInput = true;
                    nodeEditor.setConnectingLine(new ConnectingLine(
                            event.getX(), event.getY(),
                            this.startX + 10, this.startY + 15,
                            HelloApplication.HOVER_COLOR,
                            this.EXECUTE_ORDER_COLOR
                    ));
                }
            }
        });
        if (!this.leftRunLineB) {
            this.leftRunLineInput.setVisible(false);
        }
        this.rightRunLineOutput = new Polygon();
        this.rightRunLineOutput.getPoints().addAll(
                0.0 + this.endX - 20, 0.0 + this.startY + 5,
                0.0 + this.endX - 20, 20.0 + this.startY + 5,
                15.0 + this.endX - 20, 10.0 + this.startY + 5);
        this.rightRunLineOutput.setFill(EXECUTE_ORDER_COLOR);
        this.rightRunLineOutput.setStrokeWidth(1.5);
        this.rightRunLineOutput.setStroke(CARD_BORDER_COLOR);
        this.rightRunLineOutput.setOnMousePressed(event -> {
            if (HelloApplication.cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                NodeEditor nodeEditor = (NodeEditor) HelloApplication.cancelWindow.minWindowType;
                if (!nodeEditor.userConnectingLine) {
                    nodeEditor.userConnectingLine = true;
                    nodeEditor.focusNode = this;
                    HelloApplication.isConnectingInput = false;
                    nodeEditor.setConnectingLine(new ConnectingLine(
                            this.endX - 15, this.startY + 15,
                            event.getX(), event.getY(),
                            this.EXECUTE_ORDER_COLOR,
                            HelloApplication.HOVER_COLOR
                    ));
                }
            }
        });
        if (!this.rightRunLineB) {
            this.rightRunLineOutput.setVisible(false);
        }
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
        this.resetPos((float) this.startX, (float) this.startY);
    }

    public void updateInput() throws ClassNotFoundException {
        for (int i = 0; i < this.leftDataPointList.size(); i++) {
            if (this.leftDataPointList.get(i) == null) {
                this.leftCardNodes.get(i).edit.setVisible(true);
            } else {
                this.leftCardNodes.get(i).edit.setVisible(false);
                this.leftCardNodes.get(i).edit.setData(this.leftDataPointList.get(i).getData());
            }
        }
    }

    public Node createNew(double x, double y, Group root) throws ClassNotFoundException {
        List<NodeCardNode> lcn = new ArrayList<NodeCardNode>();
        for (NodeCardNode n : this.leftCardNodes) {
            lcn.add(n.createNew());
        }
        List<NodeCardNode> rcn = new ArrayList<NodeCardNode>();
        for (NodeCardNode n : this.rightCardNodes) {
            rcn.add(n.createNew());
        }
        return new Node(x, y, root, this.name, this.lore, lcn, rcn, this.topBarColor, this.leftRunLineB, this.rightRunLineB);
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
        double rY = startY + 45;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            rY += nextHeight;
        }
        double rNextHeight = 0;
        for (int i = 0; i < this.leftCardNodes.size(); i++) {
            rNextHeight += i == 0 ? (this.rightCardNodes.isEmpty() ? 0 : this.rightCardNodes.getLast().getNextHeight()) : this.leftCardNodes.get(i - 1).getNextHeight();
            this.leftCardNodes.get(i).resetPos((float) startX - 1, (float) ((float) rY + rNextHeight), startX, endX - startX);
            if (this.leftDataLines.get(i) != null)
                this.leftDataLines.get(i).resetSize(startX - 1, rY + rNextHeight);
        }
        rNextHeight = 0;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            rNextHeight += i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + rNextHeight), startX, endX - startX);
            if (this.rightDataLines.get(i) != null) {
                for (ConnectingLine cl : this.rightDataLines.get(i)) {
                    cl.resetPos(endX + 1, (startY + 45 + rNextHeight));
                }
            }
        }
        this.leftMask.setX(startX - 20);
        this.leftMask.setY(startY);
        this.rightMask.setX(startX);
        this.rightMask.setY(startY);
        this.base.setClip(getBaseMask());
        if (leftRunLine != null)
            this.leftRunLine.resetSize(this.startX + 10, this.startY + 15);
        if (rightRunLine != null)
            this.rightRunLine.resetPos(this.endX - 15, this.startY + 15);
    }

    public void resetSize(float width, float height) {
        this.endX = startX + width;
        this.endY = startY + height;
        double rY = startY + 45;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            double nextHeight = i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            rY += nextHeight;
        }
        double rNextHeight = 0;
        for (int i = 0; i < this.leftCardNodes.size(); i++) {
            rNextHeight += i == 0 ? this.leftCardNodes.getLast().getNextHeight() : this.leftCardNodes.get(i - 1).getNextHeight();
            this.leftCardNodes.get(i).resetPos((float) startX - 1, (float) ((float) rY + rNextHeight), startX, endX - startX);
            if (this.leftDataLines.get(i) != null)
                this.leftDataLines.get(i).resetSize(startX - 1, rY + rNextHeight);
        }
        rNextHeight = 0;
        for (int i = 0; i < this.rightCardNodes.size(); i++) {
            rNextHeight += i == 0 ? 0 : this.rightCardNodes.get(i - 1).getNextHeight();
            this.rightCardNodes.get(i).resetPos((float) endX + 1, (float) ((float) startY + 45 + rNextHeight), startX, endX - startX);
            if (this.rightDataLines.get(i) != null) {
                for (ConnectingLine cl : this.rightDataLines.get(i)) {
                    cl.resetPos(endX + 1, (startY + 45 + rNextHeight));
                }
            }
        }
        for (NodeCardNode nodeCardNode : this.leftCardNodes) {
            if (nodeCardNode.edit != null)
                nodeCardNode.edit.resetSize((float) ((float) endX - startX - 20), (float) 14);
            ;
        }
        for (NodeCardNode nodeCardNode : this.rightCardNodes) {
            if (nodeCardNode.edit != null)
                nodeCardNode.edit.resetSize((float) ((float) endX - startX - 20), (float) 14);
            ;
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
        this.leftMask.setWidth(endX - startX + 20);
        this.leftMask.setHeight(endY - startY);
        this.rightMask.setWidth(endX - startX + 20);
        this.rightMask.setHeight(endY - startY);
        this.base.setClip(getBaseMask());
        if (leftRunLine != null)
            this.leftRunLine.resetSize(this.startX + 10, this.startY + 15);
        if (rightRunLine != null)
            this.rightRunLine.resetPos(this.endX - 15, this.startY + 15);
        this.resetPos((float) this.startX, (float) this.startY);
    }

    private Rectangle getBaseMask() {
        Rectangle baseMask = new Rectangle(this.startX, this.startY, this.endX - this.startX, this.endY - this.startY);
        baseMask.setArcWidth(HelloApplication.ROUNDNESS);
        baseMask.setArcHeight(HelloApplication.ROUNDNESS);
        return baseMask;
    }

    public static class NodeCardNode {

        public Group point;
        public BaseLabel edit;
        public String name;
        public List<String> lore;
        public Group root;
        public boolean rightText;
        public VarType varType;

        private List<VarType> args;
        private Object data;
        private ClassAnalyzer.ClassInfo classInfo;

        public NodeCardNode(String name, List<String> lore, VarType varType, boolean rightText, List<VarType> args, Object data) throws ClassNotFoundException {
            init(name, lore, varType, rightText, args, data, null);
        }

        public NodeCardNode(String name, List<String> lore, VarType varType, boolean rightText, List<VarType> args, Object data, ClassAnalyzer.ClassInfo classInfo) throws ClassNotFoundException {
            init(name, lore, varType, rightText, args, data, classInfo);
        }

        private void init(String name, List<String> lore, VarType varType, boolean rightText, List<VarType> args, Object data, ClassAnalyzer.ClassInfo classInfo) throws ClassNotFoundException {
            this.classInfo = classInfo;
            if (varType.equals(VarType.__DEFAULT__)) {
                this.point = varType.getShape1(args, classInfo);
            } else {
                this.point = varType.getShape1(args);
            }
            this.rightText = rightText;
            if (varType.equals(VarType.__DEFAULT__)) {
                this.edit = varType.getShape2(this.rightText, data, classInfo);
            } else {
                this.edit = varType.getShape2(this.rightText, data);
            }
            if (this.edit != null) {
                this.edit.setName(name);
            }
            this.name = name;
            this.lore = lore;
            this.root = null;
            this.varType = varType;
            this.args = args;
            this.data = data;
        }

        public NodeCardNode createNew() throws ClassNotFoundException {
            Object d = this.data;
            List<BaseLabel> bl = new ArrayList<>();
            if (this.varType.equals(VarType.LIST)) {
                List<BaseLabel> r = (List<BaseLabel>) d;
                for (BaseLabel baseLabel : r) {
                    bl.add(baseLabel.createNew());
                }
            }
            return new NodeCardNode(this.name, this.lore, this.varType, this.rightText, this.args, this.varType.equals(VarType.LIST) ? bl : d, this.classInfo);
        }

        public void addTo(Group root) {
            this.root = root;
            this.root.getChildren().add(this.point);
            if (this.edit != null)
                this.edit.addTo(root);
        }

        public void resetPos(float x, float y, double yx, double yw) {
            for (javafx.scene.Node node : this.point.getChildren()) {
                if (node instanceof Circle) {
                    node.setLayoutX(x);
                    node.setLayoutY(y);
                } else {
                    node.setLayoutX(x - 7);
                    node.setLayoutY(y - 7);
                }
            }
            if (edit != null) {
                edit.resetPos((float) (yx + 10), y - 7);
                edit.resetSize((float) yw - 20, (float) Node.getLableHieght(this.varType));
            }
        }

        public double getWidth() {
            return this.point.getLayoutBounds().getWidth() + ((this.edit == null) ? 0 : this.edit.getWidth());
        }

        public double getNextHeight() {
            return Node.getNextHeight(this.varType, this.args);
        }

        public void delete() {
            this.point.getChildren().clear();
            if (this.edit != null)
                this.edit.delete();
            this.root.getChildren().remove(this.point);
        }

    }

    public static double getNextHeight(VarType varType, List<VarType> args) {
        switch (varType) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR, OBJECT, __DEFAULT__, STRING:
                return 25;
            case LIST:
                return ListSlider.LIST_LABLE_HEIGHT + 15;
            default:
                return 0;
        }
    }

    public static double getLableHieght(VarType varType) {
        return switch (varType) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR, OBJECT, __DEFAULT__, STRING:
                yield 14;
            case LIST:
                yield ListSlider.LIST_LABLE_HEIGHT;
            default:
                yield 0;
        };
    }

    public enum VarType {
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR,
        STRING,
        LIST,
        OBJECT,
        __DEFAULT__;

        public Group getShape1(List<VarType> args) {
            return this.getShape1(args, null);
        }

        public Group getShape1(List<VarType> args, ClassAnalyzer.ClassInfo classInfo) {
            Group group = new Group();
            Shape shape = null;
            Shape shape2;
            VarType rVarType;
            List<VarType> argCopy;
            switch (this) {
                case BYTE:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(60, 180, 75));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case SHORT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(100, 149, 237));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case INT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(0, 135, 81));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case LONG:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(0, 0, 255));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case FLOAT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 191, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case DOUBLE:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 140, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case BOOLEAN:
                    shape = new Rectangle(0.0, 0.0, 14.0, 14.0);
                    shape.setFill(Color.rgb(153, 102, 255));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case CHAR:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(255, 69, 0));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case STRING:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(147, 112, 219));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case LIST:
                    argCopy = new ArrayList<>(args);
                    rVarType = argCopy.getFirst();
                    argCopy.removeFirst();
                    group = rVarType.getShape1(argCopy);
                    shape2 = new Circle(0.0, 0.0, 3.0);
                    shape2.setFill(CARD_BORDER_COLOR);
                    group.getChildren().add(shape2);
                    break;
                case OBJECT:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(Color.rgb(200, 200, 200));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
                case __DEFAULT__:
                    shape = new Circle(0.0, 0.0, 7.0);
                    shape.setFill(HelloApplication.stringToColor(classInfo.fullClassName));
                    shape.setStrokeWidth(1.5f);
                    shape.setStroke(CARD_BORDER_COLOR);
                    break;
            }
            if (!group.getChildren().contains(shape) && shape != null) group.getChildren().add(shape);
            return group;
        }

        public BaseLabel getShape2(boolean rightText, Object data) throws ClassNotFoundException {
            return this.getShape2(rightText, data, null);
        }

        public BaseLabel getShape2(boolean rightText, Object data, ClassAnalyzer.ClassInfo classInfo) throws ClassNotFoundException {
            BaseLabel baseLabel = switch (this) {
                case FLOAT ->
                        new SliderFloat(0.0, 0.0, 10.0, 10.0, (Float) data, "单精度浮点-Float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f);
                case DOUBLE ->
                        new SliderDouble(0.0, 0.0, 10.0, 10.0, (Double) data, "双精度浮点-Double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5);
                case BYTE ->
                        new SliderByte(0, 0, 10, 10, (byte) data, "字节-Byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1);
                case SHORT ->
                        new SliderShort(0, 0, 10, 10, (short) data, "短整型-Short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1);
                case INT ->
                        new SliderInt(0, 0, 10, 10, (int) data, "整型-Int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1);
                case LONG ->
                        new SliderLong(0, 0, 10, 10, (long) data, "长整型-Long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1);
                case BOOLEAN ->
                        new BooleanInput(0, 0, 10, 10, (boolean) data, "布尔型-Boolean", List.of("参数描述-Parameter description"));
                case CHAR ->
                        new SliderChar(0, 0, 10, 10, (char) data, "字节型-Byte", List.of("参数描述-Parameter description"), (char) 1);
                case STRING ->
                        new StringInput(0, 0, 10, 10, (String) data, "字符串型-String", List.of("参数描述-Parameter description"));
                case LIST ->
                        new ListSlider(0, 0, 10, 10, "长整型列表-Long List", List.of("参数描述-Parameter description"), (List<BaseLabel>) data, VarType.LONG);
                case OBJECT ->
                        new ObjectInput(0, 0, 10, 10, data, "对象型-Object", List.of("参数描述-Parameter description"));
                case __DEFAULT__ ->
                        new DefaultObjectInput(0, 0, 10, 10, data, "未定义型-Default", List.of("参数描述-Parameter description"), classInfo);
            };
            baseLabel.setVisible(rightText);
            if (!rightText) {
                baseLabel.setVisible(false);
            }

            return baseLabel;
        }

    }

    public void addDataLine(Color startColor, double x, double y, boolean input, NodeCardNode ncn) {
        if (HelloApplication.cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
            NodeEditor nodeEditor = (NodeEditor) HelloApplication.cancelWindow.minWindowType;
            if (!nodeEditor.userConnectingLine) {
                nodeEditor.userConnectingLine = true;
                nodeEditor.focusNode = this;
                HelloApplication.isConnectingData = true;
                HelloApplication.isConnectingDataInput = input;
                nodeEditor.setConnectingLine(new ConnectingLine(
                        x, y,
                        x, y,
                        !input ? startColor : HelloApplication.HOVER_COLOR,
                        input ? startColor : HelloApplication.HOVER_COLOR
                ));
                nodeEditor.focusCardNode = ncn;
            }
        }
    }

    public static class DataLinePoint {
        public NodeCtr nodeCtr;
        public int index;

        public DataLinePoint(NodeCtr nodeCtr, int index) {
            this.nodeCtr = nodeCtr;
            this.index = index;
        }

        public Object getData() {
            return nodeCtr.getOutput(this.index);
        }

    }

}
