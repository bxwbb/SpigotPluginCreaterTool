package org.bxwbb.spigotplugincreatertool.WindowLabel;

import javafx.scene.Group;
import org.bxwbb.spigotplugincreatertool.WindowLabel.Base.BaseLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectionalLayout extends BaseLabel {
    private static final Logger log = LoggerFactory.getLogger(DirectionalLayout.class);
    private BaseLabel objectLabel;
    private final Group baseGroup;
    private EightDirection eightDirection = EightDirection.CENTER;

    public DirectionalLayout(double startX, double startY, double endX, double endY, BaseLabel objectLabel) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.base = new Group();
        this.baseGroup = new Group();
        this.objectLabel = objectLabel;
        this.objectLabel.setMouseTransparent(true);
        this.addChild(objectLabel);
        this.setEightDirection(EightDirection.CENTER);
    }

    @Override
    public void update() {
        resetObjectPos();
    }

    @Override
    public void setDisplayVisible(boolean visible) {
        this.visible = visible;
        objectLabel.setDisplayVisible(visible);
    }

    @Override
    public BaseLabel createNew() {
        return new DirectionalLayout(this.startX, this.startY, this.endX, this.endY, this.objectLabel);
    }

    @Override
    public void setMouseTransparent(boolean transparent) {
        super.setMouseTransparent(transparent);
        this.objectLabel.setMouseTransparent(this.isMouseTransparent());
    }

    public void setEightDirection(EightDirection eightDirection) {
        this.eightDirection = eightDirection;
        resetObjectPos();
    }

    /**
     * 重置目标组件位置（八向定位）
     * 优化点：冗余逻辑抽离、空指针保护、语义化命名、边界检查
     */
    private void resetObjectPos() {
        // 1. 空指针保护：目标组件为空时直接返回
        if (objectLabel == null) {
            log.warn("objectLabel 为空，无法重置位置");
            return;
        }

        // 2. 预计算通用变量（抽离冗余计算，提升性能和可读性）
        double parentCenterX = this.startX + this.getWidth() / 2;       // 父容器中心X
        double parentCenterY = this.startY + this.getHeight() / 2;      // 父容器中心Y
        double objectHalfWidth = objectLabel.getWidth() / 2;            // 目标组件半宽
        double objectHalfHeight = objectLabel.getHeight() / 2;          // 目标组件半高
        double objectFullHeight = objectLabel.getHeight();              // 目标组件全高
        double objectFullWidth = objectLabel.getWidth();                // 目标组件全宽

        // 3. 定义目标坐标变量
        double targetX = 0.0;
        double targetY = 0.0;

        // 4. 八向位置计算（语义化命名，逻辑清晰）
        switch (eightDirection) {
            case CENTER:
                // 居中对齐
                targetX = parentCenterX - objectHalfWidth;
                targetY = parentCenterY - objectHalfHeight;
                break;
            case UP:
                // 上居中
                targetX = parentCenterX - objectHalfWidth;
                targetY = this.startY + objectFullHeight; // 上偏移（可提取为常量）
                break;
            case UP_LEFT:
                // 左上
                targetX = this.startX;
                targetY = this.startY + objectFullHeight;
                break;
            case LEFT:
                // 左居中
                targetX = this.startX;
                targetY = parentCenterY - objectHalfHeight;
                break;
            case DOWN_LEFT:
                // 左下
                targetX = this.startX;
                targetY = this.endY;
                break;
            case DOWN:
                // 下居中
                targetX = parentCenterX - objectHalfWidth;
                targetY = this.endY;
                break;
            case DOWN_RIGHT:
                // 右下
                targetX = this.endX - objectFullWidth;
                targetY = this.endY;
                break;
            case RIGHT:
                // 右居中
                targetX = this.endX - objectFullWidth;
                targetY = parentCenterY - objectHalfHeight;
                break;
            case UP_RIGHT:
                // 右上
                targetX = this.endX - objectFullWidth;
                targetY = this.startY + objectFullHeight;
                break;
            default:
                log.warn("未知的控制类型 - {}", eightDirection);
                return; // 未知方向直接返回，避免设置非法坐标
        }

        // 5. 边界检查：确保坐标非负（可根据业务调整边界规则）
        targetX = Math.max(0.0, targetX);
        targetY = Math.max(0.0, targetY);

        // 6. 重置目标组件位置
        objectLabel.resetPos(targetX, targetY);
    }

    public EightDirection getEightDirection() {
        return this.eightDirection;
    }

    public BaseLabel getObjectLabel() {
        return objectLabel;
    }

    public void setObjectLabel(BaseLabel objectLabel) {
        this.objectLabel.delete();
        this.objectLabel = objectLabel;
        this.objectLabel.addTo(this.baseGroup);
    }

}
