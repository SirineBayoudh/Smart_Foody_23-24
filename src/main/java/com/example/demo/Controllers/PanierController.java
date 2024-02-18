package com.example.demo.Controllers;

import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    // Déclaration des éléments de l'interface
    public Button btnViderPanier;
    @FXML
    private VBox productsContainer;

    // Connexion à la base de données
    Connection cnx;

    // Constructeur
    public PanierController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Méthode d'initialisation
    public void initialize() {
        afficherProduits();
        int nombreProduits = obtenirNombreProduitsDansLePanier();
        btnViderPanier.setDisable(nombreProduits == 0);
    }

    // CRUD (Create, Read, Update, Delete) Operations

    //////////**************************** Affiche les produits dans le panier en rejoignant les
    // tables `produit` et `panier` par `ref_produit`.***********************************************
    public List<Produit> affichageProduitsDansLePanier() {
        List<Produit> produits = new ArrayList<>();
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

    /////////////////////////////////////////////// Supprime un produit du panier///////////////////////////////////////////////
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

    ///////////////////////////////// Affiche les produits dans le panier///////////////////////////////////////////////////////////
    public void afficherProduits() {
        List<Produit> produits = affichageProduitsDansLePanier();
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

    ///////////////////////////////////////// Vide le panier/////////////////////////////////////////////////////
    public void viderPanier() {
        int nombreProduits = obtenirNombreProduitsDansLePanier();

        // Si le panier n'est pas vide, afficher la boîte de dialogue de confirmation
        if (nombreProduits > 0) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Confirmation de vidage du panier");
            confirmationDialog.setContentText("Êtes-vous sûr de vouloir vider votre panier ?");

            confirmationDialog.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
            confirmationDialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/demo/css/style_panier.css").toExternalForm());
            confirmationDialog.getDialogPane().getStyleClass().add("custom-alert");
            confirmationDialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("ok-button");
            confirmationDialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("cancel-button");

            confirmationDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String requete = "DELETE FROM panier";
                    try (Statement statement = cnx.createStatement()) {
                        int rowCount = statement.executeUpdate(requete);
                        if (rowCount > 0) {
                            System.out.println("Le panier a été vidé avec succès.");
                            afficherProduits();
                        } else {
                            System.out.println("Le panier est déjà vide.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la suppression des éléments du panier : " + e.getMessage());
                    }
                } else {
                    System.out.println("Opération annulée.");
                }
            });
        }
    }



    /////////////////////// Méthode pour obtenir le nombre de produits dans le panier////////////////////////////
    private int obtenirNombreProduitsDansLePanier() {
        List<Produit> produits = affichageProduitsDansLePanier();
        return produits.size();
    }

    // Affiche les produits disponibles pour être ajoutés au panier juste pour le test dans l'intégration elle sera supprimé
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

    // Ajoute un produit au panier///////////////////////////////////////////////
    public void ajouterProduitAuPanier(String refProduit) {
        String requete = "INSERT INTO panier (ref_produit, remise, totale) VALUES (?, 0, 0)";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, refProduit);
            pst.executeUpdate();
            System.out.println("Produit ajouté au panier avec succès");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    // Méthodes de navigation//////////////////////////////////////////////////////////

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

    public void navbarre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) productsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
