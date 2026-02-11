package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class VerticalStickLine extends BaseLabel {

    private final DirectionalLayout directionalLayout;
    private final HorizontalLayout horizontalLayout;
    private final HorizontalStick horizontalStick;
    private final LineBox lineBox;
    // 内凹值
    private double narrow = 0;

    public VerticalStickLine(double length, double narrow) {
        setRectangularFrame(new RectangularFrame(0, 0, 0, length));
        this.narrow = narrow;
        lineBox = new LineBox(0, 0, length - narrow * 2, 1);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        horizontalLayout = new HorizontalLayout(0, 0, 0, 0);
        horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        horizontalStick = new HorizontalStick(narrow);
        horizontalLayout.addChild(horizontalStick);
        horizontalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, 0, length, horizontalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    public VerticalStickLine(double length) {
        setRectangularFrame(new RectangularFrame(0, 0, 0, length));
        lineBox = new LineBox(0, 0, length, 1);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        horizontalLayout = new HorizontalLayout(0, 0, 0, 0);
        horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        horizontalStick = new HorizontalStick(narrow);
        horizontalLayout.addChild(horizontalStick);
        horizontalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, 0, length, horizontalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    public VerticalStickLine() {
        setRectangularFrame(new RectangularFrame(0, 0, 0, 1));
        lineBox = new LineBox(0, 0, 1, 1);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        horizontalLayout = new HorizontalLayout(0, 0, 0, 0);
        horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        horizontalStick = new HorizontalStick(narrow);
        horizontalLayout.addChild(horizontalStick);
        horizontalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, 0, 1, horizontalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    @Override
    public void update() {
        super.update();
        directionalLayout.setLayoutHeight(getLayoutHeight());
        horizontalStick.setLayoutWidth(getNarrow());
        lineBox.setLayoutWidth(getLayoutWidth() - getNarrow() * 2);
    }

    public LineBox getLineBox() {
        return lineBox;
    }

    public double getNarrow() {
        return narrow;
    }

    public void setNarrow(double narrow) {
        this.narrow = narrow;
        update();
    }
}
