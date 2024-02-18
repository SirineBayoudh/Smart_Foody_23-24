package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarreCotroller implements Initializable {

    @FXML
    private BorderPane centerPane;

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
                    loadPage("/com/example/pitest/fxml/produit.fxml");
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
        loadPage("/com/example/demo/produit.fxml");
    }

    @FXML
    private void loadPanier() {
        loadPage("/com/example/demo/panier.fxml");
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
