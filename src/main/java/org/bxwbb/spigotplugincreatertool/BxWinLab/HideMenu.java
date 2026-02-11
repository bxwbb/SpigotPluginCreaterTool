package org.bxwbb.spigotplugincreatertool.BxWinLab;

import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Alignment;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.LabelEvent;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.RectangularFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class HideMenu extends BaseLabel {

    private static final Logger log = LoggerFactory.getLogger(HideMenu.class);
    private final Stage menuStage;
    private final SwitchBox switchBox;
    private final VerticalLayout menuVerticalLayout;
    // 自适应模式
    private boolean adaptive = true;
    // 菜单打开事件
    public Consumer<LabelEvent> menuOpenEvent;

    public HideMenu(double x, double y, double width, double height, BaseLabel baseLabel, double menuDX, double menuDY) {
        setRectangularFrame(new RectangularFrame(x, y, width, height));
        switchBox = new SwitchBox(x, y, width, height, baseLabel);
        switchBox.setStay(false);
        menuStage = new Stage(x + menuDX, y + menuDY, 500, 400);
        menuStage.getBaseRectangle().setStrokeWidth(1);
        menuStage.getBaseRectangle().setStroke(ColorSetting.CONTROL_BASE_BORDER_COLOR);
        menuVerticalLayout = new VerticalLayout(0, 0, 500, 400);
        menuVerticalLayout.setAlignment(Alignment.LEFT);
        menuVerticalLayout.setWrap(true);
        MarginsBox marginsBox = new MarginsBox(5, 5, 5, 5, menuVerticalLayout);
        marginsBox.setWrap(true);
        menuStage.addChild(marginsBox, 0, 0);
        menuStage.setVisible(false);
        menuStage.setWrap(true);
        switchBox.setWrap(true);
        this.addChild(switchBox);
        menuStage.setLayoutX(x + menuDX);
        menuStage.setLayoutY(y + menuDY);
        this.addChild(menuStage, menuDX, menuDY);
        switchBox.setMouseClickEvent(labelEvent -> {
            if (getMenuOpenEvent() != null && switchBox.isDown()) {
                getMenuOpenEvent().accept(labelEvent);
            }
            menuStage.setVisible(switchBox.isDown());
        });
    }

    public Stage getMenuStage() {
        return menuStage;
    }

    public VerticalLayout getMenuVerticalLayout() {
        return menuVerticalLayout;
    }

    public boolean isAdaptive() {
        return adaptive;
    }

    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
        switchBox.setWrap(adaptive);
        update();
    }

    public SwitchBox getSwitchBox() {
        return switchBox;
    }

    public Consumer<LabelEvent> getMenuOpenEvent() {
        return menuOpenEvent;
    }

    public void setMenuOpenEvent(Consumer<LabelEvent> menuOpenEvent) {
        this.menuOpenEvent = menuOpenEvent;
    }



}
