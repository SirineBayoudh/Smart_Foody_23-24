package com.example.demo.Controllers;


import com.example.demo.Models.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProduitController {

    @FXML
    private VBox productsContainer;

    @FXML
    private VBox productsContainer1;

    private com.example.demo.Controllers.PanierController panierController = new com.example.demo.Controllers.PanierController();

    @FXML
    public void initialize() {
        afficherProduits();
    }

    private void afficherProduits() {
        List<Produit> produits = panierController.afficherProduitDansPanier();
        productsContainer1.getChildren().clear();

        for (Produit produit : produits) {


            HBox productBox = new HBox(50);
            productBox.getStyleClass().add("product-box");
            productsContainer1.setStyle("-fx-padding:15px");
            ImageView productImage = new ImageView(new Image(produit.getImage()));
            productImage.setFitHeight(50);
            productImage.setFitWidth(50);

            Rectangle clip = new Rectangle(50, 50); // Set the dimensions as needed
            clip.setArcWidth(60); // Adjust the corner radius
            clip.setArcHeight(60);
            productImage.setClip(clip);
            productImage.setEffect(new DropShadow(10, Color.BLACK)); // Adjust the shadow parameters


            Label labelProduit = new Label(produit.getMarque() + " - " + produit.getRef() + ": " + produit.getPrix() + "TND");
            labelProduit.setAlignment(Pos.CENTER); // Center-align the label


            Button btnAjouter = new Button("Ajouter au panier");

            btnAjouter.setOnAction(event -> {
                panierController.ajouterProduitAuLigneCommande(produit.getRef());
                // Ajouter ici tout code de traitement additionnel après l'ajout au panier
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);


            HBox.setHgrow(productImage, Priority.NEVER);

            HBox.setHgrow(btnAjouter, Priority.NEVER);

            productBox.getChildren().addAll(productImage, labelProduit,spacer, btnAjouter);

            // Set the HBox's preferred width to Double.MAX_VALUE
            productBox.setPrefWidth(Double.MAX_VALUE);
            // Set the HBox's alignment to Pos.CENTER_LEFT
            productBox.setAlignment(Pos.CENTER_LEFT);
            productsContainer1.getChildren().add(productBox);


        }

    }
    // Méthode pour afficher l'interface du panier
    @FXML
    public void navbarre() {
        try {
            // Charger le fichier FXML de l'interface panier.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec l'interface chargée
            Scene scene = new Scene(root);

            // Obtenir la fenêtre principale
            Stage stage = (Stage) productsContainer1.getScene().getWindow();

            // Définir la nouvelle scène sur la fenêtre principale
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
