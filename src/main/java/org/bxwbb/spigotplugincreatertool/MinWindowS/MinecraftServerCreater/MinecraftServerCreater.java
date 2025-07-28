package org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater;

import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.FileUtils;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindow;
import org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.Server.MinecraftServerManager;
import org.bxwbb.spigotplugincreatertool.MinWindowType;
import org.bxwbb.spigotplugincreatertool.windowLabel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MinecraftServerCreater extends MinWindowType {

    static final Logger logger = LoggerFactory.getLogger(MinecraftServerCreater.class);

    public static Color BG_COLOR = Color.color(0.25, 0.25, 0.25);
    // 下载失败最大重试次数
    public static int MAX_RETRY_COUNT = 10;

    public TextButton selectServerButton;
    public MinecraftServerSearchBox searchBox;
    public Button createServerButton;
    public Button openServerButton;
    public TextButton serverTypeButton;
    public TextButton setServerVersionButton;
    public MinecraftServerVersionSearchBox serverVersionSearchBox;

    private final List<BaseLabel> labels = new ArrayList<>();
    private final List<Node> nodes = new ArrayList<>();

    public MinecraftServerCreater(Group root, Group base, Group topBase, Rectangle background) {
        super(root, base, topBase, background);
        init();
    }

    @Override
    public void init() {
        this.title = "我的世界服务器编辑器";
        this.background.setFill(BG_COLOR);
        this.selectServerButton = new TextButton(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 200, this.startY + MinWindow.PADDING + 35.0, "选择服务器", true);
        selectServerButton.addTo(this.topBase);
        this.selectServerButton.background.setOnMousePressed(event -> {
            if (this.searchBox != null) this.searchBox.delete();
            this.searchBox = new MinecraftServerSearchBox(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 30.0, 1000, 600);
            this.searchBox.addTo(this.topBase);
        });
        this.createServerButton = new Button(this.startX + this.getEditorNameWidth() + 205, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 225, this.startY + MinWindow.PADDING + 35.0, false);
        this.createServerButton.resetImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/AddServer.png"
                )))
        );
        this.createServerButton.background.setOnMousePressed(event -> {
            for (BaseLabel label : labels) {
                label.delete();
            }
            this.nodes.clear();
            labels.clear();
            this.base.getChildren().removeIf(child -> !child.equals(this.background));
            this.base.getChildren().add(getAddServerGroup());
        });
        this.createServerButton.addTo(this.topBase);
        this.openServerButton = new Button(this.startX + this.getEditorNameWidth() + 230, this.startY + MinWindow.PADDING + 15.0, this.startX + this.getEditorNameWidth() + 250, this.startY + MinWindow.PADDING + 35.0, false);
        this.openServerButton.resetImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/OpenServer.png"
                )))
        );
        this.openServerButton.addTo(this.topBase);
    }

    @Override
    public void resetPos(float x, float y) {
        double dx = x;
        double dy = y;
        super.resetPosSuper(x, y);
        dx -= this.startX;
        dy -= this.startY;
        this.selectServerButton.resetPos(this.startX + this.getEditorNameWidth() + 30, this.startY + MinWindow.PADDING + 15.0);
        this.createServerButton.resetPos(this.startX + this.getEditorNameWidth() + 205, this.startY + MinWindow.PADDING + 15.0);
        this.openServerButton.resetPos(this.startX + this.getEditorNameWidth() + 230, this.startY + MinWindow.PADDING + 15.0);
        for (BaseLabel label : this.labels) {
            label.resetPos(label.startX + dx, label.startY + dy);
        }
        for (Node node : this.nodes) {
            node.setLayoutX(node.getLayoutX() + dx);
            node.setLayoutY(node.getLayoutY() + dy);
        }
    }

    @Override
    public void resetSize(float width, float height) {
        super.resetSizeSuper(width, height);
    }

    @Override
    public void delete() {
        this.selectServerButton.delete();
        this.createServerButton.delete();
        this.openServerButton.delete();
        for (BaseLabel label : this.labels) {
            label.delete();
        }
        d(this.base, this.nodes);
        this.nodes.clear();
    }

    private void d(Group group, List<Node> nodes) {
        List<Node> r = new ArrayList<>(group.getChildren());
        for (Node child : r) {
            if (child instanceof Group) {
                d((Group) child, nodes);
            }
            if (nodes.contains(child)) {
                group.getChildren().remove(child);
            }
        }
    }

//    private void clearScreen() {
//        for (BaseLabel label : this.labels) {
//            label.delete();
//        }
//        d(this.base, this.nodes);
//    }

    @Override
    public MinWindowTypeEnum getType() {
        return MinWindowTypeEnum.MinecraftServerCreater;
    }

    private Group getAddServerGroup() {
        Group group = new Group();
        Text text = new Text("服务端类型");
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 50);
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        this.nodes.add(text);
        group.getChildren().add(text);
        TextButton setServerTypeButton = new TextButton(this.startX + 30, this.startY + MinWindow.PADDING + 60, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 80, "spigot", true);
        setServerTypeButton.resetImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Spigot.png"
                )))
        );
        setServerTypeButton.background.setOnMousePressed(event -> {
            for (BaseLabel label : this.labels) {
                if (label instanceof MinecraftServerTypeSearchBox) {
                    label.delete();
                }
            }
            MinecraftServerTypeSearchBox minecraftServerTypeSearchBox = new MinecraftServerTypeSearchBox(this.startX + 30, this.startY + MinWindow.PADDING + 80, 350, 470, this);
            this.labels.add(minecraftServerTypeSearchBox);
            minecraftServerTypeSearchBox.addTo(group);
        });
        this.serverTypeButton = setServerTypeButton;
        this.labels.add(setServerTypeButton);
        setServerTypeButton.addTo(group);
        text = new Text("设置服务器项目名称");
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 95);
        this.nodes.add(text);
        group.getChildren().add(text);
        InputBox inputBox = new InputBox(this.startX + 30, this.startY + MinWindow.PADDING + 100, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 120, "服务器名称");
        inputBox.addTo(group);
        this.labels.add(inputBox);
        text = new Text("设置服务器项目路径");
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 135);
        this.nodes.add(text);
        group.getChildren().add(text);
        PathInputBox pathInputBox = new PathInputBox(this.startX + 30, this.startY + MinWindow.PADDING + 145, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 165);
        pathInputBox.addTo(group);
        this.labels.add(pathInputBox);
        text = new Text("服务端版本");
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 180);
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        this.nodes.add(text);
        group.getChildren().add(text);
        TextButton setServerVersionButton = new TextButton(this.startX + 30, this.startY + MinWindow.PADDING + 190, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 210, "-", true);
        setServerVersionButton.resetImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                )))
        );
        setServerVersionButton.background.setOnMousePressed(event -> {
            for (BaseLabel label : this.labels) {
                if (label instanceof MinecraftServerVersionSearchBox) {
                    label.delete();
                }
            }
            MinecraftServerVersionSearchBox minecraftServerTypeSearchBox = new MinecraftServerVersionSearchBox(this.startX + 30, this.startY + MinWindow.PADDING + 215, 500, 600, this);
            serverVersionSearchBox = minecraftServerTypeSearchBox;
            this.labels.add(minecraftServerTypeSearchBox);
            minecraftServerTypeSearchBox.addTo(group);
        });
        this.setServerVersionButton = setServerVersionButton;
        this.labels.add(setServerVersionButton);
        setServerVersionButton.addTo(group);
        text = new Text("服务端内存使用");
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 225);
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        this.nodes.add(text);
        group.getChildren().add(text);
        SliderDouble maxMemorySlider = new SliderDouble(this.startX + 30, this.startY + MinWindow.PADDING + 230, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 250, 4, "服务端内存使用最大值(GB)", null, true, false, 0, 0, 1);
        this.labels.add(maxMemorySlider);
        maxMemorySlider.addTo(group);
        SliderDouble minMemorySlider = new SliderDouble(this.startX + 30, this.startY + MinWindow.PADDING + 255, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 275, 4, "服务端内存使用最小值(GB)", null, true, false, 0, 0, 1);
        this.labels.add(minMemorySlider);
        minMemorySlider.addTo(group);
        StringInput serverIp = new StringInput(this.startX + 30, this.startY + MinWindow.PADDING + 280, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 305, "127.0.0.1", "服务端IP地址", null);
        this.labels.add(serverIp);
        serverIp.addTo(group);
        text = new Text("服务端端口");
        text.setX(this.startX + 30);
        text.setY(this.startY + MinWindow.PADDING + 335);
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        this.nodes.add(text);
        group.getChildren().add(text);
        SliderInt serverPort = new SliderInt(this.startX + 30, this.startY + MinWindow.PADDING + 345, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 365, 25565, "服务器端口号", null, true, true, 0, 65535, 1);
        this.labels.add(serverPort);
        serverPort.addTo(group);
        TextButton createButton = new TextButton(this.startX + 30, this.startY + MinWindow.PADDING + 370, this.startX + this.getEditorNameWidth() + 150, this.startY + MinWindow.PADDING + 390, "创建并构建新的服务器软件", true);
        createButton.resetImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Create.png"
                )))
        );
        createButton.background.setOnMousePressed(event -> {

            logger.info("开始下载并构建服务器...");

            BuildServer task = new BuildServer();
            task.labels = this.labels;

            new Thread(task).start();

        });
        this.labels.add(createButton);
        createButton.addTo(group);
        return group;
    }

    public static class BuildServer extends Task<Void> {

        public List<BaseLabel> labels;

        @Override
        protected Void call() throws InterruptedException {
            this.labels.getLast().setVisible(false);
            logger.info("开始下载服务器...");
            downloadServer(0);
            logger.info("开始生成启动脚本...");
            startServer(0);
            return null;
        }

        protected void downloadServer(int a) {
            if (a == MAX_RETRY_COUNT) {
                logger.error("重试结束,下载服务器失败，请检查网络连接!");
                this.labels.getLast().setVisible(true);
                return;
            }
            String versionId = ((TextButton) this.labels.get(0)).textLabel.getText();
            logger.info("服务器类型：{}", versionId);
            String savePath = this.labels.get(2).getData().toString();
            String serverName = this.labels.get(1).getData().toString();
            if (versionId.equals(MinecraftServerTypeSearchBox.SearchType.MINECRAFT.name)) {
                String version = ((TextButton) this.labels.get(3)).textLabel.getText();
                logger.info("开始下载原版服务端,版本: {}, 位置: {}", version, savePath + "\\" + serverName + ".jar");
                MinecraftServerManager minecraftServerManager = new MinecraftServerManager();
                if (minecraftServerManager.downloadServer(version, savePath + "\\" + serverName + ".jar")) {
                    logger.info("下载已完成");
                } else {
                    if (version.equals("-")) {
                        logger.error("下载失败,请检查版本号是否正确");
                        this.labels.getLast().setVisible(true);
                        return;
                    }
                    logger.warn("下载失败,正在重试");
                    downloadServer(a + 1);
                }
            } else {
                logger.error("暂不支持");
                this.labels.getLast().setVisible(true);
            }
        }

        protected void startServer(int a) {
            if (a == MAX_RETRY_COUNT) {
                logger.error("重试结束,无法生成启动脚本,请手动生成");
                this.labels.getLast().setVisible(true);
                return;
            }
            String versionId = ((TextButton) this.labels.get(0)).textLabel.getText();
            String serverName = this.labels.get(1).getData().toString();
            double maxMemory = (double) this.labels.get(4).getData();
            double minMemory = (double) this.labels.get(5).getData();
            logger.info("正在生成启动脚本,类型：{}", versionId);
            String savePath = this.labels.get(2).getData().toString();
            if (versionId.equals(MinecraftServerTypeSearchBox.SearchType.MINECRAFT.name)) {
                String version = ((TextButton) this.labels.get(3)).textLabel.getText();
                logger.info("开始生成启动脚本,版本: {}, 位置: {}", version, savePath + "\\start.bat");
                FileUtils.writeLinesToFile(savePath + "\\startGUI.bat", List.of(
                        "@echo off",
                        "java -Xms" + ((int) (Math.min(maxMemory, minMemory) * 1024)) +"M -Xmx" + ((int) (Math.max(maxMemory, minMemory) * 1024)) +"M -jar " + serverName +".jar"
                ));
                if (FileUtils.writeLinesToFile(savePath + "\\start.bat", List.of(
                        "@echo off",
                        "java -Xms" + ((int) (Math.min(maxMemory, minMemory) * 1024)) +"M -Xmx" + ((int) (Math.max(maxMemory, minMemory) * 1024)) +"M -jar " + serverName +".jar nogui"
                ))) {
                    logger.info("生成已完成");
                    logger.info("尝试首次启动服务器...");
                    FileUtils batRunner = new FileUtils();
                    batRunner.addCompletionListener(exitCode -> {
                        logger.info("启动成功,请遵循Eula协议");
                        logger.info("正在同意协议");
                        List<String> Eula = FileUtils.readLinesFromFile(savePath + "\\eula.txt");
                        if (Eula != null) {
                            if (Eula.getLast().equals("eula=true")) {
                                logger.info("Eula协议已被同意");
                                return;
                            }
                            Eula.set(Eula.size() - 1, "eula=true");
                        } else {
                            logger.warn("未找到协议文件,正在尝试重试");
                            startServer(a + 1);
                            return;
                        }
                        if (FileUtils.writeLinesToFile(savePath + "\\eula.txt", Eula)) {
                            logger.info("协议已同意");
                            logger.info("正在启动服务器...");
                            batRunner.clearListeners();
                            batRunner.addOutputListener(output -> {
                                if (output.contains("Done")) {
                                    logger.info("启动已完成,正在关闭服务器");
                                    batRunner.sendInput("stop");
                                }
                            });
                            batRunner.executeBatAsync(savePath + "\\start.bat");
                        }
                    });
                    batRunner.executeBatAsync(savePath + "\\start.bat");
                } else {
                    if (version.equals("-")) {
                        logger.error("生成失败,请检查版本号是否正确");
                        this.labels.getLast().setVisible(true);
                        return;
                    }
                    logger.warn("生成失败,正在重试");
                    startServer(a + 1);
                }
            } else {
                logger.error("暂不支持");
                this.labels.getLast().setVisible(true);
            }
        }

    }

}
