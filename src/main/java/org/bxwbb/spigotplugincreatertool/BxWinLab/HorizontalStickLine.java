package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class HorizontalStickLine extends BaseLabel {

    private final DirectionalLayout directionalLayout;
    private final VerticalLayout verticalLayout;
    private final VerticalStick verticalStick;
    private final LineBox lineBox;
    // 内凹值
    private double narrow = 0;

    public  HorizontalStickLine(double length, double narrow) {
        setRectangularFrame(new RectangularFrame(0, 0, length, 0));
        this.narrow = narrow;
        lineBox = new LineBox(0, 0, 1, length - narrow * 2);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout = new VerticalLayout(0, 0, 0, 0);
        verticalLayout.setFillExtensionType(FillExtensionType.CENTER);
        verticalStick = new VerticalStick(narrow);
        verticalLayout.addChild(verticalStick);
        verticalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, length, 0, verticalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    public HorizontalStickLine(double length) {
        setRectangularFrame(new RectangularFrame(0, 0, length, 0));
        lineBox = new LineBox(0, 0, length, 1);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout = new VerticalLayout(0, 0, 0, 0);
        verticalLayout.setFillExtensionType(FillExtensionType.CENTER);
        verticalStick = new VerticalStick(narrow);
        verticalLayout.addChild(verticalStick);
        verticalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, length, 0, verticalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    public HorizontalStickLine() {
        setRectangularFrame(new RectangularFrame(0, 0, 1, 0));
        lineBox = new LineBox(0, 0, 1, 1);
        lineBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout = new VerticalLayout(0, 0, 0, 0);
        verticalLayout.setFillExtensionType(FillExtensionType.CENTER);
        verticalStick = new VerticalStick(narrow);
        verticalLayout.addChild(verticalStick);
        verticalLayout.addChild(lineBox);
        directionalLayout = new DirectionalLayout(0, 0, 1, 0, verticalLayout);
        directionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(directionalLayout);
        init(null);
    }

    @Override
    public void update() {
        super.update();
        directionalLayout.setLayoutWidth(getLayoutWidth());
        verticalStick.setLayoutHeight(getNarrow());
        lineBox.setLayoutHeight(getLayoutHeight() - getNarrow() * 2);
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
