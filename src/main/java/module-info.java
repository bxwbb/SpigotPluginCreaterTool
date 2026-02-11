module org.bxwbb.spigotplugincreatertool {
    // 导出你的Application所在的包给javafx.graphics模块
    exports org.bxwbb.spigotplugincreatertool to javafx.graphics;

    // 声明依赖JavaFX的核心模块
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;

    // 如果需要打开包给反射访问（比如用FXML时），添加这行（可选）
    opens org.bxwbb.spigotplugincreatertool to javafx.fxml;
    exports org.bxwbb.spigotplugincreatertool.BxWinLab;
    exports org.bxwbb.spigotplugincreatertool.BxWinLab.Base;
    exports org.bxwbb.spigotplugincreatertool.BxWinLab.Enum;
    exports org.bxwbb.spigotplugincreatertool.BxWinLab.Util;
}
