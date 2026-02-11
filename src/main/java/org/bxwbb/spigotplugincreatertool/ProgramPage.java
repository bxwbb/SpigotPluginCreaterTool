package org.bxwbb.spigotplugincreatertool;

import org.bxwbb.spigotplugincreatertool.BxWinLab.*;
import org.bxwbb.spigotplugincreatertool.BxWinLab.Base.FillExtensionType;
import org.bxwbb.spigotplugincreatertool.Utils.FileUtil;

public class ProgramPage {

    private ProgramPage() {
    }

    public static Stage getProgramTopStage() {
        Stage programTopStage = new Stage(0, 0, 1200, 30);
        programTopStage.getBaseRectangle().setArcWidth(0.0);
        programTopStage.getBaseRectangle().setArcHeight(0.0);
        programTopStage.getBaseRectangle().setSmooth(false);
        programTopStage.setFillExtensionType(FillExtensionType.HORIZONTAL);

        ImageBox programIcon = new ImageBox(
                0,
                0,
                30,
                30,
                FileUtil.loadResourceFile("/org/bxwbb/spigotplugincreatertool/icon/icon.png")
        );
        programIcon.setFillExtensionType(FillExtensionType.VERTICAL);
        programTopStage.addChild(programIcon);

        return programTopStage;
    }

}
