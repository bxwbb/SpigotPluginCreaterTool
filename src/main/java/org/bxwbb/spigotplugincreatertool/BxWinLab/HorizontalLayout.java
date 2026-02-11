package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Alignment;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class HorizontalLayout extends BaseLabel {

    private Alignment alignment = Alignment.CENTER;
    // 布局间隔
    private double spacing = 0;

    public HorizontalLayout(double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        init(null);
    }

    @Override
    public void update() {
        super.update();
        if (getChildren().isEmpty()) return;
        double halfHeight = getLayoutHeight() / 2;
        double positionX = getLayoutX();
        // 计算宽度
        // 剩余宽度
        double lostWidth = getLayoutWidth() - spacing * (getChildren().size() - 1);
        // 可变控件数
        int variableCount = 0;
        for (BaseLabel child : getChildren()) {
            if (child.getVisible()) continue;
            if (child.getFillExtensionType().isHorizontal()) {
                variableCount++;
            } else {
                lostWidth -= child.getLayoutWidth();
            }
        }
        if (lostWidth > 0 && variableCount > 0) {
            // 应变数
            lostWidth /= variableCount;
            for (BaseLabel child : getChildren()) {
                if (child.getVisible()) continue;
                if (child.getFillExtensionType().isHorizontal()) {
                    child.setLayoutWidthNoUpdate(lostWidth);
                }
            }
        }
        // 计算位置
        double halfChildHeight;
        for (BaseLabel child : getChildren()) {
            if (child.getVisible()) continue;
            halfChildHeight = child.getLayoutHeight() / 2;
            switch (getAlignment()) {
                case LEFT -> child.setLayoutYNoUpdate(getLayoutY());
                case CENTER -> child.setLayoutYNoUpdate(getLayoutY() + halfHeight - halfChildHeight);
                case RIGHT -> child.setLayoutYNoUpdate(getLayoutY() + getLayoutHeight() - child.getLayoutHeight());
            }
            child.setLayoutX(positionX);
            positionX += child.getLayoutWidth() + spacing;
        }
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        update();
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
        update();
    }
}
