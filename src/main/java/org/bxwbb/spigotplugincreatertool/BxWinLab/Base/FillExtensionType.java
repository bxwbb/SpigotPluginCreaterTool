package org.bxwbb.spigotplugincreatertool.BxWinLab.Base;

public enum FillExtensionType {

    // 水平延展
    HORIZONTAL(true, false),
    // 垂直延展
    VERTICAL(false, true),
    // 不延展
    NONE(false, false),
    // 中心延展
    CENTER(true, true);

    private final boolean isHorizontal;
    private final boolean isVertical;

    FillExtensionType(boolean isHorizontal, boolean isVertical) {
        this.isHorizontal = isHorizontal;
        this.isVertical = isVertical;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isVertical() {
        return isVertical;
    }

    @Override
    public String toString() {
        return "FillExtensionType(" + name() +"){" +
                "isHorizontal=" + isHorizontal() +
                ", isVertical=" + isVertical() +
                '}';
    }
}
