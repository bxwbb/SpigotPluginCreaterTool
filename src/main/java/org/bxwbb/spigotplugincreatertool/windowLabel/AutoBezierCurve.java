package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * 自动计算控制点的三次贝塞尔曲线（水平方向优先）
 */
public class AutoBezierCurve extends Path {

    private double controlRatio = 1.0 / 2; // 控制点距离比例
    private Point2D startPoint;
    private Point2D endPoint;

    /**
     * 创建自动贝塞尔曲线
     * @param startX 起点X坐标
     * @param startY 起点Y坐标
     * @param endX 终点X坐标
     * @param endY 终点Y坐标
     */
    public AutoBezierCurve(double startX, double startY, double endX, double endY) {
        this.startPoint = new Point2D(startX, startY);
        this.endPoint = new Point2D(endX, endY);
        updateCurve();
        setupStyle();
    }

    /**
     * 更新曲线形状
     */
    public void updateCurve() {
        getElements().clear();

        // 计算控制点（水平方向优先）
        Point2D[] controlPoints = calculateControlPoints(startPoint, endPoint);
        Point2D control1 = controlPoints[0];
        Point2D control2 = controlPoints[1];

        // 添加路径元素
        getElements().add(new MoveTo(startPoint.getX(), startPoint.getY()));
        getElements().add(new CubicCurveTo(
                control1.getX(), control1.getY(),
                control2.getX(), control2.getY(),
                endPoint.getX(), endPoint.getY()
        ));
    }

    /**
     * 设置默认样式
     */
    private void setupStyle() {
        setStroke(Color.GRAY);
        setStrokeWidth(2);
        setFill(Color.TRANSPARENT);
    }

    /**
     * 水平方向优先的控制点计算
     */
    private Point2D[] calculateControlPoints(Point2D start, Point2D end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();

        Point2D control1, control2;

        // 改为水平方向优先（无论x或y方向距离大，都优先沿x轴延伸控制点）
        control1 = new Point2D(start.getX() + dx * controlRatio, start.getY());
        control2 = new Point2D(end.getX() - dx * controlRatio, end.getY());

        // 可选：为垂直距离较大的情况添加微调（使曲线更自然）
        if (Math.abs(dy) > Math.abs(dx) * 0.5) {
            double verticalOffset = dy * 0.2; // 垂直方向微调
            control1 = new Point2D(control1.getX(), control1.getY() + verticalOffset);
            control2 = new Point2D(control2.getX(), control2.getY() - verticalOffset);
        }

        return new Point2D[]{control1, control2};
    }

    // Getters and Setters
    public Point2D getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point2D startPoint) {
        this.startPoint = startPoint;
        updateCurve();
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
        updateCurve();
    }

    public double getControlRatio() {
        return controlRatio;
    }

    /**
     * 设置控制点距离比例（0.0-1.0）
     * 较大值使曲线更弯曲，较小值使曲线更平缓
     */
    public void setControlRatio(double controlRatio) {
        this.controlRatio = controlRatio;
        updateCurve();
    }
}