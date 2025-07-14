package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    // 背景颜色
    public static Color BG_COLOR = Color.color(0.1, 0.1, 0.1);
    // 用户界面圆滑度
    public static float ROUNDNESS = 10.0f;
    // 边框颜色
    public static Color BORDER_COLOR = Color.color(0.4, 0.4, 0.4);
    // 未选中颜色
    public static Color UNSELECTED_COLOR = Color.color(0.2, 0.2, 0.2);
    // 悬停颜色
    public static Color HOVER_COLOR = Color.color(0.5, 0.5, 0.5);
    // 选中颜色
    public static Color SELECTED_COLOR = Color.rgb(65, 112, 210);
    // 菜单颜色
    public static Color MENU_COLOR = Color.color(0.3, 0.3, 0.3);
    // 字体颜色
    public static Color FONT_COLOR = Color.color(0.7, 0.7, 0.7);
    // 字体
    public static Font TEXT_FONT = Font.font("Arial", FontWeight.NORMAL, 12);
    // 取消显示位移量
    public static float CANCEL_SHOW_OFFSET = -10000.0f;

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        MinWindow minWindow = new MinWindow(0.0f, 10.0f, 1200.0f, 780.0f, root, MinWindowType.MinWindowTypeEnum.NodeEditorType);

        Scene scene = new Scene(root, 1200, 800);

        scene.setOnMouseClicked(minWindow::onMouseSceneClick);

        scene.setFill(BG_COLOR);
        primaryStage.setTitle("我的世界spigot插件图形化开发工具");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}