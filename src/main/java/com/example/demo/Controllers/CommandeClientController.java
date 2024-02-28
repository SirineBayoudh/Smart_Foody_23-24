package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Tools.MyConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

import static com.example.demo.Controllers.PaiementStripeUI.creerSessionPaiement;

public class CommandeClientController {


    public ComboBox map;
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
            EmailUtil.envoyerEmailAvecImageInline(emailClient, sujetEmail, contenuEmail, qrCodePath, "qrCode", "C:/Users/INFOTEC/Desktop/Smart_Foody_23-24/src/main/resources/com/example/demo/Images/trans_logo.png", "logo");

            // Afficher une alerte de succès
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
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi de l'email ou de la génération du QR code: " + e.getMessage());
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


    public void map(ActionEvent actionEvent) {

    }
}