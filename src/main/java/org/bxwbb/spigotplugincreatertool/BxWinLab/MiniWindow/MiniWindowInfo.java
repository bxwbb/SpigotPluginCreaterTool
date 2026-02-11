package org.bxwbb.spigotplugincreatertool.BxWinLab.MiniWindow;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.MiniWindow;

public record MiniWindowInfo(String iconPath, Class<? extends MiniWindow> clazz) {
}
