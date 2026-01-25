package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import org.bxwbb.spigotplugincreatertool.FileUtils;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;

import java.nio.file.Path;
import java.util.Objects;

public class Image extends BaseLabel {

    private ImageView imageView;
    public static final javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream(
            "/org/bxwbb/spigotplugincreatertool/icon/NodeEditor/Close.png"
    )));

    public Image(double startX, double startY, double endX, double endY, javafx.scene.image.Image image) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.imageView = new ImageView(
                image
        );
        this.imageView.setLayoutX(startX);
        this.imageView.setLayoutY(startY);
        this.imageView.setMouseTransparent(true);
        this.base = new Group();
    }

    public Image ImageToPath(double startX, double startY, double endX, double endY, Path path) {
        return new Image(startX, startY, endX, endY,
                new javafx.scene.image.Image(
                        path.toUri().toString()
                )
        );
    }

    public Image ImageToPathR(double startX, double startY, double endX, double endY, Path path) {
        return new Image(startX, startY, endX, endY,
                new javafx.scene.image.Image(
                        FileUtils.loadResourceFile(String.valueOf(path))
                )
        );
    }

    @Override
    public void update() {
        this.imageView.setLayoutX(startX);
        this.imageView.setLayoutY(startY);
        this.imageView.setFitWidth(this.getWidth());
        this.imageView.setFitHeight(this.getHeight());
    }

    @Override
    public void delete() {
        if (root == null) return;
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
//        this.base.getChildren().add(HelloApplication.getTestRect(startX, startY, endX - startX, endY - startY));
        if (!this.base.getChildren().contains(this.imageView)) this.base.getChildren().add(this.imageView);
        if (!this.root.getChildren().contains(this.base)) this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            this.addTo(this.root);
        } else {
            this.delete();
        }
    }

    @Override
    public Object getData() {
        return this.imageView.getImage();
    }

    @Override
    public void setData(Object data) {
        this.imageView.setImage((javafx.scene.image.Image) data);
    }

    @Override
    public BaseLabel createNew() {
        return new Image(this.startX, this.startY, this.endX, this.endY, this.imageView.getImage());
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        super.setMouseTransparent(transparent);
        this.imageView.setMouseTransparent(this.isMouseTransparent());
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(Image image) {
        this.delete();
        this.imageView = image.getImageView();
        this.addTo(this.root);
    }

    public void setImageView(ImageView image) {
        this.delete();
        this.imageView = image;
        this.addTo(this.root);
    }

    public void setImageView(javafx.scene.image.Image image) {
        this.imageView.setImage(image);
    }

}
