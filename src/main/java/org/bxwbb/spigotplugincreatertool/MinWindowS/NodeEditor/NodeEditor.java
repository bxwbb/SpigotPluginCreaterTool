package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindow;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.InputNodes;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Nodes.NodeTopBarColor;
import org.bxwbb.spigotplugincreatertool.MinWindowType;
import org.bxwbb.spigotplugincreatertool.windowLabel.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class NodeEditor extends MinWindowType {

    static final Logger logger = LoggerFactory.getLogger(NodeEditor.class);

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
    // 执行线动画点颜色
    public static Color EXECUTE_LINE_COLOR = Color.rgb(255, 255, 255);
    // 执行线动画时长
    public static double EXECUTE_LINE_DURATION = 0.5;

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
    private TextButton selectFileButton;
    private MinecraftServerSearchBox searchBox;
    private Button openFileButton;
    private Button saveFileButton;

    public NodeEditor(Group root, Group baseGroup, Group topBase, Rectangle background) throws ClassNotFoundException {
        super(root, baseGroup, topBase, background);
        init();
    }

    public void init() throws ClassNotFoundException {
        this.trueBase = new Group();
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
        this.selectFileButton = new TextButton(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 200, this.startY + MinWindow.PADDING + 35.0, "选择文件", true);
        selectFileButton.addTo(this.topBase);
        this.selectFileButton.background.setOnMousePressed(event -> {
            if (this.searchBox != null) this.searchBox.delete();
            this.searchBox = new MinecraftServerSearchBox(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 30.0, 1000, 600);
            this.searchBox.addTo(this.topBase);
        });
        this.openFileButton = new Button(this.startX + this.getEditorNameWidth() + 205, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 225, this.startY + MinWindow.PADDING + 35.0, false);
        this.openFileButton.resetImage(
                new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/OpenFile.png"))
                )
        );
        this.openFileButton.addTo(this.topBase);
        this.openFileButton.background.setOnMousePressed(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("打开蓝图文件");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("蓝图文件 (*.bbm)", "*.bbm");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(HelloApplication.primaryStage);
            // 将选择文件的内容解析为Json对象
            try {
                if (selectedFile == null) {
                    logger.error("找不到的文件路径 - null");
                    return;
                }
                if (!selectedFile.exists()) {
                    logger.error("找不到的文件路径 - {}", selectedFile.getAbsolutePath());
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(new String(Files.readAllBytes(selectedFile.toPath())));
                for (NodeCtr nodeCtr : this.nodesCtr) {
                    nodeCtr.delete();
                }
                for (ConnectingLine line : this.dataLine) {
                    line.delete();
                }
                for (ConnectingLine line : this.executeLine) {
                    line.delete();
                }
                this.nodesCtr.clear();
                this.nodes.clear();
                this.nodesCtr = NodeEditor.deserialize(jsonObject.getJSONArray("nodes"), this.trueBase);
                for (NodeCtr nodeCtr : this.nodesCtr) {
                    this.nodes.add(nodeCtr.node);
                }
                final List<Node> rNodes = new ArrayList<>(this.nodes);
                for (Node node : rNodes) {
                    node.addTo();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        this.saveFileButton = new Button(this.startX + this.getEditorNameWidth() + 230, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 250, this.startY + MinWindow.PADDING + 35.0, false);
        this.saveFileButton.resetImage(
                new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/SaveFile.png"))
                )
        );
        this.saveFileButton.addTo(this.topBase);
        this.saveFileButton.background.setOnMousePressed(event -> {
            FileChooser fileChooserSave = new FileChooser();
            fileChooserSave.setTitle("保存蓝图文件");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("蓝图文件 (*.bbm)", "*.bbm");
            fileChooserSave.getExtensionFilters().add(extFilter);
            File fileSave = fileChooserSave.showSaveDialog(HelloApplication.primaryStage);
            JSONObject saveJson = new JSONObject();
            JSONArray nodesArray = this.serialize();
            saveJson.put("nodes", nodesArray);
            saveJson.put("version", 0);
            saveJson.put("name", "Blueprint");
            saveJson.put("description", "");
            saveJson.put("author", "");
            saveJson.put("license", "");
            saveJson.put("cameraX", this.cameraX);
            saveJson.put("cameraY", this.cameraY);
            saveJson.put("cameraScale", this.cameraScale);
            try {
                Files.write(fileSave.toPath(), saveJson.toJSONString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.runButton = new Button(this.startX + this.getEditorNameWidth() + 500, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 520, this.startY + MinWindow.PADDING + 35.0, false);
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
        this.nextButton = new Button(this.startX + this.getEditorNameWidth() + 525, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 545, this.startY + MinWindow.PADDING + 35.0, false);
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
                    logger.error("没有程序入口点");
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

        this.addCard(InputNodes.MAIN.nodeCtr.createNew(100, 100, this.trueBase));
        this.addCard(InputNodes.RANDOM.nodeCtr.createNew(100, 100, this.trueBase));
        this.addCard(InputNodes.RANDOM.nodeCtr.createNew(100, 100, this.trueBase));
        this.addCard(InputNodes.RANDOM.nodeCtr.createNew(100, 100, this.trueBase));
        this.addCard(InputNodes.ADD.nodeCtr.createNew(100, 100, this.trueBase));
        this.addCard(InputNodes.LIST_GET.nodeCtr.createNew(100, 100, this.trueBase));

        this.base.getChildren().add(this.trueBase);

    }

    public void addCard(NodeCtr nodeCtr) {
        this.nodesCtr.add(nodeCtr);
        this.nodes.add(nodeCtr.node);
    }

    public void addConnectingLine(ConnectingLine connectingLine) {
        connectingLine.resetColor(Node.EXECUTE_ORDER_COLOR, Node.EXECUTE_ORDER_COLOR);
        this.executeLine.add(connectingLine);
        this.trueBase.getChildren().add(this.trueBase.getChildren().size() - 1, connectingLine.getBaseGroup(this.trueBase));
    }

    public void addConnectingDataLine(ConnectingLine connectingLine, Color startColor, Color endColor) {
        connectingLine.resetColor(startColor, endColor);
        this.dataLine.add(connectingLine);
        this.trueBase.getChildren().add(this.trueBase.getChildren().size() - 1, connectingLine.getBaseGroup(this.trueBase));
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
        if (!this.trueBase.getChildren().contains(connectingLine.getBaseGroup(this.trueBase)))
            this.trueBase.getChildren().add(this.trueBase.getChildren().size() - 1, connectingLine.getBaseGroup(this.trueBase));
    }

    public void resetPos(float x, float y) {
        super.resetPosSuper(x, y);
        this.selectFileButton.resetPos(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0);
        this.openFileButton.resetPos(this.startX + this.getEditorNameWidth() + 205, this.startY + MinWindow.PADDING + 15.0);
        this.saveFileButton.resetPos(this.startX + this.getEditorNameWidth() + 230, this.startY + MinWindow.PADDING + 15.0);
        this.runButton.resetPos(this.startX + this.getEditorNameWidth() + 500, this.startY + MinWindow.PADDING + 15.0);
        this.nextButton.resetPos(this.startX + this.getEditorNameWidth() + 525, this.startY + MinWindow.PADDING + 15.0);
    }

    public void resetSize(float width, float height) {
        super.resetSizeSuper(width, height);
    }

    @Override
    public MinWindowTypeEnum getType() {
        return MinWindowTypeEnum.NodeEditorType;
    }

    // 从逻辑层面将卡片交换到最前面
    public void swapCard(Node node) {
        int index = this.nodes.indexOf(node);
        NodeCtr nodeCtr = this.nodesCtr.get(index);
        this.nodes.remove(node);
        this.nodes.addFirst(node);
        this.nodesCtr.remove(nodeCtr);
        this.nodesCtr.addFirst(nodeCtr);
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
        this.selectFileButton.delete();
        this.openFileButton.delete();
        this.saveFileButton.delete();
        this.runButton.delete();
        this.nextButton.delete();
        this.horizontalLines.clear();
        this.verticalLines.clear();
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
            Platform.runLater(() -> {
                if (nc.node.leftRunLine != null)
                    nc.node.leftRunLine.bezierCurve.startSignalAnimation(NodeEditor.EXECUTE_LINE_COLOR, NodeEditor.EXECUTE_LINE_DURATION);
            });
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
                    NodeCtr finalNc1 = nc;
                    Platform.runLater(() -> {
                        if (finalNc1.node.leftRunLine != null)
                            finalNc1.node.leftRunLine.bezierCurve.startSignalAnimation(NodeEditor.EXECUTE_LINE_COLOR, NodeEditor.EXECUTE_LINE_DURATION);
                    });
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

    public JSONArray serialize() {
        JSONArray ret = new JSONArray();
        for (NodeCtr nodeCtr : this.nodesCtr) {
            JSONObject nodeCtrObject = new JSONObject();
            nodeCtrObject.put("startX", nodeCtr.node.startX);
            nodeCtrObject.put("startY", nodeCtr.node.startY);
            nodeCtrObject.put("endX", nodeCtr.node.endX);
            nodeCtrObject.put("endY", nodeCtr.node.endY);
            nodeCtrObject.put("name", nodeCtr.node.name);
            nodeCtrObject.put("last", nodeCtr.node.lastNode == null ? null : nodeCtr.node.lastNode.uuid.toString());
            nodeCtrObject.put("next", nodeCtr.node.nextNode == null ? null : nodeCtr.node.nextNode.uuid.toString());
            JSONArray leftDataList = new JSONArray();
            for (Node.NodeCardNode leftCardNode : nodeCtr.node.leftCardNodes) {
                if (leftCardNode.varType.equals(Node.VarType.SELF_ADAPTION_LIST)) {
                    List<Object> leftData = new ArrayList<>();
                    SelfAdaptionListSlider slider = (SelfAdaptionListSlider) leftCardNode.edit;
                    for (BaseLabel sliderLong : slider.sliderLongs) {
                        leftData.add(sliderLong.getData());
                    }
                    leftDataList.add(leftData);
                } else {
                    leftDataList.add(leftCardNode.edit.getData());
                }
            }
            nodeCtrObject.put("leftData", leftDataList);
            JSONArray rightDataList = new JSONArray();
            for (Node.NodeCardNode rightCardNode : nodeCtr.node.rightCardNodes) {
                rightDataList.add(rightCardNode.edit.getData());
            }
            nodeCtrObject.put("rightData", rightDataList);
            JSONArray leftCardNodes = new JSONArray();
            for (int i = 0; i < nodeCtr.node.leftDataPointList.size(); i++) {
                Node.DataLinePoint dataLinePoint = nodeCtr.node.leftDataPointList.get(i);
                if (nodeCtr.node.leftCardNodes.get(i).varType.equals(Node.VarType.SELF_ADAPTION_LIST)) {
                    JSONArray jsonArray = getJsonArray(nodeCtr, i);
                    leftCardNodes.add(jsonArray);
                } else {
                    if (dataLinePoint == null) {
                        leftCardNodes.add(null);
                        continue;
                    }
                    JSONObject dataLinePointJson = new JSONObject();
                    dataLinePointJson.put("index", dataLinePoint.index);
                    dataLinePointJson.put("node", dataLinePoint.nodeCtr.uuid);
                    leftCardNodes.add(dataLinePointJson);
                }
            }
            nodeCtrObject.put("leftCardNodes", leftCardNodes);
            nodeCtrObject.put("uuid", nodeCtr.uuid.toString());
            ret.add(nodeCtrObject);
        }
        return ret;
    }

    @NotNull
    private static JSONArray getJsonArray(NodeCtr nodeCtr, int i) {
        JSONArray jsonArray = new JSONArray();
        SelfAdaptionListSlider slider = (SelfAdaptionListSlider) nodeCtr.node.leftCardNodes.get(i).edit;
        for (int j = 0; j < slider.dataPointList.size(); j++) {
            JSONObject dataLinePointJson = new JSONObject();
            dataLinePointJson.put("index", slider.dataPointList.get(j).index);
            dataLinePointJson.put("node", slider.dataPointList.get(j).nodeCtr.uuid);
            jsonArray.add(dataLinePointJson);
        }
        return jsonArray;
    }

    public static List<NodeCtr> deserialize(JSONArray nodeCtrsJsonArray, Group root) throws ClassNotFoundException {
        List<NodeCtr> ret = new ArrayList<>();
        for (Object nodeCtrJsonObject : nodeCtrsJsonArray) {
            JSONObject nodeCtrJson = (JSONObject) nodeCtrJsonObject;
            String nodeName = nodeCtrJson.getString("name");
            InputNodes inputNode = null;
            for (InputNodes value : InputNodes.values()) {
                if (value.nodeCtr.node.name.equals(nodeName)) {
                    inputNode = value;
                }
            }
            NodeCtr nodeCtr;
            if (inputNode != null) {
                nodeCtr = inputNode.nodeCtr.createNew(
                        nodeCtrJson.getFloat("startX"),
                        nodeCtrJson.getFloat("startY"),
                        root
                );
            } else {
                logger.error("发现未知的节点类型 - {}", nodeName);
                return new ArrayList<>();
            }
            JSONArray leftDataList = nodeCtrJson.getJSONArray("leftData");
            for (int i = 0; i < leftDataList.size(); i++) {
                if (nodeCtr.node.leftCardNodes.get(i).varType.equals(Node.VarType.SELF_ADAPTION_LIST)) {
                    if (!leftDataList.getJSONArray(i).isEmpty()) {
                        SelfAdaptionListSlider slider = (SelfAdaptionListSlider) nodeCtr.node.leftCardNodes.get(i).edit;
                        BaseLabel label = slider.sliderLongs.getFirst();
                        slider.sliderLongs.clear();
                        slider.sliderLongs.add(label);
                        for (int j = 0; j < leftDataList.getJSONArray(i).size(); j++) {
                            try {
                                slider.addSliderLong();
                                slider.sliderLongs.get(j).setData(leftDataList.getJSONArray(i).get(j));
                            } catch (ClassCastException e) {
                                logger.warn("忽略一个不可转换的类型 - L(S)");
                            }
                        }
                    }
                } else {
                    try {
                        nodeCtr.node.leftCardNodes.get(i).edit.setData(leftDataList.get(i));
                    } catch (ClassCastException e) {
                        logger.warn("忽略一个不可转换的类型 - L");
                    }
                }
            }
            JSONArray rightDataList = nodeCtrJson.getJSONArray("rightData");
            for (int i = 0; i < rightDataList.size(); i++) {
                try {
                    nodeCtr.node.rightCardNodes.get(i).edit.setData(rightDataList.get(i));
                } catch (ClassCastException e) {
                    logger.warn("忽略一个不可转换的类型 - R");
                }
            }
            nodeCtr.uuid = UUID.fromString(nodeCtrJson.getString("uuid"));
            nodeCtr.node.uuid = nodeCtr.uuid;
            ret.add(nodeCtr);
        }
        Map<String, NodeCtr> r = new HashMap<>();
        for (NodeCtr nodeCtr : ret) {
            r.put(nodeCtr.uuid.toString(), nodeCtr);
        }
        for (int i = 0; i < ret.size(); i++) {
            NodeCtr nodeCtr = ret.get(i);
            if (((JSONObject) nodeCtrsJsonArray.get(i)).getString("last") != null) {
                nodeCtr.node.lastNode = r.get(((JSONObject) nodeCtrsJsonArray.get(i)).getString("last")).node;
                ConnectingLine connectingLine = new ConnectingLine(
                        r.get(((JSONObject) nodeCtrsJsonArray.get(i)).getString("last")).node.endX - 15,
                        r.get(((JSONObject) nodeCtrsJsonArray.get(i)).getString("last")).node.startY + 15,
                        nodeCtr.node.startX + 10,
                        nodeCtr.node.startY + 15,
                        Node.EXECUTE_ORDER_COLOR,
                        Node.EXECUTE_ORDER_COLOR
                );
                nodeCtr.node.leftRunLine = connectingLine;
                r.get(((JSONObject) nodeCtrsJsonArray.get(i)).getString("last")).node.rightRunLine = connectingLine;
                connectingLine.addTo(root);
            } else {
                nodeCtr.node.lastNode = null;
            }
            if (((JSONObject) nodeCtrsJsonArray.get(i)).getString("next") != null) {
                nodeCtr.node.nextNode = r.get(((JSONObject) nodeCtrsJsonArray.get(i)).getString("next")).node;
            } else {
                nodeCtr.node.nextNode = null;
            }
            JSONArray dataLineArray = ((JSONObject) nodeCtrsJsonArray.get(i)).getJSONArray("leftCardNodes");
            for (int j = 0; j < dataLineArray.size(); j++) {
                if (nodeCtr.node.leftCardNodes.get(j).varType.equals(Node.VarType.SELF_ADAPTION_LIST)) {
                    SelfAdaptionListSlider slider = (SelfAdaptionListSlider) nodeCtr.node.leftCardNodes.get(j).edit;
                    JSONArray dataPointLines = dataLineArray.getJSONArray(j);
                    for (int k = 0; k < dataPointLines.size(); k++) {
                        slider.dataPointList.set(k, new Node.DataLinePoint(
                                r.get(dataPointLines.getJSONObject(k).getString("node")),
                                dataPointLines.getJSONObject(k).getIntValue("index")
                        ));
                        ConnectingLine connectingLine = new ConnectingLine(
                                0,
                                0,
                                0,
                                0,
                                ((Color) ((Shape) r.get(dataPointLines.getJSONObject(k).getString("node")).node.rightCardNodes.get(dataPointLines.getJSONObject(k).getIntValue("index")).point.getChildren().getFirst()).getFill()),
                                ((Color) ((Shape) nodeCtr.node.leftCardNodes.get(j).point.getChildren().getFirst()).getFill())
                        );
                        r.get(dataPointLines.getJSONObject(k).getString("node")).node.rightDataLines.get(dataPointLines.getJSONObject(k).getIntValue("index")).add(connectingLine);
                        slider.dataLines.set(k, connectingLine);
                        connectingLine.addTo(root);
                        nodeCtr.node.leftCardNodes.get(j).edit.setVisible(false);
                    }
                    ((Rectangle) nodeCtr.node.leftCardNodes.get(j).point.getChildren().getFirst()).setHeight(14 + (((slider).dataLines.size()) - 1) * 10);
                } else {
                    if (dataLineArray.get(j) == null) {
                        nodeCtr.node.leftDataPointList.set(j, null);
                        continue;
                    }
                    JSONObject dataPointJsonObject = dataLineArray.getJSONObject(j);
                    Node.DataLinePoint dataLinePoint = new Node.DataLinePoint(
                            r.get(dataPointJsonObject.getString("node")),
                            dataPointJsonObject.getIntValue("index", 0)
                    );
                    nodeCtr.node.leftDataPointList.set(j, dataLinePoint);
                    Node right = r.get(dataPointJsonObject.getString("node")).node;
                    ConnectingLine connectingLine = new ConnectingLine(
                            0,
                            0,
                            0,
                            0,
                            ((Color) ((Shape) right.rightCardNodes.get(dataPointJsonObject.getIntValue("index")).point.getChildren().getFirst()).getFill()),
                            ((Color) ((Shape) nodeCtr.node.leftCardNodes.get(j).point.getChildren().getFirst()).getFill())
                    );
                    right.rightDataLines.get(dataPointJsonObject.getIntValue("index")).add(connectingLine);
                    nodeCtr.node.leftDataLines.set(j, connectingLine);
                    connectingLine.addTo(root);
                    nodeCtr.node.leftCardNodes.get(j).edit.setVisible(false);
                }
            }
        }
        return ret;
    }

}
