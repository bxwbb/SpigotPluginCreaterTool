package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.Objects;

public class TextButton extends BaseLabel {

    public final Rectangle background;
    public ImageView image;
    public Text textLabel;
    public String text;
    private boolean isDown;
    private final boolean hasBorder;
    private final Group baseGroup;

    public TextButton(double startX, double startY, double endX, double endY, String text, boolean hasBorder) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Group();
        this.baseGroup = new Group();
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.hasBorder = hasBorder;
        this.background.setStrokeWidth(hasBorder ? 1 : 0);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
        this.background.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (!this.isDown) {
                this.background.setFill(HelloApplication.HOVER_COLOR);
            }
        });
        this.background.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            if (!this.isDown) {
                this.background.setFill(HelloApplication.UNSELECTED_COLOR);
            }
        });
        this.image = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Empty.png"
                )))
        );
        this.image.setMouseTransparent(true);
        this.image.setX(startX + 1);
        this.image.setY(startY + 1);
        this.image.setFitWidth(endY - startY - 2);
        this.image.setFitHeight(endY - startY - 2);
        this.text = text;
        this.isDown = false;
        this.textLabel = new Text(this.text);
        this.textLabel.setFont(HelloApplication.TEXT_FONT);
        this.textLabel.setFill(HelloApplication.FONT_COLOR);
        this.textLabel.setX(startX + endY - startY - 2 + 5);
        this.textLabel.setY(startY + this.textLabel.getLayoutBounds().getHeight());
        this.textLabel.setMouseTransparent(true);
        this.baseGroup.setClip(new Rectangle(startX, startY, endX - startX, endY - startY));
    }

    public void resetImage(Image image) {
        this.image.setImage(image);
    }

    public void resetText(String text) {
        this.textLabel.setText(text);
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.background.setX(startX);
        this.background.setY(startY - 10);
        this.image.setX(startX + 1);
        this.image.setY(startY + 1 - 10);
        this.textLabel.setX(startX + endY - startY - 2 + 5);
        this.textLabel.setY(startY + this.textLabel.getLayoutBounds().getHeight() * 0.5 - 3);
        this.baseGroup.setClip(this.getMask());
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.background.setWidth(width);
        this.background.setHeight(height);
        this.image.setFitWidth(height - 2);
        this.image.setFitHeight(height - 2);
        this.baseGroup.setClip(this.getMask());
    }

    @Override
    public void delete() {
        if (root == null) return;
        this.baseGroup.getChildren().clear();
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        if (!this.base.getChildren().contains(this.background)) this.base.getChildren().add(this.background);
        if (!this.baseGroup.getChildren().contains(this.image)) this.baseGroup.getChildren().add(this.image);
        if (!this.baseGroup.getChildren().contains(this.textLabel)) this.baseGroup.getChildren().add(this.textLabel);
        if (!this.base.getChildren().contains(this.baseGroup)) this.base.getChildren().add(this.baseGroup);
        if (!this.root.getChildren().contains(this.base)) this.root.getChildren().add(this.base);
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
        return this.endY - this.startY;
    }

    @Override
    public void autoWidth() {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.background.setFill(visible ? HelloApplication.UNSELECTED_COLOR : HelloApplication.DISABLED_COLOR);
        this.background.setStroke(!visible ? HelloApplication.UNSELECTED_BORDER_COLOR : HelloApplication.BORDER_COLOR);
    }

    @Override
    public Object getData() {
        return this.isDown;
    }

    @Override
    public void setData(Object data) {
        this.isDown = (boolean) data;
        if (this.isDown) {
            this.background.setFill(HelloApplication.SELECTED_COLOR);
        } else {
            this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        }
    }

    @Override
    public BaseLabel createNew() {
        return new TextButton(this.startX, this.startY, this.endX, this.endY, this.text, this.hasBorder);
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }

    private Rectangle getMask() {
        return new Rectangle(startX, startY - (endY - startY) * 0.5, endX - startX, endY - startY);
    }

}
