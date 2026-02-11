package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class HorizontalStick extends BaseLabel {

    private final EmptyBox emptyBox;

    public HorizontalStick(double length) {
        setRectangularFrame(new RectangularFrame(0, 0, length, 0));
        emptyBox = new EmptyBox(0,0,length, 0);
        emptyBox.setFillExtensionType(FillExtensionType.CENTER);
        this.setFillExtensionType(FillExtensionType.VERTICAL);
        addChild(emptyBox);
        init(null);
    }

    @Override
    public void update() {
        super.update();
        emptyBox.setLayoutWidth(getLayoutWidth());
    }
}
