package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;

import java.util.ArrayList;
import java.util.List;

public class ObjectInput extends BaseLabel {
    public Object value;
    public String name;
    public List<String> lore;

    private Text title;
    private final Rectangle background;
    private final Rectangle backgroundBorder;
    private final Rectangle mask;
    private final Text valueText;

    public ObjectInput(double startX, double startY, double endX, double endY, Object value, String name, List<String> lore) {
        this.base = new Group();
        this.value = value;
        this.name = name;
        this.lore = lore;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.backgroundBorder = new Rectangle(startX - 1, startY - 1, endX - startX + 2, endY - startY + 2);
        this.backgroundBorder.setArcWidth(HelloApplication.ROUNDNESS);
        this.backgroundBorder.setArcHeight(HelloApplication.ROUNDNESS);
        this.backgroundBorder.setStrokeWidth(0.0f);
        this.backgroundBorder.setFill(HelloApplication.UNSELECTED_BORDER_COLOR);
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setStrokeWidth(0);
        this.background.setStroke(HelloApplication.UNSELECTED_BORDER_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.title = new Text(this.name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.title.setMouseTransparent(true);
        this.valueText = new Text(String.valueOf(this.value));
        this.valueText.setFont(HelloApplication.TEXT_FONT);
        this.valueText.setFill(HelloApplication.FONT_COLOR);
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setMouseTransparent(true);
        this.mask = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.mask.setArcWidth(HelloApplication.ROUNDNESS);
        this.mask.setArcHeight(HelloApplication.ROUNDNESS);
        this.base.setClip(mask);
    }

    public void resetPos(double x, double y) {
        this.endX = endX - startX + x;
        this.endY = endY - startY + y;
        this.startX = x;
        this.startY = y;
        this.backgroundBorder.setX(startX - 1);
        this.backgroundBorder.setY(startY - 1);
        this.background.setX(startX);
        this.background.setY(startY);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12  - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.mask.setX(startX);
        this.mask.setY(startY);
    }

    public void resetSize(double width, double height) {
        this.endX = startX + width;
        this.endY = startY + height;
        this.backgroundBorder.setWidth(endX - startX + 2);
        this.backgroundBorder.setHeight(endY - startY + 2);
        this.background.setWidth(endX - startX);
        this.background.setHeight(endY - startY);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.valueText.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
        this.mask.setWidth(this.endX - this.startX);
        this.mask.setHeight(this.endY - this.startY);
    }

    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.backgroundBorder);
        this.root.getChildren().remove(this.base);
    }

    public void addTo(Group root) {
        this.root = root;
        this.root.getChildren().add(this.backgroundBorder);
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.valueText);
        this.base.getChildren().add(this.title);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.title.setText(name);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + (this.endY - this.startY) * 0.5 + 5);
    }

    @Override
    public double getWidth() {
        return this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + 40;
    }

    @Override
    public double getHeight() {
        return this.backgroundBorder.getHeight();
    }

    @Override
    public void autoWidth() {
        this.resetSize((float) (this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + 40), (float) (this.endY - this.startY));
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.background.setMouseTransparent(!visible);
    }

    @Override
    public Object getData() {
        return this.value;
    }

    @Override
    public void setData(Object data) {
        this.value = data;
        this.valueText.setText(String.valueOf(this.value));
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.title.setVisible(true);
        this.valueText.setVisible(true);
    }

    @Override
    public BaseLabel createNew() {
        return new ObjectInput(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.value,
                this.name,
                new ArrayList<>(this.lore)
        );
    }

}
