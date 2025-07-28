package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.MinecraftServerCreater;
import org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.Server.MinecraftServerManager;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MinecraftServerTypeSearchBox extends BaseLabel {

    static final Logger logger = LoggerFactory.getLogger(MinecraftServerTypeSearchBox.class);

    public static final double TEXT_HEIGHT = 20;

    public Rectangle background;
    public TextField textField;
    public Button searchButton;
    public List<SearchType> searchResultRecords;
    public static List<SearchType> records = List.of(
            SearchType.MINECRAFT,
            SearchType.SPIGOT,
            SearchType.BUKKIT,
            SearchType.FORGE,
            SearchType.FABRIC,
            SearchType.PAPER,
            SearchType.FOLIA,
            SearchType.SPONGIE,
            SearchType.CAT_SERVER,
            SearchType.MONIST,
            SearchType.ARCLIGHT,
            SearchType.NUKKIT,
            SearchType.BUNGEE_CORD,
            SearchType.VELOCITY,
            SearchType.WATER_FALL,
            SearchType.GEYSER
    );
    public final List<Text> searchResultTexts = new ArrayList<>();
    public final List<Button> searchResultButtons = new ArrayList<>();
    public final MinecraftServerCreater minecraftServerCreater;
    public Task<Void> task = new Task<>() {
        @Override
        protected Void call() {
            return null;
        }
    };

    public MinecraftServerTypeSearchBox(double x, double y, double screenWidth, double screenHeight, MinecraftServerCreater minecraftServerCreater) {
        this.startX = x;
        this.startY = y;
        this.endX = screenWidth - 5;
        this.endY = y + ((int) ((screenHeight - y - 0) / TEXT_HEIGHT)) * TEXT_HEIGHT;
        this.minecraftServerCreater = minecraftServerCreater;
        this.base = new Group();
        this.background = new Rectangle(x, y, this.endX - this.startX, this.endY - this.startY);
        background.setFill(HelloApplication.BG_COLOR);
        background.setArcWidth(HelloApplication.ROUNDNESS);
        background.setArcHeight(HelloApplication.ROUNDNESS);
        background.setStrokeWidth(1.0);
        background.setStroke(HelloApplication.BORDER_COLOR);
        this.textField = new TextField("");
        this.textField.requestFocus();
        this.textField.setFont(HelloApplication.TEXT_FONT);
        this.textField.setStyle(
                "-fx-background-color: " + HelloApplication.toHexString(HelloApplication.BG_COLOR) + ";" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: text;" +
                        "-fx-text-fill: " + HelloApplication.toHexString(HelloApplication.FONT_COLOR) + ";"
        );
        this.textField.setLayoutX(startX + 5);
        this.textField.setLayoutY(startY + 7);
        this.textField.setPrefWidth(endX - startX - 30);
        this.searchButton = new Button(endX - 25, startY + 5, endX - 5, startY + 25, true);
        this.searchButton.resetImage(
                new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Close.png"
                )))
        );
        this.searchButton.background.setOnMouseClicked(event1 -> {
            this.delete();
            HelloApplication.cancelLabel = null;
            updateTexts();
        });
        for (int i = 0; i < ((int) ((screenHeight - y - 40) / TEXT_HEIGHT)); i++) {
            this.searchResultTexts.add(new Text("测试Text"));
            this.searchResultTexts.get(i).setX(startX + 30);
            this.searchResultTexts.get(i).setY(startY + 40 + i * TEXT_HEIGHT);
            this.searchResultTexts.get(i).setFill(HelloApplication.SELECTED_COLOR);
            final int finalI = i;
            this.searchResultTexts.get(i).setOnMouseClicked(event -> {
                minecraftServerCreater.serverTypeButton.resetImage(
                        new Image(
                                Objects.requireNonNull(getClass().getResourceAsStream(
                                        this.searchResultRecords.get(finalI).iconPath
                                ))
                        )
                );
                minecraftServerCreater.serverTypeButton.resetText(
                        this.searchResultRecords.get(finalI).name
                );
                List<MinecraftServerVersionSearchBox.SearchType> records = new ArrayList<>();
                if (this.searchResultRecords.get(finalI).equals(SearchType.MINECRAFT)) {
                    this.task.cancel();
                    this.task = new Task<>() {
                        @Override
                        protected Void call() {
                            Platform.runLater(() -> logger.info("开始加载服务器版本列表"));
                            MinecraftServerManager manager = new MinecraftServerManager();
                            records.clear();
                            MinecraftServerVersionSearchBox.records.clear();
                            for (MinecraftServerManager.VersionInfo releaseVersion : manager.getReleaseVersions()) {
                                records.add(new MinecraftServerVersionSearchBox.SearchType(releaseVersion.id, MinecraftServerVersionSearchBox.VersionType.RELEASE));
                            }
                            for (MinecraftServerManager.VersionInfo releaseVersion : manager.getSnapshotVersions()) {
                                records.add(new MinecraftServerVersionSearchBox.SearchType(releaseVersion.id, MinecraftServerVersionSearchBox.VersionType.SNAPSHOT));
                            }
                            MinecraftServerVersionSearchBox.records = records;
                            Platform.runLater(() -> logger.info("加载结束"));
                            return null;
                        }
                    };
                    new Thread(task).start();
                }
                this.delete();
                HelloApplication.cancelLabel = null;
                updateTexts();
            });
            this.searchResultButtons.add(new Button(startX + 5, startY + 40 + i * TEXT_HEIGHT - 15, startX + 25, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false));
        }
        this.searchResultRecords = records;
        updateTexts();
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {

            this.searchResultRecords = records;
            this.searchResultRecords = this.searchResultRecords.stream()
                    .filter(item -> item.name.contains(newValue))
                    .collect(Collectors.toList());

            updateTexts();

        });
    }

    public void updateTexts() {
        for (int i = 0; i < this.searchResultTexts.size(); i++) {
            if (this.searchResultRecords.size() - 1 < i) {
                this.searchResultTexts.get(i).setText("");
                this.searchResultButtons.get(i).resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                        )))
                );
            } else {
                this.searchResultTexts.get(i).setText(this.searchResultRecords.get(i).name);
                this.searchResultButtons.get(i).resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                this.searchResultRecords.get(i).iconPath
                        )))
                );
            }
        }
    }

    @Override
    public void resetPos(double x, double y) {

    }

    @Override
    public void resetSize(double width, double height) {

    }

    @Override
    public void delete() {
        this.searchButton.delete();
        for (Button searchResultButton : this.searchResultButtons) {
            searchResultButton.delete();
        }
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.textField);
        this.searchButton.addTo(this.base);
        for (Text searchResultText : this.searchResultTexts) {
            this.base.getChildren().add(searchResultText);
        }
        for (Button searchResultButton : this.searchResultButtons) {
            searchResultButton.addTo(this.base);
        }
        this.base.setClip(getMask());
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public BaseLabel createNew() {
        return null;
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }

    public enum SearchType {
        NONE("null", ""),
        MINECRAFT("Vanilla", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Minecraft.png"),
        SPIGOT("Spigot", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Spigot.png"),
        BUKKIT("Bukkit", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Bukkit.png"),
        FORGE("Forge", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Forge.png"),
        FABRIC("Fabric", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Fabric.png"),
        PAPER("Paper", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Paper.png"),
        FOLIA("Folia", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Folia.png"),
        SPONGIE("Spongie", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Spongie.png"),
        CAT_SERVER("CatServer", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/CatServer.png"),
        MONIST("Mohist", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Mohist.png"),
        ARCLIGHT("Arclight", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Arclight.png"),
        NUKKIT("Nukkit", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Nukkit.png"),
        BUNGEE_CORD("BungeeCord", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/BungeeCord.png"),
        VELOCITY("Velocity", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Velocity.png"),
        WATER_FALL("Waterfall", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Waterfall.png"),
        GEYSER("Geyser", "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Geyser.png");

        public final String name;
        public final String iconPath;
        SearchType(String name, String iconPath) {
            this.name = name;
            this.iconPath = iconPath;
        }

    }

    public Rectangle getMask() {
        return new Rectangle(this.background.getX(), this.background.getY(), this.background.getWidth(), this.background.getHeight());
    }

}
