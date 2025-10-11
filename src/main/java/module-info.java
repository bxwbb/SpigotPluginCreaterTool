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
    requires com.almasb.fxgl.all;
    requires com.github.javaparser.core;
    requires com.github.javaparser.symbolsolver.core;
    requires annotations;
    requires com.alibaba.fastjson2;
    requires java.logging;
    requires org.slf4j;
    requires hessian;
    requires java.desktop;

    opens org.bxwbb.spigotplugincreatertool to javafx.fxml;
    opens org.bxwbb.spigotplugincreatertool.windowLabel;
    opens org.bxwbb.spigotplugincreatertool.MinWindowS.NodeEditor;
    exports org.bxwbb.spigotplugincreatertool;
}