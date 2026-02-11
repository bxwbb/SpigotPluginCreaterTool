package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class EmptyBox extends BaseLabel {

    private BaseLabel element;

    public EmptyBox(double x, double y, double width, double height, BaseLabel element) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.element = element;
        this.addChild(element);
        init(null);
    }

    public EmptyBox(double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        init(null);
    }

    public BaseLabel getElement() {
        return element;
    }

}
