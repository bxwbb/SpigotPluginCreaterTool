package org.bxwbb.spigotplugincreatertool.WindowLabel;

/**
 * 八方向枚举类 - 定义八个方向常量（上下左右 + 四个斜角方向）
 */
public enum EightDirection {
    /**
     * 上方向 - Y轴负方向
     */
    UP("UP", 0, -1),
    
    /**
     * 右上方向 - X轴正方向，Y轴负方向
     */
    UP_RIGHT("UP_RIGHT", 1, -1),
    
    /**
     * 右方向 - X轴正方向
     */
    RIGHT("RIGHT", 1, 0),
    
    /**
     * 右下方向 - X轴正方向，Y轴正方向
     */
    DOWN_RIGHT("DOWN_RIGHT", 1, 1),
    
    /**
     * 下方向 - Y轴正方向
     */
    DOWN("DOWN", 0, 1),
    
    /**
     * 左下方向 - X轴负方向，Y轴正方向
     */
    DOWN_LEFT("DOWN_LEFT", -1, 1),
    
    /**
     * 左方向 - X轴负方向
     */
    LEFT("LEFT", -1, 0),
    
    /**
     * 左上方向 - X轴负方向，Y轴负方向
     */
    UP_LEFT("UP_LEFT", -1, -1),
    CENTER("CENTER",0,0);

    // 方向名称
    private final String name;
    
    // X轴偏移量
    private final int deltaX;
    
    // Y轴偏移量
    private final int deltaY;

    /**
     * 构造函数
     * @param name 方向名称
     * @param deltaX X轴偏移量
     * @param deltaY Y轴偏移量
     */
    EightDirection(String name, int deltaX, int deltaY) {
        this.name = name;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * 获取方向名称
     * @return 方向名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取X轴偏移量
     * @return X轴偏移量
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * 获取Y轴偏移量
     * @return Y轴偏移量
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * 获取相反方向
     * @return 相反方向
     */
    public EightDirection getOpposite() {
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
     * 判断是否为基本方向（上、右、下、左）
     * @return 如果是基本方向返回true，否则返回false
     */
    public boolean isBasic() {
        return this == UP || this == RIGHT || this == DOWN || this == LEFT;
    }

    /**
     * 判断是否为斜角方向（右上、右下、左下、左上）
     * @return 如果是斜角方向返回true，否则返回false
     */
    public boolean isDiagonal() {
        return this == UP_RIGHT || this == DOWN_RIGHT || this == DOWN_LEFT || this == UP_LEFT;
    }

    public boolean isCenter() {
        return this == CENTER;
    }

    public static EightDirection getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }

    @Override
    public String toString() {
        return name;
    }
}
