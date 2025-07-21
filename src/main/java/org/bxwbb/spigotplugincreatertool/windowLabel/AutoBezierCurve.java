package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.paint.Color;

/**
 * 分段绘制的贝塞尔曲线（沿路径精确渐变）
 * 核心：将曲线拆分为多段，每段使用插值颜色，模拟沿路径的渐变效果
 */
public class AutoBezierCurve extends Group {
    // 曲线核心参数
    public double startX, startY;
    public double endX, endY;

    private double control1X, control1Y; // 第一个控制点
    private double control2X, control2Y; // 第二个控制点
    private double controlRatio = 0.4;   // 控制点距离比例（Blender风格）

    // 渐变参数
    private Color startColor = Color.rgb(100, 100, 255); // 起点颜色（浅蓝色）
    private Color endColor = Color.rgb(255, 100, 100);   // 终点颜色（浅红色）
    private int segments = 30; // 分段数量（越多渐变越平滑，建议20-50）

    // 样式参数
    private double strokeWidth = 1.5;
    private boolean isStrokeRound = true;

    public AutoBezierCurve(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        calculateControlPoints(); // 计算控制点
        redrawCurve(); // 初始绘制
    }

    /**
     * 计算Blender风格的控制点（水平优先）
     */
    private void calculateControlPoints() {
        double dx = endX - startX;
        double horizontalDistance = Math.abs(dx);

        // 水平延伸的控制点（Blender节点编辑器风格）
        double handleLength = Math.max(horizontalDistance * controlRatio, 40); // 最小延伸40px
        control1X = startX + handleLength; // 起点控制点：向右延伸
        control1Y = startY;
        control2X = endX - handleLength;   // 终点控制点：向左延伸
        control2Y = endY;
    }

    /**
     * 重绘整个曲线（核心方法）
     * 1. 清除现有分段
     * 2. 按比例计算每个分段的起点、终点和颜色
     * 3. 绘制分段曲线并添加到容器
     */
    public void redrawCurve() {
        getChildren().clear(); // 清除之前的分段

        // 遍历所有分段（t从0到1，步长为1/segments）
        for (int i = 0; i < segments; i++) {
            double tStart = (double) i / segments;       // 当前段起点在曲线上的比例
            double tEnd = (double) (i + 1) / segments;   // 当前段终点在曲线上的比例

            // 1. 计算当前分段的起点和终点（贝塞尔曲线上的点）
            Point2D segmentStart = getBezierPoint(tStart);
            Point2D segmentEnd = getBezierPoint(tEnd);

            // 2. 计算当前分段的控制点（确保分段曲线平滑衔接）
            Point2D segmentControl1 = calculateSegmentControl(segmentStart, segmentEnd);
            Point2D segmentControl2 = calculateSegmentControl(segmentEnd, segmentStart);

            // 3. 计算当前分段的颜色（基于中间位置的比例插值）
            double tMid = (tStart + tEnd) / 2; // 分段中间点在整条曲线的比例
            Color segmentColor = interpolateColor(startColor, endColor, tMid);

            // 4. 创建分段曲线并设置样式
            Path segment = new Path();
            segment.getElements().add(new MoveTo(segmentStart.getX(), segmentStart.getY()));
            segment.getElements().add(new CubicCurveTo(
                    segmentControl1.getX(), segmentControl1.getY(),
                    segmentControl2.getX(), segmentControl2.getY(),
                    segmentEnd.getX(), segmentEnd.getY()
            ));

            // 设置分段样式（确保与整体风格一致）
            segment.setStroke(segmentColor);
            segment.setStrokeWidth(strokeWidth);
            segment.setStrokeLineCap(isStrokeRound ? javafx.scene.shape.StrokeLineCap.ROUND : javafx.scene.shape.StrokeLineCap.BUTT);
            segment.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            segment.setFill(Color.TRANSPARENT);

            // 添加到容器
            getChildren().add(segment);
        }
    }

    /**
     * 计算贝塞尔曲线上某比例位置的点（核心公式）
     * @param t 比例（0=起点，1=终点）
     * @return 曲线上的点坐标
     */
    private Point2D getBezierPoint(double t) {
        double u = 1 - t; // 补数（u + t = 1）
        // 三次贝塞尔曲线公式：B(t) = u³P0 + 3u²tP1 + 3ut²P2 + t³P3
        double x = u*u*u * startX
                + 3*u*u*t * control1X
                + 3*u*t*t * control2X
                + t*t*t * endX;

        double y = u*u*u * startY
                + 3*u*u*t * control1Y
                + 3*u*t*t * control2Y
                + t*t*t * endY;

        return new Point2D(x, y);
    }

    /**
     * 计算分段曲线的控制点（确保分段之间平滑过渡）
     * @param point 分段的起点/终点
     * @param nextPoint 分段的下一个点
     * @return 分段的控制点
     */
    private Point2D calculateSegmentControl(Point2D point, Point2D nextPoint) {
        // 基于原曲线的切线方向计算分段控制点，确保平滑
        double tangentWeight = 0.3; // 切线权重（控制弯曲程度）
        double dx = nextPoint.getX() - point.getX();
        double dy = nextPoint.getY() - point.getY();
        return new Point2D(
                point.getX() + dx * tangentWeight,
                point.getY() + dy * tangentWeight
        );
    }

    /**
     * 颜色插值（从start到end按比例过渡）
     * @param start 起始颜色
     * @param end 结束颜色
     * @param ratio 比例（0=start，1=end）
     * @return 插值后的颜色
     */
    private Color interpolateColor(Color start, Color end, double ratio) {
        // 确保比例在0-1之间
        ratio = Math.max(0, Math.min(1, ratio));
        // RGBA通道分别插值
        double r = start.getRed() + (end.getRed() - start.getRed()) * ratio;
        double g = start.getGreen() + (end.getGreen() - start.getGreen()) * ratio;
        double b = start.getBlue() + (end.getBlue() - start.getBlue()) * ratio;
        double a = start.getOpacity() + (end.getOpacity() - start.getOpacity()) * ratio;
        return new Color(r, g, b, a);
    }

    // ==================== 对外API（允许动态修改参数） ====================

    /**
     * 设置起点坐标
     */
    public void setStartPoint(double x, double y) {
        this.startX = x;
        this.startY = y;
        calculateControlPoints(); // 重新计算控制点
        redrawCurve(); // 重绘曲线
    }

    /**
     * 设置终点坐标
     */
    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
        calculateControlPoints();
        redrawCurve();
    }

    /**
     * 设置渐变颜色
     */
    public void setGradientColors(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
        redrawCurve(); // 颜色变化需要重绘所有分段
    }

    /**
     * 设置分段数量（影响渐变平滑度）
     * @param segments 建议值：20-50（值越大越平滑，但性能消耗略高）
     */
    public void setSegmentCount(int segments) {
        this.segments = Math.max(5, segments); // 最少5段，避免过度简化
        redrawCurve();
    }

    /**
     * 设置线宽
     */
    public void setLineWidth(double width) {
        this.strokeWidth = width;
        redrawCurve();
    }

    /**
     * 设置是否使用圆角线帽
     */
    public void setRoundCap(boolean round) {
        this.isStrokeRound = round;
        redrawCurve();
    }

    /**
     * 设置控制点比例（影响曲线弯曲程度）
     */
    public void setControlRatio(double ratio) {
        this.controlRatio = Math.max(0.1, Math.min(0.8, ratio)); // 限制在0.1-0.8之间
        calculateControlPoints();
        redrawCurve();
    }

}