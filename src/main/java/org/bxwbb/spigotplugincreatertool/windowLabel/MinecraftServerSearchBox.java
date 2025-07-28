package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.MinecraftServer;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MinecraftServerSearchBox extends BaseLabel {

    public static final double TEXT_HEIGHT = 20;

    public Rectangle background;
    public TextField textField;
    public Button searchButton;
    public List<MinecraftServer> searchResultRecords;
    public static List<MinecraftServer> records = new ArrayList<>();
    public final List<Text> searchResultTexts = new ArrayList<>();
    public final List<Button> searchResultButtons = new ArrayList<>();
    public final List<SearchType> searchResultType = new ArrayList<>();

    public MinecraftServerSearchBox(double x, double y, double screenWidth, double screenHeight) {
        this.startX = x;
        this.startY = y;
        this.endX = screenWidth - 5;
        this.endY = y + ((int) ((screenHeight - y - 0) / TEXT_HEIGHT)) * TEXT_HEIGHT;
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
            this.searchResultTexts.get(i).setOnMouseClicked(event -> {
                // 当选择服务器时
            });
            this.searchResultButtons.add(new Button(startX + 5, startY + 40 + i * TEXT_HEIGHT - 15, startX + 25, startY + 40 + (i + 1) * TEXT_HEIGHT - 15, false));
            this.searchResultType.add(SearchType.NONE);
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
                String pi = switch (this.searchResultRecords.get(i).type) {
                    case NONE -> "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png";
                    case MINECRAFT -> "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Minecraft.png";
                    case SPIGOT -> "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Spigot.png";
                    case BUKKIT -> "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Paper.png";
                    case FORGE -> "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Forge.png";
                    case FABRIC -> "/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/Fabric.png";
                };
                this.searchResultButtons.get(i).resetImage(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                pi
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
        NONE,
        MINECRAFT,
        SPIGOT,
        BUKKIT,
        FORGE,
        FABRIC
    }

    public Rectangle getMask() {
        return new Rectangle(this.background.getX(), this.background.getY(), this.background.getWidth(), this.background.getHeight());
    }

}
