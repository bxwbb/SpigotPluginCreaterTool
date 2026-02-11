package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.Button;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.LabelEvent;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

import java.util.function.Consumer;

public class ButtonBox extends BaseLabel implements Button {

    private final Stage baseStage;
    private BaseLabel baseLabel;
    private boolean isDown = false;
    private boolean isHover = false;
    private boolean changeCursor = true;

    // 鼠标进入事件
    private Consumer<LabelEvent> mouseEnterEvent;
    // 鼠标离开事件
    private Consumer<LabelEvent> mouseLeaveEvent;
    // 鼠标点击事件
    private Consumer<LabelEvent> mouseClickEvent;
    // 鼠标按下事件
    private Consumer<LabelEvent> mousePressedEvent;
    // 鼠标拖拽事件
    private Consumer<LabelEvent> mouseDraggedEvent;
    // 鼠标移动事件
    private Consumer<LabelEvent> mouseMovedEvent;

    // 默认颜色
    private Paint defaultColor = ColorSetting.CONTROL_BASE_COLOR;
    // 悬停颜色
    private Paint hoverColor = ColorSetting.CONTROL_HOVER_COLOR;
    // 按下颜色
    private Paint pressedColor = ColorSetting.CONTROL_HIGHLIGHT_COLOR;


    public ButtonBox(double x, double y, double width, double height, BaseLabel baseLabel) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.baseStage = new Stage(x, y, width, height);
        this.baseLabel = baseLabel;
        baseLabel.setMouseTransparent(true);
        this.baseStage.addChild(baseLabel, 0, 0);
        baseStage.getBaseRectangle().addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            isHover = true;
            baseStage.setBaseColor(hoverColor);
            if (getMouseEnterEvent() != null) getMouseEnterEvent().accept(new LabelEvent(this, mouseEvent));
        });
        baseStage.getBaseRectangle().addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            isHover = false;
            baseStage.setBaseColor(defaultColor);
            if (getMouseLeaveEvent() != null) getMouseLeaveEvent().accept(new LabelEvent(this, mouseEvent));
        });
        baseStage.getBaseRectangle().setOnMousePressed(mouseEvent -> {
            isDown = true;
            baseStage.setBaseColor(pressedColor);
            getFocus();
            if (getMousePressedEvent() != null) getMousePressedEvent().accept(new LabelEvent(this, mouseEvent));
        });
        baseStage.getBaseRectangle().addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            isDown = false;
            if (isHover()) {
                baseStage.setBaseColor(hoverColor);
            } else {
                baseStage.setBaseColor(defaultColor);
            }
            if (getMouseClickEvent() != null) getMouseClickEvent().accept(new LabelEvent(this, mouseEvent));
        });
        baseStage.getBaseRectangle().addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (getMouseDraggedEvent() != null) getMouseDraggedEvent().accept(new LabelEvent(this, mouseEvent));
        });
        baseStage.getBaseRectangle().addEventHandler(MouseEvent.MOUSE_MOVED, mouseEvent -> {
            if (getMouseMovedEvent() != null) getMouseMovedEvent().accept(new LabelEvent(this, mouseEvent));
        });
        this.addChild(baseStage);
        init(null);
    }

    public Stage getBaseStage() {
        return baseStage;
    }

    @Override
    public void update() {
        super.update();
        baseStage.setLayoutXNoUpdate(getLayoutX());
        baseStage.setLayoutYNoUpdate(getLayoutY());
        baseStage.setLayoutWidthNoUpdate(getLayoutWidth());
        baseStage.setLayoutHeight(getLayoutHeight());
        if (isDown()) {
            baseStage.setBaseColor(pressedColor);
        } else if (isHover()) {
            baseStage.setBaseColor(hoverColor);
        } else {
            baseStage.setBaseColor(defaultColor);
        }
        if (isChangeCursor()) {
            baseStage.getBaseRectangle().setCursor(Cursor.HAND);
        } else {
            baseStage.getBaseRectangle().setCursor(Cursor.DEFAULT);
        }
    }

    public BaseLabel getBaseLabel() {
        return baseLabel;
    }

    public void setBaseLabel(BaseLabel baseLabel) {
        this.baseLabel.setParent(null);
        this.baseLabel = baseLabel;
        this.baseStage.addChild(baseLabel);
    }

    @Override
    public Consumer<LabelEvent> getMouseEnterEvent() {
        return mouseEnterEvent;
    }

    @Override
    public void setMouseEnterEvent(Consumer<LabelEvent> mouseEnterEvent) {
        this.mouseEnterEvent = mouseEnterEvent;
    }

    @Override
    public Consumer<LabelEvent> getMouseLeaveEvent() {
        return mouseLeaveEvent;
    }

    @Override
    public void setMouseLeaveEvent(Consumer<LabelEvent> mouseLeaveEvent) {
        this.mouseLeaveEvent = mouseLeaveEvent;
    }

    @Override
    public Consumer<LabelEvent> getMouseClickEvent() {
        return mouseClickEvent;
    }

    @Override
    public void setMouseClickEvent(Consumer<LabelEvent> mouseClickEvent) {
        this.mouseClickEvent = mouseClickEvent;
    }

    @Override
    public Consumer<LabelEvent> getMousePressedEvent() {
        return mousePressedEvent;
    }

    @Override
    public void setMousePressedEvent(Consumer<LabelEvent> mousePressedEvent) {
        this.mousePressedEvent = mousePressedEvent;
    }

    @Override
    public Consumer<LabelEvent> getMouseDraggedEvent() {
        return mouseDraggedEvent;
    }

    @Override
    public void setMouseDraggedEvent(Consumer<LabelEvent> mouseDraggedEvent) {
        this.mouseDraggedEvent = mouseDraggedEvent;
    }

    @Override
    public Consumer<LabelEvent> getMouseMovedEvent() {
        return mouseMovedEvent;
    }

    @Override
    public void setMouseMovedEvent(Consumer<LabelEvent> mouseMovedEvent) {
        this.mouseMovedEvent = mouseMovedEvent;
    }

    public Paint getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Paint defaultColor) {
        this.defaultColor = defaultColor;
        update();
    }

    public Paint getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Paint hoverColor) {
        this.hoverColor = hoverColor;
        update();
    }

    public Paint getPressedColor() {
        return pressedColor;
    }

    public void setPressedColor(Paint pressedColor) {
        this.pressedColor = pressedColor;
        update();
    }

    public boolean isHover() {
        return isHover;
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    public boolean isChangeCursor() {
        return changeCursor;
    }

    public void setChangeCursor(boolean changeCursor) {
        this.changeCursor = changeCursor;
        update();
    }

}
