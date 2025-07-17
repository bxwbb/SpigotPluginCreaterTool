package org.bxwbb.spigotplugincreatertool.windowLabel;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * 支持输入0-65535的数字或单个字符的TextField
 * 当输入数字并失去焦点时，自动转换为对应的Unicode字符
 */
public class CharTextField extends TextField {

    public CharTextField() {
        // 设置文本格式化器 - 允许数字和单个字符
        this.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // 允许空输入
            if (newText.isEmpty()) {
                return change;
            }

            // 允许单个字符
            if (newText.length() == 1) {
                return change;
            }

            // 允许0-65535范围内的数字
            try {
                int value = Integer.parseInt(newText);
                if (value >= 0 && value <= 65535) {
                    return change;
                }
            } catch (NumberFormatException e) {
                // 不是有效数字，拒绝
            }

            return null; // 拒绝不符合条件的输入
        }));

        // 失去焦点时，将数字转换为字符
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // 失去焦点
                String text = this.getText();
                if (text == null || text.isEmpty()) {
                    return;
                }

                // 如果是数字，转换为字符
                try {
                    int value = Integer.parseInt(text);
                    if (value >= 0 && value <= 65535) {
                        this.setText(String.valueOf((char) value));
                    }
                } catch (NumberFormatException e) {
                    // 不是数字，保持原样
                }
            }
        });
    }

    /**
     * 获取文本框中的值
     * @return 字符的Unicode码点
     */
    public Integer getCodePoint() {
        String text = this.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 返回第一个字符的Unicode码点
        return (int) text.charAt(0);
    }

    /**
     * 设置文本框的值
     * @param codePoint 字符的Unicode码点(0-65535)
     */
    public void setCodePoint(Integer codePoint) {
        if (codePoint == null) {
            this.clear();
        } else if (codePoint >= 0 && codePoint <= 65535) {
            this.setText(String.valueOf((char) codePoint.intValue()));
        }
    }
}