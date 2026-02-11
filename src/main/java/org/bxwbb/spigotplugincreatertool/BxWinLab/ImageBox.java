package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;
import org.bxwbb.spigotplugincreatertool.HelloApplication;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public class ImageBox extends BaseLabel {

    private final ImageView imageView;

    public ImageBox(double x, double y, double width, double height, String imagePath) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        imageView = new ImageView(new javafx.scene.image.Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(imagePath))));
        imageView.setPreserveRatio(false);
        init(imageView);
    }

    public ImageBox(double x, double y, double width, double height, Path imagePath) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        imageView = new ImageView((javafx.scene.image.Image) imagePath);
        imageView.setPreserveRatio(false);
        init(imageView);
    }

    public ImageBox(double x, double y, double width, double height, javafx.scene.image.Image image) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        init(imageView);
    }

    public ImageBox(double x, double y, double width, double height, InputStream imagePath) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        imageView = new ImageView(new javafx.scene.image.Image(imagePath));
        imageView.setPreserveRatio(false);
        init(imageView);
    }

    public ImageBox(double x, double y, double width, double height) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        imageView = new ImageView();
        imageView.setPreserveRatio(false);
        init(imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void update() {
        super.update();
        this.imageView.setX(getLayoutX());
        this.imageView.setY(getLayoutY());
        this.imageView.setFitWidth(getLayoutWidth());
        this.imageView.setFitHeight(getLayoutHeight());
    }

    @Override
    protected void addTo(Group root) {
        if (!getBase().getChildren().contains(imageView)) getBase().getChildren().add(imageView);
        super.addTo(root);
    }

    @Override
    public void setVisible(boolean visible) {
        imageView.setVisible(visible);
        super.setVisible(visible);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        imageView.setMouseTransparent(transparent);
        super.setMouseTransparent(transparent);
    }
}