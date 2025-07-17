package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindow;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ALL", "unchecked"})
public class ListSlider extends BaseLabel {

    public String name;
    public List<String> lore;
    public Rectangle background;
    public Text title;
    public List<BaseLabel> sliderLongs = new ArrayList<>();
    public Rectangle mask;
    public Group sliderGroup;
    public double sliderHeight;
    public Rectangle sliderListLine;

    private double rMouseY;
    private Node.VarType varType;

    public ListSlider(double startX, double startY, double endX, double endY, String name, List<String> lore, List<BaseLabel> data, Node.VarType varType) {
        this.base = new Group();
        this.sliderGroup = new Group();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.name = name;
        this.lore = lore;
        this.sliderHeight = 0;
        this.varType = varType;
        this.title = new Text(name);
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setX(this.startX + 10);
        this.title.setY(this.startY + this.title.getLayoutBounds().getHeight() * 0.5 + 5);
        this.background = new Rectangle(this.startX, this.startY + this.title.getLayoutBounds().getHeight() + 5, this.endX - this.startX, this.endY - this.startY);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(1.0);
        this.background.setStroke(HelloApplication.UNSELECTED_BORDER_COLOR);
        this.mask = new Rectangle(this.startX + 1, this.startY + this.title.getLayoutBounds().getHeight() + 5 + 1, this.endX - this.startX - 2, this.endY - this.startY - 2);
        this.mask.setArcWidth(HelloApplication.ROUNDNESS);
        this.mask.setArcHeight(HelloApplication.ROUNDNESS);
        this.sliderGroup.setClip(this.mask);
        this.sliderLongs = data;
        for (int i = 0; i < this.sliderLongs.size(); i++) {
            this.sliderLongs.get(i).setName(name + "[" + i + "]");
            this.sliderLongs.get(i).addTo(this.sliderGroup);
        }
        HelloApplication.scene.addEventHandler(ScrollEvent.SCROLL, this::scrollEvent);
        this.sliderListLine = new Rectangle(this.startX, this.startY + this.title.getLayoutBounds().getHeight() + 5, this.endX - this.startX, this.startY + this.title.getLayoutBounds().getHeight() + 5);
        this.sliderListLine.setFill(HelloApplication.UNSELECTED_COLOR);
        this.sliderListLine.setStroke(HelloApplication.UNSELECTED_BORDER_COLOR);
        this.sliderListLine.setStrokeWidth(1.0f);
        this.sliderListLine.setArcWidth(HelloApplication.ROUNDNESS);
        this.sliderListLine.setArcHeight(HelloApplication.ROUNDNESS);
        this.sliderListLine.setOnMouseEntered(event -> {
            this.sliderListLine.setFill(HelloApplication.HOVER_COLOR);
            this.rMouseY = event.getY();
        });
        this.sliderListLine.setOnMouseExited(event -> this.sliderListLine.setFill(HelloApplication.UNSELECTED_COLOR));
        this.sliderListLine.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.rMouseY = event.getY();
            }
        });
        this.sliderListLine.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.sliderListLine.setY(this.sliderListLine.getY() + event.getY() - this.rMouseY);
                if (this.sliderListLine.getY() < (this.endY + 10)) {
                    this.sliderListLine.setY(this.endY + 10);
                }
                if (this.sliderListLine.getY() > (this.endY + this.background.getHeight() - this.sliderListLine.getHeight())) {
                    this.sliderListLine.setY(this.endY + this.background.getHeight() - this.sliderListLine.getHeight());
                }
                this.sliderHeight = (-(this.sliderLongs.size() * (this.sliderLongs.getFirst() == null ? 0 : this.sliderLongs.getFirst().getHeight() + 4) - this.background.getHeight() + 5)) * (this.sliderListLine.getY() - (this.background.getY() + 5)) / (this.background.getHeight() - 30);
                for (int i = 0; i < this.sliderLongs.size(); i++) {
                    this.sliderLongs.get(i).resetPos(this.startX + 5, this.startY + this.title.getLayoutBounds().getHeight() + 10 + i * (this.sliderLongs.getFirst() == null ? 0 : this.sliderLongs.getFirst().getHeight() + 4) + this.sliderHeight);
                }
            }
            this.rMouseY = event.getY();
        });
    }

    @Override
    public void resetPos(double x, double y) {
        this.endX = this.endX - this.startX + x;
        this.endY = this.endY - this.startY + y;
        this.startX = x;
        this.startY = y;
        this.title.setX(this.startX);
        this.title.setY(this.startY + this.title.getLayoutBounds().getHeight() * 0.5 + 5);
        this.background.setX(this.startX);
        this.background.setY(this.startY + this.title.getLayoutBounds().getHeight() + 5);
        this.mask.setX(this.startX + 1);
        this.mask.setY(this.startY + this.title.getLayoutBounds().getHeight() + 5 + 1);
        for (int i = 0; i < this.sliderLongs.size(); i++) {
            this.sliderLongs.get(i).resetPos(this.startX + 5, this.startY + this.title.getLayoutBounds().getHeight() + 10 + i * (this.sliderLongs.getFirst() == null ? 0 : this.sliderLongs.getFirst().getHeight() + 4) + this.sliderHeight);
        }
        this.sliderListLine.setX(this.endX - 12);
        this.sliderListLine.setY(this.startY + this.title.getLayoutBounds().getHeight() + 10 + (((this.sliderLongs.size() * (this.sliderLongs.isEmpty() ? 0 : this.sliderLongs.getFirst().getHeight() + 4) - this.background.getHeight() + 5) - this.sliderListLine.getHeight() - 5) * (-this.sliderHeight / (this.sliderLongs.size() * (this.sliderLongs.isEmpty() ? 0 : this.sliderLongs.getFirst().getHeight() + 4) - this.background.getHeight() + 5))));
    }

    @Override
    public void resetSize(double width, double height) {
        this.endX = this.startX + width;
        this.endY = this.startY + height;
        this.background.setWidth(this.endX - this.startX);
        this.background.setHeight(this.endY - this.startY + 60);
        this.mask.setWidth(this.endX - this.startX - 2);
        this.mask.setHeight(this.endY - this.startY + 60 - 2);
        for (BaseLabel sliderLong : this.sliderLongs) {
            sliderLong.resetSize(this.endX - this.startX - 20, this.endY - this.startY);
        }
    }

    private void scrollEvent(ScrollEvent event) {
        if (MinWindow.isPointInRectangle(this.background.getX(), this.background.getY(), this.background.getX() + this.background.getWidth(), this.background.getY() + this.background.getHeight(), event.getX(), event.getY())) {
            this.sliderHeight += event.getDeltaY() * 0.25;
            this.sliderHeight = Math.min(Math.max(this.sliderHeight, -(this.sliderLongs.size() * (this.sliderLongs.isEmpty() ? 0 : this.sliderLongs.getFirst().getHeight() + 4) - this.background.getHeight() + 5)), 0.0);
            this.resetPos(this.startX, this.startY);
        }
    }

    @Override
    public void delete() {
        for (BaseLabel slider : this.sliderLongs) {
            slider.delete();
        }
        this.sliderGroup.getChildren().clear();
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.title);
        this.sliderGroup.getChildren().add(this.sliderListLine);
        this.base.getChildren().add(this.sliderGroup);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.title.setText(name);
        for (int i = 0; i < this.sliderLongs.size(); i++) {
            this.sliderLongs.get(i).setName(name + "[" + i + "]");
        }
    }

    @Override
    public double getWidth() {
        return this.background.getWidth();
    }

    @Override
    public double getHeight() {
        return this.background.getHeight() + this.title.getLayoutBounds().getHeight();
    }

    @Override
    public void autoWidth() {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        for (BaseLabel sliderLong : this.sliderLongs) {
            sliderLong.setVisible(visible);
        }
    }

    @Override
    public Object getData() {
        List<Object> data = new ArrayList<>();
        for (BaseLabel sliderLong : this.sliderLongs) {
            data.add(sliderLong.getData());
        }
        return data;
    }

    @Override
    public void setData(Object data) {
        List<Object> dataList = (List<Object>) data;
        for (int i = 0; i < this.sliderLongs.size(); i++) {
            this.sliderLongs.get(i).setData((dataList).get(i));
        }
        int r =  this.sliderLongs.size();
        for (int i = 0; i < dataList.size() - r; i++) {
            this.sliderLongs.add(this.sliderLongs.getLast().createNew());
            this.sliderLongs.getLast().setName(name + "[" + (i + r) + "]");
            this.sliderLongs.getLast().setData(dataList.get(i + r));
            this.sliderLongs.getLast().addTo(this.sliderGroup);
            this.sliderLongs.getLast().setVisible(this.visible);
            this.resetPos(this.startX, this.startY);
        }
        System.out.println(dataList);
    }

    @Override
    public BaseLabel createNew() {
        return new ListSlider(
                this.startX,
                this.startY,
                this.endX,
                this.endY,
                this.name,
                new ArrayList<>(this.lore),
                new ArrayList<>(this.sliderLongs),
                this.varType
        );
    }
}
