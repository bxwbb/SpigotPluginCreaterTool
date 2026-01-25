package org.bxwbb.spigotplugincreatertool.WindowLabel.Base;

import javafx.scene.paint.Paint;

import java.util.function.Consumer;

public interface Button {

    void resetObject(BaseLabel objectLabel);

    double getBorderWidth();

    void setBorderWidth(double borderWidth);

    boolean isDown();

    void setDown(boolean down);

    Consumer<BaseLabel.LabelEvent> getMouseEntered();

    void setMouseEntered(Consumer<BaseLabel.LabelEvent> mouseEntered);

    Consumer<BaseLabel.LabelEvent> getMouseExited();

    void setMouseExited(Consumer<BaseLabel.LabelEvent> mouseExited);

    Consumer<BaseLabel.LabelEvent> getMouseHover();

    void setMouseHover(Consumer<BaseLabel.LabelEvent> mouseHover);

    Consumer<BaseLabel.LabelEvent> getButtonPressed();

    void setButtonPressed(Consumer<BaseLabel.LabelEvent> buttonPressed);

    Consumer<BaseLabel.LabelEvent> getButtonReleased();

    void setButtonReleased(Consumer<BaseLabel.LabelEvent> buttonReleased);

    Consumer<BaseLabel.LabelEvent> getButtonClicked() ;

    void setButtonClicked(Consumer<BaseLabel.LabelEvent> buttonClicked);

    Consumer<BaseLabel.LabelEvent> getButtonDisabled();

    void setButtonDisabled(Consumer<BaseLabel.LabelEvent> buttonDisabled);

    Consumer<BaseLabel.LabelEvent> getButtonEnabled();

    void setButtonEnabled(Consumer<BaseLabel.LabelEvent> buttonEnabled);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Paint getBaseColor();

    void setBaseColor(Paint baseColor);

    Paint getBorderColor();

    void setBorderColor(Paint borderColor);

    Paint getHoverColor();

    void setHoverColor(Paint hoverColor);

    Paint getDisabledColor();

    void setDisabledColor(Paint disabledColor);

    Paint getDisabledBorderColor();

    void setDisabledBorderColor(Paint disabledBorderColor);

    Paint getDownColor();

    void setDownColor(Paint downColor);

}
