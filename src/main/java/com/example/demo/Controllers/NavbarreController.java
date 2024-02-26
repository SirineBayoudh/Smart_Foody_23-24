package com.example.demo.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarreController implements Initializable {

    @FXML
    private BorderPane centerPane;
    @FXML
    private Button BtnConseil;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation initiale, si nécessaire
    }

    @FXML
    private void handleClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Label) {
            Label label = (Label) source;
            switch (label.getId()) {
                case "labelAccueil":
                    // Chargez la vue d'accueil, si vous en avez une
                    break;
                case "labelProduits":
                    loadPage("/com/example/pitest/fxml/panier.fxml");
                    break;
                // Ajoutez d'autres cas pour les autres labels
            }
        } else if (source instanceof ImageView) {
            ImageView imageView = (ImageView) source;
            // Gérez les clics sur les icônes ici, similaire aux labels
        }
    }

    @FXML
    private void loadProduit() {
    }

    @FXML
    private void loadPanier() {
        clear();
        loadPage("/com/example/demo/panier.fxml");
    }

    @FXML
    void loadConseil() {
        BtnConseil.setStyle("-fx-background-color: #56AB2F");
        System.out.println("changed");
        loadPage("/com/example/demo/ajoutConseil.fxml");
    }

    public void clear(){
        BtnConseil.setStyle("-fx-background-color: #ffffff");
    }


    private void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page));
            if (centerPane != null) {
                centerPane.setCenter(root);
            } else {
                System.out.println("centerPane is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
