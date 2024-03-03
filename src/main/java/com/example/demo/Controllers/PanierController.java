package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Models.CommandeHolder;
import com.example.demo.Models.LigneCommande;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.sql.*;
import java.time.LocalDate;
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
    private VBox vboxpanier;
    @FXML
    private Label remiseid;

    @FXML
    private Label soustotaleid;

    @FXML
    private Label totaleid;
    @FXML
    private HBox  soldeBand;
    double[] sousTotale = {0};
    double[] remise = {0};
    double[] totale = {0};
    // Connexion à la base de données
    Connection cnx;

    private CommandeHolder holder = CommandeHolder.getInstance();

    // Constructeur
    public PanierController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // Méthode d'initialisation
    public void initialize() {
        int nombreProduits = obtenirNombreProduitsDansLePanier();
        btnViderPanier.setDisable(nombreProduits == 0);
        btnValiderCommande.setDisable(nombreProduits == 0);
        afficherProduits();

    }

    // CRUD (Create, Read, Update, Delete) Operations

    //////////**************************** Affiche les produits dans le panier en rejoignant les
    // tables `produit` et `panier` par `ref_produit`.***********************************************
    public List<LigneCommande> affichageProduitsDansLePanier() {
        List<LigneCommande> produits = new ArrayList<>();
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc  ON p.ref = lc.ref_produit " +
                "JOIN panier pa ON pa.id_panier = lc.id_panier";
        try (Statement stm = cnx.createStatement()) {
            try (ResultSet rs = stm.executeQuery(requete)) {
                while (rs.next()) {
                    produits.add(new LigneCommande(
                            rs.getInt("id_lc"),
                            rs.getInt("id_panier"),
                            rs.getInt("quantite"),
                            rs.getString("ref_produit"),
                            rs.getInt("id_commande")

                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }
        return produits;
    }

    /////////////////////////////////////////////// Supprime un produit du panier il sera supprimer de la ligne de commande ///////////////////////////////////////////////
    public void supprimerProduitDuPanier(Integer id_lc) {
        String requete = "DELETE FROM ligne_commande WHERE id_lc = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setInt(1, id_lc);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Ligne de commande supprimé du panier");
            } else {
                System.out.println("Aucun produit avec la référence spécifiée trouvé dans le panier");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit du panier : " + e.getMessage());
        }
    }

    ///////////////////////////////// comment Affiche les produits dans le panier///////////////////////////////////////////////////////////
    public void afficherProduits() {
        List<LigneCommande> ligneCommandes = affichageProduitsDansLePanier();
        productsContainer.getChildren().clear();


        // Initialize the arrays with zeros
        sousTotale[0] = 0;
        remise[0] = 0;
        totale[0] = 0;


        recalculate(sousTotale, remise, totale);

        for (LigneCommande prod : ligneCommandes) {
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Produit p = new Produit(
                                rs.getString("ref"),
                                rs.getString("marque"),
                                rs.getString("categorie"),
                                rs.getFloat("prix"),
                                rs.getString("image"),
                                rs.getString("objectif"),
                                rs.getString("critere")
                        );
                        sousTotale[0] += prod.getQuantite() * p.getPrix();
                        recalculate(sousTotale, remise, totale);

                        HBox productBox = new HBox(80);
                        productBox.getStyleClass().add("product-box");
                        productsContainer.setStyle("-fx-padding:15px");
                        ImageView productImage = new ImageView(new Image(p.getImage()));
                        productImage.setFitHeight(50);
                        productImage.setFitWidth(50);

                        Rectangle clip = new Rectangle(50, 50); // Set the dimensions as needed
                        clip.setArcWidth(60); // Adjust the corner radius
                        clip.setArcHeight(60);
                        productImage.setClip(clip);
                        productImage.setEffect(new DropShadow(10, Color.BLACK)); // Adjust the shadow parameters


                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef() + ": " + p.getPrix() + "TND");
                        labelProduit.setMinHeight(20);
                        labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");

                        labelProduit.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label

                        Spinner<Integer> quantiteSpinner = new Spinner<>(1, 100, prod.getQuantite(), 1);
                        quantiteSpinner.setEditable(false);
                        quantiteSpinner.setMaxWidth(100);
                        quantiteSpinner.setMaxHeight(20);
                        quantiteSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
                        quantiteSpinner.setStyle("-fx-font-size: 10px;");

                        quantiteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                            sousTotale[0] += (newValue - oldValue) * p.getPrix();
                            recalculate(sousTotale, remise, totale);
                            // Update the database
                            String updateQuery = "UPDATE ligne_commande SET quantite = ? WHERE id_lc = ?";
                            try (PreparedStatement pstUpdate = cnx.prepareStatement(updateQuery)) {
                                pstUpdate.setInt(1, newValue);
                                pstUpdate.setInt(2, prod.getIdLc());
                                pstUpdate.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println("Erreur lors de la mise à jour de la quantité : " + e.getMessage());
                            }
                        });

                        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/demo/Images/delete.png")));
                        deleteIcon.setFitHeight(20);
                        deleteIcon.setFitWidth(20);

                        Button btnSupprimer = new Button("", deleteIcon);
                        btnSupprimer.setOnAction(event -> {
                            supprimerProduitDuPanier(prod.getIdLc());
                            afficherProduits();
                        });
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        HBox actionBox = new HBox(30);
                        actionBox.getChildren().addAll(quantiteSpinner, btnSupprimer);

                        HBox.setHgrow(productImage, Priority.NEVER);

                        HBox.setHgrow(actionBox, Priority.NEVER);

                        productBox.getChildren().addAll(productImage, labelProduit, spacer, actionBox);

                        productsContainer.getChildren().add(productBox);


                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }

        }
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true); // Adjust to your needs

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


    //vider directement le panier sans demander il suffit que la commande est validée la suppression d'effectue directement

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


//supprime le panier utliser dans vider panier pour réduire la répition de code

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
    public static int commande_id;
    public void updatecommande()
    {
        // Récupérer les lignes de commande actuellement dans le panier
        List<LigneCommande> ligneCommandes = affichageProduitsDansLePanier();
        float longitude=CommandeClientController.latitude;
        float lat=CommandeClientController.latitude;
        String address=CommandeClientController.address;
        // Requête SQL pour insérer une nouvelle commande dans la base de données
        System.out.println(commande_id+" =IDc");
        String insertCommandeQuery = "UPDATE commande SET longitude=?,latitude=?,address=? WHERE id_commande=LAST_INSERT_ID()";

        try (PreparedStatement pst = cnx.prepareStatement(insertCommandeQuery)) {
            // Spécifier les valeurs des paramètres de la requête
            pst.setFloat(1, longitude); // Date actuelle
            pst.setFloat(2, lat);
            pst.setString(3,address);


            // Exécuter la requête d'insertion
            pst.executeUpdate();
            System.out.println("Commande à jour avec succès");

            // Récupérer l'ID de la dernière commande insérée
            String selectCommandeQuery = "SELECT * FROM commande WHERE id_commande = LAST_INSERT_ID()";
            try (PreparedStatement pstSelect = cnx.prepareStatement(selectCommandeQuery)) {
                ResultSet rs = pstSelect.executeQuery();
                if (rs.next()) {
                    // Récupérer les détails de la commande insérée
                    int idCommande = rs.getInt("id_commande");
                    Date dateCommande = rs.getDate("date_commande");
                    int idClient = rs.getInt("id_client");
                    float totaleCommande = rs.getFloat("totalecommande");
                    float remise = rs.getFloat("remise");
                    String etat = rs.getString("etat");
                    PanierController.commande_id=idCommande;

                    // Créer un objet Commande avec les détails récupérés
                    Commande selectedCommande = new Commande(idCommande, dateCommande, idClient, totaleCommande, remise, etat);
                    holder.setCommande(selectedCommande); // Définir la commande dans le conteneur (holder)
                    viderPanier(true); // Vider le panier après avoir passé la commande
                    // Mettre à jour les lignes de commande pour lier les produits à la nouvelle commande
                    for (LigneCommande prod : ligneCommandes) {
                        String updateQuery = "UPDATE ligne_commande SET id_panier = NULL, id_commande = ? WHERE id_lc = ?";
                        try (PreparedStatement pstUpdate = cnx.prepareStatement(updateQuery)) {
                            pstUpdate.setInt(1, idCommande); // ID de la nouvelle commande
                            pstUpdate.setInt(2, prod.getIdLc()); // ID de la ligne de commande à mettre à jour
                            pstUpdate.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Erreur lors de la mise à jour du produit dans le panier : " + e.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Gérer les erreurs lors de l'ajout de la commande
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    // Ajoute un produit au panier///////////////////////////////////////////////
    public void ajouterCommande() {
        // Récupérer les lignes de commande actuellement dans le panier
        List<LigneCommande> ligneCommandes = affichageProduitsDansLePanier();
        float longitude=CommandeClientController.latitude;
        float lat=CommandeClientController.latitude;
        String address=CommandeClientController.address;

        // Requête SQL pour insérer une nouvelle commande dans la base de données
        String insertCommandeQuery = "INSERT INTO commande (date_commande, id_client, totalecommande, remise, etat,longitude,latitude,address) VALUES (?, ?, ?, ?, ?,?,?,?)";

        try (PreparedStatement pst = cnx.prepareStatement(insertCommandeQuery)) {
            // Spécifier les valeurs des paramètres de la requête
            pst.setDate(1, new java.sql.Date(System.currentTimeMillis())); // Date actuelle
            pst.setInt(2, 14); // ID du client (à remplacer par une valeur dynamique)
            pst.setFloat(3, (float) totale[0]); // Montant total de la commande
            pst.setFloat(4, (float) remise[0]); // Montant de la remise appliquée
            pst.setString(5, "Non Livre"); // État de la commande
            pst.setFloat(6,longitude);
            pst.setFloat(7,lat);
            pst.setString(8, address);

            // Exécuter la requête d'insertion
            pst.executeUpdate();
            System.out.println("Commande ajoutée avec succès");

            // Récupérer l'ID de la dernière commande insérée
            String selectCommandeQuery = "SELECT * FROM commande WHERE id_commande = LAST_INSERT_ID()";
            try (PreparedStatement pstSelect = cnx.prepareStatement(selectCommandeQuery)) {
                ResultSet rs = pstSelect.executeQuery();
                if (rs.next()) {
                    // Récupérer les détails de la commande insérée
                    int idCommande = rs.getInt("id_commande");
                    Date dateCommande = rs.getDate("date_commande");
                    int idClient = rs.getInt("id_client");
                    float totaleCommande = rs.getFloat("totalecommande");
                    float remise = rs.getFloat("remise");
                    String etat = rs.getString("etat");

                    // Créer un objet Commande avec les détails récupérés
                    Commande selectedCommande = new Commande(idCommande, dateCommande, idClient, totaleCommande, remise, etat);
                    holder.setCommande(selectedCommande); // Définir la commande dans le conteneur (holder)
                    viderPanier(true); // Vider le panier après avoir passé la commande

                    // Mettre à jour les lignes de commande pour lier les produits à la nouvelle commande
                    for (LigneCommande prod : ligneCommandes) {
                        String updateQuery = "UPDATE ligne_commande SET id_panier = NULL, id_commande = ? WHERE id_lc = ?";
                        try (PreparedStatement pstUpdate = cnx.prepareStatement(updateQuery)) {
                            pstUpdate.setInt(1, idCommande); // ID de la nouvelle commande
                            pstUpdate.setInt(2, prod.getIdLc()); // ID de la ligne de commande à mettre à jour
                            pstUpdate.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Erreur lors de la mise à jour du produit dans le panier : " + e.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Gérer les erreurs lors de l'ajout de la commande
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }




    public void ajouterProduitAuLigneCommande(String refProduit) {
        // Ajouter le produit au panier et récupérer l'ID du panier
        int idPanier = ajouterProduitAuPanier(14); // ID du client (à remplacer par une valeur dynamique)
        System.out.println("aa" + idPanier);

        // Vérifier si l'ajout au panier a réussi
        if (idPanier != -1) {
            // Vérifier si le produit existe déjà dans la ligne de commande
            String checkIfExistsQuery = "SELECT COUNT(*) FROM ligne_commande WHERE ref_produit = ? and id_panier=?";
            try (PreparedStatement pstCheck = cnx.prepareStatement(checkIfExistsQuery)) {
                pstCheck.setString(1, refProduit);
                pstCheck.setInt(2, idPanier);

                ResultSet rs = pstCheck.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                // Si le produit existe, mettre à jour la quantité en l'incrémentant de 1
                if (count > 0) {
                    String updateQuery = "UPDATE ligne_commande SET quantite = quantite + 1 WHERE ref_produit = ? and id_panier=?";
                    try (PreparedStatement pstUpdate = cnx.prepareStatement(updateQuery)) {
                        pstUpdate.setString(1, refProduit);
                        pstUpdate.setInt(2, idPanier);
                        pstUpdate.executeUpdate();
                        System.out.println("Produit mis à jour dans le panier avec succès");
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la mise à jour du produit dans le panier : " + e.getMessage());
                    }
                } else {
                    // Si le produit n'existe pas, insérer une nouvelle ligne de commande avec une quantité de 1
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







































    //Métier de calcul de prix total ainsi de recalculer avec la remise selon le nombre de commande par client

    private void recalculate(double[] sousTotale, double[] remise, double[] totale) {
        if(isPeriodOfSale()){

            remise[0] = 0.50; // 50% remise
            totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]);
            remiseid.setText("Profitez jusqu'à 50% de reduction");

        }else{
            vboxpanier.getChildren().remove(soldeBand);


            if (9 > nbrCommande(14) && nbrCommande(14) > 3) {
                remise[0] = 0.15; // 15% remise
                totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]);
            } else if (nbrCommande(14) > 9) {
                remise[0] = 0.25; // 25% remise
                totale[0] = sousTotale[0] - (sousTotale[0] * remise[0]);
            } else {
                totale[0] = sousTotale[0];
            }
            if (remise[0] == 0) {
                remiseid.setText("Aucune remise");
            } else {
                remiseid.setText(String.format("%.0f%% de remise", remise[0] * 100));
            }
        }



        soustotaleid.setText(String.format("Sous-Totale : %.2f TND", sousTotale[0]));
        totaleid.setText(String.format("Totale : %.2f TND", totale[0]));
    }
    private boolean isPeriodOfSale() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the start and end dates of the sale period
        LocalDate saleStartDate = LocalDate.of(2024, 2, 1); // February 1, 2024
        LocalDate saleEndDate = LocalDate.of(2024, 3, 29); // Mars 29, 2024


        // Check if the current date is within the sale period
        return currentDate.isAfter(saleStartDate) && currentDate.isBefore(saleEndDate);
    }

    private Integer nbrCommande(int clientId) {
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




    // Méthodes de navigation//////////////////////////////////////////////////////////

    @FXML
    private void chargerInterfaceCommande(ActionEvent event) {
        ajouterCommande();
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/demo/commande_client.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargecommandes() {
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
}
