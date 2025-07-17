package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.control.TextField;

/**
 * 只能输入正负整数的TextField，支持自动补全符号
 */
public class IntegerTextField extends TextField {

    public IntegerTextField() {
        // 设置文本格式化器，只允许输入正负整数
        this.setTextFormatter(new javafx.scene.control.TextFormatter<String>(change -> {
            String newText = change.getControlNewText();

            // 正则表达式：只允许正负整数
            if (newText.matches("^[+-]?\\d*$")) {
                return change; // 允许符合格式的输入
            }
            return null; // 拒绝不符合格式的输入
        }));
    }

    /**
     * 获取文本框中的值并转换为Long
     * @return 转换后的整数值，若格式不正确则返回null
     */
    public Long getNumericValue() {
        String text = this.getText();

        // 处理空字符串
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 处理只包含正负号的情况
        if (text.equals("+") || text.equals("-")) {
            return null;
        }

        try {
            // 使用严格的Long转换
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            System.err.println("格式错误: " + text);
            return null;
        }
    }
}