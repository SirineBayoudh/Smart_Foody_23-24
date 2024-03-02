package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.sql.SQLException;

public class PaiementStripeUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Création d'un composant WebView pour afficher la page de paiement Stripe
        WebView webView = new WebView();

        // Définition de la scène avec le composant WebView
        Scene scene = new Scene(webView, 800, 600);

        // Affichage de la scène
        primaryStage.setScene(scene);
        primaryStage.setTitle("Paiement Stripe");
        primaryStage.show();

        // Appel de la méthode pour créer la session de paiement et charger l'URL dans le WebView
        creerSessionPaiement(webView,null,null);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void creerSessionPaiement(WebView webView, Commande commande, CommandeClientController controller) {
        System.out.println("gg"+commande.toString());
        // Assurez-vous que la clé secrète Stripe est initialisée
        StripeConfig.init();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT) // Définir le mode de paiement
                .setSuccessUrl("https://checkout.stripe.com/success")
                .setCancelUrl("https://checkout.stripe.com/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount((long) (commande.getTotal_commande_devise() * 100)) // Montant en centimes
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Bonjour " +commande.getClientUsername()+", le Montant Totale de commande est")
                                                                .build())
                                                .build())
                                .build())
                .build();

        try {
            Session session = Session.create(params);
            // Redirection de l'utilisateur vers la page de paiement
            // Charger l'URL dans le WebView pour l'afficher à l'utilisateur
            webView.getEngine().load(session.getUrl());
            webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.equals("https://checkout.stripe.com/success")) {
                        // Payment was successful, load the produit.fxml page
                        controller.navbarre();
                    } else if (newValue.equals("https://checkout.stripe.com/cancel")) {
                        // Payment was canceled, call annulerCommande
                        try {
                            controller.annulerCommande(new ActionEvent());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (StripeException e) {
            e.printStackTrace();
            // Gérer les erreurs Stripe
        }
    }
}
