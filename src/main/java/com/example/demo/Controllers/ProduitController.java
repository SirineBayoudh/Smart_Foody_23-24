package com.example.demo.Controllers;


import com.example.demo.Models.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProduitController {

    @FXML
    private VBox productsContainer;

    private com.example.demo.Controllers.PanierController panierController = new com.example.demo.Controllers.PanierController();

    @FXML
    public void initialize() {
        afficherProduits();
    }

    private void afficherProduits() {
        List<Produit> produits = panierController.afficherProduitDansPanier();
        productsContainer.getChildren().clear();

        for (Produit produit : produits) {
            HBox productBox = new HBox(10);
            Label labelProduit = new Label(produit.getMarque() + " - " + produit.getRef() + ": " + produit.getPrix() + "€");
            Button btnAjouter = new Button("Ajouter au panier");

            btnAjouter.setOnAction(event -> {
                panierController.ajouterProduitAuLigneCommande(produit.getRef());
                // Ajouter ici tout code de traitement additionnel après l'ajout au panier
            });

            productBox.getChildren().addAll(labelProduit, btnAjouter);
            productsContainer.getChildren().add(productBox);
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
            Stage stage = (Stage) productsContainer.getScene().getWindow();

            // Définir la nouvelle scène sur la fenêtre principale
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
