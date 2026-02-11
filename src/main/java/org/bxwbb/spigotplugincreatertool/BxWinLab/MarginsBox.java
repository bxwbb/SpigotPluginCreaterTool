package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Alignment;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class MarginsBox extends BaseLabel {

    private final VerticalStick upVerticalStick;
    private final VerticalStick downVerticalStick;
    private final HorizontalStick leftHorizontalStick;
    private final HorizontalStick rightHorizontalStick;

    private final VerticalLayout verticalLayout;
    private final HorizontalLayout horizontalLayout;

    public MarginsBox(double paddingLeft, double paddingRight, double paddingTop, double paddingBottom, BaseLabel baseLabel) {
        setRectangularFrame(new RectangularFrame(baseLabel.getLayoutX() - paddingLeft, baseLabel.getLayoutY() - paddingTop, baseLabel.getLayoutWidth() + paddingLeft + paddingRight, baseLabel.getLayoutHeight() + paddingTop + paddingBottom));
        horizontalLayout = new HorizontalLayout(getLayoutX(), getLayoutY(), getLayoutWidth(), getLayoutHeight());
        leftHorizontalStick = new HorizontalStick(paddingLeft);
        horizontalLayout.addChild(leftHorizontalStick, 0, 0);
        verticalLayout = new VerticalLayout(getLayoutX() + paddingLeft, getLayoutY(), getLayoutWidth() - paddingLeft - paddingRight, getLayoutHeight());
        verticalLayout.setAlignment(Alignment.LEFT);
        upVerticalStick = new VerticalStick(paddingTop);
        verticalLayout.addChild(upVerticalStick, 0, 0);
        verticalLayout.addChild(baseLabel, 0, 0);
        downVerticalStick = new VerticalStick(paddingBottom);
        verticalLayout.addChild(downVerticalStick, 0, 0);
        verticalLayout.setFillExtensionType(FillExtensionType.CENTER);
        horizontalLayout.addChild(verticalLayout, 0, 0);
        rightHorizontalStick = new HorizontalStick(paddingRight);
        horizontalLayout.addChild(rightHorizontalStick, 0, 0);
        horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        horizontalLayout.setAlignment(Alignment.LEFT);
        this.addChild(horizontalLayout, 0, 0);
        this.setFillExtensionType(FillExtensionType.CENTER);
        upVerticalStick.showTextBox();
        verticalLayout.showTextBox();
        downVerticalStick.showTextBox();
    }

    @Override
    public void setWrap(boolean wrap) {
        if (wrap) {
            verticalLayout.setFillExtensionType(FillExtensionType.NONE);
            verticalLayout.setWrap(true);
            horizontalLayout.setFillExtensionType(FillExtensionType.NONE);
            horizontalLayout.setWrap(true);
        } else {
            verticalLayout.setWrap(false);
            verticalLayout.setFillExtensionType(FillExtensionType.CENTER);
            horizontalLayout.setWrap(false);
            horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        }
        this.setFillExtensionType(wrap ? FillExtensionType.NONE : FillExtensionType.CENTER);
        super.setWrap(wrap);
    }

    public double getLeftPadding() {
        return leftHorizontalStick.getLayoutWidth();
    }

    public void setLeftPadding(double leftPadding) {
        leftHorizontalStick.setLayoutWidth(leftPadding);
    }

    public double getRightPadding() {
        return rightHorizontalStick.getLayoutWidth();
    }

    public void setRightPadding(double rightPadding) {
        rightHorizontalStick.setLayoutWidth(rightPadding);
    }

    public double getTopPadding() {
        return upVerticalStick.getLayoutHeight();
    }

    public void setTopPadding(double topPadding) {
        upVerticalStick.setLayoutHeight(topPadding);
    }

    public double getBottomPadding() {
        return downVerticalStick.getLayoutHeight();
    }

    public void setBottomPadding(double bottomPadding) {
        downVerticalStick.setLayoutHeight(bottomPadding);
    }

}
