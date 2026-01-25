package org.bxwbb.spigotplugincreatertool.WindowLabel.Test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bxwbb.spigotplugincreatertool.WindowLabel.*;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

import java.util.Random;

public class LabelTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建主面板
        Group root = new Group();
        root.setStyle("-fx-background-color: #2b2b2b;");
        Scene scene = new Scene(root, 1200, 1000);
        BaseLabel.setStageRoot(root);

        // 测试 HorizontalLayout 控件
        testHorizontalLayout(root);

        // 设置舞台
        primaryStage.setTitle("WindowLabel 组件全面测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 测试 HorizontalLayout 控件的功能
     */
    private void testHorizontalLayout(Group root) {

        TextButton addTextButton = new TextButton(100, 100, 300, 200, "添加按钮到菜单中");
        addTextButton.addTo(root);

        ImageButton t = new ImageButton(500, 100, 700, 200, Image.defaultImage);
        t.addTo(root);

        VerticalMenu verticalMenu = new VerticalMenu(100, 400, 300, 700);
        verticalMenu.addTo(root);
        addTextButton.setButtonClicked(event -> {
            TextButton textButton = new TextButton(0, 0, 10, 20, String.valueOf(verticalMenu.getLabelCount()));
            textButton.setDirectional(EightDirection.UP_LEFT);
            textButton.autoSizeToText();
            verticalMenu.addLabel(textButton);
        });
        t.setButtonClicked(event -> {
            for (int i = 0; i < 10; i++) {
                TextButton textButton = new TextButton(0, 0, 10, 20, String.valueOf(verticalMenu.getLabelCount()));
                textButton.setDirectional(EightDirection.UP_LEFT);
                textButton.autoSizeToText();
                verticalMenu.addLabel(textButton);
            }
        });

        DropDownMenu dropDownMenu = new DropDownMenu(500, 400, 600, 420,
                new Text(
                        500,
                        400,
                        700,
                        600,
                        "测试下拉菜单"
                )
        );
        dropDownMenu.addTo(root);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
