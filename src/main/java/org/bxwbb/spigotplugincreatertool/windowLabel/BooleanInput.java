package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.List;

public class BooleanInput extends BaseLabel {

    public String name;
    public List<String> lore;

    private final Text title;
    private final CheckBox checkBox;

    public BooleanInput(double startX, double startY, double endX, double endY, boolean value, String name, List<String> lore) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.name = name;
        this.lore = lore;
        this.base = new Group();
        this.title = new Text(name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + 20);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.checkBox = new CheckBox();
        this.checkBox.setLayoutX(this.startX);
        this.checkBox.setLayoutY(this.startY + (this.endY - this.startY) * 0.5 - 8);
        this.checkBox.setSelected(true);
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.title.setX(this.startX + 20);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.checkBox.setLayoutX(this.startX);
        this.checkBox.setLayoutY(this.startY + (this.endY - this.startY) * 0.5 - 8);
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
    }

    @Override
    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
        this.root.getChildren().remove(this.checkBox);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.title);
        this.root.getChildren().add(this.base);
        this.root.getChildren().add(this.checkBox);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.title.setText(name);
    }

    @Override
    public double getWidth() {
        return this.checkBox.getWidth() + 20 + this.title.getLayoutBounds().getWidth();
    }

    @Override
    public double getHeight() {
        return this.checkBox.getHeight();
    }

    @Override
    public void autoWidth() {
        this.resetSize(this.getWidth(), this.endY - this.startY);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.checkBox.setDisable(!visible);
    }

    @Override
    public Object getData() {
        return this.checkBox.isSelected();
    }

    @Override
    public void setData(Object data) {
        this.checkBox.setSelected((Boolean) data);
    }

    @Override
    public BaseLabel createNew() {
        return new BooleanInput(startX, startY, endX, endY, true, name, new ArrayList<>(lore));
    }

    @Override
    public Node.VarType getVarType() {
        return Node.VarType.BOOLEAN;
    }

}
