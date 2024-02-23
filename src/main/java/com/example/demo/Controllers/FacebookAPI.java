package com.example.demo.Controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.FacebookType;

public class FacebookAPI {

    public static void main(String[] args) {
        // Remplacez cette valeur par votre jeton d'accès Facebook
        String accessToken = "EAANLAmpI2JEBO6VBMfXbdLY3ZCXRKxZAv6TZAQ3rYdcGQ9h2cD2Son0b6a8v57wQsqHvmeieNdokk5EyhSOjKxMcRSZAEK4QFXN38XhdbwZAluZAs9wZAqK2xZCWfyZCmDFZBUkJZC5ZCTrFRy3aMoSXmJ6cfqFSGk5Rt5D32DgAj95hxDHjT9LofKe0icqZA0UeZBsEfvzSZCcz5gkjRDhDgNiDPf1AZC4ZD ";

        // Initialize the Facebook client
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_19_0);

        // Message à publier
        String message = "Hello, Facebook! This is a test message.";

        // Publier le message sur le fil d'actualité de l'utilisateur
        postStatusUpdate(facebookClient, message);
    }

    public static void postStatusUpdate(FacebookClient facebookClient, String message) {
        try {
            // Créer un paramètre avec le message
            Parameter messageParam = Parameter.with("message", message);

            // Publier la mise à jour du statut sur le fil d'actualité de l'utilisateur
            FacebookType response = facebookClient.publish("me/feed", FacebookType.class, messageParam);

            // Afficher l'ID du message nouvellement créé
            System.out.println("Posted message ID: " + response.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
