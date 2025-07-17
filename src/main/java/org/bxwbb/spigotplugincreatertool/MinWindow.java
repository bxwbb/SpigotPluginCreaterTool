package org.bxwbb.spigotplugincreatertool;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MinWindow {

    public static final float PADDING = 5.0f;
    public static final float TEST_PADDING = 15.0f;
    public static final float MIN_WIDTH = 30.0f;
    public static final float MIN_HEIGHT = 30.0f;

    public float startX;
    public float startY;
    public float endX;
    public float endY;
    public MinWindowType minWindowType;
    public MinWindow minWindows;
    public MinWindow parent = null;
    public float allEndPos;
    public boolean upDown;
    public Rectangle background;
    public Rectangle windowTypeButtonBackground;
    public boolean isWindowTypeMenuOpening = false;
    public Rectangle menuBackground;
    public List<Text> menuTexts = new ArrayList<>();
    public Rectangle menuTextSelectedBackground;
    public Rectangle menuTextHoverBackground;
    public Line topBar;
    public Text title;
    public Group root;
    public Group base;
    public Group topBase;
    private boolean isCreating = false;
    private boolean isCreated = false;

    private void initMinWindow(float startX, float startY, float endX, float endY, Group root, MinWindowType.MinWindowTypeEnum type) {
        this.base = new Group();
        this.topBase = new Group();
        this.root = root;
        this.minWindows = null;
        this.upDown = false;
        this.allEndPos = endY;
        this.background = new Rectangle(startX + PADDING, startY + PADDING, endX - startX - PADDING, endY - startY - PADDING);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setStrokeWidth(0.0f);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setOnMouseMoved(event -> {
            if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                    Math.abs(this.startY + TEST_PADDING * 2 - event.getY()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.V_RESIZE);
                this.topBar.setCursor(Cursor.V_RESIZE);
            } else if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                    Math.abs(this.endY - TEST_PADDING - event.getY()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.V_RESIZE);
                this.topBar.setCursor(Cursor.V_RESIZE);
            } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                    Math.abs(this.startX + TEST_PADDING * 2 - event.getX()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.H_RESIZE);
                this.topBar.setCursor(Cursor.H_RESIZE);
            } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                    Math.abs(this.endX - TEST_PADDING - event.getX()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.H_RESIZE);
                this.topBar.setCursor(Cursor.H_RESIZE);
            } else if (Math.abs(event.getX() - this.endX + TEST_PADDING) <= TEST_PADDING &&
                    Math.abs(event.getY() - this.endY + TEST_PADDING) <= TEST_PADDING) {
                this.background.setCursor(Cursor.CROSSHAIR);
                this.topBar.setCursor(Cursor.CROSSHAIR);
            } else {
                this.background.setCursor(Cursor.DEFAULT);
                this.topBar.setCursor(Cursor.DEFAULT);
            }
        });
        this.background.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                        Math.abs(this.startY + TEST_PADDING * 2 - event.getY()) <= TEST_PADDING && !isCreating) {
                    this.background.setCursor(Cursor.V_RESIZE);
                    this.topBar.setCursor(Cursor.V_RESIZE);
                    //上边框拉动
                } else if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                        Math.abs(this.endY - TEST_PADDING - event.getY()) <= TEST_PADDING && !isCreating) {
                    this.background.setCursor(Cursor.V_RESIZE);
                    this.topBar.setCursor(Cursor.V_RESIZE);
                    //下边框拉动
                } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                        Math.abs(this.startX + TEST_PADDING * 2 - event.getX()) <= TEST_PADDING && !isCreating) {
                    this.background.setCursor(Cursor.H_RESIZE);
                    this.topBar.setCursor(Cursor.H_RESIZE);
                    //左边框拉动
                } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                        Math.abs(this.endX - TEST_PADDING - event.getX()) <= TEST_PADDING && !isCreating) {
                    this.background.setCursor(Cursor.H_RESIZE);
                    this.topBar.setCursor(Cursor.H_RESIZE);
                    //右边框拉动
                } else if (Math.abs(event.getX() - this.endX + TEST_PADDING) <= TEST_PADDING &&
                        Math.abs(event.getY() - this.endY + TEST_PADDING) <= TEST_PADDING && !isCreating) {
                    this.background.setCursor(Cursor.CROSSHAIR);
                    this.topBar.setCursor(Cursor.CROSSHAIR);
                    if (!this.isCreated) {
                        this.isCreating = true;
                    }
                }
                if (this.isCreated) {
                    this.isCreating = false;
                    if ((event.getX() >= this.startX + MIN_WIDTH) && (event.getX() <= this.minWindows.endX - MIN_WIDTH) && !this.upDown) {
                        this.resetSize((float) (event.getX() - this.startX), this.endY - this.startY);
                        this.minWindows.resetPos((float) event.getX(), this.startY);
                        this.minWindows.resetSize(this.minWindows.endX - this.minWindows.startX, this.minWindows.endY - this.minWindows.startY);
                    } else if ((event.getY() >= this.startY + MIN_HEIGHT) && (event.getY() <= this.minWindows.endY - MIN_HEIGHT) && this.upDown) {
                        this.resetSize(this.endX - this.startX, (float) (event.getY() - this.startY));
                        this.minWindows.resetPos(this.startX, (float) event.getY());
                        this.minWindows.resetSize(this.minWindows.endX - this.minWindows.startX, this.minWindows.endY - this.minWindows.startY);
                    }
                }
                if (this.isCreating) {
                    if (event.getX() <= this.endX - MIN_WIDTH) {
                        if (this.minWindows == null) {
                            this.resetSize(this.endX - this.startX - MIN_WIDTH, this.endY - this.startY);
                            this.minWindows = new MinWindow(this.endX, this.startY, this.endX + MIN_WIDTH, this.endY, this.root, this.minWindowType.getType());
                            this.minWindows.parent = this;
                            this.isCreated = true;
                        } else {
                            this.resetSize(this.endX - this.startX - MIN_WIDTH, this.endY - this.startY);
                            MinWindow r = new MinWindow(this.endX, this.startY, this.endX + MIN_WIDTH, this.endY, this.root, this.minWindowType.getType());
                            r.minWindows = this.minWindows;
                            this.minWindows = r;
                            this.isCreated = true;
                            this.minWindows.minWindows.parent = this.minWindows;
                            this.minWindows.parent = this;
                        }
                        this.upDown = false;
                    } else if (event.getY() <= this.endY - MIN_HEIGHT) {
                        if (this.minWindows == null) {
                            this.resetSize(this.endX - this.startX, this.endY - this.startY - MIN_HEIGHT);
                            this.minWindows = new MinWindow(this.startX, this.endY, this.endX, this.endY + MIN_HEIGHT, this.root, this.minWindowType.getType());
                            this.minWindows.parent = this;
                            this.isCreated = true;
                        } else {
                            this.resetSize(this.endX - this.startX, this.endY - this.startY - MIN_HEIGHT);
                            MinWindow r = new MinWindow(this.startX, this.endY, this.endX, this.endY + MIN_HEIGHT, this.root, this.minWindowType.getType());
                            r.minWindows = this.minWindows;
                            this.minWindows = r;
                            this.isCreated = true;
                            this.minWindows.minWindows.parent = this.minWindows;
                            this.minWindows.parent = this;
                        }
                        this.upDown = true;
                    }
                }
            }
        });
        this.background.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.isCreated = false;
            }
        });
        this.minWindowType = MinWindowType.MinWindowTypeEnum.createMinWindowType(this.root, this.base, this.topBase, this.background, type);
        this.minWindowType.resetPos(startX + PADDING, startY + PADDING);
        this.minWindowType.resetSize(endX - startX - PADDING, endY - startY - PADDING);
        this.topBar = new Line(startX + PADDING, startY + PADDING, endX - PADDING, startY + PADDING);
        this.topBar.setStroke(HelloApplication.MENU_COLOR);
        this.topBar.setStrokeWidth(60.0f);
        this.topBar.setOnMouseMoved(event -> {
            if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                    Math.abs(this.startY + TEST_PADDING * 2 - event.getY()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.V_RESIZE);
                this.topBar.setCursor(Cursor.V_RESIZE);
            } else if ((this.startX + TEST_PADDING * 2 <= event.getX() && this.endX - TEST_PADDING * 2 >= event.getX()) &&
                    Math.abs(this.endY - TEST_PADDING - event.getY()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.V_RESIZE);
                this.topBar.setCursor(Cursor.V_RESIZE);
            } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                    Math.abs(this.startX + TEST_PADDING * 2 - event.getX()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.H_RESIZE);
                this.topBar.setCursor(Cursor.H_RESIZE);
            } else if ((this.startY + TEST_PADDING * 2 <= event.getY() && this.endY - TEST_PADDING * 2 >= event.getY()) &&
                    Math.abs(this.endX - TEST_PADDING - event.getX()) <= TEST_PADDING) {
                this.background.setCursor(Cursor.H_RESIZE);
                this.topBar.setCursor(Cursor.H_RESIZE);
            } else if (Math.abs(event.getX() - this.endX + TEST_PADDING) <= TEST_PADDING &&
                    Math.abs(event.getY() - this.endY + TEST_PADDING) <= TEST_PADDING) {
                this.background.setCursor(Cursor.CROSSHAIR);
                this.topBar.setCursor(Cursor.CROSSHAIR);
            } else {
                this.background.setCursor(Cursor.DEFAULT);
                this.topBar.setCursor(Cursor.DEFAULT);
            }
        });
        this.menuTexts.clear();
        this.menuBackground = new Rectangle();
        this.menuBackground.setFill(HelloApplication.MENU_COLOR);
        this.menuBackground.setArcHeight(HelloApplication.ROUNDNESS);
        this.menuBackground.setArcWidth(HelloApplication.ROUNDNESS);
        this.menuBackground.setStrokeWidth(0.0f);
        this.menuBackground.setOnMouseMoved(event -> {
            this.menuTextHoverBackground.setX(HelloApplication.CANCEL_SHOW_OFFSET);
            this.menuTextHoverBackground.setY(HelloApplication.CANCEL_SHOW_OFFSET);
        });
        this.menuBackground.setVisible(false);
        this.menuTextSelectedBackground = new Rectangle();
        this.menuTextSelectedBackground.setFill(HelloApplication.SELECTED_COLOR);
        this.menuTextSelectedBackground.setArcWidth(HelloApplication.ROUNDNESS);
        this.menuTextSelectedBackground.setArcHeight(HelloApplication.ROUNDNESS);
        this.menuTextSelectedBackground.setStrokeWidth(0.0f);
        this.menuTextSelectedBackground.setMouseTransparent(true);
        this.menuTextSelectedBackground.setVisible(false);
        this.menuTextHoverBackground = new Rectangle();
        this.menuTextHoverBackground.setFill(HelloApplication.HOVER_COLOR);
        this.menuTextHoverBackground.setArcWidth(HelloApplication.ROUNDNESS);
        this.menuTextHoverBackground.setArcHeight(HelloApplication.ROUNDNESS);
        this.menuTextHoverBackground.setStrokeWidth(0.0f);
        this.menuTextHoverBackground.setVisible(false);
        this.windowTypeButtonBackground = new Rectangle();
        this.windowTypeButtonBackground.setArcWidth(HelloApplication.ROUNDNESS);
        this.windowTypeButtonBackground.setArcHeight(HelloApplication.ROUNDNESS);
        this.windowTypeButtonBackground.setStrokeWidth(0.5f);
        this.windowTypeButtonBackground.setStroke(HelloApplication.BORDER_COLOR);
        this.windowTypeButtonBackground.setFill(HelloApplication.UNSELECTED_COLOR);
        this.windowTypeButtonBackground.setOnMouseEntered(event -> {
            if (!this.isWindowTypeMenuOpening) {
                this.windowTypeButtonBackground.setFill(HelloApplication.HOVER_COLOR);
            }
        });
        this.windowTypeButtonBackground.setOnMouseExited(event -> {
            if (!this.isWindowTypeMenuOpening) {
                this.windowTypeButtonBackground.setFill(HelloApplication.UNSELECTED_COLOR);
            }
        });
        this.windowTypeButtonBackground.setOnMouseClicked(event -> {
            this.isWindowTypeMenuOpening = !this.isWindowTypeMenuOpening;
            if (this.isWindowTypeMenuOpening) {
                this.windowTypeButtonBackground.setFill(HelloApplication.SELECTED_COLOR);
                this.menuBackground.setVisible(true);
                for (Text t : this.menuTexts) {
                    t.setVisible(true);
                }
                this.menuTextSelectedBackground.setVisible(true);
                this.menuTextHoverBackground.setVisible(true);
            } else {
                this.windowTypeButtonBackground.setFill(HelloApplication.UNSELECTED_COLOR);
                this.menuBackground.setVisible(false);
                for (Text t : this.menuTexts) {
                    t.setVisible(false);
                }
                this.menuTextSelectedBackground.setVisible(false);
                this.menuTextHoverBackground.setVisible(false);
            }
        });
        this.windowTypeButtonBackground.setCursor(Cursor.HAND);
        this.title = new Text();
        this.title.setFont(HelloApplication.TEXT_FONT);
        this.title.setText(this.minWindowType.title + " ▼");
        this.title.setFill(HelloApplication.FONT_COLOR);
        this.title.setMouseTransparent(true);
        this.root.getChildren().add(this.menuBackground);
        this.root.getChildren().add(this.menuTextHoverBackground);
        this.root.getChildren().add(this.menuTextSelectedBackground);
        for (MinWindowType.MinWindowTypeEnum t : MinWindowType.MinWindowTypeEnum.values()) {
            Text text = getText(t);
            text.setVisible(false);
            this.menuTexts.add(text);
            this.root.getChildren().add(text);
        }
        resetPos(startX, startY);
        resetSize(endX - startX, endY - startY);
        this.base.getChildren().addFirst(this.background);
        this.topBase.getChildren().addFirst(this.title);
        this.topBase.getChildren().addFirst(this.windowTypeButtonBackground);
        this.topBase.getChildren().addFirst(this.topBar);
        this.base.setClip(getMask());
        this.topBase.setClip(getMask());
        this.root.getChildren().add(this.topBase);
        this.root.getChildren().addFirst(this.base);
    }

    private Text getText(MinWindowType.MinWindowTypeEnum t) {
        Text text = new Text(t.name);
        text.setFont(HelloApplication.TEXT_FONT);
        text.setFill(HelloApplication.FONT_COLOR);
        text.setOnMouseEntered(event -> {
            this.menuTextHoverBackground.setX(text.getX() - 5.0f);
            this.menuTextHoverBackground.setY(text.getY() - 12.0f);
            this.menuTextHoverBackground.setWidth(text.getLayoutBounds().getWidth() + 10.0f);
            this.menuTextHoverBackground.setHeight(text.getLayoutBounds().getHeight() + 5.0);
        });
        text.setOnMouseClicked(event -> {
            this.minWindowType.delete();
            this.minWindowType = MinWindowType.MinWindowTypeEnum.createMinWindowType(this.root, this.base, this.topBase, this.background, Objects.requireNonNull(MinWindowType.MinWindowTypeEnum.fondWithName(text.getText())));
            this.minWindowType.resetPos(this.startX, this.startY);
            this.minWindowType.resetSize(this.endX - this.startX, this.endY - this.startY);
            this.isWindowTypeMenuOpening = false;
            this.menuBackground.setVisible(false);
            this.menuTexts.forEach(r -> r.setVisible(false));
            this.menuTextSelectedBackground.setVisible(false);
            this.menuTextHoverBackground.setVisible(false);
            this.windowTypeButtonBackground.setFill(HelloApplication.UNSELECTED_COLOR);
            this.title.setText(text.getText() + " ▼");
            this.windowTypeButtonBackground.setX(startX + PADDING + 5.0);
            this.windowTypeButtonBackground.setY(startY + PADDING + 5.0);
            this.windowTypeButtonBackground.setWidth(this.title.getLayoutBounds().getWidth() + 8.0);
            this.windowTypeButtonBackground.setHeight(this.title.getLayoutBounds().getHeight() + 5.0);
            for (Text menuText : this.menuTexts) {
                if (menuText.getText().equals(this.minWindowType.title)) {
                    this.menuTextSelectedBackground.setX(menuText.getX() - 5.0f);
                    this.menuTextSelectedBackground.setY(menuText.getY() - 12.0f);
                    this.menuTextSelectedBackground.setWidth(menuText.getLayoutBounds().getWidth() + 10.0f);
                    this.menuTextSelectedBackground.setHeight(menuText.getLayoutBounds().getHeight() + 5.0);
                }
            }
        });
        return text;
    }

    public MinWindow(float startX, float startY, float endX, float endY, Group root, MinWindowType.MinWindowTypeEnum type) {
        initMinWindow(startX, startY, endX, endY, root, type);
    }

    public void resetPos(float x, float y) {
        this.startX = x;
        this.startY = y;
        this.background.setX(x + PADDING);
        this.background.setY(y + PADDING);
        this.background.setWidth(endX - startX - PADDING * 2);
        this.background.setHeight(endY - startY - PADDING * 2);
        this.topBar.setStartX(x + PADDING);
        this.topBar.setStartY(y + PADDING);
        this.topBar.setEndX(endX - PADDING);
        this.topBar.setEndY(y + PADDING);
        this.windowTypeButtonBackground.setX(x + PADDING + 5.0);
        this.windowTypeButtonBackground.setY(y + PADDING + 5.0);
        this.windowTypeButtonBackground.setWidth(this.title.getLayoutBounds().getWidth() + 8.0);
        this.windowTypeButtonBackground.setHeight(this.title.getLayoutBounds().getHeight() + 5.0);
        this.title.setX(x + PADDING * 2 + 5.0);
        this.title.setY(y + PADDING * 4 + 5.0);
        for (int i = 0; i < this.menuTexts.size(); i++) {
            this.menuTexts.get(i).setX(x + PADDING + 15.0);
            this.menuTexts.get(i).setY(y + PADDING * 10.0 + 10.0 + i * PADDING * 3.8);
            if (this.menuTexts.get(i).getText().equals(this.minWindowType.title)) {
                this.menuTextSelectedBackground.setX(this.menuTexts.get(i).getX() - 5.0f);
                this.menuTextSelectedBackground.setY(this.menuTexts.get(i).getY() - 12.0f);
                this.menuTextSelectedBackground.setWidth(this.menuTexts.get(i).getLayoutBounds().getWidth() + 10.0f);
                this.menuTextSelectedBackground.setHeight(this.menuTexts.get(i).getLayoutBounds().getHeight() + 5.0);
            }
        }
        this.menuBackground.setX(x + PADDING + 5.0);
        this.menuBackground.setY(y + PADDING * 8.0);
        float menuWidth = 0;
        float menuHeight = 0;
        for (Text t : this.menuTexts) {
            menuWidth = (float) Math.max(menuWidth, t.getLayoutBounds().getWidth());
            menuHeight += (float) (t.getLayoutBounds().getHeight() + 5.0);
        }
        this.menuBackground.setWidth(menuWidth + PADDING * 2 + 5.0);
        this.menuBackground.setHeight(menuHeight + PADDING * 2 + 5.0);
        this.menuTextHoverBackground.setWidth(menuWidth + PADDING * 2 + 5.0);
        this.base.setClip(getMask());
        this.topBase.setClip(getMask());
        this.minWindowType.resetPos(x, y);
    }

    public void resetSize(float width, float height) {
        this.endX = startX + width;
        this.endY = startY + height;
        this.background.setWidth(width - PADDING * 2);
        this.background.setHeight(height - PADDING * 2);
        this.topBar.setEndX(endX - PADDING);
        this.topBar.setEndY(startY + PADDING);
        this.base.setClip(getMask());
        this.topBase.setClip(getMask());
        this.minWindowType.resetSize(width, height);
    }

    public Rectangle getMask() {
        Rectangle mask = new Rectangle(startX + PADDING, startY + PADDING, endX - startX - PADDING * 2, endY - startY - PADDING * 2);
        mask.setStrokeWidth(0.0f);
        mask.setArcWidth(HelloApplication.ROUNDNESS);
        mask.setArcHeight(HelloApplication.ROUNDNESS);
        return mask;
    }

    public void onMouseSceneClick(MouseEvent event) {
        if (this.isWindowTypeMenuOpening) {
            if (!isPointInRectangle(this.menuBackground.getX() - 40.0f, this.menuBackground.getY() - 40.0f, this.menuBackground.getX() + this.menuBackground.getWidth() + 40.0f, this.menuBackground.getY() + this.menuBackground.getHeight() + 40.0f, event.getX(), event.getY())) {
                this.isWindowTypeMenuOpening = false;
                this.menuBackground.setVisible(false);
                for (Text t : this.menuTexts) {
                    t.setVisible(false);
                }
                this.menuTextSelectedBackground.setVisible(false);
                this.menuTextHoverBackground.setVisible(false);
                this.windowTypeButtonBackground.setFill(HelloApplication.UNSELECTED_COLOR);
            }
        }
        if (this.minWindows == null) return;
        this.minWindows.onMouseSceneClick(event);
    }

    /**
     * 判断点是否在矩形内（支持任意顺序的矩形坐标）
     *
     * @param x1 矩形的第一个x坐标
     * @param y1 矩形的第一个y坐标
     * @param x2 矩形的第二个x坐标
     * @param y2 矩形的第二个y坐标
     * @param px 待判断点的x坐标
     * @param py 待判断点的y坐标
     * @return 点在矩形内返回true，否则返回false
     */
    public static boolean isPointInRectangle(double x1, double y1, double x2, double y2, double px, double py) {
        // 计算矩形的最小和最大x坐标
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);

        // 计算矩形的最小和最大y坐标
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);

        // 判断点是否在矩形范围内（包含边界）
        return px >= minX && px <= maxX && py >= minY && py <= maxY;
    }

}
