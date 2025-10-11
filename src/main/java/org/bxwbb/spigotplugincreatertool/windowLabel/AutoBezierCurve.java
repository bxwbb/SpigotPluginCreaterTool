package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 分段绘制的贝塞尔曲线（沿路径精确渐变）
 * 支持多个独立的单次信号粒子流动画
 */
public class AutoBezierCurve extends Group {
    // 曲线核心参数
    public double startX, startY;
    public double endX, endY;

    private double control1X, control1Y; // 第一个控制点
    private double control2X, control2Y; // 第二个控制点
    private double controlRatio = 0.4;   // 控制点距离比例（Blender风格）

    // 渐变参数
    transient private Color startColor = Color.rgb(100, 100, 255); // 起点颜色（浅蓝色）
    transient private Color endColor = Color.rgb(255, 100, 100);   // 终点颜色（浅红色）
    private int segments = 30; // 分段数量（越多渐变越平滑，建议20-50）

    // 样式参数
    private double strokeWidth = 1.5;
    private boolean isStrokeRound = true;

    // 信号粒子参数
    private double particleSpeed = 1.0;  // 粒子移动速度
    private double particleSize = 0.15;  // 粒子大小（占曲线长度的比例）
    private double particleTrail = 0.3;  // 粒子尾迹长度比例

    // 动画管理
    private List<SignalAnimation> activeAnimations = new ArrayList<>();
    private AnimationTimer animationTimer;
    private boolean isAnimationRunning = false;

    public AutoBezierCurve(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        calculateControlPoints(); // 计算控制点
        redrawCurve(); // 初始绘制
        initAnimationTimer();
    }

    /**
     * 初始化动画计时器
     */
    private void initAnimationTimer() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // 将纳秒转换为秒
                double currentTime = now / 1_000_000_000.0;
                updateAnimations(currentTime);
            }
        };
    }

    /**
     * 启动一个新的信号动画（只播放一次）
     * @param particleColor 粒子颜色
     * @param duration 动画持续时间（秒）
     */
    public void startSignalAnimation(Color particleColor, double duration) {
        // 添加新动画到活跃列表
        activeAnimations.add(new SignalAnimation(
                particleColor,
                System.nanoTime() / 1_000_000_000.0,  // 开始时间（秒）
                duration
        ));

        // 如果动画计时器未运行，则启动它
        if (!isAnimationRunning) {
            animationTimer.start();
            isAnimationRunning = true;
        }
    }

    /**
     * 更新所有活跃动画并重新绘制曲线
     */
    private void updateAnimations(double currentTime) {
        // 清除过期动画
        Iterator<SignalAnimation> iterator = activeAnimations.iterator();
        while (iterator.hasNext()) {
            SignalAnimation anim = iterator.next();
            if (currentTime - anim.startTime > anim.duration) {
                iterator.remove();
            }
        }

        // 如果没有活跃动画了，停止计时器
        if (activeAnimations.isEmpty()) {
            animationTimer.stop();
            isAnimationRunning = false;
            redrawCurve(); // 绘制原始曲线
            return;
        }

        // 绘制带有所有活跃动画的曲线
        drawWithAnimations(currentTime);
    }

    /**
     * 根据当前所有活跃动画绘制曲线
     */
    private void drawWithAnimations(double currentTime) {
        getChildren().clear(); // 清除之前的绘制

        // 遍历所有分段
        for (int i = 0; i < segments; i++) {
            double tStart = (double) i / segments;
            double tEnd = (double) (i + 1) / segments;
            double tMid = (tStart + tEnd) / 2;

            // 计算当前分段的起点和终点
            Point2D segmentStart = getBezierPoint(tStart);
            Point2D segmentEnd = getBezierPoint(tEnd);

            // 计算当前分段的控制点
            Point2D segmentControl1 = calculateSegmentControl(segmentStart, segmentEnd);
            Point2D segmentControl2 = calculateSegmentControl(segmentEnd, segmentStart);

            // 基础颜色（渐变）
            Color baseColor = interpolateColor(startColor, endColor, tMid);

            // 计算所有活跃动画对当前分段的影响
            Color segmentColor = calculateCombinedColor(baseColor, tMid, currentTime);

            // 创建分段曲线
            Path segment = new Path();
            segment.getElements().add(new MoveTo(segmentStart.getX(), segmentStart.getY()));
            segment.getElements().add(new CubicCurveTo(
                    segmentControl1.getX(), segmentControl1.getY(),
                    segmentControl2.getX(), segmentControl2.getY(),
                    segmentEnd.getX(), segmentEnd.getY()
            ));

            // 设置样式
            segment.setStroke(segmentColor);
            segment.setStrokeWidth(strokeWidth);
            segment.setStrokeLineCap(isStrokeRound ? StrokeLineCap.ROUND : StrokeLineCap.BUTT);
            segment.setStrokeLineJoin(StrokeLineJoin.ROUND);
            segment.setFill(Color.TRANSPARENT);

            getChildren().add(segment);
        }
    }

    /**
     * 计算多个动画叠加后的最终颜色
     */
    private Color calculateCombinedColor(Color baseColor, double tMid, double currentTime) {
        // 从基础颜色开始
        double r = baseColor.getRed();
        double g = baseColor.getGreen();
        double b = baseColor.getBlue();
        double a = baseColor.getOpacity();

        // 叠加每个活跃动画的影响
        for (SignalAnimation anim : activeAnimations) {
            // 计算动画进度（0到1）
            double progress = (currentTime - anim.startTime) / anim.duration;
            if (progress < 0 || progress > 1) continue;

            // 计算粒子在曲线上的位置
            double particlePos = progress;

            // 计算当前位置与粒子的距离
            double distance = Math.abs(tMid - particlePos);

            // 如果在粒子核心区域
            if (distance < particleSize / 2) {
                // 使用粒子颜色覆盖
                r = anim.particleColor.getRed();
                g = anim.particleColor.getGreen();
                b = anim.particleColor.getBlue();
                a = anim.particleColor.getOpacity();
            }
            // 如果在粒子尾迹区域
            else if (distance < particleSize / 2 + particleTrail && tMid < particlePos) {
                // 尾迹颜色渐变（从粒子颜色到基础颜色）
                double trailRatio = (distance - particleSize / 2) / particleTrail;
                Color trailColor = interpolateColor(anim.particleColor, baseColor, trailRatio);

                // 混合尾迹颜色与当前颜色
                r = (r + trailColor.getRed()) / 2;
                g = (g + trailColor.getGreen()) / 2;
                b = (b + trailColor.getBlue()) / 2;
                a = Math.max(a, trailColor.getOpacity());
            }
        }

        return new Color(r, g, b, a);
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
     */
    public void redrawCurve() {
        getChildren().clear(); // 清除之前的分段

        // 遍历所有分段（t从0到1，步长为1/segments）
        for (int i = 0; i < segments; i++) {
            double tStart = (double) i / segments;
            double tEnd = (double) (i + 1) / segments;

            // 计算当前分段的起点和终点
            Point2D segmentStart = getBezierPoint(tStart);
            Point2D segmentEnd = getBezierPoint(tEnd);

            // 计算当前分段的控制点
            Point2D segmentControl1 = calculateSegmentControl(segmentStart, segmentEnd);
            Point2D segmentControl2 = calculateSegmentControl(segmentEnd, segmentStart);

            // 计算当前分段的颜色
            double tMid = (tStart + tEnd) / 2;
            Color segmentColor = interpolateColor(startColor, endColor, tMid);

            // 创建分段曲线并设置样式
            Path segment = new Path();
            segment.getElements().add(new MoveTo(segmentStart.getX(), segmentStart.getY()));
            segment.getElements().add(new CubicCurveTo(
                    segmentControl1.getX(), segmentControl1.getY(),
                    segmentControl2.getX(), segmentControl2.getY(),
                    segmentEnd.getX(), segmentEnd.getY()
            ));

            // 设置分段样式
            segment.setStroke(segmentColor);
            segment.setStrokeWidth(strokeWidth);
            segment.setStrokeLineCap(isStrokeRound ? StrokeLineCap.ROUND : StrokeLineCap.BUTT);
            segment.setStrokeLineJoin(StrokeLineJoin.ROUND);
            segment.setFill(Color.TRANSPARENT);

            // 添加到容器
            getChildren().add(segment);
        }
    }

    /**
     * 计算贝塞尔曲线上某比例位置的点
     */
    private Point2D getBezierPoint(double t) {
        double u = 1 - t;
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
     * 计算分段曲线的控制点
     */
    private Point2D calculateSegmentControl(Point2D point, Point2D nextPoint) {
        double tangentWeight = 0.3;
        double dx = nextPoint.getX() - point.getX();
        double dy = nextPoint.getY() - point.getY();
        return new Point2D(
                point.getX() + dx * tangentWeight,
                point.getY() + dy * tangentWeight
        );
    }

    /**
     * 颜色插值
     */
    private Color interpolateColor(Color start, Color end, double ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        double r = start.getRed() + (end.getRed() - start.getRed()) * ratio;
        double g = start.getGreen() + (end.getGreen() - start.getGreen()) * ratio;
        double b = start.getBlue() + (end.getBlue() - start.getBlue()) * ratio;
        double a = start.getOpacity() + (end.getOpacity() - start.getOpacity()) * ratio;
        return new Color(r, g, b, a);
    }

    // ==================== 对外API ====================

    public void setStartPoint(double x, double y) {
        this.startX = x;
        this.startY = y;
        calculateControlPoints();
        redrawCurve();
    }

    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
        calculateControlPoints();
        redrawCurve();
    }

    public void setGradientColors(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
        redrawCurve();
    }

    public void setSegmentCount(int segments) {
        this.segments = Math.max(5, segments);
        redrawCurve();
    }

    public void setLineWidth(double width) {
        this.strokeWidth = width;
        redrawCurve();
    }

    public void setRoundCap(boolean round) {
        this.isStrokeRound = round;
        redrawCurve();
    }

    public void setControlRatio(double ratio) {
        this.controlRatio = Math.max(0.1, Math.min(0.8, ratio));
        calculateControlPoints();
        redrawCurve();
    }

    public void setParticleParameters(double speed, double size, double trail) {
        this.particleSpeed = Math.max(0.1, speed);
        this.particleSize = Math.max(0.05, Math.min(0.3, size));
        this.particleTrail = Math.max(0, Math.min(0.5, trail));
    }

    /**
     * 停止所有正在运行的动画
     */
    public void stopAllAnimations() {
        activeAnimations.clear();
        if (isAnimationRunning) {
            animationTimer.stop();
            isAnimationRunning = false;
            redrawCurve();
        }
    }

    /**
     * 内部类：管理单个信号动画的参数和状态
     */
    private class SignalAnimation {
        Color particleColor;
        double startTime;  // 动画开始时间（秒）
        double duration;   // 动画持续时间（秒）

        SignalAnimation(Color color, double start, double dur) {
            particleColor = color;
            startTime = start;
            duration = dur;
        }
    }
}
