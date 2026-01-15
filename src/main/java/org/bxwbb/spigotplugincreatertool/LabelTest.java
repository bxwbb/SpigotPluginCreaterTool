package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeCtrls.Java;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeFramework;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.TokenFixer;

import java.util.List;

public class LabelTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建主面板
        Group root = new Group();
        root.setStyle("-fx-background-color: #2b2b2b;");
        Scene scene = new Scene(root, 1200, 1000);

        CodeFramework codeFramework = new CodeFramework(100, 100, 900, 800, new TokenFixer(), List.of(new Java()));
        codeFramework.addTo(root);



        // 设置舞台
        primaryStage.setTitle("代码编辑框显示测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
