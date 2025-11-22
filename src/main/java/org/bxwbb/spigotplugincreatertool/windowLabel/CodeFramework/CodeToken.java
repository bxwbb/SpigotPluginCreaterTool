package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.bxwbb.spigotplugincreatertool.windowLabel.WaveLineLabel;

import java.util.ArrayList;
import java.util.List;

public class CodeToken {

    protected TokenType type;
    protected String value;
    private CodeToken last;
    private CodeToken next;
    private final List<CodeToken> linkedTokens;
    // 前基组
    private final Group base;
    // 后基组
    private final Group nextBase;
    private final Group root;
    private final Group baseBase;
    protected Text text;
    protected TextFlow textFlow;
    private WaveLineLabel waveLine;
    boolean errorLine = false;
    protected CodeFramework codeFramework;
    private final Rectangle testBackground;

    protected CodeToken(TokenType type, String value, Text text, TextFlow textFlow, Group root, CodeFramework codeFramework) {
        this.type = type;
        this.value = value;
        this.text = text;
        this.textFlow = textFlow;
        this.codeFramework = codeFramework;
        if (!this.textFlow.getChildren().contains(this.text)) {
            this.textFlow.getChildren().add(this.text);
        }
        this.linkedTokens = new ArrayList<>();
        this.root = root;
        this.base = new Group();
        this.textFlow.getChildren().add(this.textFlow.getChildren().indexOf(this.text), this.base);
        this.nextBase = new Group();
        this.textFlow.getChildren().add(this.nextBase);
        this.baseBase = new Group();
        this.root.getChildren().add(this.baseBase);
        this.testBackground = new Rectangle(0,0,0,0);
        this.testBackground.setFill(Color.color(0,0,0,0)); // 填充透明
        this.testBackground.setStroke(Color.TRANSPARENT); // 边框透明（若需要边框可改为其他颜色，如 Color.GRAY）
        this.testBackground.setStrokeWidth(0); // 边框宽度设为0（彻底透明）
        this.testBackground.setOnMouseClicked(event -> {
            this.codeFramework.getCursor().setX(this.getX());
            this.codeFramework.getCursor().setY(this.getY());
        });
        this.baseBase.getChildren().addFirst(this.testBackground);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public CodeToken getLast() {
        return last;
    }

    public CodeToken getNext() {
        return next;
    }

    public Group getBase() {
        return base;
    }

    public Group getNextBase() {
        return nextBase;
    }

    public Group getBaseBase() {
        return baseBase;
    }

    public List<CodeToken> getLinkedTokens() {
        return linkedTokens;
    }

    public double getLocalPositonX() {
        this.text.getLayoutBounds();
        return this.text.getLayoutX();
    }

    public double getLocalPositonY() {
        this.text.getLayoutBounds();
        return this.text.getLayoutY();
    }

    public double getX() {
        return codeFramework.startX + codeFramework.lineText.getLayoutBounds().getWidth() + 15 + codeFramework.XOffset + this.getLocalPositonX();
    }

    public double getY() {
        return codeFramework.textFlow.getLayoutY() + this.getLocalPositonY();
    }

    /**
     * 获取本行第一个非空格字符
     */
    public CodeToken getFirstNonSpace() {
        CodeToken token = this;
        while (token != null && !token.getValue().equals("\n")) {
            if (token.getLast() != null) {
                token = token.getLast();
            } else {
                break;
            }
        }
        do {
            assert token != null;
            if (token.getNext() != null) {
                token = token.getNext();
            } else {
                break;
            }
        } while (token.getValue().charAt(0) == ' ');
        return token;
    }

    /**
     * 在这个token前添加一个token
     */
    public void addBefore(CodeToken token) {
        if (token == null) {
            return;
        }
        token.next = this;
        token.last = last;
        if (last != null) last.next = token;
        last = token;
    }

    /**
     * 在这个token后添加一个token
     */
    public void addAfter(CodeToken token) {
        if (token == null) {
            return;
        }
        token.next = next;
        token.last = this;
        if (next != null) next.last = token;
        next = token;
    }

    public CodeToken removeSelf() {
        if (last == null) {
            this.next.last = null;
            return this.next;
        } else if (next == null) {
            this.last.next = null;
            return this;
        } else {
            this.last.next = this.next;
            this.next.last = this.last;
            return this;
        }
    }

    public void setFont(Font font) {
        text.setFont(font);
    }

    public void setStyle(String style) {
        text.setStyle(style);
    }

    public void updateTestBackground() {
        this.testBackground.setX(this.getX());
        this.testBackground.setY(this.getY());
        if (this.getNext() == null || this.getValue().equals("\n")) {
            this.testBackground.setWidth(this.codeFramework.endX);
        } else {
            this.testBackground.setWidth(this.text.getLayoutBounds().getWidth());
        }
        this.testBackground.setHeight(this.text.getLayoutBounds().getHeight());
    }

    public Text getText() {
        return text;
    }

    public void setColor(Paint color) {
        this.text.setFill(color);
    }

    public void setText(String text) {
        this.text.setText(text);
        this.value = text;
    }

    public Group getRoot() {
        return root;
    }

    public void setErrorLine(boolean errorLine, Color color) {
        this.errorLine = errorLine;
        if (errorLine) {
            waveLine = new WaveLineLabel();
            waveLine.resetPos(this.getX(), this.getY() + this.text.getLayoutBounds().getHeight());
            waveLine.resetSize(this.text.getLayoutBounds().getWidth(), 0);
            waveLine.setStrokeColor(color);
            waveLine.setAmplitude(5);
            waveLine.setPeriod(60);
            waveLine.setStrokeWidth(1.5);
            waveLine.addTo(this.baseBase);
        } else {
            if (waveLine != null) {
                waveLine.delete();
                waveLine = null;
            }
        }
    }

}
