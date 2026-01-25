package org.bxwbb.spigotplugincreatertool.WindowLabel;

import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

public class VerticalMenu extends BaseLabel {

    // 控件间隙
    public static double SPACING = 3;
    // 滑条宽度
    public static double SLIDER_WIDTH = 10;

    private final Stage base;
    private final VerticalLayout verticalLayout;
    private final VerticalSlider verticalSlider;
    private int labelCount = 0;

    public VerticalMenu(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Stage(startX, startY, endX, endY);
        this.base.getBackground().setFill(HelloApplication.UNSELECTED_COLOR);
        this.verticalLayout = new VerticalLayout(startX + SPACING, startY, endX - SPACING - SLIDER_WIDTH, endY);
        this.verticalLayout.setSpacing(0);
        this.base.addChild(verticalLayout);
        this.verticalSlider = new VerticalSlider(endX - SLIDER_WIDTH, startY, endX, endY);
        this.verticalSlider.setMaxValue(10);
        this.verticalSlider.setValue(0);
        this.verticalSlider.setDisplayVisible(false);
        this.verticalSlider.setSliderValueChange(value -> this.verticalLayout.setY(this.getY() - value * (this.verticalLayout.getTotalHeight() - this.getHeight())));
        this.base.addChild(this.verticalSlider);
        this.addChild(base);
    }

    @Override
    public void update() {
        this.base.resetPos(getX(), getY());
        this.base.resetSize(getWidth(), getHeight());
        this.verticalLayout.resetPos(getX() + SPACING, getY() - this.verticalSlider.getPercent() * (this.verticalLayout.getTotalHeight() - this.getHeight()));
        this.verticalLayout.resetSize(getWidth() - SPACING * 2 - SLIDER_WIDTH, getHeight());
        this.verticalSlider.resetPos(endX - SLIDER_WIDTH, getY());
        this.verticalSlider.resetSize(SLIDER_WIDTH, getHeight());
        if (this.verticalLayout.getTotalHeight() > this.getHeight()) {
            this.verticalSlider.setMaxValue(this.verticalLayout.getTotalHeight() - this.getHeight());
            this.verticalSlider.setDisplayVisible(true);
        } else {
            this.verticalSlider.setDisplayVisible(false);
            this.verticalLayout.setY(getY());
        }
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        this.base.setDisplayVisible(visible);
    }

    public void addLabel(BaseLabel label) {
        this.verticalLayout.addChild(label);
        labelCount++;
        update();
    }

    public void removeLabel(BaseLabel label) {
        this.verticalLayout.removeChild(label);
        label.delete();
        labelCount--;
        update();
    }

    public void insertLabel(BaseLabel label, int index) {
        this.verticalLayout.getChildren().add(index, label);
        labelCount++;
        update();
    }

    public int getLabelCount() {
        return this.labelCount;
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new VerticalMenu(getX(), getY(), getWidth(), getHeight());
    }

}
