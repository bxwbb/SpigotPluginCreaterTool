package org.bxwbb.spigotplugincreatertool.BxWinLab.MiniWindow;

import org.bxwbb.spigotplugincreatertool.BxWinLab.*;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.MiniWindow;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.Utils.FileUtil;

public abstract class TopMenuMiniWindow extends Stage implements org.bxwbb.spigotplugincreatertool.BxWinLab.Base.MiniWindow {

    public final String NAME;
    private Stage topStage;
    private HorizontalLayout topHorizontalLayout;
    private Stage baseStage;

    public TopMenuMiniWindow(String showName, Class<? extends org.bxwbb.spigotplugincreatertool.BxWinLab.Base.MiniWindow> clazz) {
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

    public TopMenuMiniWindow(String showName, Class<? extends MiniWindow> clazz, String iconPath) {
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
        baseStage = new Stage(getLayoutX(), getLayoutY(), getLayoutWidth(), getLayoutHeight());
        baseStage.setFillExtensionType(FillExtensionType.CENTER);
        VerticalLayout baseVerticalLayout = new VerticalLayout(getLayoutX(), getLayoutY(), getLayoutWidth(), getLayoutHeight());
        baseVerticalLayout.setFillExtensionType(FillExtensionType.VERTICAL);
        baseVerticalLayout.setSpacing(1);
        topStage = new Stage(getLayoutX(), getLayoutY(), getLayoutWidth(), 36);
        topStage.setFillExtensionType(FillExtensionType.HORIZONTAL);
        topStage.setHasMask(false);
        topHorizontalLayout = new HorizontalLayout(getLayoutX(), getLayoutY(), getLayoutWidth(), 36);
        topHorizontalLayout.setFillExtensionType(FillExtensionType.CENTER);
        topStage.addChild(topHorizontalLayout);
        baseVerticalLayout.addChild(topStage);
        baseVerticalLayout.addChild(baseStage);
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
        initMenu(hideMenu);
        topHorizontalLayout.addChild(hideMenu);
        this.addChild(baseStage);
    }

    private void initMenu(HideMenu hideMenu) {
        for (String name : MiniWindowEnum.getWindows().keySet()) {
            HorizontalLayout horizontalLayout = new HorizontalLayout(getLayoutX() + 10, getLayoutY() + 10, 200, 16);
            if (MiniWindowEnum.getMiniWindowInfo(name).iconPath() != null) {
                ImageBox imageBox = new ImageBox(getLayoutX() + 10, getLayoutY() + 10, 16, 16, FileUtil.loadResourceFile(MiniWindowEnum.getMiniWindowInfo(name).iconPath()));
                horizontalLayout.addChild(imageBox);
                horizontalLayout.addChild(new HorizontalStick(5));
            } else {
                horizontalLayout.addChild(new HorizontalStick(21));
            }
            TextBox textBox = new TextBox(getLayoutX() + 10, getLayoutY() + 10, 200, 16, name);
            textBox.setTextColor(ColorSetting.CONTROL_TEXT_BASE_COLOR);
            textBox.setLineMode(true);
            horizontalLayout.addChild(textBox);
            horizontalLayout.setWrap(true);
            MarginsBox marginsBox = new MarginsBox(5, 5, 5, 5, horizontalLayout);
            marginsBox.setLayoutX(getLayoutX() + 10);
            marginsBox.setLayoutY(getLayoutY() + 10);
            ButtonBox buttonBox = new ButtonBox(marginsBox.getLayoutX(), marginsBox.getLayoutY(), marginsBox.getLayoutWidth(), marginsBox.getLayoutHeight(), marginsBox);
            buttonBox.setVisible(false);
            hideMenu.getMenuVerticalLayout().addChild(buttonBox, 0, 0);
        }
    }

    public abstract void init();

    @Override
    public String getWindowName() {
        return NAME;
    }

    @Override
    public Stage getBaseStage() {
        return baseStage;
    }

    public Stage getTopStage() {
        return topStage;
    }

    public HorizontalLayout getTopHorizontalLayout() {
        return topHorizontalLayout;
    }
}
