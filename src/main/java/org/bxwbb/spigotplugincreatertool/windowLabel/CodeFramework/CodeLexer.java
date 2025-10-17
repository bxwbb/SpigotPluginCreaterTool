package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import java.util.List;

public abstract class CodeLexer {
    private final Document document;
    private final TokenCache tokenCache;

    // 构造器：依赖文档和缓存
    public CodeLexer(Document document, TokenCache tokenCache) {
        this.document = document;
        this.tokenCache = tokenCache;
    }

    // 核心：增量解析并返回全量Token列表
    public abstract List<CodeLexer> tokenize();

    // 解析单行文本为Token（子类需实现具体语言的单行解析规则）
    protected abstract List<CodeLexer> tokenizeLine(String lineText, int lineNum);

    public TokenCache getTokenCache() {
        return tokenCache;
    }

    public Document getDocument() {
        return document;
    }
}
