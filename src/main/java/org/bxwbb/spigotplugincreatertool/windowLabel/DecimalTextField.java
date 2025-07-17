package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.control.TextField;

/**
 * 只能输入正负小数的TextField，允许直接输入小数点（自动补全为0.xxx）
 */
public class DecimalTextField extends TextField {

    public DecimalTextField() {
        // 设置文本格式化器，允许直接输入小数点
        this.setTextFormatter(new javafx.scene.control.TextFormatter<String>(change -> {
            String newText = change.getControlNewText();

            // 正则表达式：允许直接输入小数点
            if (newText.matches("^[+-]?(\\d+(\\.\\d*)?|\\.\\d*|\\d*)$")) {
                return change; // 允许符合格式的输入
            }
            return null; // 拒绝不符合格式的输入
        }));

        // 监听文本变化，处理自动补零
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // 处理以小数点开头的情况（自动补零）
            if (newValue.startsWith(".") && !newValue.startsWith("0.")) {
                this.setText("0" + newValue);
                // 将光标移到小数点后
                this.positionCaret(2);
            }

            // 处理以负号和小数点开头的情况（如-.5 → -0.5）
            if (newValue.startsWith("-.")) {
                this.setText("-0." + newValue.substring(2));
                // 将光标移到小数点后
                this.positionCaret(3);
            }

            // 处理以正号和小数点开头的情况（如+.5 → +0.5）
            if (newValue.startsWith("+.")) {
                this.setText("+0." + newValue.substring(2));
                // 将光标移到小数点后
                this.positionCaret(3);
            }
        });
    }

    /**
     * 获取文本框中的值并转换为Double
     * @return 转换后的浮点值，若格式不正确则返回null
     */
    public Double getNumericValue() {
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
            // 使用严格的Double转换
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.err.println("格式错误: " + text);
            return null;
        }
    }

    /**
     * 设置浮点值
     * @param value 要设置的浮点值
     */
    public void setNumericValue(Double value) {
        if (value == null) {
            this.clear();
        } else {
            this.setText(value.toString());
        }
    }
}