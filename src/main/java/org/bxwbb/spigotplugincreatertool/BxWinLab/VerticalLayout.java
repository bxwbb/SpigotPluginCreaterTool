package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Alignment;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalLayout extends BaseLabel {

    private static final Logger log = LoggerFactory.getLogger(VerticalLayout.class);
    private Alignment alignment = Alignment.CENTER;
    // 布局间隔
    private double spacing = 0;

    public VerticalLayout(double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        init(null);
    }

    @Override
    public void update() {
        super.update();
        if (getChildren().isEmpty()) return;
        double halfWidth = getLayoutWidth() / 2;
        double positionY = getLayoutY();
        // 计算高度
        // 剩余高度
        double lostHeight = getLayoutHeight() - spacing * (getChildren().size() - 1);
        // 可变控件数
        int variableCount = 0;
        for (BaseLabel child : getChildren()) {
            if (child.getVisible()) continue;
            if (child.getFillExtensionType().isVertical()) {
                variableCount++;
            } else {
                lostHeight -= child.getLayoutHeight();
            }
        }
        if (lostHeight > 0 && variableCount > 0) {
            // 应变数
            lostHeight /= variableCount;
            for (BaseLabel child : getChildren()) {
                if (child.getVisible()) continue;
                if (child.getFillExtensionType().isVertical()) {
                    child.setLayoutHeightNoUpdate(lostHeight);
                }
            }
        }
        // 计算位置
        double halfChildWidth;
        for (BaseLabel child : getChildren()) {
            if (child.getVisible()) continue;
            halfChildWidth = child.getLayoutWidth() / 2;
            switch (getAlignment()) {
                case LEFT -> child.setLayoutXNoUpdate(getLayoutX());
                case CENTER -> child.setLayoutXNoUpdate(getLayoutX() + halfWidth - halfChildWidth);
                case RIGHT -> child.setLayoutXNoUpdate(getLayoutX() + getLayoutWidth() - child.getLayoutWidth());
            }
            child.setLayoutY(positionY);
            positionY += child.getLayoutHeight() + spacing;
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
