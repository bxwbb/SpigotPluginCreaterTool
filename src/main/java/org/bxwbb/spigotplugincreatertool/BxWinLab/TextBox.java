package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class TextBox extends BaseLabel implements org.bxwbb.spigotplugincreatertool.BxWinLab.Base.Text {

    private final Text textView;
    // 行模式
    private boolean lineMode;

    public TextBox(double x, double y, double width, double height, String text) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        textView = new Text(text);
        init(textView);
    }

    public TextBox(double x, double y, double width, double height, Text textView) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.textView = textView;
        init(textView);
    }

    public Text getTextView() {
        return textView;
    }

    @Override
    public void update() {
        this.textView.setX(getLayoutX());
        this.textView.setY(getLayoutY() + this.textView.getBaselineOffset());
        if (isLineMode()) {
            this.textView.setWrappingWidth(0);
        } else {
            this.textView.setWrappingWidth(getLayoutWidth());
        }
        super.update();
    }

    @Override
    public double getLayoutWidth() {
        return isLineMode() ? getTextWidth() : super.getLayoutWidth();
    }

    @Override
    public double getLayoutHeight() {
        return isLineMode() ? getTextHeight() : super.getLayoutHeight();
    }

    @Override
    protected void addTo(Group root) {
        if (!getBase().getChildren().contains(textView)) getBase().getChildren().add(textView);
        super.addTo(root);
    }

    public void setVisible(boolean visible) {
        textView.setVisible(visible);
        super.setVisible(visible);
    }

    public void setMouseTransparent(boolean transparent) {
        textView.setMouseTransparent(transparent);
        super.setMouseTransparent(transparent);
    }

    @Override
    public double getTextWidth() {
        return getTextView().getLayoutBounds().getWidth();
    }

    @Override
    public double getTextHeight() {
        return getTextView().getLayoutBounds().getHeight();
    }

    @Override
    public Paint getTextColor() {
        return getTextView().getFill();
    }

    @Override
    public void setTextColor(Paint color) {
        getTextView().setFill(color);
    }

    @Override
    public Font getTextFont() {
        return getTextView().getFont();
    }

    @Override
    public void setTextFont(Font font) {
        getTextView().setFont(font);
    }

    public boolean isLineMode() {
        return lineMode;
    }

    public void setLineMode(boolean lineMode) {
        this.lineMode = lineMode;
        update();
    }
}