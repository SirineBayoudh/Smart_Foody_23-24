package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.sql.*;
import java.util.Date;

import static com.example.demo.Controllers.PaiementStripeUI.creerSessionPaiement;

public class CommandeClientController {

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_2;

    @FXML
    private Pane pane_3;
    @FXML
    private TextField Trecherche;

    @FXML
    private Pane clickpane;

    @FXML
    private Label cmd;

    @FXML
    private Label cmdlivre;

    @FXML
    private Label cmdnonlivre;
    @FXML
    private TableView<Commande> commandeTableView;

    @FXML
    private TableColumn<Commande, Integer> id_commandeColumn;

    @FXML
    private TableColumn<Commande, String> etat;
    @FXML
    private TableColumn<Commande, Float> remise;

    @FXML
    private TableColumn<Commande, Date> date_commandeColumn;

    @FXML
    private TableColumn<Commande, Integer> id_clientColumn;

    @FXML
    private TableColumn<Commande, Float> total_commandeColumn;

    @FXML
    private TableColumn<Commande, Integer> nbre_commandeColumn;
    PanierController panierController = new PanierController(); // Création d'une instance de PanierController



    public CommandeClientController(PanierController panierController) {
        this.panierController = panierController;
    }


    // Liste observable pour stocker les commandes
    private ObservableList<Commande> commandeList = FXCollections.observableArrayList();

    // Connexion à la base de données
    private Connection cnx;
    @FXML
    private Label titleLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private ComboBox<String> addressComboBox;

    // Constructeur
    public CommandeClientController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void initialize() {



        int idUtilisateur = 1; // Supposons que l'utilisateur connecté ait l'id 1

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

                // Mettre à jour le label de salutation avec le nom de l'utilisateur
                titleLabel.setText("Bonjour Mr. " + nom);

                // Afficher les informations du client dans le label infoLabel
                infoLabel.setText("Vos informations sont comme suit :\n" +
                        "Nom : " + nom + "\n" +
                        "Prénom : " + prenom + "\n" +
                        "Email : " + email + "\n" +
                        "Adresse : " + adresse);

                // Charger les adresses de livraison disponibles dans la combobox
                addressComboBox.getItems().addAll("Adresse 1", "Adresse 2", "Adresse 3"); // Supposons que ce sont les adresses enregistrées pour le client

                // Sélectionner par défaut la première adresse dans la combobox
                addressComboBox.getSelectionModel().selectFirst();
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'identifiant spécifié.");
            }

            // Fermer les ressources
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des informations de l'utilisateur : " + e.getMessage());
        }
    }

    // Méthode appelée lorsque l'utilisateur valide la livraison
    @FXML
    public void ajouterCommande() {
        String insertCommandeQuery = "INSERT INTO commande (date_commande, id_client, totalecommande, remise, etat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(insertCommandeQuery)) {
            pst.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            pst.setInt(2, 14);
            pst.setFloat(3, (float) panierController.totale[0]);
            pst.setFloat(4, (float) panierController.remise[0]);
            pst.setString(5, "en cours");
            pst.executeUpdate();

            String emailClient = "saidifadhila24@gmail.com";
            String sujetEmail = "Confirmation de commande";
            String contenuEmail = "Votre commande a été passée avec succès. Merci de votre confiance.";

            EmailUtil.envoyerEmail(emailClient, sujetEmail, contenuEmail);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Commande passée");
            successAlert.setHeaderText(null);
            successAlert.setContentText("La commande a été ajoutée avec succès et un e-mail de confirmation a été envoyé.");
            successAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/demo/css/style_panier.css").toExternalForm());
            successAlert.getDialogPane().getStyleClass().add("custom-alert");
            successAlert.showAndWait();

            System.out.println("Commande ajoutée avec succès");
            panierController.viderPanier(true);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void appelerViderPanier(ActionEvent event) {
        panierController.viderPanier(false); // Appel de la méthode viderPanier avec false pour indiquer que la commande n'est pas validée
    }
    // la méthodes qui selectionne tous les commandes passe a partir de base




    public void payer() {
        Stage stage = new Stage();
        WebView webView = new WebView();
        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Paiement Stripe");
        stage.show();
        creerSessionPaiement(webView);

    }



}