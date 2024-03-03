module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    //requires javax.mail.api;
   // requires stripe.java;
    requires javafx.web;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires jxmapviewer2;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires io;
    requires activation;
    requires com.google.gson;
    requires stripe.java;
    requires java.mail;
    // requires com.google.gson;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.Controllers;
    opens com.example.demo.Controllers to javafx.fxml;
    exports com.example.demo.Tools;
    opens com.example.demo.Tools to javafx.fxml;

    opens com.example.demo.Models to javafx.base;
    // autres directives du module


}