package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PathInputBox extends BaseLabel {

    private List<BaseLabel> labels = new ArrayList<>();

    public PathInputBox(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Group();
        InputBox inputBox = new InputBox(startX, startY, endX - 25, endY, "路径");
        this.labels.add(inputBox);
        Button button = new Button(endX - 20, startY, endX, endY, true);
        button.resetImage(
                new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/org/bxwbb/spigotplugincreatertool/icon/MinecraftServerCreater/OpenFile.png"))
                )
        );
        this.labels.add(button);
        button.background.setOnMousePressed(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择路径");
            File selectedDirectory = chooser.showDialog(HelloApplication.primaryStage);

            if (!Objects.equals(inputBox.getData(), "")) {
                File initialDirectory = new File(System.getProperty((String) inputBox.getData()));
                if (initialDirectory.exists()) {
                    chooser.setInitialDirectory(initialDirectory);
                }
            }

            if (selectedDirectory != null) {
                try {
                    inputBox.setData(selectedDirectory.getAbsolutePath());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        for (BaseLabel label : this.labels) {
            label.addTo(this.base);
        }
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        double rW = startX;
        for (BaseLabel label : this.labels) {
            label.resetPos(rW, startY);
            rW += label.getWidth() + 5;
        }
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        labels.getFirst().resetSize(width - 25, height);
        labels.getLast().resetSize(25, height);
    }

    @Override
    public void delete() {
        for (BaseLabel label : labels) {
            label.delete();
        }
        this.labels.clear();
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return this.endX - this.startX;
    }

    @Override
    public double getHeight() {
        return this.startY - this.startX;
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public Object getData() {
        return this.labels.getFirst().getData();
    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {
        this.labels.getFirst().setData(data);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        PathInputBox horizontalGroup = new PathInputBox(this.startX, this.startY, this.endX, this.endY);
        List<BaseLabel> r = new ArrayList<>();
        for (BaseLabel label : this.labels) {
            r.add(label.createNew());
        }
        horizontalGroup.labels = r;
        return horizontalGroup;
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }
}
