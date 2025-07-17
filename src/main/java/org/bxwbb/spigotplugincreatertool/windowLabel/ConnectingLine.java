package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import org.bxwbb.spigotplugincreatertool.HelloApplication;

public class ConnectingLine extends BaseLabel {

    public Circle startPoint;
    public Circle endPoint;
    public AutoBezierCurve bezierCurve;
    public Color lineStartColor;
    public Shape combined;

    private final LinearGradient lineGradient;

    public ConnectingLine(double startX, double startY, double endX, double endY, Color lineStartColor) {
        this.base = new Group();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.lineStartColor = lineStartColor;
        this.startPoint = new Circle(startX, startY, 5);
        this.startPoint.setFill(this.lineStartColor);
        this.endPoint = new Circle(endX, endY, 5);
        this.endPoint.setFill(HelloApplication.HOVER_COLOR);
        this.bezierCurve = new AutoBezierCurve(startX, startY, endX, endY);
        this.bezierCurve.setStrokeWidth(5);
        this.lineGradient = new LinearGradient(
                0, 0,
                1, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, this.lineStartColor),
                new Stop(1, HelloApplication.HOVER_COLOR)
        );
        this.bezierCurve.setStroke(this.lineGradient);
    }

    @Override
    public void resetPos(double x, double y) {

    }

    @Override
    public void resetSize(double width, double height) {

    }

    @Override
    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.bezierCurve);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public double getWidth() {
        return endX - startX;
    }

    @Override
    public double getHeight() {
        return endY - startY;
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public BaseLabel createNew() {
        return new ConnectingLine(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.lineStartColor
        );
    }
}
