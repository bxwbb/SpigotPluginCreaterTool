package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class CustomButton extends BaseLabel implements Button {

    private static final Logger log = LoggerFactory.getLogger(PushButton.class);

    private boolean isDown;
    private double borderWidth = 0.0;

    // 鼠标移入事件
    private Consumer<LabelEvent> mouseEntered;
    // 鼠标移出事件
    private Consumer<BaseLabel.LabelEvent> mouseExited;
    // 鼠标悬停事件
    private Consumer<BaseLabel.LabelEvent> mouseHover;
    // 按钮按下事件
    private Consumer<BaseLabel.LabelEvent> buttonPressed;
    // 按钮弹起事件
    private Consumer<BaseLabel.LabelEvent> buttonReleased;
    // 按钮点击事件
    private Consumer<BaseLabel.LabelEvent> buttonClicked;
    // 按钮禁用事件
    private Consumer<BaseLabel.LabelEvent> buttonDisabled;
    // 按钮启用事件
    private Consumer<BaseLabel.LabelEvent> buttonEnabled;
    // 是否启用按钮
    private boolean enabled = true;
    // 默认颜色
    private Paint baseColor = HelloApplication.UNSELECTED_COLOR;
    // 边框颜色
    private Paint borderColor = HelloApplication.BORDER_COLOR;
    // 悬停颜色
    private Paint hoverColor = HelloApplication.HOVER_COLOR;
    // 禁用颜色
    private Paint disabledColor = HelloApplication.DISABLED_COLOR;
    // 禁用边框颜色
    private Paint disabledBorderColor = HelloApplication.BORDER_COLOR;
    // 按下颜色
    private Paint downColor = HelloApplication.SELECTED_COLOR;
    private BaseLabel objectLabel;
    private boolean isMouseInto = false;

    public CustomButton(double startX, double startY, double endX, double endY, BaseLabel baseLabel) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Group();
        this.background = new Rectangle(startX, startY, endX - startX, endY - startY);
        this.background.setFill(this.baseColor);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(this.borderWidth);
        this.background.setStroke(this.borderColor);
        this.background.setSmooth(true);
        this.background.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            this.background.setFill(this.hoverColor);
            if (this.mouseEntered != null) {
                this.mouseEntered.accept(new LabelEvent(event, this));
            }
            if (this.buttonPressed != null) {
                this.buttonPressed.accept(new LabelEvent(event, this));
            }
            isMouseInto = true;
        });
        this.background.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            this.background.setFill(this.baseColor);
            if (this.mouseExited != null) {
                this.mouseExited.accept(new LabelEvent(event, this));
            }
            if (this.buttonReleased != null) {
                this.buttonReleased.accept(new LabelEvent(event, this));
            }
            isMouseInto = false;
        });
        this.background.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            this.isDown = true;
            this.background.setFill(this.downColor);
            if (this.buttonPressed != null) {
                this.buttonPressed.accept(new LabelEvent(event, this));
            }
        });
        this.background.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            this.isDown = false;
            this.background.setFill(this.isMouseInto ? this.hoverColor : this.baseColor);
            if (this.buttonReleased != null) {
                this.buttonReleased.accept(new LabelEvent(event, this));
            }
            if (this.buttonClicked != null) {
                this.buttonClicked.accept(new LabelEvent(event, this));
            }
        });
        this.objectLabel = baseLabel;
        baseLabel.setMouseTransparent(true);
        this.addChild(this.objectLabel);
        this.isDown = false;
        update();
    }

    public void resetObject(BaseLabel objectLabel) {
        this.objectLabel.delete();
        this.objectLabel = objectLabel;
        this.addChild(this.objectLabel);
        this.objectLabel.addTo(this.base);
    }

    @Override
    public void update() {
        this.background.setX(startX);
        this.background.setY(startY);
        this.background.setWidth(endX - startX);
        this.background.setHeight(endY - startY);
        this.background.setStrokeWidth(this.borderWidth);
        this.objectLabel.update();
        this.background.setFill(enabled ? this.baseColor : this.disabledColor);
        this.background.setStroke(enabled ? this.baseColor : this.disabledBorderColor);
        this.background.setFill(this.baseColor);
        this.background.setStroke(this.borderColor);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.background.setVisible(visible);
        this.objectLabel.setDisplayVisible(visible);
    }

    @Override
    public Object getData() {
        return this.isDown;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof Boolean) {
            this.setDown((boolean) data);
        } else {
            log.error("错误的数据类型 - {}", data.getClass());
        }
    }

    @Override
    public BaseLabel createNew() {
        return new PushButton(this.startX, this.startY, this.endX, this.endY, this.objectLabel);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        super.setMouseTransparent(transparent);
        this.background.setMouseTransparent(transparent);
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(double borderWidth) {
        this.borderWidth = borderWidth;
        this.update();
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        this.isDown = down;
        if (this.isDown) {
            this.background.setFill(this.downColor);
        } else {
            this.background.setFill(this.baseColor);
        }
        this.update();
    }

    public Consumer<LabelEvent> getMouseEntered() {
        return mouseEntered;
    }

    public void setMouseEntered(Consumer<LabelEvent> mouseEntered) {
        this.mouseEntered = mouseEntered;
    }

    public Consumer<LabelEvent> getMouseExited() {
        return mouseExited;
    }

    public void setMouseExited(Consumer<LabelEvent> mouseExited) {
        this.mouseExited = mouseExited;
    }

    public Consumer<LabelEvent> getMouseHover() {
        return mouseHover;
    }

    public void setMouseHover(Consumer<LabelEvent> mouseHover) {
        this.mouseHover = mouseHover;
    }

    public Consumer<LabelEvent> getButtonPressed() {
        return buttonPressed;
    }

    public void setButtonPressed(Consumer<LabelEvent> buttonPressed) {
        this.buttonPressed = buttonPressed;
    }

    public Consumer<LabelEvent> getButtonReleased() {
        return buttonReleased;
    }

    public void setButtonReleased(Consumer<LabelEvent> buttonReleased) {
        this.buttonReleased = buttonReleased;
    }

    public Consumer<LabelEvent> getButtonClicked() {
        return buttonClicked;
    }

    public void setButtonClicked(Consumer<LabelEvent> buttonClicked) {
        this.buttonClicked = buttonClicked;
    }

    public Consumer<LabelEvent> getButtonDisabled() {
        return buttonDisabled;
    }

    public void setButtonDisabled(Consumer<LabelEvent> buttonDisabled) {
        this.buttonDisabled = buttonDisabled;
    }

    public Consumer<LabelEvent> getButtonEnabled() {
        return buttonEnabled;
    }

    public void setButtonEnabled(Consumer<LabelEvent> buttonEnabled) {
        this.buttonEnabled = buttonEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.background.setFill(visible ? HelloApplication.UNSELECTED_COLOR : HelloApplication.DISABLED_COLOR);
        this.background.setStroke(!visible ? HelloApplication.UNSELECTED_BORDER_COLOR : HelloApplication.BORDER_COLOR);
        if (enabled) {
            if (this.buttonEnabled != null) {
                new Thread(() -> this.buttonEnabled.accept(new LabelEvent(null, this))).start();
            }
        } else {
            if (this.buttonDisabled != null) {
                new Thread(() -> this.buttonDisabled.accept(new LabelEvent(null, this))).start();
            }
        }
    }

    public Paint getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Paint baseColor) {
        this.baseColor = baseColor;
        this.update();
    }

    public Paint getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Paint borderColor) {
        this.borderColor = borderColor;
        this.update();
    }

    public Paint getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Paint hoverColor) {
        this.hoverColor = hoverColor;
        this.update();
    }

    public Paint getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(Paint disabledColor) {
        this.disabledColor = disabledColor;
        this.update();
    }

    public Paint getDisabledBorderColor() {
        return disabledBorderColor;
    }

    public void setDisabledBorderColor(Paint disabledBorderColor) {
        this.disabledBorderColor = disabledBorderColor;
        this.update();
    }

    public Paint getDownColor() {
        return downColor;
    }

    public void setDownColor(Paint downColor) {
        this.downColor = downColor;
        this.update();
    }

}
