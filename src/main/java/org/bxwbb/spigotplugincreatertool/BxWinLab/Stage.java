package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class Stage extends BaseLabel {

    private final Rectangle baseRectangle;
    private final Rectangle maskRectangle;
    private boolean hasMask = true;

    private Paint baseColor = ColorSetting.CONTROL_BASE_COLOR;

    public Stage (double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        baseRectangle = new Rectangle(x, y, width, height);
        baseRectangle.setFill(this.baseColor);
        baseRectangle.setArcWidth(ColorSetting.ROUNDNESS);
        baseRectangle.setArcHeight(ColorSetting.ROUNDNESS);
        maskRectangle = new Rectangle(x, y, width, height);
        maskRectangle.setArcWidth(ColorSetting.ROUNDNESS);
        maskRectangle.setArcHeight(ColorSetting.ROUNDNESS);
        getBase().setClip(maskRectangle);
        init(baseRectangle);
    }

    @Override
    public void update() {
        super.update();
        baseRectangle.setX(getLayoutX());
        baseRectangle.setY(getLayoutY());
        baseRectangle.setWidth(getLayoutWidth());
        baseRectangle.setHeight(getLayoutHeight());
        maskRectangle.setX(getLayoutX());
        maskRectangle.setY(getLayoutY());
        maskRectangle.setWidth(getLayoutWidth());
        maskRectangle.setHeight(getLayoutHeight());
        maskRectangle.setArcWidth(baseRectangle.getArcWidth());
        maskRectangle.setArcHeight(baseRectangle.getArcHeight());
    }

    public Rectangle getBaseRectangle() {
        return baseRectangle;
    }

    @Override
    protected void addTo(Group root) {
        if (!getBase().getChildren().contains(baseRectangle)) getBase().getChildren().add(baseRectangle);
        super.addTo(root);
    }

    public boolean isHasMask() {
        return hasMask;
    }

    public void setHasMask(boolean hasMask) {
        this.hasMask = hasMask;
        if (hasMask) {
            baseRectangle.setClip(baseRectangle);
        } else {
            baseRectangle.setClip(null);
        }
    }

    public Rectangle getMaskRectangle() {
        return maskRectangle;
    }

    public Paint getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Paint baseColor) {
        this.baseColor = baseColor;
        baseRectangle.setFill(baseColor);
    }

    @Override
    public void setVisible(boolean visible) {
        baseRectangle.setVisible(visible);
        super.setVisible(visible);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        baseRectangle.setMouseTransparent(transparent);
        super.setMouseTransparent(transparent);
    }
}
