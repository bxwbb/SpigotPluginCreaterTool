package org.bxwbb.spigotplugincreatertool;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bxwbb.spigotplugincreatertool.BxWinLab.BaseLabel;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.BxWinLab.VerticalLayout;
import org.bxwbb.spigotplugincreatertool.Window.FileManager;
import org.bxwbb.spigotplugincreatertool.Window.StartPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ALL")
public class HelloApplication extends Application {

    static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    public static Scene scene;

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
        logger.info("工具开始启动并加载...");
        openMainWindow();
    }

    public static void openMainWindow() throws ClassNotFoundException {
        Stage mainStage = new Stage();

        Group root = new Group();

        scene = new Scene(root, 1200, 800);
        scene.setFill(ColorSetting.PROGRAM_BASE_COLOR);

        BaseLabel.setObjectScene(scene);

        VerticalLayout baseVerticalLayout = new VerticalLayout(0, 0, 1200, 800);
        baseVerticalLayout.setFillExtensionType(FillExtensionType.CENTER);
        baseVerticalLayout.setSpacing(1);

        baseVerticalLayout.addChild(ProgramPage.getProgramTopStage());
        baseVerticalLayout.addChild(new StartPage());
//        baseVerticalLayout.addChild(new FileManager());

        baseVerticalLayout.update();

        mainStage.setTitle("我的世界spigot插件图形化开发工具 -BY BXWBB bilibili:1814140675 QQ:3754934636");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
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