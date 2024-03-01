package com.example.demo.Controllers;

import com.example.demo.Models.*;
import com.example.demo.Tools.MyConnection;
import com.example.demo.Tools.ServiceCommande;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.demo.Controllers.PaiementStripeUI.creerSessionPaiement;
import static javafx.scene.paint.Color.BLACK;

public class CommandeClientController{
    static Stage stage;
    private CommandeHolder holder = CommandeHolder.getInstance();
    private Commande CurrentCommande = holder.getCommande();
    @FXML
    private CheckBox payOnDeliveryCheckBox;
    public ComboBox map;
    @FXML
    private VBox productsContainer;
    @FXML
    private BorderPane centerPane;
    @FXML
    private  VBox productsContainer1;
    static PanierController panierController = new PanierController(); // Création d'une instance de PanierController

    private static final String API_KEY = "2687d76eef6bf2a7b59beecb";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;
    public CommandeClientController(PanierController panierController) {
        this.panierController = panierController;
    }
    private ServiceCommande commandeService;
    static NavbarreCotroller navbarreController = new NavbarreCotroller();

    static float latitude;
    static float longitude;
    public void savecoords(String latitude, String longitude) {
        CommandeClientController.latitude =Float.parseFloat(latitude);
        CommandeClientController.longitude =Float.parseFloat(longitude);
        System.out.println("Latitude from controller = "+latitude);
        System.out.println("Longitude from controller= "+longitude);
        saveOrder();
    }

    private void saveOrder() {
    }


    // Liste observable pour stocker les commandes
    private ObservableList<Commande> commandeList = FXCollections.observableArrayList();

    // Connexion à la base de données
    private Connection cnx;
    @FXML
    private Text remiseInput;
    @FXML
    private TextArea clientInput;

    @FXML
    private Label titleLabel;

    @FXML
    private Text totaleInputEur;

    @FXML
    private Text totaleInputTnd;
    @FXML
    private Text adress;

    @FXML
    private Label infoLabel;
    @FXML
    private DatePicker dateCreationInput;
    @FXML
    private ComboBox<String> addressComboBox;

    double[] sousTotale = {0};
    double[] remise = {0};
    double[] totale = {0};
    // Constructeur
    public CommandeClientController() {
        cnx = MyConnection.getInstance().getCnx();
    }
    // Supposons que l'utilisateur connecté ait l'id 1
    int idUtilisateur = 14;
    Utilisateur utilisateur = new Utilisateur();
    public void initialize() {
        this.afficherProduits();



        try {
            // Préparer la requête pour récupérer les données de l'utilisateur
            String selectQuery = "SELECT nom, prenom, email, adresse FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement pst = cnx.prepareStatement(selectQuery);
            pst.setInt(1, idUtilisateur);

            // Exécuter la requête
            ResultSet rs = pst.executeQuery();

            // Vérifier si des résultats sont retournés
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String adresse = rs.getString("adresse");

                utilisateur = new Utilisateur(nom,prenom,email,adresse);



            } else {
                System.out.println("Aucun utilisateur trouvé avec l'identifiant spécifié.");
            }

            // Fermer les ressources
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des informations de l'utilisateur : " + e.getMessage());
        }

        try {
            this.initData(CurrentCommande);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initData(Commande quest) throws IOException {
        // Initialisation de la commande courante avec les données passées en paramètre.
        CurrentCommande = quest;
        String toCurrency = "EUR"; // Définit la devise cible pour la conversion.

        // Met à jour l'interface utilisateur avec les détails de la commande courante.
        holder.setCommande(CurrentCommande);

        // Initialise l'objet de service pour interagir avec les données des commandes.
        commandeService = new ServiceCommande();

        // Convertit la date de la commande de String à LocalDate pour l'affichage.
        LocalDate createdConverted = LocalDate.parse(CurrentCommande.getDate_commande().toString());
        dateCreationInput.setValue(createdConverted);

        // Récupère et affiche le nom d'utilisateur du client à partir de son ID.
        clientInput.setText(String.valueOf(commandeService.usernameById(CurrentCommande.getId_client())));

        // Formatage et affichage de la remise en pourcentage.
        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        String remiseText = decimalFormat.format(CurrentCommande.getRemise() * 100) + " %";
        remiseInput.setText(remiseText);

        // Affichage de l'adresse de l'utilisateur.
        adress.setText(utilisateur.getAdresse());

        // Affichage du total de la commande en TND.
        totaleInputTnd.setText(CurrentCommande.getTotal_commande() + " TND");

        // Conversion de la devise de TND à EUR via une requête HTTP à un service de conversion de devises.
        /** Convertisseur Devise TND -> EUR */
        URL url = new URL(BASE_URL + "pair/TND/" + toCurrency + "/" + CurrentCommande.getTotal_commande());
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convertit la réponse en JSON.
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        // Récupère le résultat de la conversion et l'affiche.
        Float req_result = jsonobj.get("conversion_result").getAsFloat();
        totaleInputEur.setText(String.format("%.2f %s", req_result, toCurrency));
    }

    public void afficherProduits() {
        // Récupérer les lignes de commande pour afficher les produits
        List<LigneCommande> ligneCommandes = affichageProduitsDansCommande();

        // Nettoyer le conteneur des produits avant d'afficher de nouveaux produits
        productsContainer.getChildren().clear();

        // Boucle à travers chaque ligne de commande
        for (LigneCommande prod : ligneCommandes) {
            // Requête SQL pour récupérer les détails du produit à partir de la base de données
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    // Si la requête retourne un résultat, créer un objet Produit
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

                        // Créer un conteneur VBox pour le produit
                        VBox productBox = new VBox(10);
                        productBox.getStyleClass().add("product-box");
                        productsContainer.setStyle("-fx-padding:15px");

                        // Créer et styliser l'image du produit
                        ImageView productImage = new ImageView(new Image(p.getImage()));
                        productImage.setFitHeight(15);
                        productImage.setFitWidth(15);
                        Rectangle clip = new Rectangle(15, 15); // Set the dimensions as needed
                        clip.setArcWidth(30); // Adjust the corner radius
                        clip.setArcHeight(30);
                        productImage.setClip(clip);
                        productImage.setEffect(new DropShadow(10, BLACK)); // Adjust the shadow parameters

                        // Créer des étiquettes pour afficher les détails du produit
                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef());
                        labelProduit.setMinHeight(10);
                        labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit.setAlignment(Pos.BOTTOM_CENTER);

                        DecimalFormat decimalFormat = new DecimalFormat("0.##");
                        decimalFormat.setDecimalSeparatorAlwaysShown(false);
                        Label labelProduit1 = new Label(decimalFormat.format(p.getPrix() * prod.getQuantite()) + " TND");
                        labelProduit1.setMinHeight(10);
                        labelProduit1.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit1.setAlignment(Pos.BOTTOM_CENTER);

                        // Champ de texte pour afficher la quantité du produit
                        TextField quantiteTextField = new TextField(String.valueOf(prod.getQuantite()));
                        quantiteTextField.setEditable(false);
                        quantiteTextField.setMaxWidth(100);
                        quantiteTextField.setMaxHeight(20);
                        quantiteTextField.setStyle("-fx-font-size: 10px;");
                        quantiteTextField.setAlignment(Pos.CENTER);

                        // Ajouter les éléments graphiques au conteneur productBox
                        productBox.setAlignment(Pos.CENTER);
                        productBox.setPrefWidth(166);
                        HBox.setHgrow(productBox, Priority.ALWAYS);
                        productBox.getChildren().addAll(productImage, labelProduit, labelProduit1, quantiteTextField);

                        // Ajouter le conteneur productBox au conteneur principal productsContainer
                        productsContainer.getChildren().add(productBox);
                    }
                }
            } catch (SQLException e) {
                // Gérer les erreurs liées à l'affichage du produit dans le panier
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }
        }

        // Créer un ScrollPane pour contenir les produits
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true); // Ajuster à la largeur du parent
    }



    //////////**************************** Affiche les produits dans le panier en rejoignant les
    // tables `produit` et `panier` par `ref_produit`.***********************************************
    private List<LigneCommande> affichageProduitsDansCommande() {
        // Création d'une liste pour stocker les lignes de commande des produits
        List<LigneCommande> produits = new ArrayList<>();

        // Requête SQL pour récupérer les lignes de commande des produits dans une commande spécifique
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc ON p.ref = lc.ref_produit and lc.id_commande=? ";

        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            // Remplacer le paramètre de la commande dans la requête SQL avec l'ID de la commande actuelle
            pst.setInt(1, CurrentCommande.getId_commande());

            try (ResultSet rs = pst.executeQuery()) {
                // Parcourir les résultats de la requête
                while (rs.next()) {
                    // Créer un objet LigneCommande pour chaque ligne de commande récupérée
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
            // Gérer les erreurs liées à la récupération des produits dans le panier
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }

        // Retourner la liste des lignes de commande des produits
        return produits;
    }


    // Méthode appelée lorsque l'utilisateur valide la livraison
    @FXML
    public void ajouterCommande() throws IOException, WriterException {
        if(!payOnDeliveryCheckBox.isSelected()){
            payer();
            loadPage("/com/example/demo/produit.fxml");

        }else{
            validCommande() ;
            loadPage("/com/example/demo/produit.fxml");
        }
        panierController.updatecommande();




    }

    //* valid commande finale
    public static void validCommande() throws IOException, WriterException {
        // URL de votre page Facebook Smart Foody
        String facebookPageURL = "https://www.facebook.com/smartfoody.tn";

        // Générer le code QR pour la commande avec l'URL de la page Facebook
        String qrCodePath = "C:\\Users\\INFOTEC\\Desktop\\Smart_Foody_23-24\\qr_code.png"; // Remplacer par le chemin où vous souhaitez enregistrer le code QR
        QRCodeGenerator.generateQRCode(facebookPageURL, 200, 200, qrCodePath);

        // Informations pour l'email
        String emailClient = "saidifadhila24@gmail.com";
        String sujetEmail = "Confirmation de commande";

        // Modifier le contenuEmail pour inclure le code QR
        String contenuEmail = "<html><body>"
                + "<div style='display: flex; justify-content: space-between; width: 100%;'>"
                + "<div style='width: 50%;'><img src='cid:logo' alt='Logo' style='width: 100px; float: left;'/></div>" // Logo poussé à gauche
                + "<div style='width: 50%;'><img src='cid:qrCode' alt='QR Code' style='width: 100px; float: right;'/></div>" // QR Code poussé à droite
                + "</div>"
                + "<div style='clear: both; border: 2px solid green; padding: 20px; margin-top: 20px;'>"
                + "<h1 style='text-align: center;'>Confirmation de commande</h1>"
                + "<p>Votre commande a été passée avec succès. Merci de votre confiance.</p>"
                + "</div>"
                + "</body></html>";

        // Assurez-vous que la méthode envoyerEmailAvecImageInline est adaptée pour gérer plusieurs images (logo et code QR)
        //EmailUtil.envoyerEmailAvecImageInline(emailClient, sujetEmail, contenuEmail, qrCodePath, "qrCode", "C:/Users/INFOTEC/Desktop/Smart_Foody_23-24/src/main/resources/com/example/demo/Images/trans_logo.png", "logo");

        // Afficher une alerte de succès
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Commande passée");
        successAlert.setHeaderText(null);
        successAlert.setContentText("La commande a été ajoutée avec succès et un e-mail de confirmation a été envoyé.");
        successAlert.getDialogPane().getStylesheets().add(CommandeClientController.class.getResource("/com/example/demo/css/style_panier.css").toExternalForm());
        successAlert.getDialogPane().getStyleClass().add("custom-alert");
        successAlert.showAndWait();

        System.out.println("Commande ajoutée avec succès");
        panierController.viderPanier(true);


    }




    public void payer() {
        Stage stage = new Stage();
        WebView webView = new WebView();
        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Paiement Stripe");
        stage.show();
        creerSessionPaiement(webView);

    }



    public void map(ActionEvent actionEvent) {
        MapController m=new MapController();
        m.showWindow();
        loadPage("/com/example/demo/map.fxml");
    }



       /* // Create a dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Insert Location");

        // Set the button types
        ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField latitudeField = new TextField(adress.getText());
        latitudeField.setPromptText("Adresse");


        grid.add(new Label("Adresse:"), 0, 0);
        grid.add(latitudeField, 1, 0);


        // Enable/Disable insert button depending on whether a latitude and longitude are entered
        Node insertButton = dialog.getDialogPane().lookupButton(insertButtonType);
        insertButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        latitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the latitude field by default.
        Platform.runLater(() -> latitudeField.requestFocus());

        // Convert the result to a latitude-longitude-pair when the insert button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                return latitudeField.getText() ;
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();

        // If the user clicked the insert button, show the location
        result.ifPresent(location -> {
            adress.setText(location);
            // Call a method to show the location on the map
        });*/

    @FXML
    private void annulerCommande() throws SQLException {
        try {
            commandeService.deleteOne(CurrentCommande.getId_commande());
            loadPanier();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void loadProduit() {
        loadPage("/com/example/demo/produit.fxml");
    }

    @FXML
    private void loadPanier() {
        loadPage("/com/example/demo/produit.fxml");
    }

    public void loadValidCommande() {
        loadPage("/com/example/demo/commande_client.fxml");
    }

    public void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page));
            if (centerPane != null) {
                centerPane.setCenter(root);
                centerPane.setPrefWidth(1200);
            } else {
                System.out.println("centerPane is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}