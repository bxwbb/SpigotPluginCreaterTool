package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class LineBox extends BaseLabel {

    private final Line line;

    public LineBox(double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.line = new Line(getLayoutX(), getLayoutY(), getRectangularFrame().getEndX(), getRectangularFrame().getEndY());
        line.setStroke(ColorSetting.PROGRAM_BASE_COLOR);
        init(line);
    }

    @Override
    public void update() {
        super.update();
        line.setStartX(getLayoutX());
        line.setStartY(getLayoutY());
        line.setEndX(getRectangularFrame().getEndX());
        line.setEndY(getRectangularFrame().getEndY());
    }

    public Line getLine() {
        return line;
    }

    @Override
    protected void addTo(Group root) {
        if (!getBase().getChildren().contains(line)) getBase().getChildren().add(line);
        super.addTo(root);
    }

    @Override
    public void setVisible(boolean visible) {
        this.line.setVisible(visible);
        super.setVisible(visible);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        line.setMouseTransparent(transparent);
        super.setMouseTransparent(transparent);
    }
}
