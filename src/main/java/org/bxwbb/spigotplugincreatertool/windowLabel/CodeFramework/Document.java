package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

// 抽象文档父类：定义代码存储和修改的核心接口
public abstract class Document {
    // 获取指定行的文本
    public abstract String getLine(int lineNum);

    // 获取总行数
    public abstract int getLineCount();

    // 更新文档内容（参数：新文本、修改的起止行列）
    public abstract void update(String newText, int startLine, int startCol, int endLine, int endCol);

    // 获取最近修改的行范围（返回int[2]：[startLine, endLine]）
    public abstract int[] getModifiedLineRange();
}