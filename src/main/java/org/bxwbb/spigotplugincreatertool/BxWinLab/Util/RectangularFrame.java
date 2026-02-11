package org.bxwbb.spigotplugincreatertool.BxWinLab.Util;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.BxWinLab.BaseLabel;

public class RectangularFrame {

    private double startX;
    private double startY;
    private double width;
    private double height;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double minWidth;
    private double minHeight;
    private double maxWidth;
    private double maxHeight;
    private Rectangle testBox;

    public RectangularFrame(double startX, double startY, double width, double height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }

    public RectangularFrame createRectangularFrameToEnd(double startX, double startY, double endX, double endY) {
        return new RectangularFrame(
                startX,
                startY,
                endX - startX,
                endY - startY
        );
    }

    public double getX() {
        return startX;
    }

    public void setX(double startX) {
        this.startX = startX;
        if (testBox != null) testBox.setX(startX);
    }

    public double getY() {
        return startY;
    }

    public void setY(double startY) {
        this.startY = startY;
        if (testBox != null) testBox.setY(startY);
    }

    public double getEndX() {
        return startX + width;
    }

    public void setEndX(double endX) {
        this.width = endX - startX;
        if (testBox != null) testBox.setWidth(width);
    }

    public double getEndY() {
        return startY + height;
    }

    public void setEndY(double endY) {
        this.height = endY - startY;
        if (testBox != null) testBox.setHeight(height);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        if (testBox != null) testBox.setWidth(width);
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        if (testBox != null) testBox.setHeight(height);
    }

    public double getCenterX() {
        return startX + width / 2;
    }

    public double getCenterY() {
        return startY + height / 2;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getCenterMaxX() {
        return getMaxX() + getMaxWidth() / 2;
    }

    public double getCenterMaxY() {
        return getMaxY() + getMaxHeight() / 2;
    }

    public double getCenterMinX() {
        return getMinX() + getMinWidth() / 2;
    }

    public double getCenterMinY() {
        return getMinY() + getMinHeight() / 2;
    }

    public double getEndMaxX() {
        return getMaxX() + getMaxWidth();
    }

    public double getEndMaxY() {
        return getMaxY() + getMaxHeight();
    }

    public double getEndMinX() {
        return getMinX() + getMinWidth();
    }

    public double getEndMinY() {
        return getMinY() + getMinHeight();
    }

    public void setMaxEndX(double maxX) {
        setMaxWidth(maxX - getMaxX());
    }

    public void setMaxEndY(double maxY) {
        setMaxHeight(maxY - getMaxY());
    }

    public void setMinEndX(double minX) {
        setMinWidth(minX - getMinX());
    }

    public void setMinEndY(double minY) {
        setMinHeight(minY - getMinY());
    }

    public boolean isIn(double x,double y) {
        return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
    }

    public void showTextBox(Paint color) {
        testBox = new Rectangle(getX(), getY(), getWidth(), getHeight());
        testBox.setFill(javafx.scene.paint.Color.TRANSPARENT);
        testBox.setStroke(color);
        testBox.setStrokeWidth(3);
        testBox.setMouseTransparent(true);
        BaseLabel.getObjectScene().getRoot().getChildren().add(testBox);
    }

}
