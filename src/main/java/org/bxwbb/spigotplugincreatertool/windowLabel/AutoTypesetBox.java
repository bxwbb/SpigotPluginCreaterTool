package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AutoTypesetBox<E> extends BaseLabel {

    private static final Logger log = LoggerFactory.getLogger(AutoTypesetBox.class);
    private final List<E> children = new ArrayList<>();
    private TypesettingMode mode = TypesettingMode.V;

    // 排版方式
    public enum TypesettingMode {
        H,
        V
    }

    @Override
    public void resetPos(double x, double y) {
        this.startX = x;
        this.startY = y;
        reposition();
    }

    @Override
    public void resetSize(double width, double height) {

    }

    @Override
    public void delete() {
        this.base.getChildren().clear();
        this.root.getChildren().remove(this.base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

    }

    public TypesettingMode getMode() {
        return mode;
    }

    public void setMode(TypesettingMode mode) {
        this.mode = mode;
        reposition();
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public void autoWidth() {

    }

    @Override
    public void setDisplayVisible(boolean visible) {

    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {

    }

    @Override
    public Object getData() {
        reposition();
        return this.children;
    }

    public void addNode(E node) {
        this.children.add(node);
        if (node instanceof Node) {
            this.base.getChildren().add((Node) node);
        } else if (node instanceof BaseLabel label) {
            label.addTo(this.base);
        } else {
            log.warn("未知的控件类型被加入到自动布局控件中 - {}", node.getClass().getName());
            return;
        }
        reposition();
    }

    public void removeNode(E node) {
        this.children.remove(node);
        if (node instanceof Node) {
            this.base.getChildren().remove(node);
        } else if (node instanceof BaseLabel label) {
            label.delete();
        } else {
            log.warn("尝试删除一个未知的控件类型在自动布局控件中 - {}", node.getClass().getName());
        }
        reposition();
    }

    public void clear() {
        this.children.clear();
        this.base.getChildren().clear();
        reposition();
    }

    public void reposition() {
        if (this.mode.equals(TypesettingMode.V)) {
            double y = this.startY;
            for (E node : this.children) {
                if (node instanceof Node javafxNode) {
                    javafxNode.setLayoutX(this.startX);
                    javafxNode.setLayoutY(y);
                    y += javafxNode.getLayoutBounds().getHeight();
                } else if (node instanceof AutoTypesetBox label) {
                    label.resetPos(this.startX, y);
                    y += label.getMaxHeight();
                }
            }
        } else {
            double x = this.startX;
            for (E node : this.children) {
                if (node instanceof Node javafxNode) {
                    javafxNode.setLayoutX(x);
                    javafxNode.setLayoutY(this.startY);
                    x += javafxNode.getLayoutBounds().getWidth();
                } else if (node instanceof AutoTypesetBox label) {
                    label.resetPos(x, this.startY);
                    x += label.getMaxWidth();
                }
            }
        }
    }

    public double getMaxWidth() {
        double maxWidth = 0;
        for (E node : this.children) {
            if (node instanceof Node javafxNode) {
                maxWidth = Math.max(maxWidth, javafxNode.getLayoutBounds().getWidth());
            } else if (node instanceof AutoTypesetBox label) {
                maxWidth = Math.max(maxWidth, label.getMaxWidth());
            }
        }
        return maxWidth;
    }

    public double getMaxHeight() {
        double maxHeight = 0;
        for (E node : this.children) {
            if (node instanceof Node javafxNode) {
                maxHeight = Math.max(maxHeight, javafxNode.getLayoutBounds().getHeight());
            } else if (node instanceof AutoTypesetBox label) {
                maxHeight = Math.max(maxHeight, label.getMaxHeight());
            }
            System.out.println(maxHeight + " | " + node.getClass().getName() + ((node instanceof Text) ? " | " + ((Text) node).getText() : ""));
        }
        return maxHeight;
    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        AutoTypesetBox<E> autoTypesetBox = new AutoTypesetBox<>();
        autoTypesetBox.setData(this.children);
        return autoTypesetBox;
    }

}
