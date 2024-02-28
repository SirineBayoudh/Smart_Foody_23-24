package com.example.demo.Controllers;

import com.example.demo.Models.LigneCommande;
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
    @FXML
    private Button btnValiderCommande;

    @FXML
    private Button btnViderPanier;

    @FXML
    private VBox productsContainer;

    @FXML
    private Label remiseid;

    @FXML
    private Label soustotaleid;

    @FXML
    private Label totaleid;

    double[] sousTotale = {0};
    double[] remise = {0};
    double[] totale = {0};
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
        btnValiderCommande.setDisable(nombreProduits == 0);
    }

    // CRUD (Create, Read, Update, Delete) Operations

    //////////**************************** Affiche les produits dans le panier en rejoignant les
    // tables `produit` et `panier` par `ref_produit`.***********************************************
    public List<LigneCommande> affichageProduitsDansLePanier() {
        // Initialisation de la liste pour stocker les produits dans le panier
        List<LigneCommande> produits = new ArrayList<>();

        // Requête SQL pour sélectionner les lignes de commande associées aux produits dans le panier
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc  ON p.ref = lc.ref_produit " +
                "JOIN panier pa ON pa.id_panier = lc.id_panier";

        try (Statement stm = cnx.createStatement()) { // Création de l'objet Statement pour exécuter la requête
            try (ResultSet rs = stm.executeQuery(requete)) { // Exécution de la requête et récupération des résultats dans un ResultSet
                while (rs.next()) { // Itération sur chaque ligne du ResultSet
                    // Création d'un nouvel objet LigneCommande avec les données récupérées et ajout à la liste de produits
                    produits.add(new LigneCommande(
                            rs.getInt("id_lc"), // Récupération de l'identifiant de la ligne de commande
                            rs.getInt("id_panier"), // Récupération de l'identifiant du panier
                            rs.getInt("quantite"), // Récupération de la quantité
                            rs.getString("ref_produit"), // Récupération de la référence du produit
                            rs.getInt("id_commande")));
                }
            }
        } catch (SQLException e) { // Gestion des exceptions SQL
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }

        // Retourne la liste des produits dans le panier
        return produits;
    }


    /////////////////////////////////////////////// Supprime un produit du panier///////////////////////////////////////////////
    public void supprimerProduitDuPanier(Integer id_lc) {
        // Requête SQL pour supprimer une ligne de commande du panier en fonction de son identifiant
        String requete = "DELETE FROM ligne_commande WHERE id_lc = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) { // Création de l'objet PreparedStatement pour exécuter la requête SQL
            pst.setInt(1, id_lc); // Définition du premier paramètre de la requête (identifiant de la ligne de commande)
            int affectedRows = pst.executeUpdate(); // Exécution de la requête et récupération du nombre de lignes affectées
            if (affectedRows > 0) { // Vérifie si des lignes ont été supprimées avec succès
                System.out.println("Ligne de commande supprimée du panier"); // Affiche un message de confirmation
            } else {
                System.out.println("Aucun produit avec la référence spécifiée trouvé dans le panier"); // Affiche un message si aucune ligne n'a été supprimée
            }
        } catch (SQLException e) { // Gestion des exceptions SQL
            System.out.println("Erreur lors de la suppression du produit du panier : " + e.getMessage()); // Affiche un message d'erreur en cas d'échec de la suppression
        }
    }


    ///////////////////////////////// Affiche les produits dans le panier///////////////////////////////////////////////////////////
    public void afficherProduits() {
        // Récupérer la liste des lignes de commande associées aux produits dans le panier
        List<LigneCommande> ligneCommandes = affichageProduitsDansLePanier();

        // Nettoyer le conteneur des produits avant d'afficher les nouveaux produits
        productsContainer.getChildren().clear();

        // Initialiser les tableaux avec des zéros pour les totaux
        sousTotale[0] = 0;
        remise[0] = 0;
        totale[0] = 0;

        // Calculer les totaux
        recalculate(sousTotale, remise, totale);

        // Parcourir chaque ligne de commande dans la liste
        for (LigneCommande prod : ligneCommandes) {
            // Requête SQL pour récupérer les détails du produit
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        // Création d'un objet Produit avec les détails récupérés de la base de données
                        Produit p = new Produit(
                                rs.getString("ref"),
                                rs.getString("marque"),
                                rs.getString("categorie"),
                                rs.getFloat("prix"),
                                rs.getString("image"),
                                rs.getString("objectif"),
                                rs.getString("critere")
                        );

                        // Création d'une boîte HBox pour afficher les détails du produit
                        HBox productBox = new HBox(10);
                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef() + ": " + p.getPrix() + "€");
                        Spinner<Integer> quantiteSpinner = new Spinner<>(1, 100, prod.getQuantite(), 1);
                        quantiteSpinner.setEditable(true);

                        // Mettre à jour le sous-total en fonction de la quantité sélectionnée
                        sousTotale[0] += p.getPrix() * quantiteSpinner.getValue();
                        // Recalculer la remise et le total
                        recalculate(sousTotale, remise, totale);

                        // Création d'une icône de suppression pour supprimer le produit du panier
                        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/demo/Images/delete.png")));
                        deleteIcon.setFitHeight(20);
                        deleteIcon.setFitWidth(20);
                        Button btnSupprimer = new Button("", deleteIcon);
                        btnSupprimer.setOnAction(event -> {
                            // Appel de la méthode pour supprimer le produit du panier et rafraîchir l'affichage des produits
                            supprimerProduitDuPanier(prod.getIdLc());
                            afficherProduits();
                        });

                        // Mettre à jour le sous-total lorsqu'il y a un changement dans la quantité
                        quantiteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                            sousTotale[0] += (newValue - oldValue) * p.getPrix();
                            // Recalculer la remise et le total
                            recalculate(sousTotale, remise, totale);
                        });

                        // Ajouter les éléments à la boîte de produit
                        productBox.getChildren().addAll(labelProduit, quantiteSpinner, btnSupprimer);
                        // Ajouter la boîte de produit au conteneur de produits
                        productsContainer.getChildren().add(productBox);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }
        }
    }


////////////////////////////////////////////recalculer les prix////////////////////// ///////////////////////
private void recalculate(double[]  sousTotale, double[] remise, double[] totale) {
    // Vérifie le nombre de commandes pour l'article avec l'identifiant 14
    if (nbrCommande(14) > 3) { // Si le nombre de commandes est supérieur à 3
        remise[0] = 0.15; // Appliquer une remise de 15%
        totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]); // Calculer le total avec la remise appliquée
    } else if (nbrCommande(14) > 5) { // Sinon, si le nombre de commandes est supérieur à 9
        remise[0] = 0.25; // Appliquer une remise de 25%
        totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]); // Calculer le total avec la remise appliquée
    } else if (nbrCommande(14) > 10) { // Sinon, si le nombre de commandes est supérieur à 9
        remise[0] = 0.50; // Appliquer une remise de 25%
        totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]); // Calculer le total avec la remise appliquée
    }else { // Sinon
        totale[0] = sousTotale[0]; // Le total reste inchangé sans remise
    }

    // Afficher le libellé de la remise
    if (remise[0] == 0) { // Si aucune remise n'est appliquée
        remiseid.setText("Aucune remise"); // Afficher "Aucune remise"
    } else { // Sinon
        remiseid.setText(String.format("%.0f%% de remise", remise[0] * 100)); // Afficher le pourcentage de remise
    }

    // Afficher le sous-total et le total avec ou sans remise
    soustotaleid.setText(String.format("Sous-Totale : %.2f TND", sousTotale[0]));
    totaleid.setText(String.format("Totale : %.2f TND", totale[0]));
}


    private Integer nbrCommande (int clientId){
        String query = "SELECT COUNT(*) AS commande FROM commande WHERE id_client = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, clientId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int nbrCommande = rs.getInt("commande");
                    System.out.println("Nombre de commandes pour le client " + clientId + " : " + nbrCommande);
                    return nbrCommande;
                } else {
                    System.out.println("Aucune commande trouvée pour le client " + clientId);
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nombre de commandes pour le client " + clientId + " : " + e.getMessage());
            return -1; // or throw an exception
        }

    }
    @FXML

    ///////////////////////////////////////// Vide le panier/////////////////////////////////////////////////////
    private void appelerViderPanier(ActionEvent event) {
        viderPanier(false); // Appel de la méthode viderPanier avec false pour indiquer que la commande n'est pas validée
    }

    public void viderPanier(boolean commandeValidee) {
        int nombreProduits = obtenirNombreProduitsDansLePanier();

        // Si le panier n'est pas vide
        if (nombreProduits > 0) {
            // Si la commande n'est pas validée, afficher la boîte de dialogue de confirmation
            if (!commandeValidee) {
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
                        supprimerPanier();
                    } else {
                        System.out.println("Opération annulée.");
                    }
                });
            } else {
                // Si la commande est validée, supprimer le panier directement
                supprimerPanier();
            }
        } else {
            // Si le panier est vide, afficher un message ou désactiver le bouton ici
            // Par exemple, désactiver le bouton :
            // boutonViderPanier.setDisable(true);
        }
    }

    private void supprimerPanier() {
        String requete = "DELETE FROM panier";
        try (Statement statement = cnx.createStatement()) {
            int rowCount = statement.executeUpdate(requete);
            statement.executeUpdate(requete);
            if (rowCount > 0) {
                System.out.println("Le panier a été vidé avec succès.");
                afficherProduits();
            } else {
                System.out.println("Le panier est déjà vide.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des éléments du panier : " + e.getMessage());
        }
    }





    /////////////////////// Méthode pour obtenir le nombre de produits dans le panier////////////////////////////
    private int obtenirNombreProduitsDansLePanier() {
        List<LigneCommande> produits = affichageProduitsDansLePanier();
        return produits.size();
    }




    // Ajoute  Commande ///////////////////////////////////////////////





















    public void ajouterProduitAuLigneCommande(String refProduit) {
        // Appeler la méthode pour ajouter un produit au panier, en supposant que 14 est l'identifiant du client
        int idPanier = ajouterProduitAuPanier(14);

        // Vérifier si l'ajout du produit au panier a réussi
        if(idPanier != -1) { // Si l'identifiant du panier est différent de -1 (ce qui signifie que l'ajout au panier a réussi)
            // Requête SQL pour vérifier si le produit existe déjà dans la table ligne_commande
            String checkIfExistsQuery = "SELECT COUNT(*) FROM ligne_commande WHERE ref_produit = ?";
            try (PreparedStatement pstCheck = cnx.prepareStatement(checkIfExistsQuery)) {
                pstCheck.setString(1, refProduit);
                ResultSet rs = pstCheck.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                if (count > 0) { // Si le produit existe déjà dans la table ligne_commande
                    // Requête SQL pour mettre à jour la quantité du produit en incrémentant de 1
                    String updateQuery = "UPDATE ligne_commande SET quantite = quantite + 1 WHERE ref_produit = ? AND id_panier = ?";
                    try (PreparedStatement pstUpdate = cnx.prepareStatement(updateQuery)) {
                        pstUpdate.setString(1, refProduit);
                        pstUpdate.setInt(2, idPanier);
                        pstUpdate.executeUpdate();
                        System.out.println("Produit mis à jour dans le panier avec succès");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la mise à jour du produit dans le panier : " + e.getMessage());
                    }
                } else { // Si le produit n'existe pas dans la table ligne_commande
                    // Requête SQL pour insérer une nouvelle ligne_commande avec les valeurs par défaut
                    String insertQuery = "INSERT INTO ligne_commande (id_panier, quantite, ref_produit) VALUES (?, 1, ?)";
                    try (PreparedStatement pstInsert = cnx.prepareStatement(insertQuery)) {
                        pstInsert.setInt(1, idPanier);
                        pstInsert.setString(2, refProduit);
                        pstInsert.executeUpdate();
                        System.out.println("Produit ajouté au panier avec succès");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la vérification de l'existence du produit dans le panier : " + e.getMessage());
            }
        }
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
///ajouter le produit avec l id de client dans le panier
    public int ajouterProduitAuPanier(int id_client) {
        String checkIfExistsQuery = "SELECT id_panier FROM panier WHERE id_client = ?";
        try (PreparedStatement pstCheck = cnx.prepareStatement(checkIfExistsQuery)) {
            pstCheck.setInt(1, id_client);
            ResultSet rs = pstCheck.executeQuery();
            if (rs.next()) {
                int idPanier = rs.getInt("id_panier");
                System.out.println("Panier existant trouvé. ID du panier : " + idPanier);
                return idPanier;
            } else {
                String insertQuery = "INSERT INTO panier (id_client, remise, totale) VALUES (?, 0, 0)";
                try (PreparedStatement pstInsert = cnx.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstInsert.setInt(1, id_client);
                    pstInsert.executeUpdate();
                    ResultSet rsInsert = pstInsert.getGeneratedKeys();
                    if (rsInsert.next()) {
                        int idPanier = rsInsert.getInt(1);
                        System.out.println("Nouveau panier ajouté avec succès. ID du panier : " + idPanier);
                        return idPanier;
                    } else {
                        System.out.println("Erreur lors de la récupération de l'ID du panier");
                        return -1; // or throw an exception
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur lors de l'ajout du Ligne de commande au panier : " + e.getMessage());
                    return -1; // or throw an exception
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'existence du panier : " + e.getMessage());
            return -1; // or throw an exception
        }
    }

    // Méthodes de navigation//////////////////////////////////////////////////////////

    @FXML
    private void chargerInterfaceCommande(ActionEvent event) {
        ajouterCommande();
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/demo/Commande_client.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ajouterCommande() {
        String insertCommandeQuery = "INSERT INTO commande (date_commande, id_client, totalecommande, remise, etat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(insertCommandeQuery)) {
            pst.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            pst.setInt(2, 14);
            pst.setFloat(3, (float) totale[0]);
            pst.setFloat(4, (float) remise[0]);
            pst.setString(5, "en cours"); // Fournir une valeur pour le champ 'etat'
            pst.executeUpdate();

            // Afficher une notification de commande réussie avec le style personnalisé
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Commande passée");
            successAlert.setHeaderText(null);
            successAlert.setContentText("La commande a été ajoutée avec succès.");

            // Appliquer la classe CSS personnalisée à la boîte de dialogue
            successAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/demo/css/style_panier.css").toExternalForm());
            successAlert.getDialogPane().getStyleClass().add("custom-alert");

            successAlert.showAndWait();

            System.out.println("Commande ajoutée avec succès");
           // viderPanier();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }
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
                    String requete1 = "DELETE FROM ligne_commande";
                    try (Statement statement = cnx.createStatement()) {
                        int rowCount = statement.executeUpdate(requete);
                        statement.executeUpdate(requete1);
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
