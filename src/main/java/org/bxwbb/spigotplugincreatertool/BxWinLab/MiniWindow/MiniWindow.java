package org.bxwbb.spigotplugincreatertool.BxWinLab.MiniWindow;

import org.bxwbb.spigotplugincreatertool.BxWinLab.*;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.Utils.FileUtil;

public abstract class MiniWindow extends Stage implements org.bxwbb.spigotplugincreatertool.BxWinLab.Base.MiniWindow {

    public final String NAME;

    public MiniWindow(String showName, Class<? extends MiniWindow> clazz) {
        super(100, 100, 100, 100);
        this.NAME = showName;
        this.setFillExtensionType(FillExtensionType.CENTER);
        this.getBaseRectangle().setArcWidth(0);
        this.getBaseRectangle().setArcHeight(0);
        this.getBaseRectangle().setSmooth(false);
        MiniWindowEnum.register(showName, new MiniWindowInfo(null, clazz));
        init();
        initSwitchButton();
    }

    public MiniWindow(String showName, Class<? extends MiniWindow> clazz, String iconPath) {
        super(100, 100, 100, 100);
        this.NAME = showName;
        this.setFillExtensionType(FillExtensionType.CENTER);
        this.getBaseRectangle().setArcWidth(0);
        this.getBaseRectangle().setArcHeight(0);
        this.getBaseRectangle().setSmooth(false);
        MiniWindowEnum.register(showName, new MiniWindowInfo(iconPath, clazz));
        init();
        initSwitchButton();
    }

    /**
     * 初始化切换按钮
     */
    public final void initSwitchButton() {
        HorizontalLayout horizontalLayout = new HorizontalLayout(getLayoutX() + 5, getLayoutY() + 5, 200, 16);
        if (MiniWindowEnum.getMiniWindowInfo(NAME).iconPath() != null) {
            ImageBox imageBox = new ImageBox(getLayoutX() + 5, getLayoutY() + 5, 16, 16, FileUtil.loadResourceFile(MiniWindowEnum.getMiniWindowInfo(NAME).iconPath()));
            horizontalLayout.addChild(imageBox);
            horizontalLayout.addChild(new HorizontalStick(5));
        }
        TextBox textBox = new TextBox(getLayoutX() + 5, getLayoutY() + 5, 200, 16, NAME);
        textBox.setTextColor(ColorSetting.CONTROL_TEXT_BASE_COLOR);
        textBox.setLineMode(true);
        horizontalLayout.addChild(textBox);
        horizontalLayout.setWrap(true);
        MarginsBox marginsBox = new MarginsBox(5, 5, 5, 5, horizontalLayout);
        marginsBox.setLayoutX(getLayoutX() + 5);
        marginsBox.setLayoutY(getLayoutY() + 5);
        marginsBox.setWrap(true);
        HideMenu hideMenu = new HideMenu(getLayoutX() + 5, getLayoutY() + 5, 100, 16, marginsBox, 0, 31);
        hideMenu.setMenuOpenEvent(event -> initMenu(hideMenu));
        this.addChild(hideMenu);
    }

    private void initMenu(HideMenu hideMenu) {
        hideMenu.getMenuVerticalLayout().clearChildren();
        for (String name : MiniWindowEnum.getWindows().keySet()) {
            HorizontalLayout horizontalLayout = new HorizontalLayout(hideMenu.getLayoutX(), hideMenu.getLayoutY() + 31, 200, 16);
            if (MiniWindowEnum.getMiniWindowInfo(name).iconPath() != null) {
                ImageBox imageBox = new ImageBox(hideMenu.getLayoutX(), hideMenu.getLayoutY() + 31, 16, 16, FileUtil.loadResourceFile(MiniWindowEnum.getMiniWindowInfo(name).iconPath()));
                horizontalLayout.addChild(imageBox);
                horizontalLayout.addChild(new HorizontalStick(5));
            } else {
                horizontalLayout.addChild(new HorizontalStick(21));
            }
            TextBox textBox = new TextBox(hideMenu.getLayoutX(), hideMenu.getLayoutY() + 31, 200, 16, name);
            textBox.setTextColor(ColorSetting.CONTROL_TEXT_BASE_COLOR);
            textBox.setLineMode(true);
            horizontalLayout.addChild(textBox, 0, 0);
            horizontalLayout.setWrap(true);
            MarginsBox marginsBox = new MarginsBox(5, 5, 5, 5, horizontalLayout);
            marginsBox.setLayoutX(getLayoutX() + 10);
            marginsBox.setLayoutY(getLayoutY() + 41);
            ButtonBox buttonBox = new ButtonBox(marginsBox.getLayoutX(), marginsBox.getLayoutY(), marginsBox.getLayoutWidth(), marginsBox.getLayoutHeight(), marginsBox);
            hideMenu.getMenuVerticalLayout().addChild(buttonBox, 0, 0);
        }
    }

    public abstract void init();

    @Override
    public Stage getBaseStage() {
        return this;
    }

    @Override
    public String getWindowName() {
        return NAME;
    }
}
