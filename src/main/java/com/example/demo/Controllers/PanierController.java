package com.example.demo.Controllers;


import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierController {

    @FXML
    private VBox productsContainer;
    Connection cnx;


    public PanierController() {
        cnx = MyConnection.getInstance().getCnx();

    }
    public void initialize() {
        afficherProduits();
    }


    // Affiche les produits dans le panier en rejoignant les tables `produit` et `panier` par `ref_produit`.

    public List<Produit> affichageProduitsDansLePanier() {
        List<Produit> produits = new ArrayList<>();
        // Requête SQL modifiée pour rejoindre les tables `produit` et `panier` par `ref_produit`
        String requete = "SELECT p.* FROM produit p JOIN panier pa ON p.ref = pa.ref_produit";
        try (Statement stm = cnx.createStatement()) {
            try (ResultSet rs = stm.executeQuery(requete)) {
                while (rs.next()) {
                    produits.add(new Produit(
                            rs.getString("ref"),
                            rs.getString("marque"),
                            rs.getString("categorie"),
                            rs.getFloat("prix"),
                            rs.getString("image"),
                            rs.getString("objectif"),
                            rs.getString("critere")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }
        return produits;
    }

    public void supprimerProduitDuPanier(String refProduit) {
        String requete = "DELETE FROM panier WHERE ref_produit = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, refProduit);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Produit supprimé du panier");
            } else {
                System.out.println("Aucun produit avec la référence spécifiée trouvé dans le panier");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit du panier : " + e.getMessage());
        }
    }
    public void afficherProduits() {
        List<Produit> produits =affichageProduitsDansLePanier();
        productsContainer.getChildren().clear();

        for (Produit produit : produits) {
            HBox productBox = new HBox(10);
            Label labelProduit = new Label(produit.getMarque() + " - " + produit.getRef() + ": " + produit.getPrix() + "€");
            Spinner<Integer> quantiteSpinner = new Spinner<>(1, 100, 1);
            quantiteSpinner.setEditable(true);

            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/demo/Images/delete.png")));
            deleteIcon.setFitHeight(20);
            deleteIcon.setFitWidth(20);
            Button btnSupprimer = new Button("", deleteIcon);
            btnSupprimer.setOnAction(event -> {
                supprimerProduitDuPanier(produit.getRef());
                afficherProduits();
            });

            productBox.getChildren().addAll(labelProduit, quantiteSpinner, btnSupprimer);
            productsContainer.getChildren().add(productBox);
        }

    }





    ////////par le bouton ajouter au panier
    public List<Produit> afficherProduitDansPanier() {
        List<Produit> produits = new ArrayList<>();
        String requete = "SELECT * FROM produit";
        try (Statement stm = cnx.createStatement()) {
            try (ResultSet rs = stm.executeQuery(requete)) {
                while (rs.next()) {
                    produits.add(new Produit(
                            rs.getString("ref"),
                            rs.getString("marque"),
                            rs.getString("categorie"),
                            rs.getFloat("prix"),
                            rs.getString("image"),
                            rs.getString("objectif"),
                            rs.getString("critere")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des produits : " + e.getMessage());
        }
        return produits;
    }
    public void ajouterProduitAuPanier(String refProduit) {
        // La requête SQL pour insérer un produit dans le panier avec 'ref_produit'
        // et en définissant les valeurs par défaut pour 'remise' et 'total'
        String requete = "INSERT INTO panier (ref_produit, remise, totale) VALUES (?, 0, 0)";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, refProduit); // Assigner la référence du produit à la première variable de la requête
            pst.executeUpdate(); // Exécuter la requête d'insertion
            System.out.println("Produit ajouté au panier avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    @FXML
    private void chargerInterfaceCommande(ActionEvent event) {
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/pitest/fxml/commande.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void afficherProduit() {
//        try {
//            // Charger le fichier FXML de l'interface panier.fxml
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Produit.fxml"));
//            Parent root = loader.load();
//
//            // Créer une nouvelle scène avec l'interface chargée
//            Scene scene = new Scene(root);
//
//            // Obtenir la fenêtre principale
//            Stage stage = (Stage) productsContainer.getScene().getWindow();
//
//            // Définir la nouvelle scène sur la fenêtre principale
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
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
