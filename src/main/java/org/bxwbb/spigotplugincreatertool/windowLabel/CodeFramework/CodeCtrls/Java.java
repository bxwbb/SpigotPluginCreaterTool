package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeCtrls;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.bxwbb.spigotplugincreatertool.HelloApplication;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeFramework;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeToken;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.TokenShader;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Java extends TokenShader {

    // 生成一个包含JAVA中所有关键字的列表
    public static List<String> KEYWORDS = Arrays.asList("private", "protected", "public", "default", "abstract", "class", "extends", "final", "implements", "interface", "native", "new", "static", "strictfp", "synchronized", "transient", "volatile", "break", "case", "continue", "do", "else", "for", "if", "instanceof", "return", "switch", "while", "assert", "catch", "finally", "throw", "throws", "try", "import", "package", "byte", "char", "double", "float", "int", "long", "short", "super", "this", "void", "goto", "const");
    // 成对符号的颜色列表
    public static Color[] PAIR_COLORS = {
            Color.rgb(60, 170, 180),   // 柔和青 - 适合 ()（替换原红色，避免错误联想）
            Color.rgb(70, 160, 70),    // 柔和绿 - 适合 {}
            Color.rgb(70, 100, 180),   // 沉静蓝 - 适合 []
            Color.rgb(230, 150, 60),   // 暖橙色 - 适合 <>（或其他成对符号）
            Color.rgb(150, 80, 180)    // 雅致紫 - 适合额外成对符号
    };
    // 特殊符号列表
    public static List<String> SPECIAL_SYMBOLS = Arrays.asList(
            "+=",
            "-=",
            "*=",
            "/=",
            "%=",
            "==",
            "!=",
            ">=",
            "<=",
            "&&",
            "||",
            "++",
            "--",
            "<<",
            ">>",
            ">>>",
            "&=",
            "|=",
            "^=",
            "->",
            "=",
            "+",
            "-",
            "*",
            "/",
            "%",
            "?",
            "!",
            "&",
            "|",
            "^",
            "~",
            "<",
            ">"
    );

    // 小括号计数
    int bracketLCount = 0;
    // 中括号计数
    int bracketMCount = 0;
    // 大括号计数
    int bracketBCount = 0;
    // 尖括号计数
    int bracketHCount = 0;
    boolean stringMode = false;
    boolean commentMode = false;
    // 多行注释模式
    boolean multiLineCommentMode = false;
    // 注解模式
    boolean annotationMode = false;

    @Override
    public void reset() {
        bracketLCount = 0;
        bracketMCount = 0;
        bracketBCount = 0;
        bracketHCount = 0;
        stringMode = false;
        commentMode = false;
        multiLineCommentMode = false;
        annotationMode = false;
    }

    @Override
    public void shader(CodeToken token, CodeFramework codeFramework) {
        if (token.getValue().isEmpty()) return;
        if (token.getValue().charAt(0) == '\\') {
            token.setColor(CodeFramework.KEY_WORD_COLOR);
        } else if (token.getValue().equals("\"")) {
            stringMode = !stringMode;
            token.setColor(CodeFramework.STRING_COLOR);
        } else if (Pattern.matches("#[0-9a-fA-F]{6}", token.getValue())) {
            // 将颜色代码转换为颜色
            token.setColor(Color.web(token.getValue()));
            Rectangle rectangle = new Rectangle(0, -2, token.getText().getLayoutBounds().getHeight(), token.getText().getLayoutBounds().getHeight() * 10);
            rectangle.setFill(Color.web(token.getValue()));
            rectangle.setArcWidth(HelloApplication.ROUNDNESS);
            rectangle.setArcHeight(HelloApplication.ROUNDNESS);
            token.addNodeBack(rectangle);
            token.setText(token.getValue());
        } else if (stringMode) {
            token.setColor(CodeFramework.STRING_COLOR);
        } else if (token.getValue().equals("/*")) {
            multiLineCommentMode = true;
            token.setColor(CodeFramework.COMMENT_COLOR);
        } else if (multiLineCommentMode) {
            if (token.getValue().equals("*/")) {
                multiLineCommentMode = false;
                token.setColor(CodeFramework.COMMENT_COLOR);
            } else {
                token.setColor(CodeFramework.COMMENT_COLOR);
            }
        } else if (token.getValue().equals("//")) {
            commentMode = true;
            token.setColor(CodeFramework.COMMENT_COLOR);
        } else if (token.getValue().equals("\n")) {
            commentMode = false;
            annotationMode = false;
        } else if (commentMode) {
            token.setColor(CodeFramework.COMMENT_COLOR);
        } else if (KEYWORDS.contains(token.getValue())) {
            token.setColor(CodeFramework.KEY_WORD_COLOR);
        } else if (token.getValue().equals(".") || token.getValue().equals(";")) {
            token.setColor(CodeFramework.CODE_COLOR);
        } else if (token.getValue().equals("(")) {
            bracketLCount++;
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
        } else if (token.getValue().equals(")")) {
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
            bracketLCount--;
        } else if (token.getValue().equals("{")) {
            bracketBCount++;
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
        } else if (token.getValue().equals("}")) {
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
            bracketBCount--;
            if (bracketBCount < 0) {
                token.setErrorLine(true, CodeFramework.ERROR_COLOR);
            } else {
                int index = 1;
                boolean enter = false;
                CodeToken temp = token;
                while (index >= 1) {
                    temp = temp.getLast();
                    if (temp.getValue().equals("{")) {
                        index--;
                    } else if (temp.getValue().equals("}")) {
                        index++;
                    }
                    if (temp.getValue().equals("\n")) {
                        enter = true;
                    }
                }
                if (!enter) return;
                Line line = getLine(token, codeFramework, temp);
                token.getBaseBase().getChildren().add(line);
            }
        } else if (token.getValue().equals("[")) {
            bracketMCount++;
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
        } else if (token.getValue().equals("]")) {
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
            bracketMCount--;
        } else if (token.getValue().equals("<")) {
            bracketHCount++;
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
        } else if (token.getValue().equals(">")) {
            token.setColor(PAIR_COLORS[Math.abs(bracketBCount) % PAIR_COLORS.length]);
            bracketHCount--;
        } else if (SPECIAL_SYMBOLS.contains(token.getValue())) {
            token.setColor(CodeFramework.KEY_WORD_COLOR);
        } else if (token.getValue().matches("[0-9]+")) {
            token.setColor(CodeFramework.NUMBER_COLOR);
        } else if (token.getValue().equals("@")) {
            annotationMode = true;
            token.setColor(CodeFramework.ANNOTATION_COLOR);
        } else if (annotationMode) {
            token.setColor(CodeFramework.ANNOTATION_COLOR);
        } else {
            token.setColor(CodeFramework.CODE_COLOR);
        }
    }

    private Line getLine(CodeToken token, CodeFramework codeFramework, CodeToken temp) {
        double lineX = token.getFirstNonSpace().getX() - 2;
//        double lineY = token.getY() + token.getText().getLayoutBounds().getHeight();
        double lineY = codeFramework.textFlow.getLayoutY() + temp.getLocalPositonY() + temp.getText().getLayoutBounds().getHeight();
        Line line = new Line(lineX, lineY, lineX, token.getY());
        line.setStroke(PAIR_COLORS[(bracketBCount + 1) % PAIR_COLORS.length]);
        line.setStrokeWidth(1);
        return line;
    }

}
