package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

public enum TokenType {
    KEYWORD,      // 关键字（如Java的class、public）
    IDENTIFIER,   // 标识符（如变量名、类名）
    STRING,       // 字符串（如"abc"）
    NUMBER,       // 数字（如123、3.14）
    COMMENT,      // 注释（如// 单行注释、/* 多行注释 */）
    OPERATOR,     // 运算符（如+、=、&&）
    PUNCTUATION,  // 标点符号（如;、{、}）
    UNKNOWN,      // 未知类型（默认）
    ERROR         // 错误（如未定义的标识符）
}