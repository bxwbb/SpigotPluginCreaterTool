module org.bxwbb.spigotplugincreatertool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.alibaba.fastjson2;
    requires java.logging;
    requires org.slf4j;
    requires java.desktop;

    opens org.bxwbb.spigotplugincreatertool to javafx.fxml;
    opens org.bxwbb.spigotplugincreatertool.WindowLabel;
    opens org.bxwbb.spigotplugincreatertool.WindowLabel.Base;
    exports org.bxwbb.spigotplugincreatertool;
    exports org.bxwbb.spigotplugincreatertool.WindowLabel;
    exports org.bxwbb.spigotplugincreatertool.WindowLabel.Base;
    exports org.bxwbb.spigotplugincreatertool.WindowLabel.Test;
    opens org.bxwbb.spigotplugincreatertool.WindowLabel.Test to javafx.fxml;
}