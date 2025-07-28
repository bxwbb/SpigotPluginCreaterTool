package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.windowLabel.*;

import java.util.List;

public class LabelTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Stage mainStage = new Stage();

        Group root = new Group();

        Scene scene = new Scene(root, 1200, 800);
        HelloApplication.scene = scene;

        boolean isDisable = true;

        BaseLabel baseLabel = new Button(100, 100, 400, 120, true);
        baseLabel.addTo(root);
        baseLabel.setVisible(isDisable);

        baseLabel = baseLabel.createNew();
        baseLabel.resetPos(100, 130);
        baseLabel.resetSize(300, 300);
        baseLabel.addTo(root);
        baseLabel.setVisible(isDisable);

        baseLabel = baseLabel.createNew();
        baseLabel.resetPos(100, 440);
        baseLabel.resetSize(300, 20);
        baseLabel.addTo(root);
        baseLabel.setVisible(!isDisable);

        baseLabel = baseLabel.createNew();
        baseLabel.resetPos(100, 470);
        baseLabel.resetSize(50, 100);
        baseLabel.addTo(root);
        baseLabel.setVisible(isDisable);

        scene.setFill(HelloApplication.BG_COLOR);
        mainStage.setTitle("控件测试实例");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
