package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor.Node;
import org.bxwbb.spigotplugincreatertool.windowLabel.BaseLabel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeFramework extends BaseLabel {

    // 默认代码颜色
    public static Color CODE_COLOR = Color.rgb(230,230,230);
    // 关键字颜色
    public static Color KEY_WORD_COLOR = Color.rgb(255, 124, 222);
    // 字符串颜色
    public static Color STRING_COLOR = Color.rgb(53, 173, 94);
    // 注释颜色
    public static Color COMMENT_COLOR = Color.rgb(128,128,128);
    // 注解颜色
    public static Color ANNOTATION_COLOR = Color.rgb(230, 230, 0);

    public static Font codeFont = Font.loadFont(HelloApplication.class.getResourceAsStream(
            "/org/bxwbb/spigotplugincreatertool/font/Consolas.ttf"), 14  // 默认字号（可在使用时覆盖）
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
    // 生成一个包含JAVA中所有关键字的列表
    public static List<String> KEYWORDS = Arrays.asList("private", "protected", "public", "default", "abstract", "class", "extends", "final", "implements", "interface", "native", "new", "static", "strictfp", "synchronized", "transient", "volatile", "break", "case", "continue", "do", "else", "for", "if", "instanceof", "return", "switch", "while", "assert", "catch", "finally", "throw", "throws", "try", "import", "package", "byte", "char", "double", "float", "int", "long", "short", "super", "this", "void", "goto", "const");

    public static List<String> splitIncludingSpaces(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return result;
        }

        StringBuilder current = new StringBuilder();
        boolean inDoubleQuotes = false; // 是否在双引号字符串内
        boolean isEscaped = false;      // 当前字符是否被转义
        boolean isSpace = false;        // 当前片段是否为空格（仅双引号外有效）
        boolean inComment = false;      // 是否进入//注释模式

        for (int i = 0; i < s.length(); i++) {
            // 注释模式：所有内容作为独立元素，不再分割
            if (inComment) {
                current.append(s.charAt(i));
                continue;
            }

            char c = s.charAt(i);

            // 处理转义（仅非注释、非字符串内有效）
            if (isEscaped) {
                current.append(c);
                isEscaped = false;
                continue;
            }

            // 处理反斜杠（标记转义）
            if (c == '\\') {
                current.append(c);
                isEscaped = true;
                continue;
            }

            // 处理双引号（重点：起始引号前的内容先加入列表）
            if (c == '"') {
                // 仅处理非转义的双引号
                // 情况1：遇到起始双引号（从非字符串到字符串）
                if (!inDoubleQuotes) {
                    // 先将引号前的内容加入列表（若有）
                    if (!current.isEmpty()) {
                        result.add(current.toString());
                        current = new StringBuilder(); // 重置，准备收集字符串
                    }
                    // 开始收集字符串（加入起始双引号）
                    current.append(c);
                    inDoubleQuotes = true; // 标记进入字符串
                }
                // 情况2：遇到结束双引号（从字符串到非字符串）
                else {
                    current.append(c); // 加入结束双引号
                    // 整个字符串完成，加入列表
                    result.add(current.toString());
                    current = new StringBuilder(); // 重置，准备收集后续内容
                    inDoubleQuotes = false; // 标记退出字符串
                    isSpace = false; // 重置空格标记
                }
                continue;
            }

            // 处理//注释（双引号外才生效）
            if (!inDoubleQuotes && c == '/' && i < s.length() - 1 && s.charAt(i + 1) == '/') {
                // 先添加当前积累的非注释内容（若有）
                if (!current.isEmpty()) {
                    result.add(current.toString());
                    current = new StringBuilder();
                }
                // 拼接//并进入注释模式
                current.append(c).append(s.charAt(++i));
                inComment = true;
                continue;
            }

            // 双引号外的普通字符处理（按空格分割）
            if (!inDoubleQuotes) {
                boolean currentIsSpace = (c == ' ');
                if (currentIsSpace == isSpace) {
                    current.append(c); // 同类型（空格/非空格）继续拼接
                } else {
                    // 类型不同，分割当前片段
                    if (!current.isEmpty()) {
                        result.add(current.toString());
                    }
                    current = new StringBuilder().append(c);
                    isSpace = currentIsSpace;
                }
            } else {
                // 双引号内：所有字符直接拼接（属于字符串的一部分）
                current.append(c);
            }
        }

        // 处理剩余内容（可能是未闭合的字符串、普通内容或注释）
        if (!current.isEmpty()) {
            result.add(current.toString());
        }

        return result;
    }

    public CodeFramework(double x, double y, double width, double height) {
        this.startX = x;
        this.startY = y;
        this.endX = x + width;
        this.endY = y + height;
        this.base = new Group();
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
                "public class LabelTest extends Application {",
                "",
                "    @Override",
                "    public void start(Stage primaryStage) {",
                "        // 创建主面板",
                "        Group root = new Group();",
                "        root.setStyle(\"-fx-background-color: #2b2b2b;\");",
                "        Scene scene = new Scene(root, 1000, 800);",
                "",
                "        CodeFramework codeFramework = new CodeFramework(100, 100, 700, 600);",
                "        codeFramework.addTo(root);",
                "",
                "        // 设置舞台",
                "        primaryStage.setTitle(\"贝塞尔曲线独立信号动画测试\");",
                "        primaryStage.setScene(scene);",
                "        primaryStage.show();",
                "    }",
                "",
                "    public static void main(String[] args) {",
                "        launch(args);",
                "    }",
                "}"
        );
        this.background = new Rectangle(x, y, width, height);
        this.background.setFill(HelloApplication.UNSELECTED_COLOR);
        this.background.setArcWidth(HelloApplication.ROUNDNESS);
        this.background.setArcHeight(HelloApplication.ROUNDNESS);
        this.background.setStrokeWidth(3.0);
        this.background.setStroke(HelloApplication.BORDER_COLOR);
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
        updateText();
    }

    public void updateText() {
        StringBuilder textBuffer = new StringBuilder();
        this.textFlow.getChildren().clear();
        for (int i = 1; i <= this.codeText.size(); i++) {
            textBuffer.append(i).append("\n");
            List<String> tokens = splitIncludingSpaces(this.codeText.get(i - 1) + "\n");
            for (String token : tokens) {
                Text text = getText(token);
                this.textFlow.getChildren().add(text);
            }
        }
        this.lineText.setText(textBuffer.toString());
        this.lineText.setX(this.startX + 8);
        this.lineText.setY(this.startY + lineOffset + 15);
        this.lineTextLine.setStartX(this.startX + this.lineText.getLayoutBounds().getWidth() + 13);
        this.lineTextLine.setEndX(this.startX + this.lineText.getLayoutBounds().getWidth() + 13);
        this.lineTextLine.setStartY(this.startY);
        this.lineTextLine.setEndY(this.endY);
        this.textFlow.setLayoutX(this.startX + this.lineText.getLayoutBounds().getWidth() + 15 + XOffset);
        this.textFlow.setLayoutY(this.startY + lineOffset + 2);
    }

    @NotNull
    private static Text getText(String token) {
        Text text = new Text(token);
        text.setFont(codeFont);
        text.setStyle("-fx-font-family: 'Consolas', 'Microsoft YaHei', 'SimSun';");

        return text;
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
        this.base.getChildren().add(this.lineText);
        this.base.getChildren().add(this.lineTextLine);
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
        return new CodeFramework(this.startX, this.startY, this.endX - this.startX, this.endY - this.startY);
    }

    @Override
    public Node.VarType getVarType() {
        return null;
    }
}
