package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeType;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

/**
 * 继承 BaseLabel 的折线标签，用法与 Line 一致，支持最大偏移量和周期设置
 */
public class WaveLineLabel extends BaseLabel  {

    // 折线核心属性：最大偏移量（起伏幅度，默认5像素）、周期（每个起伏的长度，默认20像素）
    private double amplitude = 5.0;
    private double period = 20.0;

    // 折线绘制核心：JavaFX Path 组件（存储折线顶点）
    private transient Path wavePath;

    // 辅助字段：名称（用于标识）、样式相关
    private String name;
    private Color strokeColor = Color.BLACK; // 默认线条颜色
    private double strokeWidth = 1.0;       // 默认线条宽度

    // 1. 构造方法（初始化容器和折线）
    public WaveLineLabel() {
        // 初始化父类的 transient Group（父类为 transient，必须手动初始化）
        this.root = new Group();
        this.base = new Group();
        // 初始化折线组件
        initWavePath();
        // 将 base（包含折线）添加到 root 容器
        this.root.getChildren().add(this.base);
    }

    // 带初始位置的构造方法（与 Line 用法一致）
    public WaveLineLabel(double startX, double startY, double endX, double endY) {
        this();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        // 初始绘制折线
        updateWavePath();
    }

    // 2. 初始化折线 Path（设置默认样式）
    private void initWavePath() {
        wavePath = new Path();
        wavePath.setStroke(strokeColor);
        wavePath.setStrokeWidth(strokeWidth);
        wavePath.setStrokeType(StrokeType.CENTERED);
        wavePath.setVisible(this.visible); // 跟随父类可见性
        // 将折线添加到 base 容器（base 用于统一管理组件）
        this.base.getChildren().add(wavePath);
    }

    // 3. 核心逻辑：计算并更新折线（基于正弦曲线，平滑起伏）
    private void updateWavePath() {
        if (wavePath == null) initWavePath(); // 防止序列化后空指针

        // 清空原有路径
        wavePath.getElements().clear();

        // 边界条件：起点终点重合 或 周期≤0 → 不绘制
        double dx = endX - startX;
        double dy = endY - startY;
        double lineLength = Math.hypot(dx, dy); // 起点到终点的直线距离
        if (lineLength < 0.1 || period <= 0) {
            return;
        }

        // 退化条件：偏移量≤0 → 绘制直线（与 Line 行为一致）
        if (amplitude <= 0.1) {
            wavePath.getElements().addAll(
                    new MoveTo(startX, startY),
                    new LineTo(endX, endY)
            );
            return;
        }

        // 折线参数：步长（越小越平滑，默认2像素，平衡性能）
        double step = 2.0;
        int pointCount = (int) Math.ceil(lineLength / step); // 顶点总数
        double lineAngle = Math.atan2(dy, dx); // 直线与X轴夹角（弧度）

        // 起点顶点
        wavePath.getElements().add(new MoveTo(startX, startY));

        // 生成中间起伏顶点
        for (int i = 1; i < pointCount; i++) {
            // 当前点在直线上的距离（从起点出发）
            double distance = i * step;
            // 直线上的基础坐标（无偏移）
            double baseX = startX + distance * Math.cos(lineAngle);
            double baseY = startY + distance * Math.sin(lineAngle);

            // 正弦曲线偏移计算：y = A * sin(2πx/T)，A=偏移量，T=周期
            double offsetRatio = Math.sin(2 * Math.PI * distance / period);
            // 偏移方向：垂直于直线（避免沿直线方向偏移，视觉更合理）
            double offsetX = -amplitude * offsetRatio * Math.sin(lineAngle);
            double offsetY = amplitude * offsetRatio * Math.cos(lineAngle);

            // 最终顶点坐标（基础坐标 + 垂直偏移）
            double finalX = baseX + offsetX;
            double finalY = baseY + offsetY;

            wavePath.getElements().add(new LineTo(finalX, finalY));
        }

        // 终点顶点（确保折线精准结束在 endX/endY）
        wavePath.getElements().add(new LineTo(endX, endY));
    }

    // 4. 实现 BaseLabel 所有抽象方法
    @Override
    public void resetPos(double x, double y) {
        this.startX = x;
        this.startY = y;
        updateWavePath(); // 位置变化，更新折线
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = startX + width;
        this.endY = startY + height;
        updateWavePath(); // 尺寸变化，更新折线
    }

    @Override
    public void delete() {
        // 移除所有子组件，释放资源
        if (wavePath != null) {
            this.base.getChildren().remove(wavePath);
            wavePath = null;
        }
        if (this.base != null) {
            this.root.getChildren().remove(this.base);
            this.base = null;
        }
        // 从父容器中移除自身
        if (this.root != null && this.root.getParent() != null) {
            ((Group) this.root.getParent()).getChildren().remove(this.root);
            this.root = null;
        }
    }

    @Override
    public void addTo(Group root) {
        // 将当前组件的 root 添加到外部容器
        if (this.root != null && !root.getChildren().contains(this.root)) {
            root.getChildren().add(this.root);
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getWidth() {
        return Math.abs(endX - startX); // 宽度=终点X-起点X（取绝对值）
    }

    @Override
    public double getHeight() {
        return Math.abs(endY - startY); // 高度=终点Y-起点Y（取绝对值）
    }

    @Override
    public void autoWidth() {
        // 折线宽度由 startX/endX 决定，无需自动调整（如需可扩展）
        updateWavePath();
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (wavePath != null) {
            wavePath.setVisible(visible);
        }
        if (this.base != null) {
            this.base.setVisible(visible);
        }
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object data) {
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        // 创建新实例，复制当前所有属性
        WaveLineLabel newLine = new WaveLineLabel();
        newLine.setData(this.getData());
        return newLine;
    }

    @Override
    public Node.VarType getVarType() {
        // 请根据你的 Node.VarType 枚举调整（例如新增 WAVE_LINE 类型）
        // 若枚举中无对应类型，可返回默认类型（如 Node.VarType.LINE）或扩展枚举
        return Node.VarType.__DEFAULT__;
    }

    // 5. 新增：折线专属属性 Setter/Getter
    // 最大偏移量（起伏幅度，不能为负数）
    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        if (amplitude < 0) {
            throw new IllegalArgumentException("最大偏移量不能为负数");
        }
        this.amplitude = amplitude;
        updateWavePath();
    }

    // 周期（每个起伏的长度，不能为负数）
    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        if (period < 0) {
            throw new IllegalArgumentException("周期不能为负数");
        }
        this.period = period;
        updateWavePath();
    }

    // 6. 样式设置（与 Line 用法一致）
    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
        if (wavePath != null) {
            wavePath.setStroke(strokeColor);
        }
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        if (strokeWidth < 0) {
            throw new IllegalArgumentException("线条宽度不能为负数");
        }
        this.strokeWidth = strokeWidth;
        if (wavePath != null) {
            wavePath.setStrokeWidth(strokeWidth);
        }
    }

    // 设置虚线样式（例如：setStrokeDashArray(5,2) 表示5像素实线+2像素空白）
    public void setStrokeDashArray(double... dashSegments) {
        if (wavePath != null) {
            wavePath.getStrokeDashArray().clear();
            for (double segment : dashSegments) {
                wavePath.getStrokeDashArray().add(segment);
            }
        }
    }

    public String getName() {
        return name;
    }

    // 8. 可选：直接操作 endX/endY 的 Setter（与 Line 用法完全对齐）
    public void setEndX(double endX) {
        this.endX = endX;
        updateWavePath();
    }

    public void setEndY(double endY) {
        this.endY = endY;
        updateWavePath();
    }

    public void setStartX(double startX) {
        this.startX = startX;
        updateWavePath();
    }

    public void setStartY(double startY) {
        this.startY = startY;
        updateWavePath();
    }
}