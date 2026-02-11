package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class VerticalStick extends BaseLabel {

    private final EmptyBox emptyBox;

    public VerticalStick(double length) {
        setRectangularFrame(new RectangularFrame(0, 0, 0, length));
        emptyBox = new EmptyBox(0,0,0, length);
        emptyBox.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.HORIZONTAL);
        addChild(emptyBox);
        init(null);
    }

    @Override
    public void update() {
        super.update();
        emptyBox.setLayoutHeight(getLayoutHeight());
    }
}
