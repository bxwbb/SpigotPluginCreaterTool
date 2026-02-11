package org.bxwbb.spigotplugincreatertool.Window;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.bxwbb.spigotplugincreatertool.BxWinLab.*;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Enum.Direction;
import org.bxwbb.spigotplugincreatertool.BxWinLab.MiniWindow.MiniWindow;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Util.ColorSetting;
import org.bxwbb.spigotplugincreatertool.Utils.FileUtil;

public class StartPage extends MiniWindow {

    public StartPage() {
        super("开始标签页", StartPage.class, "/org/bxwbb/spigotplugincreatertool/icon/MiniWindowIcon/StartPage.png");
    }

    @Override
    public void init() {
        VerticalLayout verticalLayout = new VerticalLayout(0, 0, 700, 500);
        verticalLayout.setFillExtensionType(FillExtensionType.VERTICAL);
        verticalLayout.addChild(new VerticalStick(100));
        TextBox title = new TextBox(0, 0, 550, 50, "开始或选择一个项目吧!");
        title.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 48)
        );
        title.getTextView().setFill(Color.WHITE);
        DirectionalLayout directionalLayout = new DirectionalLayout(0, 0, 250, 50, title);
        directionalLayout.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout.addChild(directionalLayout);
        verticalLayout.addChild(new VerticalStickLine(50, 160));
        VerticalLayout verticalLayout1 = new VerticalLayout(0, 0, 500, 100);
        verticalLayout1.setFillExtensionType(FillExtensionType.VERTICAL);
        verticalLayout1.setSpacing(30);

        ImageBox imageBox = new ImageBox(0, 0, 50, 50, FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/Universal/OpenFolder.png"));
        TextBox textBox = new TextBox(0, 0, 250, 50, "打开一个项目");
        textBox.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 50)
        );
        ImageTextButtonBox buttonBox = new ImageTextButtonBox(0, 0, 250, 50, imageBox, textBox);
        buttonBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout1.addChild(buttonBox);

        imageBox = new ImageBox(0, 0, 50, 50, FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/Universal/NewProject.png"));
        textBox = new TextBox(0, 0, 250, 50, "新建一个项目");
        textBox.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 50)
        );
        buttonBox = new ImageTextButtonBox(0, 0, 250, 50, imageBox, textBox);
        buttonBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout1.addChild(buttonBox);

        imageBox = new ImageBox(0, 0, 50, 50, FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/Universal/Setting.png"));
        textBox = new TextBox(0, 0, 250, 50, "设置");
        textBox.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 50)
        );
        buttonBox = new ImageTextButtonBox(0, 0, 250, 50, imageBox, textBox);
        buttonBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout1.addChild(buttonBox);

        imageBox = new ImageBox(0, 0, 50, 50, FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/Universal/Learn.png"));
        textBox = new TextBox(0, 0, 250, 50, "学习如何使用");
        textBox.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 50)
        );
        buttonBox = new ImageTextButtonBox(0, 0, 250, 50, imageBox, textBox);
        buttonBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        verticalLayout1.addChild(buttonBox);

        imageBox = new ImageBox(0, 0, 50, 50, FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/Universal/Quit.png"));
        textBox = new TextBox(0, 0, 250, 50, "退出");
        textBox.getTextView().setFont(
                Font.font(ColorSetting.PREFERRED_FONT_FAMILY, 50)
        );
        buttonBox = new ImageTextButtonBox(0, 0, 250, 50, imageBox, textBox);
        buttonBox.setFillExtensionType(FillExtensionType.HORIZONTAL);
        buttonBox.setMouseClickEvent(event -> Platform.exit());
        verticalLayout1.addChild(buttonBox);

        DirectionalLayout directionalLayout1 = new DirectionalLayout(0, 0, 250, 100, verticalLayout1);
        directionalLayout1.setFillExtensionType(FillExtensionType.CENTER);
        verticalLayout.addChild(directionalLayout1);
        verticalLayout.addChild(new VerticalStick(50));

        DirectionalLayout baseDirectionalLayout = new DirectionalLayout(0, 0, 100, 100, verticalLayout);
        baseDirectionalLayout.setDirection(Direction.CENTER);
        baseDirectionalLayout.setFillExtensionType(FillExtensionType.CENTER);
        this.addChild(baseDirectionalLayout);
    }
}
