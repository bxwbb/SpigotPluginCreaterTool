package org.bxwbb.spigotplugincreatertool;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.bxwbb.spigotplugincreatertool.MinWindowS.ItemBarEditor;
import org.bxwbb.spigotplugincreatertool.MinWindowS.ItemEditor;
import org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.MinecraftServerCreater;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.NodeEditor;
import org.bxwbb.spigotplugincreatertool.MinWindowS.ShowEntityEditor;

public abstract class MinWindowType {

    public Group root;
    public Group base;
    public Group topBase;
    public Rectangle background;
    public float startX = 0.0f;
    public float startY = 0.0f;
    public float endX = 0.0f;
    public float endY = 0.0f;
    public String title = "Window Type 窗口类型";

    public MinWindowType(Group root, Group base, Group topBase, Rectangle background) {
        this.root = root;
        this.base = base;
        this.topBase = topBase;
        this.background = background;
    }

    public abstract void init() throws ClassNotFoundException;

    public abstract void resetPos(float x, float y);

    public void resetPosSuper(float x, float y) {
        startX = x;
        startY = y;
    }

    public double getEditorNameWidth() {
        return (new Text(this.title)).getLayoutBounds().getWidth() + 8.0;
    }

    public abstract void resetSize(float width, float height);

    public void resetSizeSuper(float width, float height) {
        endX = startX + width;
        endY = startY + height;
    }

    public abstract void delete();

    public enum MinWindowTypeEnum {
        // 节点编辑器
        NodeEditorType("节点编辑器"),
        // 物品栏编辑器
        ItemBarEditorType("物品栏编辑器"),
        // 物品编辑器
        ItemEditorType("物品编辑器"),
        // 展示实体编辑器
        ShowEntityEditorType("展示实体编辑器"),
        MinecraftServerCreater("我的世界服务器编辑器");

        public final String name;

        MinWindowTypeEnum(String name) {
            this.name = name;
        }

        public static MinWindowType createMinWindowType(Group root, Group base, Group topBase, Rectangle background, MinWindowTypeEnum type) throws ClassNotFoundException {
            return switch (type) {
                case NodeEditorType -> new NodeEditor(root, base, topBase, background);
                case ItemBarEditorType -> new ItemBarEditor(root, base, topBase, background);
                case ItemEditorType -> new ItemEditor(root, base, topBase, background);
                case ShowEntityEditorType -> new ShowEntityEditor(root, base, topBase, background);
                case MinecraftServerCreater -> new MinecraftServerCreater(root, base, topBase, background);
            };
        }

        public static MinWindowTypeEnum fondWithName(String name) {
            for (MinWindowTypeEnum type : MinWindowTypeEnum.values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    public abstract MinWindowTypeEnum getType();

}
