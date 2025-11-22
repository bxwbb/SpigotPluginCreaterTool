package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class TokenFixer {

    protected CodeToken head;
    private CodeToken last;
    protected Group root;
    protected CodeFramework codeFramework;

    private String code;

    public CodeToken getHead() {
        return head;
    }

    public TokenFixer() {
    }

    protected TokenFixer(TextFlow textFlow, String code, Group root, CodeFramework codeFramework) {
        this.code = code;
        head = new CodeToken(TokenType.UNKNOWN, code, new Text(), textFlow, root, codeFramework);
        last = head;
        splitCode();
        textFlow.getChildren().remove(this.head.text);
        this.head = this.head.getNext();
        this.codeFramework = codeFramework;
    }

    protected TokenFixer(TextFlow textFlow, List<String> codes, Group root, CodeFramework codeFramework) {
        StringBuilder code = new StringBuilder();
        for (String c : codes) {
            code.append(c).append("\n");
        }
        this.code = code.toString();
        head = new CodeToken(TokenType.UNKNOWN, code.toString(), new Text(), textFlow, root, codeFramework);
        last = head;
        this.codeFramework = codeFramework;
    }

    protected void setCode(TextFlow textFlow, String code) {
        this.code = code;
        this.setHead(new CodeToken(TokenType.UNKNOWN, code, new Text(), textFlow, root, codeFramework));
    }

    protected void setCode(TextFlow textFlow, List<String> codes) {
        StringBuilder code = new StringBuilder();
        for (String c : codes) {
            code.append(c).append("\n");
        }
        this.code = code.toString();
        this.setHead(new CodeToken(TokenType.UNKNOWN, code.toString(), new Text(), textFlow, root, codeFramework));
    }

    /**
     * 将完整的代码分割为token链表
     */
    protected void splitCode() {
        step(this.code);
        this.head.text.setText("");
    }

    public void step(String s) {
        if (s == null || s.isEmpty()) {
            return;
        }

        StringBuilder token = new StringBuilder();
        int index = 0;
        boolean spaceMode = false;
        boolean stringMode = false;

        while (index < s.length()) {
            char c = s.charAt(index);

            // 1. 优先处理转义字符（\ + 任意字符，单独作为token）
            if (c == '\\' && index + 1 < s.length()) {
                if (!token.isEmpty()) {
                    addToken(TokenType.UNKNOWN, token.toString());
                    token.setLength(0);
                    spaceMode = false;
                }
                String escapeToken = s.substring(index, index + 2);
                addToken(TokenType.UNKNOWN, escapeToken);
                index += 2;
                continue;
            }

            // 2. 处理运算符和比较符（优先双字符，再单字符，非字符串模式下生效）
            if (!stringMode) {
                // 2.1 检查双字符运算符/比较符
                if (index + 1 < s.length()) {
                    String twoChar = s.substring(index, index + 2);
                    if (isTwoCharOperator(twoChar)) {
                        if (!token.isEmpty()) {
                            addToken(TokenType.UNKNOWN, token.toString());
                            token.setLength(0);
                            spaceMode = false;
                        }
                        addToken(TokenType.UNKNOWN, twoChar);
                        index += 2;
                        continue;
                    }
                }

                // 2.2 检查单字符运算符/比较符
                if (isSingleCharOperator(c)) {
                    if (!token.isEmpty()) {
                        addToken(TokenType.UNKNOWN, token.toString());
                        token.setLength(0);
                        spaceMode = false;
                    }
                    addToken(TokenType.UNKNOWN, String.valueOf(c));
                    index++;
                    continue;
                }
            }

            // 3. 处理独立分割符号：()[]{}<>;.（每个符号单独作为token）
            if (isSpecialSeparator(c)) {
                if (stringMode) {
                    token.append(c); // 字符串内的符号作为普通内容
                    index++;
                    continue;
                }
                // 非字符串模式：提交当前token（若有），符号单独成token
                if (!token.isEmpty()) {
                    addToken(TokenType.UNKNOWN, token.toString());
                    token.setLength(0);
                    spaceMode = false;
                }
                addToken(TokenType.UNKNOWN, String.valueOf(c));
                index++;
                continue;
            }

            // 4. 处理逗号（独立分割，不合并）
            if (c == ',') {
                if (stringMode) {
                    token.append(c);
                    index++;
                    continue;
                }
                if (!token.isEmpty()) {
                    addToken(TokenType.UNKNOWN, token.toString());
                    token.setLength(0);
                    spaceMode = false;
                }
                addToken(TokenType.UNKNOWN, String.valueOf(c));
                index++;
                continue;
            }

            // 5. 处理未转义的双引号（字符串边界）
            if (c == '"') {
                if (stringMode) {
                    token.append(c);
                    stringMode = false;
                    addToken(TokenType.UNKNOWN, token.toString());
                    token.setLength(0);
                } else {
                    if (!token.isEmpty()) {
                        addToken(TokenType.UNKNOWN, token.toString());
                        token.setLength(0);
                    }
                    token.append(c);
                    stringMode = true;
                }
                index++;
                continue;
            }

            // 6. 处理空格（连续空格合并）
            if (c == ' ') {
                if (stringMode) {
                    token.append(c);
                    index++;
                    continue;
                }
                if (spaceMode) {
                    token.append(c);
                } else {
                    if (!token.isEmpty()) {
                        addToken(TokenType.UNKNOWN, token.toString());
                        token.setLength(0);
                    }
                    token.append(c);
                    spaceMode = true;
                }
                index++;
                continue;
            }

            // 7. 字符串模式下：识别并分离颜色代码（#ABCDEF格式）
            if (stringMode && c == '#' && index + 6 < s.length()) {
                boolean isColorCode = true;
                for (int i = 1; i <= 6; i++) {
                    char hexChar = s.charAt(index + i);
                    if (!isHexChar(hexChar)) {
                        isColorCode = false;
                        break;
                    }
                }
                if (isColorCode) {
                    if (!token.isEmpty()) {
                        addToken(TokenType.UNKNOWN, token.toString());
                        token.setLength(0);
                    }
                    String colorCode = s.substring(index, index + 7);
                    addToken(TokenType.UNKNOWN, colorCode);
                    index += 7;
                    continue;
                }
            }

            // 8. 处理普通字符
            if (stringMode) {
                token.append(c);
                index++;
            } else if (spaceMode) {
                addToken(TokenType.UNKNOWN, token.toString());
                token.setLength(0);
                token.append(c);
                spaceMode = false;
                index++;
            } else {
                token.append(c);
                index++;
            }
        }

        // 处理剩余未提交的token
        if (!token.isEmpty()) {
            addToken(TokenType.UNKNOWN, token.toString());
        }
    }

    // 判断是否为双字符运算符/比较符
    private boolean isTwoCharOperator(String str) {
        return str.equals("+=") || str.equals("-=") || str.equals("*=") || str.equals("/=")
                || str.equals("%=") || str.equals("==") || str.equals("!=") || str.equals(">=")
                || str.equals("<=") || str.equals("&&") || str.equals("||") || str.equals("++")
                || str.equals("--") || str.equals("<<") || str.equals(">>") || str.equals(">>>")
                || str.equals("&=") || str.equals("|=") || str.equals("^=") || str.equals("->")
                || str.equals("//") || str.equals("/*") || str.equals("*/");
    }

    // 判断是否为单字符运算符/比较符
    private boolean isSingleCharOperator(char c) {
        return c == '=' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%'
                || c == '?' || c == '!' || c == '&' || c == '|' || c == '^' || c == '~'
                || c == '<' || c == '>' || c == '\n' || c == '"' || c == '\''
                || c == '@';
    }

    // 判断是否为十六进制字符（0-9, A-F, a-f）
    private boolean isHexChar(char c) {
        return (c >= '0' && c <= '9')
                || (c >= 'A' && c <= 'F')
                || (c >= 'a' && c <= 'f');
    }

    // 独立分割符号：()[]{}<>;.
    private boolean isSpecialSeparator(char c) {
        return c == '(' || c == ')'
                || c == '[' || c == ']'
                || c == '{' || c == '}'
                || c == ';' || c == '.'; // 注意：<和>已作为运算符处理，这里移除
    }

    public CodeToken createToken(TokenType type, String value) {
        return new CodeToken(type, value, new Text(value), head.textFlow, root, codeFramework);
    }

    public void addToken(TokenType type, String value) {
        last.addAfter(createToken(type, value));
        this.last = this.last.getNext();
    }

    public void setHead(CodeToken head) {
        this.head = head;
        this.last = head;
    }

}
