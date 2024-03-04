package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Models.CommandeHolder;
import com.example.demo.Models.LigneCommande;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import com.example.demo.Tools.ServiceCommande;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.BLACK;

public class ProfileController {

    @FXML
    private Text adresseInput;
    @FXML
    private TextArea clientInput;

    @FXML
    private DatePicker dateCreationInput;
    @FXML
    private Text etatInput;
    @FXML
    private Text remiseInput;
    @FXML
    private Text totaleInputEur;

    @FXML
    private Text totaleInputTnd;

    @FXML
    private VBox productsContainer;
    @FXML
    private VBox productsContainer1;
    @FXML
    private AnchorPane detailsCommande;
    @FXML
    private Pane detailsCommandesPane;
    @FXML
    private Pane detailsCommandesPane1;
    private Connection cnx;

    private ServiceCommande serviceCommande;

    private CommandeHolder holder = CommandeHolder.getInstance();
    @FXML
    private VBox vboxCommande;

    @FXML
    private ToggleButton histButton;

    public ProfileController() {
        cnx = MyConnection.getInstance().getCnx();
    }
    private static final String API_KEY = "da81771213450868d6ffe772";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;
    //ici on va mettre id de user connecté
    int idClient = 14;

    public void initialize() throws IOException {
        detailsCommandesPane.setVisible(true);
        detailsCommande.getChildren().remove(detailsCommandesPane1);
        vboxCommande.setVisible(true);
        afficherCommandes();
    }
    @FXML
    private void onPaneClicked() throws IOException {
        if (histButton.isSelected()) {
            vboxCommande.setVisible(true);

            afficherCommandes();
        } else {
            vboxCommande.setVisible(false);
        }
    }

    public void afficherCommandes() throws IOException {
        serviceCommande = new ServiceCommande();
        // Récupérer les lignes de commande pour afficher les produits
        List<Commande> Commandes = serviceCommande.commandeByClient(idClient);
        String toCurrency = "EUR";
        // Nettoyer le conteneur des produits avant d'afficher de nouveaux produits
        productsContainer.getChildren().clear();
        DecimalFormat df = new DecimalFormat("#.##");

        // Boucle à travers chaque ligne de commande
        for (Commande prod : Commandes) {



            HBox productBox = new HBox(120);
            productBox.getStyleClass().add("product-box");
            productsContainer.setStyle("-fx-padding:15px");


            Label labelProduit = new Label(prod.getId_commande() + " - " + prod.getDate_commande() );
            labelProduit.setMinHeight(20);
            labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");

            labelProduit.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label

            Label labelProduit1 = new Label(df.format(prod.getTotal_commande()) + " TND ");
            labelProduit1.setMinHeight(20);
            labelProduit1.setStyle("-fx-position:absolute ;-fx-top:12px;");

            labelProduit1.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label

            /** Convertisseur Devise TND -> EUR */
            URL url = new URL(BASE_URL + "pair/TND/" + toCurrency + "/" + prod.getTotal_commande());
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convertit la réponse en JSON.
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            // Récupère le résultat de la conversion et l'affiche.
            Float req_result = jsonobj.get("conversion_result").getAsFloat();

            Label labelProduit2 = new Label(df.format(req_result) + " € ");
            labelProduit2.setMinHeight(20);
            labelProduit2.setStyle("-fx-position:absolute ;-fx-top:12px;");

            labelProduit2.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label





            ImageView detailsIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/demo/Images/icons8-eye-100.png")));
            detailsIcon.setFitHeight(30);
            detailsIcon.setFitWidth(30);

            Button btnDetail = new Button("", detailsIcon);
            btnDetail.setOnAction(event -> {
                try {
                    this.afficherProduits(prod);
                    this.initData(prod);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                detailsCommande.getChildren().remove(detailsCommandesPane);
                detailsCommande.getChildren().add(detailsCommandesPane1);

            });
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox actionBox = new HBox(30);
            actionBox.getChildren().addAll(labelProduit2, btnDetail);

            HBox.setHgrow(labelProduit, Priority.NEVER);

            HBox.setHgrow(actionBox, Priority.NEVER);

            productBox.getChildren().addAll(labelProduit, labelProduit1, spacer, actionBox);

            productsContainer.getChildren().add(productBox);






        }



        // Créer un ScrollPane pour contenir les produits
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true); // Ajuster à la largeur du parent
    }

    public void afficherProduits(Commande cmd) {
        List<LigneCommande> ligneCommandes = affichageProduitsDansCommande(cmd);
        productsContainer1.getChildren().clear();

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

                        VBox productBox = new VBox(10);
                        productBox.getStyleClass().add("product-box");
                        productsContainer1.setStyle("-fx-padding:15px");

                        ImageView productImage = new ImageView(new Image(p.getImage()));
                        productImage.setFitHeight(50);
                        productImage.setFitWidth(50);

                        Rectangle clip = new Rectangle(50, 50); // Set the dimensions as needed
                        clip.setArcWidth(30); // Adjust the corner radius
                        clip.setArcHeight(30);
                        productImage.setClip(clip);
                        productImage.setEffect(new DropShadow(10, BLACK)); // Adjust the shadow parameters

                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef());
                        labelProduit.setMinHeight(10);
                        labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label
                        DecimalFormat decimalFormat = new DecimalFormat("0.##");
                        decimalFormat.setDecimalSeparatorAlwaysShown(false);

                        Label labelProduit1 = new Label(decimalFormat.format(p.getPrix() * prod.getQuantite()) + " TND");

                        labelProduit1.setMinHeight(10);
                        labelProduit1.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit1.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label

                        TextField quantiteTextField = new TextField(String.valueOf(prod.getQuantite()));
                        quantiteTextField.setEditable(false);
                        quantiteTextField.setMaxWidth(100);
                        quantiteTextField.setMaxHeight(20);
                        quantiteTextField.setStyle("-fx-font-size: 10px;");
                        quantiteTextField.setAlignment(Pos.CENTER);

                        productBox.setAlignment(Pos.CENTER);
                        productBox.setPrefWidth(185);
                        HBox.setHgrow(productBox, Priority.ALWAYS);

                        productBox.getChildren().addAll(productImage, labelProduit, labelProduit1, quantiteTextField);
                        productsContainer1.getChildren().add(productBox);


                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }

        }
        ScrollPane scrollPane = new ScrollPane(productsContainer1);
        scrollPane.setFitToWidth(true); // Adjust to your needs

    }

    private List<LigneCommande> affichageProduitsDansCommande(Commande cmd) {
        List<LigneCommande> produits = new ArrayList<>();
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc  ON p.ref = lc.ref_produit and lc.id_commande=? ";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setInt(1, cmd.getId_commande());

            try (ResultSet rs = pst.executeQuery()) {
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


    public void initData(Commande quest) throws IOException {
        String toCurrency = "EUR";

        serviceCommande = new ServiceCommande();
        LocalDate createdConverted = LocalDate.parse(quest.getDate_commande().toString());
        dateCreationInput.setValue(createdConverted);


        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        String remiseText = decimalFormat.format(quest.getRemise() * 100) + " %";
        remiseInput.setText(remiseText);

        totaleInputTnd.setText(quest.getTotal_commande() + " TND");
        /** Convertiseur Devise TND -> EUR*/
        URL url = new URL(BASE_URL + "pair/TND/" + toCurrency + "/" + quest.getTotal_commande());
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

// Convert to JSON
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

// Accessing object
        Float req_result = jsonobj.get("conversion_result").getAsFloat();

        totaleInputEur.setText(String.format("%.2f %s", req_result, toCurrency));


    }

    @FXML
    private void switchButton(){
        detailsCommande.getChildren().remove(detailsCommandesPane1);
        detailsCommande.getChildren().add(detailsCommandesPane);
    }

    @FXML
    private void modifierProfil(){

    }
    @FXML
    private void HidePasswordOnActionA(){

    }
    @FXML
    private void HidePasswordOnActionN(){

    }
    @FXML
    private void mdpUser(){

    }
    @FXML
    private void modifierMDP(){

    }
    @FXML
    private void Close_Eye_OnClickA(){

    }
    @FXML
    private void Open_Eye_OnClickA(){

    }
    @FXML
    private void Close_Eye_OnClickN(){

    }
    @FXML
    private void Open_Eye_OnClickN(){

    }
    @FXML
    private void ShowPasswordOnActionA(){

    }
    @FXML
    private void ShowPasswordOnActionN(){

    }

    @FXML
    private void infosUser(){

    }


}
