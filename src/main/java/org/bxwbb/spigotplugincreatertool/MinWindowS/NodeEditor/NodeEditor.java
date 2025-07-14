package org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.MinWindowType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeEditor extends MinWindowType {

    // 网格颜色
    public static Color GRID_COLOR = Color.color(0.3, 0.3, 0.3);
    // 背景颜色
    public static Color BG_COLOR = Color.color(0.2, 0.2, 0.2);
    // 横向线条预设数
    public static int HORIZONTAL_LINE_COUNT = 50;
    // 纵向线条预设数
    public static int VERTICAL_LINE_COUNT = 50;
    // 线条间距
    public static float LINE_SPACING = 30.0f;

    public double cameraX = 0.0f;
    public double cameraY = 0.0f;
    public double cameraScale = 1.5f;
    public List<Line> horizontalLines = new ArrayList<>();
    public List<Line> verticalLines = new ArrayList<>();
    public Rectangle r;
    public List<Node> nodes = new ArrayList<>();

    //临时记录鼠标坐标
    private double rMouseX;
    private double rMouseY;
    private double rrMouseX;
    private double rrMouseY;
    //临时记录摄像机坐标
    private double rCameraX;
    private double rCameraY;
    private final EventHandler<MouseEvent> pressHandler = event -> {
        if (event.isMiddleButtonDown()) {
            rMouseX = event.getX();
            rMouseY = event.getY();
            rCameraX = cameraX;
            rCameraY = cameraY;
            rrMouseX = event.getX();
            rrMouseY = event.getY();
        }
    };
    private final EventHandler<MouseEvent> dragHandler = event -> {
        if (event.getButton() == MouseButton.MIDDLE) {
            cameraX = rCameraX - (event.getX() - rMouseX);
            cameraY = rCameraY - (event.getY() - rMouseY);
            this.background.setCursor(Cursor.MOVE);
            for (Node node : this.nodes) {
                node.resetPos((float) (node.startX - this.rrMouseX + event.getX()), (float) (node.startY - this.rrMouseY + event.getY()));
            }
            // 计算网格偏移量（确保网格从屏幕边缘开始）
            double hOffset = cameraY % (LINE_SPACING * cameraScale);
            double vOffset = cameraX % (LINE_SPACING * cameraScale);

            // 水平线条
            for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
                if (i < horizontalLines.size()) {
                    double y = -hOffset + i * LINE_SPACING * cameraScale;
                    horizontalLines.get(i).setStartY(y);
                    horizontalLines.get(i).setEndY(y);
                    horizontalLines.get(i).setStartX(0);
                    horizontalLines.get(i).setEndX(10000);
                }
            }

            // 垂直线条
            for (int i = 0; i < VERTICAL_LINE_COUNT; i++) {
                if (i < verticalLines.size()) {
                    double x = -vOffset + i * LINE_SPACING * cameraScale;
                    verticalLines.get(i).setStartX(x);
                    verticalLines.get(i).setEndX(x);
                    verticalLines.get(i).setStartY(0);
                    verticalLines.get(i).setEndY(10000);
                }
            }
            rrMouseX = event.getX();
            rrMouseY = event.getY();
        }
    };
    private final EventHandler<MouseEvent> releaseHandler = event -> this.background.setCursor(Cursor.DEFAULT);
    private final EventHandler<ScrollEvent> scrollHandler = event -> {
        if (event.getDeltaY() > 0.0) {
            this.cameraScale += 0.25;
        } else {
            this.cameraScale -= 0.25;
        }
        if (this.cameraScale <= 1) {
            this.cameraScale = 1;
        } else if (this.cameraScale >= 5.0) {
            this.cameraScale = 5.0;
        }
        double deltaX = event.getX() * (1 - cameraScale);
        double deltaY = event.getY() * (1 - cameraScale);
        // 应用摄像机位移
        cameraX += deltaX;
        cameraY += deltaY;
        for (int i = 0; i < this.horizontalLines.size(); i++) {
            horizontalLines.get(i).setStartY((cameraY % (HORIZONTAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
            horizontalLines.get(i).setEndY((cameraY % (HORIZONTAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
        }
        for (int i = 0; i < this.verticalLines.size(); i++) {
            verticalLines.get(i).setStartX((cameraX % (VERTICAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
            verticalLines.get(i).setEndX((cameraX % (VERTICAL_LINE_COUNT * LINE_SPACING * 0.02)) + i * LINE_SPACING * cameraScale);
        }
    };

    public NodeEditor(Group root, Group baseGroup, Group topBase, Rectangle background) {
        super(root, baseGroup, topBase, background);
        init();
    }

    public void init() {
        this.title = "节点编辑器";
        this.background.setFill(BG_COLOR);
        for (int i = 0; i < HORIZONTAL_LINE_COUNT; i++) {
            Line line = new Line();
            line.setStroke(GRID_COLOR);
            line.setStrokeWidth(0.5f);
            line.setStartX(0.0f);
            line.setStartY(i * LINE_SPACING * cameraScale);
            line.setEndX(10000);
            line.setEndY(i * LINE_SPACING * cameraScale);
            line.setMouseTransparent(true);
            this.horizontalLines.add(line);
            this.base.getChildren().add(line);
        }
        for (int i = 0; i < VERTICAL_LINE_COUNT; i++) {
            Line line = new Line();
            line.setStroke(GRID_COLOR);
            line.setStrokeWidth(0.5f);
            line.setStartX(i * LINE_SPACING * cameraScale);
            line.setStartY(0.0);
            line.setEndX(i * LINE_SPACING * cameraScale);
            line.setEndY(10000.0);
            line.setMouseTransparent(true);
            this.verticalLines.add(line);
            this.base.getChildren().add(line);
        }
        this.background.addEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
        this.background.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragHandler);
        this.background.addEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
        this.background.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
        this.nodes.add(new Node(200.0, 200.0, this.base, "测试节点-Test node", new ArrayList<>(), new ArrayList<>(), Arrays.asList(
                new Node.NodeCardNode("测试字节型参数A-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BYTE, false),
                new Node.NodeCardNode("测试短整型参数B-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.SHORT, false),
                new Node.NodeCardNode("测试整型参数C-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.INT, false),
                new Node.NodeCardNode("测试长整型参数D-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.LONG, false),
                new Node.NodeCardNode("测试单精度浮点数参数E-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.FLOAT, false),
                new Node.NodeCardNode("测试双精度浮点数参数F-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.DOUBLE, false),
                new Node.NodeCardNode("测试布尔参数G-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.BOOLEAN, false),
                new Node.NodeCardNode("测试字节型参数H-Test parameter", List.of("参数描述-Parameter description"), Node.VarType.CHAR, false)
        )));
    }

    public void resetPos(float x, float y) {
        super.resetPosSuper(x, y);
    }

    public void resetSize(float width, float height) {
        super.resetSizeSuper(width, height);
    }

    @Override
    public MinWindowTypeEnum getType() {
        return MinWindowTypeEnum.NodeEditorType;
    }

    public void delete() {
        for (Line line : this.horizontalLines) {
            this.base.getChildren().remove(line);
        }
        for (Line line : this.verticalLines) {
            this.base.getChildren().remove(line);
        }
        for (Node node : this.nodes) {
            node.delete();
        }
        this.horizontalLines.clear();
        this.verticalLines.clear();
        this.background.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
        this.background.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragHandler);
        this.background.removeEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
        this.background.removeEventHandler(ScrollEvent.SCROLL, scrollHandler);
        this.base.getChildren().remove(this.r);
    }

}
