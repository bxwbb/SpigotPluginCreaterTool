package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.paint.Paint;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.Button;

import java.util.function.Consumer;

public class DropDownMenu extends BaseLabel implements Button {

    private final PushButton button;
    private final VerticalMenu verticalMenu;
    private BaseLabel baseLabel;
    private double officeX = 0;
    private double officeY = 25;
    private double menuWidth = 400;
    private double menuHeight = 200;

    public DropDownMenu(double startX, double startY, double endX, double endY, BaseLabel baseLabel) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.button = new PushButton(startX, startY, endX, endY,
                baseLabel
        );
        this.verticalMenu = new VerticalMenu(
                startX + this.officeX,
                startY + this.officeY,
                endX + this.officeX + this.menuWidth,
                endY + this.officeY + this.menuHeight
        );
        this.button.setButtonClicked(event -> {
            this.verticalMenu.setDisplayVisible(this.button.isDown());
            this.verticalMenu.update();
        });
        this.verticalMenu.setDisplayVisible(false);
        this.baseLabel = baseLabel;
        this.button.addTo(this.base);
        this.addChild(button);
        this.addChild(verticalMenu);
    }

    @Override
    public double getHeight() {
        return button.getHeight();
    }

    @Override
    public void update() {
        this.button.resetPos(this.startX, this.startY);
        this.button.resetSize(this.endX - this.startX, this.endY - this.startY);
        this.verticalMenu.resetPos(this.startX + this.officeX, this.startY + this.officeY);
        this.verticalMenu.resetSize(this.menuWidth, this.menuHeight);
        this.baseLabel.resetPos(this.startX, this.startY);
        this.baseLabel.resetSize(this.getWidth(), this.getHeight());
    }

    public PushButton getButton() {
        return button;
    }

    public VerticalMenu getVerticalMenu() {
        return verticalMenu;
    }

    public double getMenuHeight() {
        return menuHeight;
    }

    public void setMenuHeight(double menuHeight) {
        this.menuHeight = menuHeight;
    }

    public double getMenuWidth() {
        return menuWidth;
    }

    public void setMenuWidth(double menuWidth) {
        this.menuWidth = menuWidth;
    }

    public double getOfficeY() {
        return officeY;
    }

    public void setOfficeY(double officeY) {
        this.officeY = officeY;
    }

    public double getOfficeX() {
        return officeX;
    }

    public void setOfficeX(double officeX) {
        this.officeX = officeX;
    }

    public BaseLabel getBaseLabel() {
        return baseLabel;
    }

    public void setBaseLabel(BaseLabel baseLabel) {
        this.baseLabel = baseLabel;
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.button.setDisplayVisible(visible);
    }

    @Override
    public void setData(Object data) {
        button.setData(data);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new CustomButton(this.startX, this.startY, this.endX, this.endY, this.baseLabel);
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

}
