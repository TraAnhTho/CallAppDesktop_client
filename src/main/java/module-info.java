module com.example.dacs4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.web;

    // Optional libs
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // Cho phép FXML truy cập controller
    opens com.example.dacs4 to javafx.fxml;
    opens com.example.dacs4.controllers to javafx.fxml;

    exports com.example.dacs4;
    exports com.example.dacs4.controllers;
}
