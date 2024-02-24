module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.json;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.Controllers;
    opens com.example.demo.Controllers to javafx.fxml;
    exports com.example.demo.Tools;
    opens com.example.demo.Tools to javafx.fxml;
    opens com.example.demo.Models to javafx.base;
    requires jdk.jpackage;
    requires java.desktop;
    requires itextpdf; // Ajoutez cette ligne pour accéder à jdk.jpackage

}