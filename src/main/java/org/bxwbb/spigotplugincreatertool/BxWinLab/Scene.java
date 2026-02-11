package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;

public class Scene extends BaseLabel {

    private final javafx.scene.Scene scene;

    public Scene(double x, double y, double width, double height, javafx.scene.Scene scene) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        this.scene = scene;
        this.scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            setLayoutWidth(newValue.doubleValue());
        });
        this.scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            setLayoutHeight(newValue.doubleValue());
        });
        this.addTo((Group) scene.getRoot());
    }

    @Override
    public void update() {
        super.update();
    }

    public javafx.scene.Scene getScene() {
        return scene;
    }

}
