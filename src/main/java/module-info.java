module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.mail;
    requires tess4j;
    requires itextpdf;
    requires java.desktop;
    requires spring.web;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.Controllers;
    opens com.example.demo.Controllers to javafx.fxml;
    exports com.example.demo.Tools;
    opens com.example.demo.Tools to javafx.fxml;
    opens com.example.demo.Models to javafx.base;
}