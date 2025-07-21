package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.JavaSourceScannerFixed;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeCtr;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeEditor;
import org.bxwbb.spigotplugincreatertool.windowLabel.BaseLabel;
import org.bxwbb.spigotplugincreatertool.windowLabel.ConnectingLine;
import org.bxwbb.spigotplugincreatertool.windowLabel.SearchBox;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ALL")
public class HelloApplication extends Application {

    // 背景颜色
    public static Color BG_COLOR = Color.color(0.1, 0.1, 0.1);
    // 用户界面圆滑度
    public static float ROUNDNESS = 10.0f;
    // 边框颜色
    public static Color BORDER_COLOR = Color.color(0.4, 0.4, 0.4);
    // 未选中颜色
    public static Color UNSELECTED_COLOR = Color.color(0.2, 0.2, 0.2);
    // 未选中边框颜色
    public static Color UNSELECTED_BORDER_COLOR = Color.BLACK;
    // 悬停颜色
    public static Color HOVER_COLOR = Color.color(0.5, 0.5, 0.5);
    // 选中颜色
    public static Color SELECTED_COLOR = Color.rgb(65, 112, 210);
    // 菜单颜色
//    public static Color MENU_COLOR = Color.color(0.3, 0.3, 0.3);
    public static Color MENU_COLOR = Color.color(0.2, 0.2, 0.2);
    // 字体颜色
    public static Color FONT_COLOR = Color.color(0.7, 0.7, 0.7);
    // 字体
    public static Font TEXT_FONT = Font.font("Arial", FontWeight.NORMAL, 12);
    // 取消显示位移量

    // 交点窗口
    public static MinWindow cancelWindow;
    @SuppressWarnings("ClassEscapesDefinedScope")
    public static BaseLabel cancelLabel;
    public static float CANCEL_SHOW_OFFSET = -10000.0f;
    public static Scene scene;
    public static boolean isConnectingInput = false;
    public static boolean isConnectingData = false;
    public static boolean isConnectingDataInput = false;
    public static ImageView NodeAddImage;
    public static double mouseX;
    public static double mouseY;
    public static List<String> paths = List.of(
//            "F:\\McServer\\Plugin\\dir\\src",
            "F:\\McServer\\Plugin\\SpigotPluginCreaterTool\\src\\main\\java\\org\\bxwbb\\spigotplugincreatertool"
    );
    public static NodeEditor openNodeEditor;

    private static boolean isConnectingLine = false;

    public static void ini() {
        for (String path : paths) {
            SearchBox.records.addAll(JavaSourceScannerFixed.scanJavaSources(path));
        }
    }

    @Override
    public void start(Stage primaryStage) {

        // ==================== 1. 创建无边框启动页 ====================
        // 启动页布局（垂直排列图片和文字）
        VBox splashLayout = new VBox(20);
        splashLayout.setStyle("-fx-background-color: #2c3e50;"); // 深色背景
        splashLayout.setAlignment(Pos.CENTER);

        // 添加启动页图片（可选）
        Image splashImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/StartBackGround.png") // 替换为你的图片路径
        ));
        ImageView imageView = new ImageView(splashImage);
        imageView.setFitWidth(200); // 调整图片大小
        imageView.setPreserveRatio(true);

        // 添加加载文字
        Label loadingLabel = new Label("加载中...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        splashLayout.getChildren().addAll(imageView, loadingLabel);

        // 创建启动页场景
        Scene splashScene = new Scene(splashLayout, 400, 300); // 启动页大小
        primaryStage.setScene(splashScene);
        primaryStage.initStyle(StageStyle.UNDECORATED); // 无边框
        primaryStage.centerOnScreen(); // 居中显示
        primaryStage.show();

        CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                ini();
                System.out.println("初始化完成，耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                Platform.runLater(() -> {
                    primaryStage.close();
                    openMainWindow();
                });
            } finally {
                // 处理初始化失败
                Platform.runLater(() -> {
                    Label errorLabel = new Label("初始化失败");
                    errorLabel.setStyle("-fx-text-fill: red");
                    ((VBox) primaryStage.getScene().getRoot()).getChildren().set(0, errorLabel);
                });
            }
        });

    }

    public static void openMainWindow() {
        Stage mainStage = new Stage();

        Group root = new Group();

        scene = new Scene(root, 1200, 800);

        MinWindow minWindow = new MinWindow(0.0f, 10.0f, 1200.0f, 780.0f, root, MinWindowType.MinWindowTypeEnum.NodeEditorType);

        NodeAddImage = new ImageView(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Add.png"
        ))));
        NodeAddImage.setFitWidth(30.0f);
        NodeAddImage.setFitHeight(30.0f);
        NodeAddImage.setVisible(false);
        root.getChildren().add(NodeAddImage);

        scene.setOnMouseClicked(minWindow::onMouseSceneClick);

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            MinWindow r = minWindow;
            while (r != null) {
                if (MinWindow.isPointInRectangle(r.startX, r.startY, r.endX, r.endY, event.getX(), event.getY())) {
                    cancelWindow = r;
                    return;
                }
                r = r.minWindows;
            }
        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            // 节点编辑器
            if (cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                NodeEditor nodeEditor = (NodeEditor) cancelWindow.minWindowType;
                if (event.getButton() == MouseButton.PRIMARY) {
                    NodeAddImage.setX(event.getX() + 15);
                    NodeAddImage.setY(event.getY() - 35);
                    if (nodeEditor.userConnectingLine) {
                        NodeAddImage.setVisible(true);
                        isConnectingLine = true;
                        if (isConnectingData) {
                            if (isConnectingDataInput) {
                                nodeEditor.connectingLine.resetPos(event.getX(), event.getY());
                            } else {
                                nodeEditor.connectingLine.resetSize(event.getX(), event.getY());
                            }
                            for (Node node : nodeEditor.nodes) {
                                if (MinWindow.isPointInRectangle(node.startX, node.startY, node.endX, node.endY, event.getX(), event.getY())) {
                                    if (isConnectingDataInput) {
                                        if (node.rightCardNodes.isEmpty()) continue;
                                        if (MinWindow.isPointInRectangle(
                                                node.startX, node.startY, node.endX, node.endY,
                                                nodeEditor.connectingLine.bezierCurve.endX,
                                                nodeEditor.connectingLine.bezierCurve.endY
                                        )) continue;
                                        if (nodeEditor.focusNode == node) continue;
                                        for (Node.NodeCardNode nodeCardNode : node.rightCardNodes) {
                                            if (MinWindow.isPointInRectangle(
                                                    nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY(),
                                                    nodeCardNode.point.getChildren().getFirst().getLayoutX() - 114514, nodeCardNode.point.getChildren().getFirst().getLayoutY() + nodeCardNode.edit.getHeight(),
                                                    event.getX(), event.getY()
                                            )) {
                                                nodeEditor.connectingLine.resetPos(nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY());
                                            }
                                        }
                                    } else {
                                        if (node.leftCardNodes.isEmpty()) continue;
                                        if (MinWindow.isPointInRectangle(
                                                node.startX, node.startY, node.endX, node.endY,
                                                nodeEditor.connectingLine.bezierCurve.startX,
                                                nodeEditor.connectingLine.bezierCurve.startY
                                        )) continue;
                                        if (nodeEditor.focusNode == node) continue;
                                        for (Node.NodeCardNode nodeCardNode : node.leftCardNodes) {
                                            if (MinWindow.isPointInRectangle(
                                                    nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY(),
                                                    nodeCardNode.point.getChildren().getFirst().getLayoutX() + 114514, nodeCardNode.point.getChildren().getFirst().getLayoutY() + nodeCardNode.edit.getHeight(),
                                                    event.getX(), event.getY()
                                            )) {
                                                nodeEditor.connectingLine.resetSize(nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY());
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        } else {
                            if (isConnectingInput) {
                                nodeEditor.connectingLine.resetPos(event.getX(), event.getY());
                            } else {
                                nodeEditor.connectingLine.resetSize(event.getX(), event.getY());
                            }
                            for (Node node : nodeEditor.nodes) {
                                if (MinWindow.isPointInRectangle(node.startX, node.startY, node.endX, node.endY, event.getX(), event.getY())) {
                                    if (isConnectingInput) {
                                        if (node.rightCardNodes.isEmpty()) continue;
                                        if (MinWindow.isPointInRectangle(
                                                node.startX, node.startY, node.endX, node.endY,
                                                nodeEditor.connectingLine.bezierCurve.endX,
                                                nodeEditor.connectingLine.bezierCurve.endY
                                        )) continue;
                                        nodeEditor.connectingLine.resetPos(node.endX - 15, node.startY + 15);
                                    } else {
                                        if (node.leftCardNodes.isEmpty()) continue;
                                        if (MinWindow.isPointInRectangle(
                                                node.startX, node.startY, node.endX, node.endY,
                                                nodeEditor.connectingLine.bezierCurve.startX,
                                                nodeEditor.connectingLine.bezierCurve.startY
                                        )) continue;
                                        nodeEditor.connectingLine.resetSize(node.startX + 10, node.startY + 15);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            } else {
                if (isConnectingLine) {
                    MinWindow r = minWindow;
                    while (r != null) {
                        if (r.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                            NodeEditor nodeEditor = (NodeEditor) r.minWindowType;
                            if (nodeEditor.userConnectingLine) {
                                nodeEditor.userConnectingLine = false;
                                nodeEditor.connectingLine.delete();
                                nodeEditor.connectingLine = null;
                            }
                        }
                        r = r.minWindows;
                    }
                }
            }
        });

        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isConnectingLine) {
                // 节点编辑器
                if (cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                    NodeEditor nodeEditor = (NodeEditor) cancelWindow.minWindowType;
                    if (event.getButton() == MouseButton.PRIMARY) {
                        NodeAddImage.setX(event.getX() + 15);
                        NodeAddImage.setY(event.getY() - 35);
                        if (nodeEditor.userConnectingLine) {
                            NodeAddImage.setVisible(true);
                            isConnectingLine = true;
                            if (isConnectingData) {
                                for (Node node : nodeEditor.nodes) {
                                    if (MinWindow.isPointInRectangle(node.startX, node.startY, node.endX, node.endY, event.getX(), event.getY())) {
                                        if (isConnectingDataInput) {
                                            if (node.rightCardNodes.isEmpty()) continue;
                                            if (MinWindow.isPointInRectangle(
                                                    node.startX, node.startY, node.endX, node.endY,
                                                    nodeEditor.connectingLine.bezierCurve.endX,
                                                    nodeEditor.connectingLine.bezierCurve.endY
                                            )) continue;
                                            if (nodeEditor.focusNode == node) continue;
                                            int index = 0;
                                            for (Node.NodeCardNode nodeCardNode : node.rightCardNodes) {
                                                if (MinWindow.isPointInRectangle(
                                                        nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY(),
                                                        nodeCardNode.point.getChildren().getFirst().getLayoutX() - 114514, nodeCardNode.point.getChildren().getFirst().getLayoutY() + nodeCardNode.edit.getHeight(),
                                                        event.getX(), event.getY()
                                                )) {

                                                    ConnectingLine cl = (ConnectingLine) nodeEditor.connectingLine.createNew();
                                                    nodeEditor.addConnectingDataLine(cl, (Color) ((Circle) nodeCardNode.point.getChildren().getFirst()).getFill(), (Color) ((Circle) nodeEditor.focusCardNode.point.getChildren().getFirst()).getFill());
                                                    int r = 0;
                                                    for (Node.NodeCardNode cardNode : nodeEditor.focusNode.rightCardNodes) {
                                                        if (cardNode == nodeEditor.focusCardNode) break;
                                                        r++;
                                                    }
                                                    if (nodeEditor.focusNode.leftDataLines.get(r - 1) != null) {
                                                        nodeEditor.removeConnectingDataLine(nodeEditor.focusNode.leftDataLines.get(r - 1));
                                                    }
                                                    nodeEditor.focusNode.leftDataLines.set(r - 1, cl);
                                                    nodeEditor.focusNode.leftCardNodes.get(r - 1).edit.setVisible(false);
                                                    nodeEditor.focusNode.leftDataPointList.set(nodeEditor.focusNode.leftDataPointList.size() == 1 ? r - 1 : r, new Node.DataLinePoint(nodeEditor.nodesCtr.get(nodeEditor.nodes.indexOf(node)), index));
                                                    node.rightDataLines.get(index).add(cl);

                                                }
                                                index++;
                                            }
                                        } else {
                                            if (node.leftCardNodes.isEmpty()) continue;
                                            if (MinWindow.isPointInRectangle(
                                                    node.startX, node.startY, node.endX, node.endY,
                                                    nodeEditor.connectingLine.bezierCurve.startX,
                                                    nodeEditor.connectingLine.bezierCurve.startY
                                            )) continue;
                                            if (nodeEditor.focusNode == node) continue;
                                            int index = 0;
                                            for (Node.NodeCardNode nodeCardNode : node.leftCardNodes) {
                                                if (MinWindow.isPointInRectangle(
                                                        nodeCardNode.point.getChildren().getFirst().getLayoutX(), nodeCardNode.point.getChildren().getFirst().getLayoutY(),
                                                        nodeCardNode.point.getChildren().getFirst().getLayoutX() + 114514, nodeCardNode.point.getChildren().getFirst().getLayoutY() + nodeCardNode.edit.getHeight(),
                                                        event.getX(), event.getY()
                                                )) {

                                                    ConnectingLine cl = (ConnectingLine) nodeEditor.connectingLine.createNew();
                                                    nodeEditor.addConnectingDataLine(cl, (Color) ((Circle) nodeEditor.focusCardNode.point.getChildren().getFirst()).getFill(), (Color) ((Circle) nodeCardNode.point.getChildren().getFirst()).getFill());
                                                    int r = 0;
                                                    for (Node.NodeCardNode cardNode : nodeEditor.focusNode.rightCardNodes) {
                                                        if (cardNode == nodeEditor.focusCardNode) break;
                                                        r++;
                                                    }
                                                    nodeEditor.focusNode.rightDataLines.get(r).add(cl);
                                                    if (node.leftDataLines.get(index) != null)
                                                        nodeEditor.removeConnectingDataLine(node.leftDataLines.get(index));
                                                    node.leftDataLines.set(index, cl);
                                                    node.leftCardNodes.get(index).edit.setVisible(false);
                                                    NodeCtr nc = nodeEditor.nodesCtr.get(nodeEditor.nodes.indexOf(nodeEditor.focusNode));
                                                    node.leftDataPointList.set(index, new Node.DataLinePoint(nc, r));

                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }
                                if (isConnectingDataInput) {
                                    nodeEditor.connectingLine.resetPos(event.getX(), event.getY());
                                } else {
                                    nodeEditor.connectingLine.resetSize(event.getX(), event.getY());
                                }
                            } else {
                                for (Node node : nodeEditor.nodes) {
                                    if (MinWindow.isPointInRectangle(node.startX, node.startY, node.endX, node.endY, event.getX(), event.getY())) {
                                        if (isConnectingInput) {
                                            if (node.rightCardNodes.isEmpty()) continue;
                                            if (MinWindow.isPointInRectangle(
                                                    node.startX, node.startY, node.endX, node.endY,
                                                    nodeEditor.connectingLine.bezierCurve.endX,
                                                    nodeEditor.connectingLine.bezierCurve.endY
                                            )) continue;
                                            ConnectingLine cl = (ConnectingLine) nodeEditor.connectingLine.createNew();
                                            if (node.rightRunLine != null)
                                                nodeEditor.removeConnectingLine(node.rightRunLine);
                                            node.rightRunLine = cl;
                                            if (nodeEditor.focusNode.leftRunLine != null)
                                                nodeEditor.removeConnectingLine(nodeEditor.focusNode.leftRunLine);
                                            nodeEditor.focusNode.leftRunLine = cl;
                                            node.nextNode = nodeEditor.focusNode;
                                            nodeEditor.focusNode.lastNode = node;
                                            nodeEditor.addConnectingLine(cl);
                                            nodeEditor.testConnectingLine(nodeEditor.focusNode);
                                        } else {
                                            if (node.leftCardNodes.isEmpty()) continue;
                                            if (MinWindow.isPointInRectangle(
                                                    node.startX, node.startY, node.endX, node.endY,
                                                    nodeEditor.connectingLine.bezierCurve.startX,
                                                    nodeEditor.connectingLine.bezierCurve.startY
                                            )) continue;
                                            ConnectingLine cl = (ConnectingLine) nodeEditor.connectingLine.createNew();
                                            if (node.leftRunLine != null)
                                                nodeEditor.removeConnectingLine(node.leftRunLine);
                                            node.leftRunLine = cl;
                                            if (nodeEditor.focusNode.rightRunLine != null)
                                                nodeEditor.removeConnectingLine(nodeEditor.focusNode.rightRunLine);
                                            nodeEditor.focusNode.rightRunLine = cl;
                                            nodeEditor.focusNode.nextNode = node;
                                            node.lastNode = nodeEditor.focusNode;
                                            nodeEditor.addConnectingLine(cl);
                                            nodeEditor.testConnectingLine(nodeEditor.focusNode);
                                        }
                                        break;
                                    }
                                }
                                if (isConnectingInput) {
                                    nodeEditor.connectingLine.resetPos(event.getX(), event.getY());
                                } else {
                                    nodeEditor.connectingLine.resetSize(event.getX(), event.getY());
                                }
                            }
                        }
                    }
                }
                MinWindow r = minWindow;
                while (r != null) {
                    if (r.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                        NodeEditor nodeEditor = (NodeEditor) r.minWindowType;
                        if (nodeEditor.userConnectingLine) {
                            nodeEditor.userConnectingLine = false;
                            NodeAddImage.setVisible(false);
                            nodeEditor.connectingLine.delete();
                            nodeEditor.connectingLine = null;
                            isConnectingData = false;
                        }
                    }
                    r = r.minWindows;
                }
            }
        });

        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.A) {
                if (cancelWindow == null) return;
                if (cancelWindow.minWindowType.getType().equals(MinWindowType.MinWindowTypeEnum.NodeEditorType)) {
                    NodeEditor nodeEditor = (NodeEditor) cancelWindow.minWindowType;
                    if (cancelLabel != null) cancelLabel.delete();
                    cancelLabel = new SearchBox(mouseX, mouseY, scene.getWidth(), scene.getHeight());
                    cancelLabel.addTo(root);
                    openNodeEditor = nodeEditor;
                }
            }
        });

        scene.setFill(BG_COLOR);
        mainStage.setTitle("我的世界spigot插件图形化开发工具 -BY BXWBB bilibili:1814140675 QQ:3754934636");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String toHexString(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);

        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static final synchronized void fuckThisTool(byte a, short b, int c, long d, float e, double f, char g, boolean h, String i) {
        return;
    }

}