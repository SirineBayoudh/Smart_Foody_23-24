package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final String CURRENCY = "DT";

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PageAccueil.fxml"));
        Scene scene = new Scene(root);
        stage.setHeight(700);
        stage.setWidth(1200);
        stage.setTitle("Smart foody");

        stage.setScene(scene);
        stage.show();
    }
}