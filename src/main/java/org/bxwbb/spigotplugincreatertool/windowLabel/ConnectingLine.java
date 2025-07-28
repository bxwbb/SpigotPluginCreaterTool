package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

public class ConnectingLine extends BaseLabel {

    public Circle startPoint;
    public Circle endPoint;
    public AutoBezierCurve bezierCurve;
    public Color lineStartColor;
    public Color lineEndColor;

    public ConnectingLine(double startX, double startY, double endX, double endY, Color lineStartColor, Color lineEndColor) {
        this.base = new Group();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.lineStartColor = lineStartColor;
        this.lineEndColor = lineEndColor;
        this.startPoint = new Circle(startX, startY, 5);
        this.startPoint.setFill(this.lineStartColor);
        this.endPoint = new Circle(endX, endY, 5);
        this.endPoint.setFill(HelloApplication.HOVER_COLOR);
        this.bezierCurve = new AutoBezierCurve(startX, startY, endX, endY);
        this.bezierCurve.setGradientColors(this.lineStartColor, this.lineEndColor);
        this.bezierCurve.setLineWidth(5.0);
    }

    public void resetColor(Color lineStartColor, Color lineEndColor) {
        this.lineStartColor = lineStartColor;
        this.lineEndColor = lineEndColor;
        this.bezierCurve.setGradientColors(this.lineStartColor, this.lineEndColor);
    }

    @Override
    public void resetPos(double x, double y) {
        this.startX = x;
        this.startY = y;
        this.bezierCurve.setStartPoint(startX, startY);
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = width;
        this.endY = height;
        this.bezierCurve.setEndPoint(endX, endY);
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
                this.lineStartColor,
                this.lineEndColor
        );
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }
}
