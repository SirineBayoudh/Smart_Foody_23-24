package com.example.demo.Controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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
        creerSessionPaiement(webView);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void creerSessionPaiement(WebView webView) {
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
                                                .setUnitAmount(2000L) // Montant en centimes
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Votre produit")
                                                                .build())
                                                .build())
                                .build())
                .build();

        try {
            Session session = Session.create(params);
            // Redirection de l'utilisateur vers la page de paiement
            // Charger l'URL dans le WebView pour l'afficher à l'utilisateur
            webView.getEngine().load(session.getUrl());
        } catch (StripeException e) {
            e.printStackTrace();
            // Gérer les erreurs Stripe
        }
    }
}
