module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires quartz;
    requires twilio;
    requires itextpdf;
    requires java.desktop;
    requires xmlworker;
    requires poi;
    requires poi.ooxml;
    requires restfb;
    requires com.google.gson;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.Models to javafx.base;
    exports com.example.demo;
    exports com.example.demo.Controllers;
    opens com.example.demo.Controllers to javafx.fxml;
    exports com.example.demo.Tools;
    opens com.example.demo.Tools to javafx.fxml;
}