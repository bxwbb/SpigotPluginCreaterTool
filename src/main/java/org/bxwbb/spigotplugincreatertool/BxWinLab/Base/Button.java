package org.bxwbb.spigotplugincreatertool.BxWinLab.Base;

import javafx.scene.paint.Paint;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.LabelEvent;

import java.util.function.Consumer;

public interface Button {

    Consumer<LabelEvent> getMouseEnterEvent();

    void setMouseEnterEvent(Consumer<LabelEvent> mouseEnterEvent);

    Consumer<LabelEvent> getMouseLeaveEvent();

    void setMouseLeaveEvent(Consumer<LabelEvent> mouseLeaveEvent);

    Consumer<LabelEvent> getMouseClickEvent();

    void setMouseClickEvent(Consumer<LabelEvent> mouseClickEvent);

    Consumer<LabelEvent> getMousePressedEvent();

    void setMousePressedEvent(Consumer<LabelEvent> mousePressedEvent);

    Consumer<LabelEvent> getMouseDraggedEvent();

    void setMouseDraggedEvent(Consumer<LabelEvent> mouseDraggedEvent);

    Consumer<LabelEvent> getMouseMovedEvent();

    void setMouseMovedEvent(Consumer<LabelEvent> mouseMovedEvent);

    Paint getDefaultColor();

    void setDefaultColor(Paint defaultColor);

    Paint getHoverColor();

    void setHoverColor(Paint hoverColor);

    Paint getPressedColor();

    void setPressedColor(Paint pressedColor);

    boolean isHover();

    boolean isDown();

    void setDown(boolean down);

    boolean isChangeCursor();

    void setChangeCursor(boolean changeCursor);

}
