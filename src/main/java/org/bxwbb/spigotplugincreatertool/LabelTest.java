package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.bxwbb.spigotplugincreatertool.windowLabel.AutoBezierCurve;

public class LabelTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建主面板
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #2b2b2b;");
        Scene scene = new Scene(root, 800, 600);

        // 创建测试曲线
        AutoBezierCurve curve = new AutoBezierCurve(100, 300, 700, 300);
        curve.setGradientColors(Color.rgb(50, 50, 100), Color.rgb(100, 50, 50));
        curve.setLineWidth(3.0);
        curve.setControlRatio(0.5);
        curve.setParticleParameters(1.0, 0.15, 0.3);

        // 添加到面板
        root.getChildren().add(curve);

        // 添加鼠标点击事件，每次点击启动一个新的信号动画
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            private int clickCount = 0;

            @Override
            public void handle(MouseEvent event) {
                clickCount++;
                // 每次点击使用不同的颜色
                Color[] colors = {
                        Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
                        Color.CYAN, Color.MAGENTA, Color.YELLOW
                };
                Color color = colors[clickCount % colors.length];

                // 启动一个新的信号动画，持续2秒
                curve.startSignalAnimation(color, 2.0);
            }
        });

        // 设置舞台
        primaryStage.setTitle("贝塞尔曲线独立信号动画测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
