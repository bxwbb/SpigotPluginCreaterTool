package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.input.MouseEvent;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine.Animation;
import org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine.AnimationEngine;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.Button;

import java.util.function.Consumer;

public class VerticalSlider extends BaseLabel {

    private final PushButton baseButton;
    private final PushButton sliderButton;
    // 空隙
    private double gap = 1;
    // 滑条最大值
    private double maxValue = 100;
    // 滑条高度与基底的高度比
    private double sliderHeightRatio = 0.5;
    private double lastY = 0;
    // 百分比
    private double percent = 0;
    // 跳转动画
    Animation jumpAnimation;
    // 滑条数值改变事件
    public Consumer<Double> sliderValueChange;
    private RatioComputationMethod ratioComputationMethod = RatioComputationMethod.LINEAR;

    public VerticalSlider(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.baseButton = new PushButton(startX, startY, endX, endY, BaseLabel.NULL_LABEL);
        this.baseButton.setBorderWidth(1);
        this.baseButton.setHoverColor(this.baseButton.getBaseColor());
        this.baseButton.setDownColor(this.baseButton.getHoverColor());
        this.sliderButton = new PushButton(startX + gap, startY, endX - gap, endY, BaseLabel.NULL_LABEL);
        this.sliderButton.setBorderWidth(1);
        this.sliderButton.setBaseColor(HelloApplication.UNSELECTED_MENU_COLOR);
        this.sliderButton.setDownColor(this.sliderButton.getBaseColor());
        this.jumpAnimation = new Animation(this.sliderButton, 100);
        this.addChild(baseButton);
        this.addChild(sliderButton);
        this.sliderButton.setButtonPressed(event -> lastY = ((MouseEvent) event.event()).getY());
        this.sliderButton.background.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            double dY = event.getY() - lastY;
            if (this.sliderButton.getY() + dY >= this.baseButton.getStartY() + gap
                    && this.sliderButton.getEndY() + dY <= this.baseButton.getEndY() - gap) {
                this.sliderButton.setY(this.sliderButton.getY() + dY);
            } else {
                if (this.sliderButton.getY() + dY < this.baseButton.getStartY() + gap) {
                    this.sliderButton.setY(this.baseButton.getStartY() + gap);
                } else if (this.sliderButton.getEndY() + dY > this.baseButton.getEndY() - gap) {
                    this.sliderButton.setY(this.baseButton.getEndY() - gap - sliderButton.getHeight());
                }
            }
            lastY = event.getY();
            double lostY = this.baseButton.getHeight() - sliderButton.getHeight() - 2 * gap;
            this.percent = ((this.sliderButton.getY() - getY()) / lostY);
            this.percent = Math.min(1.0, Math.max(0.0, this.percent));
            if (this.sliderValueChange != null) this.sliderValueChange.accept(getPercent());
        });
        this.baseButton.setButtonClicked(event -> {
            double lostY = this.baseButton.getHeight() - sliderButton.getHeight() - 2 * gap;
            double mouseY = ((MouseEvent) event.event()).getY();
            if (mouseY >= getY() + gap && mouseY <= getY() + lostY) {
                this.setPercent((mouseY - getY() - gap) / lostY);
                update();
            } else {
                this.setPercent(1);
                update();
            }
        });
        update();
    }

    @Override
    public void update() {
        this.baseButton.resetPos(startX, startY);
        this.baseButton.resetSize(endX - startX, endY - startY);
        sliderButton.setHeight(sliderHeightRatio * (this.baseButton.getHeight() - 2 * gap));
        double lostY = this.baseButton.getHeight() - sliderButton.getHeight() - 2 * gap;
        sliderButton.setY(startY + gap + lostY * percent);
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.baseButton.setDisplayVisible(visible);
        this.sliderButton.setDisplayVisible(visible);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return null;
    }

    public Button getBaseButton() {
        return baseButton;
    }

    public Button getSliderButton() {
        return sliderButton;
    }

    public double getGap() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public double getMaxValue() {
        return maxValue;
    }

    /**
     * 设置滑块最大值，并优化滑块高度比例计算
     *
     * @param maxValue 新的最大值
     */
    public void setMaxValue(double maxValue) {
        // 1. 保护最大值不小于1，避免除零错误
        double newMaxValue = Math.max(1, maxValue);

        // 2. 保存当前值（按百分比），避免最大值变化后值异常
        double currentValue = this.maxValue * getPercent();

        // 3. 更新最大值
        this.maxValue = newMaxValue;

        // 4. 恢复当前值（按新最大值缩放）
        this.setValue(currentValue);

        // 5. 优化后的滑块高度比例计算（方案1：线性比例）
        // 核心修改：线性衰减，范围0.1~0.8，最大值越大，滑块越窄但始终可见
        switch (ratioComputationMethod) {
            case LINEAR:
                this.sliderHeightRatio = Math.max(0.1, Math.min(0.8, 0.8 - (newMaxValue / 1000)));
                break;
            case LOGARITHMIC:
                double baseRatio = 0.6;
                double decayFactor = Math.min(0.4, newMaxValue / 200);
                this.sliderHeightRatio = baseRatio - decayFactor;
                break;
            case FIXED_RANGE:
                 double logValue = Math.log10(Math.max(1, newMaxValue));
                 this.sliderHeightRatio = Math.max(0.1, Math.min(0.7, 0.7 - (logValue / 10)));
                break;
        }
        update();
    }

    public double getValue() {
        return this.maxValue * getPercent();
    }

    public void setValue(double value) {
        this.setPercent(value / maxValue);
        update();
    }

    /**
     * 获取百分比
     */
    public double getPercent() {
        return this.percent;
    }

    public void setPercent(double percent) {
        this.percent = Math.min(1.0, Math.max(0.0, this.percent));
        double oldY = sliderButton.getY();
        double lostY = this.baseButton.getHeight() - sliderButton.getHeight() - 2 * gap;
        this.percent = percent;
        double newY = startY + gap + lostY * percent;
        jumpAnimation.setUpdateFunction(p -> {
            this.sliderButton.setStartY((newY - oldY) * p + oldY);
            return null;
        });
        AnimationEngine.getInstance().startAnimation(jumpAnimation);
        if (this.sliderValueChange != null) this.sliderValueChange.accept(getPercent());
        update();
    }

    public Consumer<Double> getSliderValueChange() {
        return sliderValueChange;
    }

    public void setSliderValueChange(Consumer<Double> sliderValueChange) {
        this.sliderValueChange = sliderValueChange;
    }

    public enum RatioComputationMethod {
        LINEAR,
        LOGARITHMIC,
        FIXED_RANGE
    }

    public RatioComputationMethod getRatioComputationMethod() {
        return ratioComputationMethod;
    }

    public void setRatioComputationMethod(RatioComputationMethod ratioComputationMethod) {
        this.ratioComputationMethod = ratioComputationMethod;
        update();
    }
}
