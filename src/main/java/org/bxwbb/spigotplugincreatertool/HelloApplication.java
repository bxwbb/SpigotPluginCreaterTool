package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.MinWindows.NodeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ALL")
public class HelloApplication extends Application {

    static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    // 背景颜色
    public static Color BG_COLOR = Color.color(0.1, 0.1, 0.1);
    // 次背景颜色
    public static Color UNSELECTED_MENU_COLOR = Color.color(0.3, 0.3, 0.3);
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
    // 未启用颜色
    public static Color DISABLED_COLOR = Color.color(0.5, 0.3, 0.3);
    // 菜单颜色
//    public static Color MENU_COLOR = Color.color(0.3, 0.3, 0.3);
    public static Color MENU_COLOR = Color.color(0.2, 0.2, 0.2);
    // 字体颜色
    public static Color FONT_COLOR = Color.color(0.7, 0.7, 0.7);
    // 字体未选中颜色
    public static Color UNSELECTED_FONT_COLOR = Color.color(0.5, 0.5, 0.5);
    // 字体
    public static Font TEXT_FONT = Font.font("Arial", FontWeight.NORMAL, 12);

    public static Scene scene;

    public static List<String> paths = List.of(
//            "F:\\McServer\\Plugin\\dir\\src",
            "F:\\McServer\\Plugin\\SpigotPluginCreaterTool\\src\\main\\java\\org\\bxwbb\\spigotplugincreatertool"
    );
    public static Stage primaryStage;

    private static final Button textField = new Button();

    public static void ini() {
        for (String path : paths) {
            logger.info("加载第1/1项-扫描类");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("工具开始启动并加载...");
        HelloApplication.primaryStage = primaryStage;

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
                logger.info("初始化完成，耗时: " + (System.currentTimeMillis() - startTime) + "ms");
                Platform.runLater(() -> {
                    primaryStage.close();
                    try {
                        openMainWindow();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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

    public static void openMainWindow() throws ClassNotFoundException {
        Stage mainStage = new Stage();

        Group root = new Group();

        scene = new Scene(root, 1200, 800);

        NodeEditor nodeEditor = new NodeEditor();
        nodeEditor.init();
        nodeEditor.resetPos(20, 20);
        nodeEditor.resetSize(1160, 760);
        nodeEditor.addTo(root);

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

    public static Color stringToColor(String input) {
        if (input == null) {
            input = ""; // 处理null值，统一转换为空字符串的颜色
        }

        try {
            // 使用SHA-1哈希算法获取字符串的哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 从哈希字节数组中提取RGB三个分量的值
            // 为了让颜色分布更均匀，我们间隔地取字节
            int red = (hashBytes[0] & 0xFF) % 256;
            int green = (hashBytes[5] & 0xFF) % 256;
            int blue = (hashBytes[10] & 0xFF) % 256;

            // 将RGB值转换为0-1之间的浮点数（JavaFX Color需要的格式）
            double r = red / 255.0;
            double g = green / 255.0;
            double b = blue / 255.0;

            // 返回不透明的颜色（alpha值为1.0）
            return new Color(r, g, b, 1.0);

        } catch (NoSuchAlgorithmException e) {
            // 理论上SHA-1算法在所有Java平台都存在，所以这个异常几乎不会发生
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    /**
     * 模拟Blender节点编辑器的缩放效果
     * 围绕指定原点(通常是鼠标位置)进行缩放，保持视觉连续性
     *
     * @param node     要缩放的节点容器
     * @param newScale 新的缩放值
     * @param oldScale 旧的缩放值
     * @param pivotX   缩放原点X坐标(屏幕坐标)
     * @param pivotY   缩放原点Y坐标(屏幕坐标)
     */
    public static void scaleNodeLikeBlender(javafx.scene.Node node, double newScale, double oldScale, double pivotX, double pivotY) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        if (oldScale <= 0 || newScale <= 0) {
            throw new IllegalArgumentException("Scale values must be positive");
        }
        if (Math.abs(oldScale - newScale) < 0.001) {
            return; // 缩放值变化极小，无需处理
        }

        // 计算缩放比例
        double scaleFactor = newScale / oldScale;

        // 获取节点当前的位置和缩放
        double currentTranslateX = node.getTranslateX();
        double currentTranslateY = node.getTranslateY();

        // 核心计算：调整位置以保持缩放原点视觉上的稳定
        // 这模拟了Blender中围绕鼠标指针缩放的效果
        double newTranslateX = pivotX - scaleFactor * (pivotX - currentTranslateX);
        double newTranslateY = pivotY - scaleFactor * (pivotY - currentTranslateY);

        // 应用新的位置和缩放
        node.setTranslateX(newTranslateX);
        node.setTranslateY(newTranslateY);
        node.setScaleX(newScale);
        node.setScaleY(newScale);
    }

    /**
     * 带动画过渡的Blender风格缩放
     *
     * @param node        要缩放的节点容器
     * @param targetScale 目标缩放值
     * @param pivotX      缩放原点X坐标
     * @param pivotY      缩放原点Y坐标
     * @param duration    动画持续时间(毫秒)
     */
    public static void scaleNodeLikeBlenderAnimated(javafx.scene.Node node, double targetScale, double pivotX, double pivotY, int duration) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        double startScale = node.getScaleX();
        long startTime = System.currentTimeMillis();

        // 创建动画循环
        new Thread(() -> {
            while (System.currentTimeMillis() - startTime < duration) {
                long currentTime = System.currentTimeMillis();
                double progress = (currentTime - startTime) / (double) duration;

                // 使用缓动函数使动画更自然
                double easedProgress = progress * progress * (3 - 2 * progress);
                double currentScale = startScale + (targetScale - startScale) * easedProgress;

                // 在JavaFX应用线程中更新UI
                Platform.runLater(() ->
                        scaleNodeLikeBlender(node, currentScale, startScale, pivotX, pivotY)
                );

                try {
                    Thread.sleep(15); // 约60fps
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // 确保最终状态正确
            Platform.runLater(() ->
                    scaleNodeLikeBlender(node, targetScale, startScale, pivotX, pivotY)
            );
        }).start();
    }

    public static void scaleTo(javafx.scene.Node container, double targetScale, double pivotX, double pivotY) {
        // 参数校验
        if (container == null) {
            throw new IllegalArgumentException("容器不能为空");
        }
        if (targetScale <= 0) {
            throw new IllegalArgumentException("缩放倍数必须为正数");
        }

        // 获取当前缩放值（作为旧缩放值）
        double currentScale = container.getScaleX();

        // 如果目标缩放值与当前值相同，则无需处理
        if (Math.abs(targetScale - currentScale) < 0.001) {
            return;
        }

        // 计算缩放比例（目标/当前）
        double scaleRatio = targetScale / currentScale;

        // 计算新位置，保持缩放原点视觉位置不变
        double newLayoutX = pivotX - scaleRatio * (pivotX - container.getLayoutX());
        double newLayoutY = pivotY - scaleRatio * (pivotY - container.getLayoutY());

        // 应用新的位置和缩放
        container.setLayoutX(newLayoutX);
        container.setLayoutY(newLayoutY);
        container.setScaleX(targetScale);
        container.setScaleY(targetScale);
    }

    // 失去焦点
    public static void loseFocus() {
        HelloApplication.textField.requestFocus();
    }

    @Override
    public void init() throws Exception {
        super.init();
        logger.info("执行初始化...");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("执行停止...");
    }

}