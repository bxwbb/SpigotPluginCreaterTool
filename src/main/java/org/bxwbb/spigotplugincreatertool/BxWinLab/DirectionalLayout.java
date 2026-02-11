package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Direction;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class DirectionalLayout extends BaseLabel {

    private BaseLabel baseLabel;
    private Direction direction = Direction.CENTER;

    public DirectionalLayout(double x, double y, double width, double height, BaseLabel baseLabel) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.baseLabel = baseLabel;
        this.addChild(baseLabel);
    }

    @Override
    public void update() {
        super.update();
        double halfWidth = getLayoutWidth() / 2;
        double halfHeight = getLayoutHeight() / 2;
        double halfBaseWidth = baseLabel.getLayoutWidth() / 2;
        double halfBaseHeight = baseLabel.getLayoutHeight() / 2;
        double baseWidth = baseLabel.getLayoutWidth();
        double baseHeight = baseLabel.getLayoutHeight();
        switch (getDirection()) {
            case UP_LEFT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX());
                baseLabel.setLayoutY(getLayoutY());
            }
            case UP -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + halfWidth - halfBaseWidth);
                baseLabel.setLayoutY(getLayoutY());
            }
            case UP_RIGHT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + getLayoutWidth() - baseWidth);
                baseLabel.setLayoutY(getLayoutY());
            }
            case LEFT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX());
                baseLabel.setLayoutY(getLayoutY() + halfHeight - halfBaseHeight);
            }
            case CENTER -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + halfWidth - halfBaseWidth);
                baseLabel.setLayoutY(getLayoutY() + halfHeight - halfBaseHeight);
            }
            case RIGHT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + getLayoutWidth() - baseWidth);
                baseLabel.setLayoutY(getLayoutY() + halfHeight - halfBaseHeight);
            }
            case DOWN_LEFT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX());
                baseLabel.setLayoutY(getLayoutY() + getLayoutHeight() - baseHeight);
            }
            case DOWN -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + halfWidth - halfBaseWidth);
                baseLabel.setLayoutY(getLayoutY() + getLayoutHeight() - baseHeight);
            }
            case DOWN_RIGHT -> {
                baseLabel.setLayoutXNoUpdate(getLayoutX() + getLayoutWidth() - baseWidth);
                baseLabel.setLayoutY(getLayoutY() + getLayoutHeight() - baseHeight);
            }
        }
    }

    public BaseLabel getBaseLabel() {
        return baseLabel;
    }

    public void setBaseLabel(BaseLabel baseLabel) {
        this.baseLabel.setParent(null);
        this.baseLabel = baseLabel;
        baseLabel.setParent(this);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        update();
    }

}
