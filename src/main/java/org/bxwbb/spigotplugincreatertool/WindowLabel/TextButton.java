package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.paint.Paint;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.Button;

import java.util.function.Consumer;

public class TextButton extends BaseLabel implements Button {

    private final CustomButton button;
    private final Text text;
    private final DirectionalLayout directionalLayout;

    public TextButton(double startX, double startY, double endX, double endY, String Text) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.text = new Text(startX, startY, endX, endY, Text);
        this.directionalLayout = new DirectionalLayout(startX, startY, endX, endY, text);
        this.button = new CustomButton(startX, startY, endX, endY,
                directionalLayout
        );
        this.button.addTo(this.base);
        this.addChild(button);
    }

    @Override
    public double getHeight() {
        return button.getHeight();
    }

    @Override
    public void update() {
        this.button.resetPos(this.startX, this.startY);
        this.button.resetSize(this.endX - this.startX, this.endY - this.startY);
        this.directionalLayout.resetPos(this.startX, this.startY);
        this.directionalLayout.resetSize(this.getWidth(), this.getHeight());
    }

    public Button getButton() {
        return button;
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.button.setDisplayVisible(visible);
    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {
        button.setData(data);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new TextButton(this.startX, this.startY, this.endX, this.endY, this.getText());
    }

    @Override
    public void resetObject(BaseLabel objectLabel) {
        this.button.resetObject(objectLabel);
    }

    @Override
    public double getBorderWidth() {
        return this.button.getBorderWidth();
    }

    @Override
    public void setBorderWidth(double borderWidth) {
        this.button.setBorderWidth(borderWidth);
    }

    @Override
    public boolean isDown() {
        return this.button.isDown();
    }

    @Override
    public void setDown(boolean down) {
        this.button.setDown(down);
    }

    @Override
    public Consumer<LabelEvent> getMouseEntered() {
        return this.button.getMouseEntered();
    }

    @Override
    public void setMouseEntered(Consumer<LabelEvent> mouseEntered) {
        this.button.setMouseEntered(mouseEntered);
    }

    @Override
    public Consumer<LabelEvent> getMouseExited() {
        return this.button.getMouseExited();
    }

    @Override
    public void setMouseExited(Consumer<LabelEvent> mouseExited) {
        this.button.setMouseExited(mouseExited);
    }

    @Override
    public Consumer<LabelEvent> getMouseHover() {
        return this.button.getMouseHover();
    }

    @Override
    public void setMouseHover(Consumer<LabelEvent> mouseHover) {
        this.button.setMouseHover(mouseHover);
    }

    @Override
    public Consumer<LabelEvent> getButtonPressed() {
        return this.button.getButtonPressed();
    }

    @Override
    public void setButtonPressed(Consumer<LabelEvent> buttonPressed) {
        this.button.setButtonPressed(buttonPressed);
    }

    @Override
    public Consumer<LabelEvent> getButtonReleased() {
        return this.button.getButtonReleased();
    }

    @Override
    public void setButtonReleased(Consumer<LabelEvent> buttonReleased) {
        this.button.setButtonPressed(buttonReleased);
    }

    @Override
    public Consumer<LabelEvent> getButtonClicked() {
        return this.button.getButtonClicked();
    }

    @Override
    public void setButtonClicked(Consumer<LabelEvent> buttonClicked) {
        this.button.setButtonClicked(buttonClicked);
    }

    @Override
    public Consumer<LabelEvent> getButtonDisabled() {
        return this.button.getButtonDisabled();
    }

    @Override
    public void setButtonDisabled(Consumer<LabelEvent> buttonDisabled) {
        this.button.setButtonDisabled(buttonDisabled);
    }

    @Override
    public Consumer<LabelEvent> getButtonEnabled() {
        return this.button.getButtonEnabled();
    }

    @Override
    public void setButtonEnabled(Consumer<LabelEvent> buttonEnabled) {
        this.button.setButtonEnabled(buttonEnabled);
    }

    @Override
    public boolean isEnabled() {
        return this.button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.button.setEnabled(enabled);
    }

    public void setDirectional(EightDirection directional) {
        this.directionalLayout.setEightDirection(directional);
    }

    public EightDirection getDirectional() {
        return this.directionalLayout.getEightDirection();
    }

    @Override
    public Paint getBaseColor() {
        return this.button.getBaseColor();
    }

    @Override
    public void setBaseColor(Paint baseColor) {
        this.button.setBaseColor(baseColor);
    }

    @Override
    public Paint getBorderColor() {
        return this.button.getBorderColor();
    }

    @Override
    public void setBorderColor(Paint borderColor) {
        this.button.setBorderColor(borderColor);
    }

    @Override
    public Paint getHoverColor() {
        return this.button.getHoverColor();
    }

    @Override
    public void setHoverColor(Paint hoverColor) {
        this.button.setHoverColor(hoverColor);
    }

    @Override
    public Paint getDisabledColor() {
        return this.button.getDisabledColor();
    }

    @Override
    public void setDisabledColor(Paint disabledColor) {
        this.button.setDisabledColor(disabledColor);
    }

    @Override
    public Paint getDisabledBorderColor() {
        return this.button.getDisabledBorderColor();
    }

    @Override
    public void setDisabledBorderColor(Paint disabledBorderColor) {
        this.button.setDisabledColor(disabledBorderColor);
    }

    @Override
    public Paint getDownColor() {
        return this.button.getDownColor();
    }

    @Override
    public void setDownColor(Paint downColor) {
        this.button.setDownColor(downColor);
    }

    /**
     * 自适应于文本的大小
     */
    public void autoSizeToText() {
        this.resetSize(this.text.getWidth() + this.button.getBorderWidth() * 2, this.text.getHeight() + this.button.getBorderWidth() * 2);
    }

}
