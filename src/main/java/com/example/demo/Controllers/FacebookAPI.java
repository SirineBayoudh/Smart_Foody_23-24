package com.example.demo.Controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.FacebookType;

public class FacebookAPI {

    public static void main(String[] args) {
        // Remplacez cette valeur par votre jeton d'accès Facebook
        String accessToken = "EAANLAmpI2JEBOwbvRtdm76IprQ5jpytGPZAe1aAC0Gn5VacKxqbSLXW7kcUMElkLfjd4CQKiq6WfYZCReqlZCFg7qLJYvP1xO6UcaOuTB3EMhlgrB1fd9hLbPlsVBZAA6KdwAv8aC1V37YRnOFKAhShPnZCXi5VZBIBnpk6NOGTXUuLySMuNEAo2ZCz ";

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
