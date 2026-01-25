package org.bxwbb.spigotplugincreatertool.WindowLabel;

import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

/**
 * 垂直布局容器
 * 功能：将子组件沿Y轴垂直排列，支持间距、对齐方式、自动适配尺寸
 */
public class VerticalLayout extends BaseLabel {
    // 子元素间隔
    private double spacing = 0.0;
    private EightDirection horizontalAlignment = EightDirection.LEFT;

    // 是否自动适配布局容器的宽度/高度
    private boolean autoFitWidth = true;

    public VerticalLayout(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * 核心布局更新方法
     * 优化点：支持对齐方式、自动适配尺寸、空指针保护、边界检查
     */
    @Override
    public void update() {
        // 空指针/空集合保护
        if (getChildren() == null || getChildren().isEmpty()) {
            return;
        }

        double currentY; // 当前布局的Y起始位置
        double maxChildWidth = getWidth();    // 子组件最大宽度

        // 第一步：计算子组件最大宽度和总高度
        for (BaseLabel child : getChildren()) {
            if (child == null || !child.isVisible()) {
                continue; // 跳过空组件和不可见组件
            }
            maxChildWidth = Math.max(maxChildWidth, child.getWidth());
        }

        // 第三步：排列子组件（支持水平对齐）
        currentY = this.getY(); // 重置起始Y坐标
        double layoutWidth = this.getWidth();

        for (BaseLabel child : getChildren()) {
            if (child == null || !child.isVisible()) {
                continue;
            }

            // 根据水平对齐方式计算子组件的X坐标
            double childX = calculateChildX(child.getWidth(), layoutWidth);

            // 设置子组件位置
            child.resetPos(childX, currentY - child.getHeight() / 2);
            if (autoFitWidth) {
                child.setWidth(maxChildWidth);
            }
            // 更新下一个组件的Y坐标
            currentY += child.getHeight() / 2 + spacing;

            // 强制更新子组件
            child.update();
        }
    }

    /**
     * 根据水平对齐方式计算子组件的X坐标
     */
    private double calculateChildX(double childWidth, double layoutWidth) {
        return switch (horizontalAlignment) {
            case LEFT -> this.getX(); // 左对齐：与布局容器左边界对齐
            case CENTER -> this.getX() + (layoutWidth - childWidth) / 2; // 居中对齐
            case RIGHT -> this.getX() + layoutWidth - childWidth; // 右对齐
            default -> this.getX();
        };
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        if (getChildren() != null) {
            for (BaseLabel child : getChildren()) {
                if (child != null) {
                    child.setDisplayVisible(visible);
                }
            }
        }
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        VerticalLayout newLayout = new VerticalLayout(startX, startY, endX, endY);
        newLayout.setSpacing(this.spacing);
        newLayout.setHorizontalAlignment(this.horizontalAlignment);
        newLayout.setAutoFitWidth(this.autoFitWidth);
        return newLayout;
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
        update();
    }

    public EightDirection getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * 设置子组件水平对齐方式
     */
    public void setHorizontalAlignment(EightDirection horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment != null ? horizontalAlignment : EightDirection.LEFT;
        update();
    }

    public boolean isAutoFitWidth() {
        return autoFitWidth;
    }

    /**
     * 设置是否自动适配宽度（适配为最大子组件宽度）
     */
    public void setAutoFitWidth(boolean autoFitWidth) {
        this.autoFitWidth = autoFitWidth;
        update();
    }

    /**
     * 获取子组件的总高度
     */
    public double getTotalHeight() {
        return getChildren().stream()
                .filter(child -> child != null && child.isVisible())
                .mapToDouble(child -> child.getHeight() / 2 + spacing)
                .sum();
    }

}