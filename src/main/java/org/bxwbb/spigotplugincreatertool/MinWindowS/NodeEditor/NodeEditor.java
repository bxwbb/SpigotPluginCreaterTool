package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.MinWindow;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.InputNodes;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.NodeTopBarColor;
import org.bxwbb.spigotplugincreatertool.MinWindowType;
import org.bxwbb.spigotplugincreatertool.windowLabel.Button;
import org.bxwbb.spigotplugincreatertool.windowLabel.ConnectingLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NodeEditor extends MinWindowType {

    // 网格颜色
    public static Color GRID_COLOR = Color.color(0.3, 0.3, 0.3);
    // 背景颜色
    public static Color BG_COLOR = Color.color(0.2, 0.2, 0.2);
    // 横向线条预设数
    public static int HORIZONTAL_LINE_COUNT = 50;
    // 纵向线条预设数
    public static int VERTICAL_LINE_COUNT = 50;
    // 线条间距
    public static float LINE_SPACING = 30.0f;
    // 顺序节点连接循环错误颜色
    public static Color ERROR_COLOR = Color.rgb(255, 118, 118);

    public double cameraX = 0.0f;
    public double cameraY = 0.0f;
    public double cameraScale = 1.5f;
    public List<Line> horizontalLines = new ArrayList<>();
    public List<Line> verticalLines = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();
    public List<NodeCtr> nodesCtr = new ArrayList<>();
    public ConnectingLine connectingLine;
    public boolean userConnectingLine = false;
    // 执行顺序连接线
    public List<ConnectingLine> executeLine = new ArrayList<>();
    // 数据传输连接线
    public List<ConnectingLine> dataLine = new ArrayList<>();
    public Group connectingLineGroup;
    public Group connectingDataLineGroup;
    // 聚焦节点
    public Node focusNode;
    public NodeCtr runNodectr;
    public Node.NodeCardNode focusCardNode;
    public Group trueBase;


    // 临时记录鼠标坐标
    private double rMouseX;
    private double rMouseY;
    private double rrMouseX;
    private double rrMouseY;
    //临时记录摄像机坐标
    private double rCameraX;
    private double rCameraY;
    private Button runButton;
    private Button nextButton;
    private final EventHandler<MouseEvent> pressHandler = event -> {
        if (event.isMiddleButtonDown()) {
            rCameraX = cameraX;
            rCameraY = cameraY;
            rrMouseX = event.getX();
            rrMouseY = event.getY();
        }
    };
    private final EventHandler<MouseEvent> dragHandler = event -> {
        if (event.getButton() == MouseButton.MIDDLE) {
            cameraX = rCameraX - (event.getX() - rMouseX);
            cameraY = rCameraY - (event.getY() - rMouseY);
            this.background.setCursor(Cursor.MOVE);
            for (Node node : this.nodes) {
                node.resetPos((float) (node.startX - this.rrMouseX + event.getX()), (float) (node.startY - this.rrMouseY + event.getY()));
            }
            // 计算网格偏移量（确保网格从屏幕边缘开始）
            double hOffset = cameraY % (LINE_SPACING * cameraScale);
            double vOffset = cameraX % (LINE_SPACING * cameraScale);

            // 水平线条
            for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
                if (i < horizontalLines.size()) {
                    double y = -hOffset + i * LINE_SPACING * cameraScale;
                    horizontalLines.get(i).setStartY(y);
                    horizontalLines.get(i).setEndY(y);
                    horizontalLines.get(i).setStartX(0);
                    horizontalLines.get(i).setEndX(10000);
                }
            }

            // 垂直线条
            for (int i = 0; i < VERTICAL_LINE_COUNT; i++) {
                if (i < verticalLines.size()) {
                    double x = -vOffset + i * LINE_SPACING * cameraScale;
                    verticalLines.get(i).setStartX(x);
                    verticalLines.get(i).setEndX(x);
                    verticalLines.get(i).setStartY(0);
                    verticalLines.get(i).setEndY(10000);
                }
            }
            rrMouseX = event.getX();
            rrMouseY = event.getY();
        }
    };
    private final EventHandler<MouseEvent> releaseHandler = event -> this.background.setCursor(Cursor.DEFAULT);
    private final EventHandler<ScrollEvent> scrollHandler = event -> {
        if (event.getDeltaY() > 0.0) {
            this.cameraScale += 0.05;
//            HelloApplication.scaleTo(this.trueBase, this.cameraScale, 0, 0);
        } else {
            this.cameraScale -= 0.05;
//            HelloApplication.scaleTo(this.trueBase, this.cameraScale, 0, 0);
        }
        if (this.cameraScale <= 1) {
            this.cameraScale = 1;
        } else if (this.cameraScale >= 5.0) {
            this.cameraScale = 5.0;
        }
        double deltaX = event.getX() * (1 - cameraScale);
        double deltaY = event.getY() * (1 - cameraScale);
        // 应用摄像机位移
        cameraX += deltaX;
        cameraY += deltaY;
        for (int i = 0; i < this.horizontalLines.size(); i++) {
            horizontalLines.get(i).setStartY((cameraY % (HORIZONTAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
            horizontalLines.get(i).setEndY((cameraY % (HORIZONTAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
        }
        for (int i = 0; i < this.verticalLines.size(); i++) {
            verticalLines.get(i).setStartX((cameraX % (VERTICAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
            verticalLines.get(i).setEndX((cameraX % (VERTICAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
        }
    };
    private final EventHandler<MouseEvent> mpHandler = event -> {
        this.rMouseX = event.getX();
        this.rMouseY = event.getY();
    };
    private RunCode runCode;
    private RunACode runACode;

    public NodeEditor(Group root, Group baseGroup, Group topBase, Rectangle background) throws ClassNotFoundException {
        super(root, baseGroup, topBase, background);
        init();
    }

    public void init() throws ClassNotFoundException {
        this.trueBase = new Group();
        this.connectingLineGroup = new Group();
        this.connectingDataLineGroup = new Group();
        this.title = "节点编辑器";
        this.background.setFill(BG_COLOR);
        for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
            Line line = new Line();
            line.setStroke(GRID_COLOR);
            line.setStrokeWidth(0.5f);
            line.setStartX(0.0f);
            line.setStartY(i * LINE_SPACING * cameraScale);
            line.setEndX(10000);
            line.setEndY(i * LINE_SPACING * cameraScale);
            line.setMouseTransparent(true);
            this.horizontalLines.add(line);
            this.trueBase.getChildren().add(line);
        }
        for (int i = 0; i < VERTICAL_LINE_COUNT; i++) {
            Line line = new Line();
            line.setStroke(GRID_COLOR);
            line.setStrokeWidth(0.5f);
            line.setStartX(i * LINE_SPACING * cameraScale);
            line.setStartY(0.0);
            line.setEndX(i * LINE_SPACING * cameraScale);
            line.setEndY(10000.0);
            line.setMouseTransparent(true);
            this.verticalLines.add(line);
            this.trueBase.getChildren().add(line);
        }
        this.background.addEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
        this.background.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragHandler);
        this.background.addEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
        this.background.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
        this.background.addEventHandler(MouseEvent.ANY, mpHandler);

        this.runButton = new Button(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 50, this.startY + MinWindow.PADDING + 35.0, false);
        this.runButton.addTo(this.topBase);
        this.runButton.background.setOnMouseClicked(event -> {
            if (!(Boolean) this.runButton.getData()) {
                this.runCode = new RunCode(this.runButton, this.nodesCtr);
                new Thread(this.runCode).start();
            } else {
                runButton.setData(false);
                runButton.resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Run.png"
                        )))
                );
                if (this.runCode != null)
                    this.runCode.cancel();
                if (this.runACode != null)
                    this.runACode.cancel();
                this.runNodectr = null;
                for (Node node : this.nodes) {
                    node.backgroundBorder.setFill(Node.CARD_BORDER_COLOR);
                }
            }
        });
        this.nextButton = new Button(this.startX + this.getEditorNameWidth() + 50, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 70, this.startY + MinWindow.PADDING + 35.0, false);
        this.nextButton.resetImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/next.png"))));
        this.nextButton.addTo(this.topBase);
        this.nextButton.background.setOnMouseClicked(event -> {
            this.nextButton.setData(true);
            if (!(Boolean) this.runButton.getData()) {
                for (NodeCtr nodectr : nodesCtr) {
                    if (nodectr.node.name.equals(NodeTopBarColor.START_NODE_TITLE)) {
                        this.runNodectr = nodectr;
                        break;
                    }
                }
                if (this.runNodectr == null) {
                    System.out.println("没有程序入口点");
                    return;
                } else {
                    runButton.setData(true);
                    runButton.resetImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                    "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Stop.png"
                            )))
                    );
                    this.runNodectr.node.backgroundBorder.setFill(Node.RUNNING_COLOR);
                }
            } else {
                this.runACode = new RunACode(this.runNodectr);
                new Thread(this.runACode).start();
                if (this.runNodectr.node.nextNode != null) {
                    for (NodeCtr nc : this.nodesCtr) {
                        if (nc.node == this.runNodectr.node.nextNode) {
                            this.runNodectr = nc;
                            break;
                        }
                    }
                } else {
                    runButton.setData(false);
                    runButton.resetImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                    "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Run.png"
                            )))
                    );
                    this.runNodectr = null;
                }
            }
            this.nextButton.setData(false);
        });

        this.trueBase.getChildren().add(this.connectingLineGroup);
        this.trueBase.getChildren().add(this.connectingDataLineGroup);

//        this.nodes.add(new Node(100.0, 100.0, this.base, "测试节点-Test node", new ArrayList<>(), Arrays.asList(
//                new Node.NodeCardNode("测试字节型参数A-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BYTE, true, null, (byte) 0),
//                new Node.NodeCardNode("测试短整型参数B-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.SHORT, true, null, (short) 0),
//                new Node.NodeCardNode("测试整型参数C-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.INT, true, null, 0),
//                new Node.NodeCardNode("测试长整型参数D-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LONG, true, null, (long) 0),
//                new Node.NodeCardNode("测试单精度浮点数参数E-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.FLOAT, true, null, 0.0f),
//                new Node.NodeCardNode("测试双精度浮点数参数F-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.DOUBLE, true, null, 0.0d),
//                new Node.NodeCardNode("测试布尔参数G-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BOOLEAN, true, null, true),
//                new Node.NodeCardNode("测试字符型参数H-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.CHAR, true, null, 'A'),
//                new Node.NodeCardNode("测试字符串参数I-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.STRING, true, null, "文本"),
//                new Node.NodeCardNode("测试字节型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.BYTE
//                ), List.of(
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1)
//                )),
//                new Node.NodeCardNode("测试短整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.SHORT
//                ), List.of(
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1)
//                )),
//                new Node.NodeCardNode("测试整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.INT
//                ), List.of(
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                )),
//                new Node.NodeCardNode("测试长整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.LONG
//                ), List.of(
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                )),
//                new Node.NodeCardNode("测试单精度浮点列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.FLOAT
//                ), List.of(
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f)
//                )),
//                new Node.NodeCardNode("测试双精度浮点列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.DOUBLE
//                ), List.of(
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5)
//                )),
//                new Node.NodeCardNode("测试布尔列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.BOOLEAN
//                ), List.of(
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description"))
//                )),
//                new Node.NodeCardNode("测试字节型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.CHAR
//                ), List.of(
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1)
//                )),
//                new Node.NodeCardNode("测试字符串列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.STRING
//                ), List.of(
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本")
//                )),
//                new Node.NodeCardNode("测试二维列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, true, List.of(
//                        Node.VarType.LIST, Node.VarType.INT
//                ), List.of(
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT)
//                ))
//        ), Arrays.asList(
//                new Node.NodeCardNode("测试字节型参数AI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BYTE, false, null, (byte) 0),
//                new Node.NodeCardNode("测试短整型参数BI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.SHORT, false, null, (short) 0),
//                new Node.NodeCardNode("测试整型参数CI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.INT, false, null, 0),
//                new Node.NodeCardNode("测试长整型参数DI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LONG, false, null, 0L),
//                new Node.NodeCardNode("测试单精度浮点数参数EI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.FLOAT, false, null, 0.0f),
//                new Node.NodeCardNode("测试双精度浮点数参数FI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.DOUBLE, false, null, 0.0d),
//                new Node.NodeCardNode("测试布尔参数GI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BOOLEAN, false, null, false),
//                new Node.NodeCardNode("测试字符型参数HI-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.CHAR, false, null, 'A'),
//                new Node.NodeCardNode("测试字符串型参数II-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.STRING, false, null, "文本"),
//                new Node.NodeCardNode("测试字节型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.BYTE
//                ), List.of(
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1),
//                        new SliderByte(0, 0, 0, 0, (byte) 0, "byte", List.of("参数描述-Parameter description"), true, true, (byte) -128, (byte) 127, (byte) 1)
//                )),
//                new Node.NodeCardNode("测试短整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.SHORT
//                ), List.of(
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1),
//                        new SliderShort(0, 0, 0, 0, (short) 0, "short", List.of("参数描述-Parameter description"), false, false, (short) 0, (short) 0, (short) 1)
//                )),
//                new Node.NodeCardNode("测试整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.INT
//                ), List.of(
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                )),
//                new Node.NodeCardNode("测试长整型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.LONG
//                ), List.of(
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                        new SliderLong(0, 0, 0, 0, 0, "long", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                )),
//                new Node.NodeCardNode("测试单精度浮点列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.FLOAT
//                ), List.of(
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f),
//                        new SliderFloat(0, 0, 0, 0, 0.0f, "float", List.of("参数描述-Parameter description"), false, false, 0.0f, 0.0f, 0.5f)
//                )),
//                new Node.NodeCardNode("测试双精度浮点列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.DOUBLE
//                ), List.of(
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5),
//                        new SliderDouble(0, 0, 0, 0, 0.0, "double", List.of("参数描述-Parameter description"), false, false, 0.0, 0.0, 0.5)
//                )),
//                new Node.NodeCardNode("测试布尔列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.BOOLEAN
//                ), List.of(
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description")),
//                        new BooleanInput(0, 0, 0, 0, true, "boolean", List.of("参数描述-Parameter description"))
//                )),
//                new Node.NodeCardNode("测试字节型列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.CHAR
//                ), List.of(
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1),
//                        new SliderChar(0, 0, 0, 0, 'A', "char", List.of("参数描述-Parameter description"), (char) 1)
//                )),
//                new Node.NodeCardNode("测试字符串列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.STRING
//                ), List.of(
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本"),
//                        new StringInput(0, 0, 0, 0, "string", List.of("参数名-Parameter name"), "文本")
//                )),
//                new Node.NodeCardNode("测试二维列表参数-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LIST, false, List.of(
//                        Node.VarType.LIST, Node.VarType.INT
//                ), List.of(
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT),
//                        new ListSliderLong(0, 0, 0, 0, "list", List.of("参数名-Parameter name"), List.of(
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1),
//                                new SliderInt(0, 0, 0, 0, 0, "int", List.of("参数描述-Parameter description"), false, false, 0, 0, 1)
//                        ), Node.VarType.INT)
//                ))
//        ), Color.BLUE));

        this.addCard(InputNodes.MAIN.nodeCtr.createNew(100, 100, this.trueBase));
//        this.addCard(InputNodes.PRINT.nodeCtr.createNew(100, 100, this.trueBase));
//        this.addCard(InputNodes.PRINT.nodeCtr.createNew(100, 100, this.base));
//        this.addCard(InputNodes.PRINT.nodeCtr.createNew(100, 100, this.base));
//        this.addCard(InputNodes.LIST_GET.nodeCtr.createNew(100, 100, this.base));
//        this.addCard(InputNodes.INT_TO_STRING.nodeCtr.createNew(100, 100, this.base));
//        this.addCard(InputNodes.LIST_GET.nodeCtr.createNew(100, 100, this.base));
//        this.addCard(InputNodes.LIST_GET.nodeCtr.createNew(100, 100, this.base));

        this.base.getChildren().add(this.trueBase);

    }

    public void addCard(NodeCtr nodeCtr) {
        this.nodesCtr.add(nodeCtr);
        this.nodes.add(nodeCtr.node);
    }

    public void addConnectingLine(ConnectingLine connectingLine) {
        connectingLine.resetColor(Node.EXECUTE_ORDER_COLOR, Node.EXECUTE_ORDER_COLOR);
        this.executeLine.add(connectingLine);
        connectingLine.addTo(this.connectingLineGroup);
    }

    public void addConnectingDataLine(ConnectingLine connectingLine, Color startColor, Color endColor) {
        connectingLine.resetColor(startColor, endColor);
        this.dataLine.add(connectingLine);
        connectingLine.addTo(this.connectingDataLineGroup);
    }

    public void removeConnectingLine(ConnectingLine connectingLine) {
        for (Node node : this.nodes) {
            if (node.leftRunLine == connectingLine) {
                node.leftRunLine = null;
            }
            if (node.rightRunLine == connectingLine) {
                node.rightRunLine = null;
                node.nextNode = null;
            }
        }
        connectingLine.delete();
    }

    public void removeConnectingDataLine(ConnectingLine connectingLine) {
        for (Node node : this.nodes) {
            if (node.leftDataLines.contains(connectingLine)) {
                node.leftDataPointList.set(node.leftDataLines.indexOf(connectingLine), null);
                node.leftCardNodes.get(node.leftDataLines.indexOf(connectingLine)).edit.setVisible(true);
                node.leftDataLines.set(node.leftDataLines.indexOf(connectingLine), null);
            }
            for (List<ConnectingLine> cl : node.rightDataLines) {
                cl.remove(connectingLine);
            }
        }
        connectingLine.delete();
    }

    public void testConnectingLine(Node node) {
        Node rNode = node;
        while (rNode != null) {
            if (rNode.rightRunLine != null) {
                rNode.rightRunLine.resetColor(Node.EXECUTE_ORDER_COLOR, Node.EXECUTE_ORDER_COLOR);
            }
            if (rNode.nextNode == node) {
                // 发现循环
                Node r = node.nextNode;
                while (r != node) {
                    r.rightRunLine.resetColor(ERROR_COLOR, ERROR_COLOR);
                    r.leftRunLine.resetColor(ERROR_COLOR, ERROR_COLOR);
                    r = r.nextNode;
                }
                return;
            }
            rNode = rNode.nextNode;
        }
    }

    public void setConnectingLine(ConnectingLine connectingLine) {
        if (this.connectingLine != null) {
            this.connectingLine.delete();
        }
        this.connectingLine = connectingLine;
        this.connectingLine.addTo(this.trueBase);
    }

    public void resetPos(float x, float y) {
        super.resetPosSuper(x, y);
        this.runButton.resetPos(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0);
        this.nextButton.resetPos(this.startX + this.getEditorNameWidth() + 50, this.startY + MinWindow.PADDING + 15.0);
    }

    public void resetSize(float width, float height) {
        super.resetSizeSuper(width, height);
        this.runButton.resetPos(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0);
        this.nextButton.resetPos(this.startX + this.getEditorNameWidth() + 50, this.startY + MinWindow.PADDING + 15.0);
    }

    @Override
    public MinWindowTypeEnum getType() {
        return MinWindowTypeEnum.NodeEditorType;
    }

    public void delete() {
        for (Line line : this.horizontalLines) {
            this.trueBase.getChildren().remove(line);
        }
        for (Line line : this.verticalLines) {
            this.trueBase.getChildren().remove(line);
        }
        for (Node node : this.nodes) {
            node.delete();
        }
        this.runButton.delete();
        this.nextButton.delete();
        this.horizontalLines.clear();
        this.verticalLines.clear();
        this.connectingLineGroup.getChildren().clear();
        this.connectingDataLineGroup.getChildren().clear();
        this.background.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
        this.background.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragHandler);
        this.background.removeEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
        this.background.removeEventHandler(ScrollEvent.SCROLL, scrollHandler);
        this.background.removeEventHandler(MouseEvent.ANY, mpHandler);
        this.trueBase.getChildren().clear();
        this.base.getChildren().remove(this.trueBase);
    }

    public static class RunACode extends Task<Void> {

        private final NodeCtr nc;

        public RunACode(NodeCtr nc) {
            this.nc = nc;
        }

        @Override
        protected Void call() {
            nc.runCard();
            Platform.runLater(nc::setRunningColorToNext);
            return null;
        }

    }

    public static class RunCode extends Task<Void> {
        private final Button runButton;
        private final List<NodeCtr> nodesCtr;

        public RunCode(Button runButton, List<NodeCtr> nodesCtr) {
            this.runButton = runButton;
            this.nodesCtr = nodesCtr;
        }

        @Override
        protected Void call() {
            Platform.runLater(() -> {
                runButton.setData(true);
                runButton.resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Stop.png"
                        )))
                );
            });

            NodeCtr nc = null;
            for (NodeCtr nodectr : nodesCtr) {
                if (nodectr.node.name.equals(NodeTopBarColor.START_NODE_TITLE)) {
                    nc = nodectr;
                    break;
                }
            }

            if (nc != null) {
                NodeCtr finalNc = nc;
                Platform.runLater(() -> finalNc.node.backgroundBorder.setFill(Node.RUNNING_COLOR));
                do {
                    nc.runCard();
                    Platform.runLater(nc::setRunningColorToNext);

                    if (nc.node.nextNode == null) break;

                    NodeCtr nextNodeCtr = null;
                    for (NodeCtr nodectr : nodesCtr) {
                        if (nodectr.node == nc.node.nextNode) {
                            nextNodeCtr = nodectr;
                            break;
                        }
                    }

                    if (nextNodeCtr == null) break;
                    nc = nextNodeCtr;
                } while (true);
            } else {
                Platform.runLater(() -> System.out.println("没有程序入口点"));
            }
            Platform.runLater(() -> {
                runButton.setData(false);
                runButton.resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Run.png"
                        )))
                );
            });

            return null;
        }

    }

}
