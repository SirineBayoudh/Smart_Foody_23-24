package com.example.demo.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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
        // Récupérer l'adresse à partir de la div
        WebEngine webEngine = webView.getEngine();
        String address = (String) webEngine.executeScript("document.getElementById('address').innerText");

        // Créer une boîte de dialogue de confirmation
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation de localisation");
        confirmationDialog.setHeaderText("Êtes-vous sûr de la localisation suivante ?");
        confirmationDialog.setContentText(address);
        confirmationDialog.getDialogPane().getStylesheets().add(CommandeClientController.class.getResource("/com/example/demo/css/style_panier.css").toExternalForm());
        confirmationDialog.getDialogPane().getStyleClass().add("custom-alert");

        // Ajouter les boutons "Oui" et "Non"
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                // Si l'utilisateur clique sur "Oui", exécuter le code pour sauvegarder les coordonnées et l'adresse
                String script = "document.getElementById('latitude').innerText + ',' + document.getElementById('longitude').innerText";
                String latLong = (String) webEngine.executeScript(script);

                if (latLong != null && !latLong.isEmpty()) {
                    String[] parts = latLong.split(",");
                    if (parts.length == 2) {
                        String latitude = parts[0].trim();
                        String longitude = parts[1].trim();

                        // Passer les coordonnées et l'adresse à la méthode savecoords
                        CommandeClientController commandeClientController = new CommandeClientController();
                        commandeClientController.savecoords(latitude, longitude, address);

                        // Now you can navigate back to commande_client.fxml
                        try {
                            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/demo/commande_client.fxml")));
                            Scene scene = new Scene(root);
                            Stage currentStage = (Stage) webView.getScene().getWindow();
                            currentStage.setScene(scene);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Error parsing latitude and longitude");
                    }
                } else {
                    System.err.println("Latitude and longitude not found in HTML");
                }
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("TEST Initialize");
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/googlemaps.html").toExternalForm());
    }

    public void switchButton(ActionEvent actionEvent) {
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/demo/navbarre.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
