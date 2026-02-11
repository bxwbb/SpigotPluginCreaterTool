package org.bxwbb.spigotplugincreatertool.BxWinLab.Enum;

/**
 * 方向枚举（八向+中心）
 * 适用于JavaFX组件定位、移动、布局等场景
 * 完善点：添加访问器、工具方法、语义化注释、边界检查
 */
public enum Direction {
    // 八方向 + 中心（注释说明偏移含义：xD/x轴偏移，yD/y轴偏移）
    UP(0, 1, "上"),          // Y轴正方向（上）
    UP_RIGHT(1, 1, "右上"),  // X轴正+Y轴正（右上）
    RIGHT(1, 0, "右"),       // X轴正方向（右）
    DOWN_RIGHT(1, -1, "右下"),// X轴正+Y轴负（右下）
    DOWN(0, -1, "下"),       // Y轴负方向（下）
    DOWN_LEFT(-1, -1, "左下"),// X轴负+Y轴负（左下）
    LEFT(-1, 0, "左"),       // X轴负方向（左）
    UP_LEFT(-1, 1, "左上"),  // X轴负+Y轴正（左上）
    CENTER(0, 0, "中心");    // 无偏移（中心）

    // 原始偏移量（私有不可变）
    private final double xDelta;
    private final double yDelta;
    // 中文名称（便于日志/显示）
    private final String chineseName;

    /**
     * 构造方法（私有化，仅枚举内部使用）
     * @param xDelta X轴偏移量（-1=左，0=无，1=右）
     * @param yDelta Y轴偏移量（-1=下，0=无，1=上）
     * @param chineseName 中文名称
     */
    Direction(double xDelta, double yDelta, String chineseName) {
        // 边界检查：确保偏移量仅为-1/0/1，避免非法值
        this.xDelta = validateDelta(xDelta);
        this.yDelta = validateDelta(yDelta);
        this.chineseName = chineseName;
    }

    /**
     * 验证偏移量合法性（仅允许-1、0、1）
     */
    private double validateDelta(double delta) {
        if (delta != -1 && delta != 0 && delta != 1) {
            throw new IllegalArgumentException("方向偏移量只能是-1、0、1，当前值：" + delta);
        }
        return delta;
    }

    // ========== 基础访问器 ==========
    /**
     * 获取X轴偏移量
     * @return -1（左）、0（无）、1（右）
     */
    public double getXDelta() {
        return xDelta;
    }

    /**
     * 获取Y轴偏移量
     * @return -1（下）、0（无）、1（上）
     */
    public double getYDelta() {
        return yDelta;
    }

    /**
     * 获取中文名称（便于日志/UI显示）
     */
    public String getChineseName() {
        return chineseName;
    }

    // ========== 实用工具方法（核心扩展） ==========
    /**
     * 根据偏移量计算目标坐标（JavaFX组件定位常用）
     * @param startX 起始X坐标
     * @param startY 起始Y坐标
     * @param step 步长（每次移动的像素数）
     * @return 目标坐标数组 [目标X, 目标Y]
     */
    public double[] calculateTargetPos(double startX, double startY, double step) {
        double targetX = startX + (this.xDelta * step);
        double targetY = startY + (this.yDelta * step);
        return new double[]{targetX, targetY};
    }

    /**
     * 判断是否为水平方向（左/右）
     */
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * 判断是否为垂直方向（上/下）
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    /**
     * 判断是否为对角线方向（右上/右下/左下/左上）
     */
    public boolean isDiagonal() {
        return switch (this) {
            case UP_RIGHT, DOWN_RIGHT, DOWN_LEFT, UP_LEFT -> true;
            default -> false;
        };
    }

    /**
     * 判断是否为中心（无偏移）
     */
    public boolean isCenter() {
        return this == CENTER;
    }

    /**
     * 获取反方向（比如UP → DOWN，RIGHT → LEFT）
     */
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case UP_RIGHT -> DOWN_LEFT;
            case RIGHT -> LEFT;
            case DOWN_RIGHT -> UP_LEFT;
            case DOWN -> UP;
            case DOWN_LEFT -> UP_RIGHT;
            case LEFT -> RIGHT;
            case UP_LEFT -> DOWN_RIGHT;
            case CENTER -> CENTER;
        };
    }

    /**
     * 重写toString，便于日志输出
     */
    @Override
    public String toString() {
        return String.format("%s (x偏移: %.0f, y偏移: %.0f)", chineseName, xDelta, yDelta);
    }
}