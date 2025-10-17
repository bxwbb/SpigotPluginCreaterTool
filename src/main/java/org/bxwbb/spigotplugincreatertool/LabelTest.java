package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeFramework;

public class LabelTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建主面板
        Group root = new Group();
        root.setStyle("-fx-background-color: #2b2b2b;");
        Scene scene = new Scene(root, 1000, 800);

        CodeFramework codeFramework = new CodeFramework(100, 100, 700, 600);
        codeFramework.addTo(root);

        // 设置舞台
        primaryStage.setTitle("贝塞尔曲线独立信号动画测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
