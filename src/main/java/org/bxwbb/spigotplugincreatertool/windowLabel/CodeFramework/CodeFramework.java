package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.windowLabel.BaseLabel;

import java.util.*;

public class CodeFramework extends BaseLabel {

    // 默认代码颜色
    public static Color CODE_COLOR = Color.rgb(210, 210, 210);
    // 关键字颜色
    public static Color KEY_WORD_COLOR = Color.rgb(255, 124, 222);
    // 字符串颜色
    public static Color STRING_COLOR = Color.rgb(53, 173, 94);
    // 注释颜色
    public static Color COMMENT_COLOR = Color.rgb(128, 128, 128);
    // 注解颜色
    public static Color ANNOTATION_COLOR = Color.rgb(230, 230, 0);
    // 数字颜色
    public static Color NUMBER_COLOR = Color.rgb(228, 94, 11);
    // 错误主色：深红（醒目但不刺眼，避免纯红的刺眼感，符合错误提示语义）
    public static Color ERROR_COLOR = Color.rgb(220, 38, 38); // 十六进制 #DC2626
    // 光标颜色
    public static Paint CURSOR_COLOR = Color.rgb(230, 230, 230);

    public static Font codeFont = Font.font(
            "Consolas",
            14
    );
    public static Font lineFont = Font.font(
            "Consolas",
            FontWeight.BOLD,
            14
    );

    // 上下位移比率
    public double lineOffset = 0.0;
    public double XOffset = 0.0;

    public List<String> codeText;
    public final TextFlow textFlow;
    public final Text lineText;

    private final Rectangle background;
    // 分割行号和正文的线
    private final Line lineTextLine;
    private final TokenFixer tokenFixer;
    private final List<TokenShader> tokenShaders;
    // 光标
    private final Cursor mainCursor;

    public CodeFramework(double x, double y, double width, double height, TokenFixer tokenFixer, List<TokenShader> tokenShaders) {
        this.startX = x;
        this.startY = y;
        this.endX = x + width;
        this.endY = y + height;
        this.base = new Group();
        tokenFixer.codeFramework = this;
//        this.codeText = new ArrayList<>();
        this.codeText = Arrays.asList(
                "package org.bxwbb.spigotplugincreatertool;",
                "",
                "import javafx.application.Application;",
                "import javafx.scene.Group;",
                "import javafx.scene.Scene;",
                "import javafx.stage.Stage;",
                "import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeFramework;",
                "",
                "@ALL(\"Test\")",
                "public class LabelTest extends Application {",
                "",
                "    @Override",
                "    public void start(Stage primaryStage) {",
                "        // 创建主面板",
                "        Group root = new Group();",
                "        root.setStyle(\"-fx-background-color: #FbCb8b;\");",
                "        Scene scene = new Scene(root, 1000, 800);",
                "",
                "        codeFramework codeFramework = new CodeFramework(100, 100, 700, 600);",
                "        codeFramework.addTo(root);",
                "",
                "        // 设置舞台",
                "        primaryStage.setTitle(\"贝塞尔曲线独立信号动画测试\");",
                "        System.out.println(\"在Java中使用\\\\\\\"来表示\\\"\");",
                "        System.out.println(\"所以要表示上面这句话就要这样写\\\\\\\\\\\\\\\"来表示\\\\\\\"\")",
                "        primaryStage.setScene(scene);",
                "        primaryStage.show();",
                "        a = ((((1 + 1 - 1 * 1 / 1 % 1))));",
                "        b = (1 == 1 && 1 != 1 || (!1 == 1)) ? 1 > 1 : 1 < 1;",
                "        c = 1 >= 1 ? 1 <= 1 : () -> {{{{{{{{{{{}}}}}}}}}}};",
                "        List<String> f = new ArrayList<>();",
                "    }",
                "",
                "    public static void main(String[] args) {",
                "        launch(args);",
                "    }",
                "",
                "}"
        );
        this.background = new Rectangle(x, y, width, height);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(3.0);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
        this.mainCursor = new Cursor(this);
        this.mainCursor.cursor.setX(this.startX);
        this.mainCursor.cursor.setY(this.startY);
        this.mainCursor.cursor.setWidth(3);
        this.mainCursor.cursor.setHeight(codeFont.getSize());
        this.mainCursor.setBlink(true);
        Rectangle mask = new Rectangle(x + 1, y + 1, width - 2, height - 2);
        mask.setArcWidth(HelloApplication.ROUNDNESS);
        mask.setArcHeight(HelloApplication.ROUNDNESS);
        mask.setStrokeWidth(1.5);
        this.base.setClip(mask);
        this.textFlow = new TextFlow();
        this.lineText = new Text();
        this.lineText.setX(this.startX);
        this.lineText.setY(this.startY + lineOffset);
        this.lineText.setFont(lineFont);
        this.lineText.setFill(HelloApplication.UNSELECTED_FONT_COLOR);
        this.lineTextLine = new Line(this.startX + this.lineText.getLayoutBounds().getWidth() + 13, this.startY, this.startX + this.lineText.getLayoutBounds().getWidth() + 13, this.endY);
        this.lineTextLine.setFill(HelloApplication.BORDER_COLOR);
        this.lineTextLine.setStroke(HelloApplication.BORDER_COLOR);
        this.textFlow.setLayoutX(this.startX + this.lineText.getLayoutBounds().getWidth() + 15 + XOffset);
        this.textFlow.setLayoutY(this.startY + lineOffset + 2);
        this.tokenFixer = tokenFixer;
        this.tokenFixer.root = base;
        this.tokenShaders = tokenShaders;
        updateText();
    }

    public void updateText() {
        StringBuilder textBuffer = new StringBuilder();
        this.textFlow.getChildren().clear();
        for (int i = 1; i <= this.codeText.size(); i++) {
            textBuffer.append(i).append("\n");
        }
        this.lineText.setText(textBuffer.toString());
        this.lineText.setX(this.startX + 8);
        this.lineText.setY(this.startY + lineOffset + 15);
        this.lineTextLine.setStartX(this.startX + this.lineText.getLayoutBounds().getWidth() + 13);
        this.lineTextLine.setEndX(this.startX + this.lineText.getLayoutBounds().getWidth() + 13);
        this.lineTextLine.setStartY(this.startY);
        this.lineTextLine.setEndY(this.endY);
        this.tokenFixer.setCode(this.textFlow, this.codeText);
        this.tokenFixer.splitCode();
        this.textFlow.getChildren().remove(this.tokenFixer.head.text);
        this.tokenFixer.head = this.tokenFixer.head.getNext();
        CodeToken token = tokenFixer.getHead();
        for (TokenShader tokenShader : this.tokenShaders) {
            tokenShader.reset();
        }
        this.textFlow.setLayoutX(this.startX + this.lineText.getLayoutBounds().getWidth() + 15 + XOffset);
        this.textFlow.setLayoutY(this.startY + lineOffset + 2);
        while (token != null) {
            token.setFont(codeFont);
            token = token.getNext();
        }
        token = tokenFixer.getHead();
        while (token != null) {
            token.updateTestBackground();
            token = token.getNext();
        }
        token = tokenFixer.getHead();
        while (token != null) {
            for (TokenShader tokenShader : this.tokenShaders) {
                tokenShader.shader(token, this);
            }
            token = token.getNext();
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
        this.base.getChildren().removeAll();
        this.root.getChildren().remove(this.base);
    }

    @Override
    public void addTo(Group root) {
        this.root = root;
        this.base.getChildren().addFirst(this.lineTextLine);
        this.base.getChildren().addFirst(this.lineText);
        this.base.getChildren().addFirst(this.mainCursor.cursor);
        this.base.getChildren().addFirst(this.textFlow);
        this.base.getChildren().addFirst(this.background);
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
        return new CodeFramework(this.startX, this.startY, this.endX - this.startX, this.endY - this.startY, this.tokenFixer, this.tokenShaders);
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }

    public Cursor getMainCursor() {
        return this.mainCursor;
    }

}
