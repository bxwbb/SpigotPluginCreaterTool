package org.bxwbb.spigotplugincreatertool.BxWinLab;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.LabelEvent;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;
import org.bxwbb.spigotplugincreatertool.Utils.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseLabel {

    private static final Logger log = LoggerFactory.getLogger(BaseLabel.class);
    public static Scene objectScene;
    public static boolean debugMode = false;
    public static BaseLabel NULL_LABEL;

    private RectangularFrame rectangularFrame;
    private BaseLabel parent = null;
    private final List<BaseLabel> children = new ArrayList<>();
    private Group root;
    private final Group base = new Group();
    private FillExtensionType fillExtensionType = FillExtensionType.NONE;
    // 相对父组件的坐标
    protected double relativeX;
    protected double relativeY;

    // 焦点控件（全局唯一）
    public static BaseLabel focusLabel;
    // 获得焦点失去焦点事件
    private Consumer<BaseLabel> onGetFocusEvent;
    private Consumer<BaseLabel> onLostFocusEvent;
    private boolean isVisible = false;
    private boolean isMouseTransparent = false;
    // 包裹模式
    private boolean isWrap = false;

    // 鼠标进入事件
    public Consumer<LabelEvent> mouseEnterEvent;
    // 鼠标离开事件
    public Consumer<LabelEvent> mouseLeaveEvent;
    // 鼠标点击事件
    public Consumer<LabelEvent> mouseClickEvent;
    // 鼠标按下事件
    public Consumer<LabelEvent> mousePressedEvent;
    // 鼠标抬起事件
    public Consumer<LabelEvent> mouseReleasedEvent;
    // 鼠标拖动事件
    public Consumer<LabelEvent> mouseDraggedEvent;
    // 鼠标移动事件
    public Consumer<LabelEvent> mouseMovedEvent;

    protected void init(Node node) {
        if (node != null) {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                if (mouseEnterEvent != null) mouseEnterEvent.accept(new LabelEvent(this, event));
            });
            node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                if (mouseLeaveEvent != null) mouseLeaveEvent.accept(new LabelEvent(this, event));
            });
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (mouseClickEvent != null) mouseClickEvent.accept(new LabelEvent(this, event));
            });
            node.setOnMousePressed(event -> {
                getFocus();
                if (mousePressedEvent != null) mousePressedEvent.accept(new LabelEvent(this, event));
            });
            node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                if (mouseReleasedEvent != null) mouseReleasedEvent.accept(new LabelEvent(this, event));
            });
            node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                if (mouseDraggedEvent != null) mouseDraggedEvent.accept(new LabelEvent(this, event));
            });
            node.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                if (mouseMovedEvent != null) mouseMovedEvent.accept(new LabelEvent(this, event));
            });
        }
        this.setParent(getObjectScene());
        if (debugMode) {
            log.info("控件库Debug模式已开启");
            this.showTextBox();
        }
        if (getFillExtensionType().equals(FillExtensionType.NONE)) update();
    }

    public final RectangularFrame getRectangularFrame() {
        return rectangularFrame;
    }

    public void setRectangularFrame(RectangularFrame rectangularFrame) {
        this.rectangularFrame = rectangularFrame;
    }

    public final BaseLabel getParent() {
        return parent;
    }

    protected void addTo(Group root) {
        for (BaseLabel child : this.getChildren()) {
            child.addTo(base);
        }
        this.root = root;
        if (!this.root.getChildren().contains(base)) this.root.getChildren().add(base);
    }

    protected void delete() {
        for (BaseLabel child : this.getChildren()) {
            child.delete();
        }
        this.base.getChildren().clear();
        this.root.getChildren().remove(base);
    }

    public Group getBase() {
        return base;
    }

    public final void setParent(BaseLabel parent) {
        if (parent == null) parent = getObjectScene();
        if (this.parent != null) this.parent.deleteChild(this);
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
            this.addTo(parent.getBase());
            switch (this.getFillExtensionType()) {
                case HORIZONTAL:
                    this.setLayoutWidth(parent.getLayoutWidth());
                    this.setLayoutX(parent.getLayoutX());
                    break;
                case CENTER:
                    this.setLayoutWidth(parent.getLayoutWidth());
                    this.setLayoutX(parent.getLayoutX());
                case VERTICAL:
                    this.setLayoutHeight(parent.getLayoutHeight());
                    this.setLayoutY(parent.getLayoutY());
                    break;
            }
            relativeX = getRectangularFrame().getX() - parent.getRectangularFrame().getX();
            relativeY = getRectangularFrame().getY() - parent.getRectangularFrame().getY();
            parent.update();
        }
    }

    public final void setParent(BaseLabel parent, double dx, double dy) {
        if (parent == null) parent = getObjectScene();
        if (this.parent != null) this.parent.deleteChild(this);
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
            this.addTo(parent.getBase());
            switch (this.getFillExtensionType()) {
                case HORIZONTAL:
                    this.setLayoutWidth(parent.getLayoutWidth());
                    this.setLayoutX(parent.getLayoutX());
                    break;
                case CENTER:
                    this.setLayoutWidth(parent.getLayoutWidth());
                    this.setLayoutX(parent.getLayoutX());
                case VERTICAL:
                    this.setLayoutHeight(parent.getLayoutHeight());
                    this.setLayoutY(parent.getLayoutY());
                    break;
            }
            relativeX = dx;
            relativeY = dy;
            parent.update();
        }
    }

    public final List<BaseLabel> getChildren() {
        return children;
    }

    public final void addChild(BaseLabel child) {
        child.setParent(this);
    }

    public final void addChild(BaseLabel child, double dx, double dy) {
        child.setParent(this, dx, dy);
    }

    public final void deleteChild(BaseLabel child) {
        child.parent = null;
        children.remove(child);
        child.delete();
        relativeX = 0;
        relativeY = 0;
        update();
    }

    public void update() {
        if (isWrap()) {
            wrap(this);
        }
    }

    public double getLayoutX() {
        return getRectangularFrame().getX();
    }

    public void setLayoutX(double layoutX) {
        setLayoutXNoUpdate(layoutX);
        update();
    }

    protected void setLayoutXNoUpdate(double layoutX) {
        rectangularFrame.setX(layoutX);
        for (BaseLabel child : getChildren()) {
            child.setLayoutX(layoutX + child.relativeX);
        }
        if (getParent() != null) {
            this.relativeX = layoutX - getParent().getRectangularFrame().getX();
        }
    }

    public double getLayoutY() {
        return getRectangularFrame().getY();
    }

    public void setLayoutY(double layoutY) {
        setLayoutYNoUpdate(layoutY);
        update();
    }

    protected void setLayoutYNoUpdate(double layoutY) {
        rectangularFrame.setY(layoutY);
        for (BaseLabel child : getChildren()) {
            child.setLayoutY(layoutY + child.relativeY);
        }
        if (getParent() != null) {
            this.relativeY = layoutY - getParent().getRectangularFrame().getY();
        }
    }

    public double getLayoutWidth() {
        return getRectangularFrame().getWidth();
    }

    public void setLayoutWidth(double layoutWidth) {
        setLayoutWidthNoUpdate(layoutWidth);
        update();
    }

    protected void setLayoutWidthNoUpdate(double layoutWidth) {
        rectangularFrame.setWidth(layoutWidth);
        for (BaseLabel child : getChildren()) {
            switch (child.getFillExtensionType()) {
                case HORIZONTAL, CENTER -> {
                    child.setLayoutWidthNoUpdate(layoutWidth);
                    child.setLayoutX(getLayoutX());
                }
            }
        }
    }

    public double getLayoutHeight() {
        return getRectangularFrame().getHeight();
    }

    public void setLayoutHeight(double layoutHeight) {
        setLayoutHeightNoUpdate(layoutHeight);
        update();
    }

    protected void setLayoutHeightNoUpdate(double layoutHeight) {
        rectangularFrame.setHeight(layoutHeight);
        for (BaseLabel child : getChildren()) {
            switch (child.getFillExtensionType()) {
                case VERTICAL, CENTER -> {
                    child.setLayoutHeightNoUpdate(layoutHeight);
                    child.setLayoutY(getLayoutY());
                }
            }
        }
    }

    public Group getRoot() {
        return root;
    }

    public Consumer<BaseLabel> getOnGetFocusEvent() {
        return onGetFocusEvent;
    }

    public void getFocus() {
        if (focusLabel != null) {
            if (focusLabel.equals(this)) return;
            if (focusLabel.getOnLostFocusEvent() != null) {
                focusLabel.getOnLostFocusEvent().accept(focusLabel);
            }
        }
        focusLabel = this;
        if (onGetFocusEvent != null) {
            onGetFocusEvent.accept(this);
        }
        log.info("控件焦点改变至 - {}", this);
    }

    public void setOnGetFocusEvent(Consumer<BaseLabel> onGetFocusEvent) {
        this.onGetFocusEvent = onGetFocusEvent;
    }

    public Consumer<BaseLabel> getOnLostFocusEvent() {
        return onLostFocusEvent;
    }

    public void setOnLostFocusEvent(Consumer<BaseLabel> onLostFocusEvent) {
        this.onLostFocusEvent = onLostFocusEvent;
    }

    public Consumer<LabelEvent> getMouseEnterEvent() {
        return mouseEnterEvent;
    }

    public void setMouseEnterEvent(Consumer<LabelEvent> mouseEnterEvent) {
        this.mouseEnterEvent = mouseEnterEvent;
    }

    public Consumer<LabelEvent> getMouseLeaveEvent() {
        return mouseLeaveEvent;
    }

    public void setMouseLeaveEvent(Consumer<LabelEvent> mouseLeaveEvent) {
        this.mouseLeaveEvent = mouseLeaveEvent;
    }

    public Consumer<LabelEvent> getMouseClickEvent() {
        return mouseClickEvent;
    }

    public void setMouseClickEvent(Consumer<LabelEvent> mouseClickEvent) {
        this.mouseClickEvent = mouseClickEvent;
    }

    public Consumer<LabelEvent> getMousePressedEvent() {
        return mousePressedEvent;
    }

    public void setMousePressedEvent(Consumer<LabelEvent> mousePressedEvent) {
        this.mousePressedEvent = mousePressedEvent;
    }

    public Consumer<LabelEvent> getMouseReleasedEvent() {
        return mouseReleasedEvent;
    }

    public void setMouseReleasedEvent(Consumer<LabelEvent> mouseReleasedEvent) {
        this.mouseReleasedEvent = mouseReleasedEvent;
    }

    public Consumer<LabelEvent> getMouseDraggedEvent() {
        return mouseDraggedEvent;
    }

    public void setMouseDraggedEvent(Consumer<LabelEvent> mouseDraggedEvent) {
        this.mouseDraggedEvent = mouseDraggedEvent;
    }

    public Consumer<LabelEvent> getMouseMovedEvent() {
        return mouseMovedEvent;
    }

    public void setMouseMovedEvent(Consumer<LabelEvent> mouseMovedEvent) {
        this.mouseMovedEvent = mouseMovedEvent;
    }

    public static Scene getObjectScene() {
        return objectScene;
    }

    public static void setObjectScene(javafx.scene.Scene objectScene) {
        BaseLabel.objectScene = new Scene(
                objectScene.getX(),
                objectScene.getY(),
                objectScene.getWidth(),
                objectScene.getHeight(),
                objectScene
        );
        NULL_LABEL = new EmptyBox(-5, -5, 0, 0);
    }

    public FillExtensionType getFillExtensionType() {
        return fillExtensionType;
    }

    public void setFillExtensionType(FillExtensionType fillExtensionType) {
        this.fillExtensionType = fillExtensionType;
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        for (BaseLabel child : getChildren()) {
            child.setVisible(visible);
        }
    }

    public boolean getMouseTransparent() {
        return this.isMouseTransparent;
    }

    public void setMouseTransparent(boolean transparent) {
        this.isMouseTransparent = transparent;
        for (BaseLabel child : getChildren()) {
            child.setMouseTransparent(transparent);
        }
    }

    public void showTextBox() {
        showTextBox(ColorUtil.getRandomColor());
    }

    public void showTextBox(Paint paint) {
        getRectangularFrame().showTextBox(paint);
    }

    /**
     * 在父控件中至于顶层
     */
    public void showToTop() {
        getParent().getChildren().remove(this);
        getParent().getChildren().add(this);
        getParent().update();
    }

    public boolean isWrap() {
        return isWrap;
    }

    public void setWrap(boolean wrap) {
        isWrap = wrap;
        update();
    }

    /**
     * 将这个组件完全包裹其全部子组件，并且使这个组件最小
     */
    private static void wrap(BaseLabel baseLabel) {
        if (baseLabel.getChildren().isEmpty()) {
            log.warn("尝试包裹空元素 - {}",baseLabel);
            return;
        }
        double wrapStartX = 0, wrapStartY = 0, wrapEndX = 0, wrapEndY = 0;
        // 计算初始值
        boolean xChange = false;
        boolean yChange = false;
        for (BaseLabel child : baseLabel.getChildren()) {
            if (!child.getFillExtensionType().isHorizontal() && !xChange) {
                xChange = true;
                wrapStartX = child.getLayoutX();
                wrapEndX = child.getLayoutX() + child.getLayoutWidth();
            }
            if (!child.getFillExtensionType().isVertical()) {
                yChange = true;
                wrapStartY = child.getLayoutY();
                wrapEndY = child.getLayoutY() + child.getLayoutHeight();
            }
        }
        if (!xChange || !yChange) {
            String v = !xChange ? "XChange" : "YChange";
            if (!xChange && !yChange) v = "XChange & YChange";
            log.warn("无法包裹元素({},无法初始化值{}) - 尝试包裹浮动体", baseLabel, v);
            return;
        }
        for (BaseLabel child : baseLabel.getChildren()) {
            if (!child.getFillExtensionType().isHorizontal()) wrapStartX = Math.min(wrapStartX, child.getLayoutX());
            if (!child.getFillExtensionType().isVertical()) wrapStartY = Math.min(wrapStartY, child.getLayoutY());
            if (!child.getFillExtensionType().isHorizontal())
                wrapEndX = Math.max(wrapEndX, child.getLayoutX() + child.getLayoutWidth());
            if (!child.getFillExtensionType().isVertical())
                wrapEndY = Math.max(wrapEndY, child.getLayoutY() + child.getLayoutHeight());
        }
        baseLabel.getRectangularFrame().setX(wrapStartX);
        baseLabel.getRectangularFrame().setY(wrapStartY);
        baseLabel.getRectangularFrame().setWidth(wrapEndX - wrapStartX);
        baseLabel.getRectangularFrame().setHeight(wrapEndY - wrapStartY);
    }

    public void clearChildren() {
        for (BaseLabel child : getChildren()) {
            child.delete();
        }
        getChildren().clear();
        update();
    }
}
