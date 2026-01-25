package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.input.MouseEvent;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine.Animation;
import org.bxwbb.spigotplugincreatertool.WindowLabel.AnimationEngine.AnimationEngine;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.Button;

import java.util.function.Consumer;

/**
 * 水平滑动条控件
 * 核心逻辑：基于X轴拖拽，替代原有的Y轴拖拽
 */
public class HorizontalSlider extends BaseLabel {

    private final PushButton baseButton;
    private final PushButton sliderButton;
    // 空隙
    private double gap = 1;
    // 滑条最大值
    private double maxValue = 100;
    // 滑条宽度与基底的宽度比（原高度比改为宽度比）
    private double sliderWidthRatio = 0.5;
    private double lastX = 0; // 原lastY改为lastX
    // 百分比
    public double percent = 0;
    // 跳转动画
    Animation jumpAnimation;
    // 滑条数值改变事件
    public Consumer<Double> sliderValueChange;

    public HorizontalSlider(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        // 初始化基底按钮
        this.baseButton = new PushButton(startX, startY, endX, endY, BaseLabel.NULL_LABEL);
        this.baseButton.setBorderWidth(1);
        this.baseButton.setHoverColor(this.baseButton.getBaseColor());
        this.baseButton.setDownColor(this.baseButton.getHoverColor());

        // 初始化滑块按钮（X轴方向，调整宽度）
        this.sliderButton = new PushButton(
                startX + gap,
                startY + gap,  // Y轴留空隙
                startX + gap + (endX - startX - 2 * gap) * sliderWidthRatio,  // 宽度按比例计算
                endY - gap,// Y轴留空隙
                BaseLabel.NULL_LABEL
        );
        this.sliderButton.setBorderWidth(1);
        this.sliderButton.setBaseColor(HelloApplication.UNSELECTED_MENU_COLOR);
        this.sliderButton.setDownColor(this.sliderButton.getBaseColor());

        // 初始化动画
        this.jumpAnimation = new Animation(this.sliderButton, 100);

        // 添加子组件
        this.addChild(baseButton);
        this.addChild(sliderButton);

        // 绑定滑块按下事件（改为记录X坐标）
        this.sliderButton.setButtonPressed(event -> lastX = ((MouseEvent) event.event()).getX());

        // 绑定滑块拖拽事件（核心：改为X轴拖拽）
        this.sliderButton.background.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            // 计算X轴偏移量（原dY改为dX）
            double dX = event.getX() - lastX;
            double newSliderX = this.sliderButton.getX() + dX;

            // 计算X轴边界值
            double minX = this.baseButton.getStartX() + gap;
            double maxX = this.baseButton.getEndX() - gap - sliderButton.getWidth();

            // 边界检查（改为X轴）
            if (newSliderX >= minX && newSliderX <= maxX) {
                this.sliderButton.setX(newSliderX);
            } else {
                this.sliderButton.setX(Math.max(minX, Math.min(maxX, newSliderX)));
            }

            lastX = event.getX();

            // 计算可用宽度（原lostY改为lostX）
            double lostX = this.baseButton.getWidth() - sliderButton.getWidth() - 2 * gap;
            if (lostX <= 0) {
                this.percent = 0;
            } else {
                // 计算百分比（基于X轴位置）
                this.percent = ((this.sliderButton.getX() - getX() - gap) / lostX);
                this.percent = Math.min(1.0, Math.max(0.0, this.percent));
            }

            // 触发值变化事件（空指针保护）
            if (this.sliderValueChange != null) {
                this.sliderValueChange.accept(getPercent());
            }

            event.consume(); // 阻止事件冒泡
        });

        // 绑定基底点击事件（改为X轴计算）
        this.baseButton.setButtonClicked(event -> {
            double lostX = this.baseButton.getWidth() - sliderButton.getWidth() - 2 * gap;
            double mouseX = ((MouseEvent) event.event()).getX();

            if (lostX > 0) {
                if (mouseX >= getX() + gap && mouseX <= getX() + getWidth() - gap) {
                    this.setPercent((mouseX - getX() - gap) / lostX);
                } else if (mouseX < getX() + gap) {
                    this.setPercent(0);
                } else {
                    this.setPercent(1);
                }
                update();
            }
        });

        update();
    }

    @Override
    public void update() {
        this.baseButton.resetPos(startX, startY);
        this.baseButton.resetSize(endX - startX, endY - startY);
        // 调整滑块宽度（原高度改为宽度）
        sliderButton.setWidth(sliderWidthRatio * (this.baseButton.getWidth() - 2 * gap));

        // 计算X轴位置（原Y轴改为X轴）
        double lostX = this.baseButton.getWidth() - sliderButton.getWidth() - 2 * gap;
        if (lostX > 0) {
            sliderButton.setX(startX + gap + lostX * percent);
        } else {
            sliderButton.setX(startX + gap);
        }
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.baseButton.setDisplayVisible(visible);
        this.sliderButton.setDisplayVisible(visible);
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        // 修复重复添加组件问题：返回新实例
        return new HorizontalSlider(this.startX, this.startY, this.endX, this.endY);
    }

    // Getter & Setter 改造
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
        this.gap = Math.max(0, gap); // 防止负数空隙
        update();
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = Math.max(1, maxValue); // 防止非正数
        this.sliderWidthRatio = Math.max(0.1, 50 / getMaxValue()); // 调整宽度比例
        if (this.sliderValueChange != null) {
            this.sliderValueChange.accept(getPercent());
        }
        update();
    }

    public double getValue() {
        return this.maxValue * getPercent();
    }

    public void setValue(double value) {
        double clampedValue = Math.max(0, Math.min(maxValue, value));
        this.setPercent(clampedValue / maxValue);
        update();
    }

    /**
     * 获取百分比
     */
    public double getPercent() {
        return Math.min(1.0, Math.max(0.0, this.percent));
    }

    public void setPercent(double percent) {
        // 边界检查
        this.percent = Math.min(1.0, Math.max(0.0, percent));

        double oldX = sliderButton.getX();
        double lostX = this.baseButton.getWidth() - sliderButton.getWidth() - 2 * gap;
        double newX = startX + gap + lostX * this.percent;

        // 设置动画（X轴）
        jumpAnimation.setUpdateFunction(p -> {
            this.sliderButton.setX((newX - oldX) * p + oldX);
            return null;
        });
        AnimationEngine.getInstance().startAnimation(jumpAnimation);

        // 触发值变化事件
        if (this.sliderValueChange != null) {
            this.sliderValueChange.accept(getPercent());
        }

        update();
    }

    public Consumer<Double> getSliderValueChange() {
        return sliderValueChange;
    }

    public void setSliderValueChange(Consumer<Double> sliderValueChange) {
        this.sliderValueChange = sliderValueChange;
    }
}