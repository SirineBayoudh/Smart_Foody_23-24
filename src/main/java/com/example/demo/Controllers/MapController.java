package com.example.demo.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private WebView webView;

    public void showWindow() {
        System.out.println("OPENING MAPS");

        System.out.println("TEST SHOW WINDOW");

    }
    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    String latitude;
    String longitude;
    private void closeWindow() {
        // Get the Stage and close it
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void confirm() throws IOException {

        WebEngine webEngine = webView.getEngine();

        String script = "document.getElementById('latitude').innerText + ',' + document.getElementById('longitude').innerText";

        String latLong = (String) webEngine.executeScript(script);

        if (latLong != null && !latLong.isEmpty()) {
            String[] parts = latLong.split(",");
            if (parts.length == 2) {
                String latitude = parts[0].trim();
                String longitude = parts[1].trim();
                CommandeClientController commandeClientController=new CommandeClientController();
                commandeClientController.savecoords(latitude,longitude);
                // Now you can navigate back to commande_client.fxml
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/demo/commande_client.fxml")));
                Scene scene = new Scene(root);
                Stage currentStage = (Stage) webView.getScene().getWindow();
                currentStage.setScene(scene);
            } else {
                System.err.println("Error parsing latitude and longitude");
            }
        } else {
            System.err.println("Latitude and longitude not found in HTML");
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("TEST Initialize");
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/googlemaps.html").toExternalForm());
    }

    public void switchButton(ActionEvent actionEvent) {
    }
}
