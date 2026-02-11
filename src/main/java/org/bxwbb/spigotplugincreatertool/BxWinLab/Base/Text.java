package org.bxwbb.spigotplugincreatertool.BxWinLab.Base;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public interface Text {

    double getTextWidth();
    double getTextHeight();

    javafx.scene.text.Text getTextView();

    Paint getTextColor();
    void setTextColor(Paint color);

    Font getTextFont();
    void setTextFont(Font font);

}
