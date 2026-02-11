package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.Node;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class JavaFXNode extends BaseLabel {

    private final Node node;

    public JavaFXNode(double x, double y, double width, double height, Node node) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.node = node;
        init(node);
    }

    @Override
    public void update() {
        super.update();
        node.setLayoutX(getLayoutX());
        node.setLayoutY(getLayoutY());
        node.prefWidth(getLayoutWidth());
        node.prefHeight(getLayoutHeight());
    }

    @Override
    protected void addTo(Group root) {
        if (!getBase().getChildren().contains(node)) getBase().getChildren().add(node);
        super.addTo(root);
    }

    @Override
    public void setVisible(boolean visible) {
        node.setVisible(visible);
        super.setVisible(visible);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        node.setMouseTransparent(transparent);
        super.setMouseTransparent(transparent);
    }
}
