package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import java.util.List;

// 抽象Token缓存父类：定义缓存管理接口
public abstract class TokenCache {
    // 缓存指定行的Token列表
    public abstract void cacheLineTokens(int lineNum, List<CodeToken> tokens);

    // 清除指定行范围的缓存
    public abstract void invalidateLines(int startLine, int endLine);

    // 合并缓存中的Token和新解析的Token（返回全量有序Token列表）
    public abstract List<CodeToken> mergeTokens(List<CodeToken> newTokens);
}