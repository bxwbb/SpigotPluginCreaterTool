package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeFramework extends BaseLabel {

    public static Font codeFont = Font.loadFont(
            HelloApplication.class.getResourceAsStream("/org/bxwbb/spigotplugincreatertool/font/Consolas.ttf"),
            14  // 默认字号（可在使用时覆盖）
    );

    // 上下位移比率
    public double lineOffset = 0.0;

    public List<String> codeText;
    public final TextFlow textFlow;
    public final TextFlow lineTextFlow;

    private final Rectangle background;

    public CodeFramework(double x, double y, double width, double height) {
        this.startX = x;
        this.startY = y;
        this.endX = x + width;
        this.endY = y + height;
        this.base = new Group();
//        this.codeText = new ArrayList<>();
        this.codeText = Arrays.asList(
                "public static void main(String args[]) {",
                "    System.out.println(\"Hello world!\");",
                "}"
        );
        this.background = new Rectangle(x, y, width, height);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(3.0);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
        Rectangle mask = new Rectangle(x, y, width, height);
        mask.setArcWidth(HelloApplication.ROUNDNESS);
        mask.setArcHeight(HelloApplication.ROUNDNESS);
        mask.setStrokeWidth(1.5);
//        this.base.setClip(mask);
        Text text = new Text(" ");
        text.setFont(codeFont);
        this.textFlow = new TextFlow();
        this.textFlow.getChildren().add(text);
        this.lineTextFlow = new TextFlow();
        updateText();
    }

    public void updateText() {
        for (int i = 1;i <= this.codeText.size();i++) {
            if (i < this.lineTextFlow.getChildren().size()) {
                Text text = (Text) this.lineTextFlow.getChildren().get(i - 1);
                text.setLayoutX(this.startX);
                text.setY(this.startY + i * (text.getLayoutBounds().getHeight() + this.lineOffset));
            } else {
                Text text = new Text(i + "\n");
                text.setLayoutX(this.startX);
                text.setY(this.startY + i * (text.getLayoutBounds().getHeight() + this.lineOffset));
                text.setFill(HelloApplication.UNSELECTED_FONT_COLOR);
                text.setFont(codeFont);
                this.lineTextFlow.getChildren().add(text);
            }
        }
    }

    @Override
    public void resetPos(double x, double y) {

    }

    @Override
    public void resetSize(double width, double height) {

    }

    @Override
    public void delete() {
        this.base.getChildren().retainAll();
        this.root.getChildren().remove(this.base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().add(this.background);
        this.base.getChildren().add(this.textFlow);
        this.base.getChildren().add(this.lineTextFlow);
        this.root.getChildren().add(this.base);
    }

    @Override
    public void setName(String name) {

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
    public void setVisible(boolean visible) {

    }

    @Override
    public Object getData() {
        return this.codeText;
    }

    @Override
    public void setData(Object data) throws ClassNotFoundException {

    }

    @Override
    public BaseLabel createNew() throws ClassNotFoundException {
        return new CodeFramework(
                this.startX,
                this.startY,
                this.endX - this.startX,
                this.endY - this.startY
        );
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }
}
