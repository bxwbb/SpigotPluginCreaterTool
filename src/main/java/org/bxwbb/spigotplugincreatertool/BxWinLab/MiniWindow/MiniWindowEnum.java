package org.bxwbb.spigotplugincreatertool.BxWinLab.MiniWindow;

import java.util.HashMap;
import java.util.Map;

public class MiniWindowEnum {

    private static final Map<String, MiniWindowInfo> windows = new HashMap<>();

    private MiniWindowEnum() {}

    /**
     * 注册一个窗口
     */
    public static void register(String name, MiniWindowInfo miniWindowInfo) {
        windows.put(name, miniWindowInfo);
    }

    /**
     * 注销一个窗口
     */
    public static void unregister(String name) {
        windows.remove(name);
    }

    /**
     * 是否注册了窗口
     */
    public static boolean isRegistered(String name) {
        return windows.containsKey(name);
    }

    public static Map<String, MiniWindowInfo> getWindows() {
        return windows;
    }

    public static MiniWindowInfo getMiniWindowInfo(String name) {
        return windows.get(name);
    }

    public static void setInfo(String name, MiniWindowInfo info) {
        windows.put(name, info);
    }

}
