package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.Button;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;

public class ImageTextButtonBox extends ButtonBox implements Button {

    private final HorizontalLayout horizontalLayout;
    private final MarginsBox marginsBox;

    public ImageTextButtonBox(double x, double y, double width, double height, ImageBox imageBox, TextBox textBox) {
        super(x - 5, y - 5, width + 10, height + 10, BaseLabel.NULL_LABEL);
        horizontalLayout = new HorizontalLayout(0, 0, width, height);
        horizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        imageBox.setFillExtensionType(FillExtensionType.VERTICAL);
        horizontalLayout.addChild(imageBox);
        textBox.setFillExtensionType(FillExtensionType.CENTER);
        horizontalLayout.addChild(new HorizontalStick(10));
        horizontalLayout.addChild(textBox);
        horizontalLayout.setMouseTransparent(true);
        marginsBox = new MarginsBox(5, 5, 5, 5, horizontalLayout);
//        setBaseLabel(horizontalLayout);
        setBaseLabel(marginsBox);
    }

    public HorizontalLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public MarginsBox getMarginsBox() {
        return marginsBox;
    }

}
