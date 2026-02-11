package org.bxwbb.spigotplugincreatertool.Utils;

import javafx.scene.paint.Color;

public class ColorUtil {

    public static Color getRandomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }

    public static Color getRandomColor(double alpha) {
        return Color.color(Math.random(), Math.random(), Math.random(), alpha);
    }

}
