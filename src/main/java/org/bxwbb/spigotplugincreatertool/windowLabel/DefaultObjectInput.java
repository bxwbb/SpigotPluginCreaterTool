package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.CodeTool.ClassAnalyzer;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DefaultObjectInput extends BaseLabel {
    public Object value;
    public String name;
    public List<String> lore;
    public ClassAnalyzer.ClassInfo classInfo;

    private final Text title;
    private final Rectangle background;
    private final Rectangle mask;
    private final Text valueText;
    private final Class<?> type;
    private final Group baseGroup;

    public DefaultObjectInput(double startX, double startY, double endX, double endY, Object value, String name, List<String> lore, ClassAnalyzer.ClassInfo classInfo) throws ClassNotFoundException {
        this.base = new Group();
        this.baseGroup = new Group();
        this.classInfo = classInfo;
        this.type = Class.forName(classInfo.fullClassName);
        this.value = this.type.cast(value);
        this.name = name;
        this.lore = lore;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setStrokeWidth(1);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
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
        this.baseGroup.setClip(mask);
    }

    public void resetPos(double x, double y) {
        this.endX = endX - startX + x;
        this.endY = endY - startY + y;
        this.startX = x;
        this.startY = y;
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
        this.baseGroup.getChildren().clear();
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
    }

    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.baseGroup.getChildren().add(this.valueText);
        this.baseGroup.getChildren().add(this.title);
        this.base.getChildren().add(this.baseGroup);
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
        return this.background.getHeight();
    }

    @Override
    public void autoWidth() {
        this.resetSize((float) (this.title.getLayoutBounds().getWidth() + this.valueText.getLayoutBounds().getWidth() + 40), (float) (this.endY - this.startY));
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.background.setMouseTransparent(!visible);
        this.background.setFill(visible ? HelloApplication.UNSELECTED_COLOR : HelloApplication.DISABLED_COLOR);
        this.background.setStroke(!visible ? HelloApplication.UNSELECTED_BORDER_COLOR : HelloApplication.BORDER_COLOR);
    }

    @Override
    public Object getData() {
        return this.type.cast(this.value);
    }

    @Override
    public void setData(Object data) {
        if (data instanceof Field) {
            try {
                this.value = this.type.cast(((Field) data).get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.value = this.type.cast(data);
        }
        this.valueText.setText(String.valueOf(this.value));
        this.valueText.setX(this.endX - 12 - this.valueText.getLayoutBounds().getWidth());
        this.title.setVisible(true);
        this.valueText.setVisible(true);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new DefaultObjectInput(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.value,
                this.name,
                new ArrayList<>(this.lore),
                this.classInfo
        );
    }

    @Override
    public Node.VarType getVarType() {
        return Node.VarType.__DEFAULT__;
    }

}
