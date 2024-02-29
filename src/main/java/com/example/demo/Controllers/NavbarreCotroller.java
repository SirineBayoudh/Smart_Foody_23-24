package com.example.demo.Controllers;

import com.microsoft.schemas.vml.CTGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarreCotroller implements Initializable {

    public Button BtnConseil;
    public ImageView panier;
    public Button produit;
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
        produit.setStyle("-fx-background-color: #56AB2F");
      

        loadPage("/com/example/demo/produit.fxml");
        clear();
    }

    @FXML
    private void loadPanier() {
        panier.getStyleClass().add("highlighted-basket");

        loadPage("/com/example/demo/panier.fxml");
        clear();
    }
    public void clear(){
        panier.setStyle("-fx-background-color:#ffffff");
        produit.setStyle("-fx-background-color:#ffffff");

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
