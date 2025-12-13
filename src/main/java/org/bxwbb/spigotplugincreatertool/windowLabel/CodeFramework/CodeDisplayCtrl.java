package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.scene.paint.Color;

public abstract class CodeDisplayCtrl {

    public abstract Color getTokenColor(String token);

    public record CodeToken(String token, CodeDisplayCtrl.CodeToken lastToken, CodeDisplayCtrl.CodeToken nextToken) {}

}
