package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeCtrls;

import javafx.scene.paint.Color;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeDisplayCtrl;
import org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework.CodeFramework;

public class Java extends CodeDisplayCtrl {
    @Override
    public Color getTokenColor(String token) {
        if (CodeFramework.KEYWORDS.contains(token)) {
            return CodeFramework.KEY_WORD_COLOR;
        } else if (token.startsWith("\"") && token.endsWith("\"")) {
            return CodeFramework.STRING_COLOR;
        } else if (token.startsWith("'") && token.endsWith("'")) {
            return CodeFramework.STRING_COLOR;
        } else if (token.startsWith("//")) {
            return CodeFramework.COMMENT_COLOR;
        } else if (token.startsWith("@")) {
            return CodeFramework.ANNOTATION_COLOR;
        } else {
            return CodeFramework.CODE_COLOR;
        }
    }
}
