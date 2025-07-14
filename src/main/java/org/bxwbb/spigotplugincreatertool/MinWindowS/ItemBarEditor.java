package org.bxwbb.spigotplugincreatertool.MinWindowS;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.MinWindowType;

public class ItemBarEditor extends MinWindowType {

    public Text test;

    public ItemBarEditor(Group root, Group base, Group topBase, Rectangle background) {
        super(root, base, topBase, background);
        init();
    }

    @Override
    public void init() {
        this.title = "物品栏编辑器";
        this.background.setFill(Color.color(0.2, 0.2, 0.2, 1.0));
        this.background.setFill(Color.GREEN);
        test = new Text("这是一个巨大的测试\n物品栏编辑器");
        test.setFill(Color.BLACK);
        test.setStrokeWidth(50.0f);
        test.setFont(new Font(40));
        this.base.getChildren().add(test);
    }

    @Override
    public void resetPos(float x, float y) {
        super.resetPosSuper(x, y);
        test.setX(this.startX + (this.endX - this.startX) * 0.5);
        test.setY(this.startY + (this.endY - this.startY) * 0.5);
    }

    @Override
    public void resetSize(float width, float height) {
        super.resetSizeSuper(width, height);
        test.setX(this.startX + (this.endX - this.startX) * 0.5);
        test.setY(this.startY + (this.endY - this.startY) * 0.5);
    }

    @Override
    public void delete() {
        this.base.getChildren().remove(this.test);
    }

    @Override
    public MinWindowTypeEnum getType() {
        return MinWindowTypeEnum.ItemBarEditorType;
    }
}
