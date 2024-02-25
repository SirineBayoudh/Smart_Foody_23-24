package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class PageAccueilController {
    private static final String ACCESS_KEY = "8SwFxNJG53NadG8LlDRnWphLiLd56udZIqAY6v9DE2Y";
    private static final String SEARCH_QUERY = "healthy%20ingredientfood"; // Modifier cette requête pour rechercher des images de produits alimentaires
    private static final String API_URL = "https://api.unsplash.com/photos/random?query=" + SEARCH_QUERY + "&client_id=" + ACCESS_KEY;
    private static final int NUM_IMAGES = 4; // Nombre d'images à afficher
    private static final int IMAGE_WIDTH = 300; // Largeur fixe des images
    private static final int IMAGE_HEIGHT = 350;
    @FXML
    private HBox imageContainer;
    @FXML
    private VBox imagesVbox;
    private Connection conn;
    @FXML
    private HBox categoryIconsContainer;
    @FXML
    private ImageView AllProductImageView;

    @FXML
    private ImageView fruitImageView;

    @FXML
    private ImageView grainImageView;

    @FXML
    private ImageView laitImageView;

    @FXML
    private ImageView vegImageView;

    @FXML
    void handleAllProductImageClick(MouseEvent event) {
       displayAllProducts();
    }

    @FXML
    void handleFruitImageClick(MouseEvent event) {
        displayImagesByCategory("fruit");
    }

    @FXML
    void handleGrainImageClick(MouseEvent event) {
        displayImagesByCategory("grain");
    }

    @FXML
    void handleLaitierImageClick(MouseEvent event) {
        displayImagesByCategory("laitier");
    }

    @FXML
    void handleVegImageClick(MouseEvent event) {
        displayImagesByCategory("legume");
    }

    private void displayImagesByCategory(String categoryName) {
        try {
            // Requête SQL pour récupérer les images de la catégorie spécifiée depuis la base de données
            String sql = "SELECT image FROM produit WHERE categorie = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            imagesVbox.getChildren().clear();

            // Créer une HBox pour chaque ligne d'images
            HBox hbox = new HBox();
            hbox.setSpacing(40); // Espace entre les cartes

            // Ajouter les images à chaque HBox et gérer les lignes
            while (resultSet.next()) {
                String imageUrl = resultSet.getString("image");
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(190); // Fixer la largeur de l'image
                imageView.setFitHeight(190);
                imageView.setPreserveRatio(false);

                // Créer une carte blanche pour chaque image
                StackPane cardPane = new StackPane();
                cardPane.setStyle("-fx-background-color: white; -fx-background-radius: 30; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-border-color: #56ab2f;-fx-border-radius: 30;-fx-border-width: 2px;");
                cardPane.getChildren().add(imageView);
                cardPane.setPrefWidth(220);
                cardPane.setPrefHeight(220);

                // Ajouter la carte à la HBox
                hbox.getChildren().add(cardPane);
                // Créer une nouvelle ligne si la ligne actuelle est pleine
                if (hbox.getChildren().size() >= 3) {
                    imagesVbox.getChildren().add(hbox);
                    hbox = new HBox();
                    hbox.setSpacing(40); // Espace entre les cartes
                }
            }
            // Ajouter la dernière ligne si elle n'est pas pleine
            if (!hbox.getChildren().isEmpty()) {
                imagesVbox.getChildren().add(hbox);
            }
            imagesVbox.setSpacing(20);

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des images depuis la base de données : " + e.getMessage());
        }
    }

    private void displayAllProducts() {
        try {
            // Requête SQL pour récupérer toutes les URL des images depuis la base de données
            String sql = "SELECT image FROM produit";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            imagesVbox.getChildren().clear();

            // Créer une HBox pour chaque ligne d'images
            HBox hbox = new HBox();
            hbox.setSpacing(40); // Espace entre les cartes

            // Ajouter les images à chaque HBox et gérer les lignes
            while (resultSet.next()) {
                String imageUrl = resultSet.getString("image");
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(190); // Fixer la largeur de l'image
                imageView.setFitHeight(190);
                imageView.setPreserveRatio(false);

                // Créer une carte blanche pour chaque image
                StackPane cardPane = new StackPane();
                cardPane.setStyle("-fx-background-color: white; -fx-background-radius: 30; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-border-color: #56ab2f;-fx-border-radius: 30;-fx-border-width: 2px;");
                cardPane.getChildren().add(imageView);
                cardPane.setPrefWidth(220);
                cardPane.setPrefHeight(220);

                // Ajouter la carte à la HBox
                hbox.getChildren().add(cardPane);
                // Créer une nouvelle ligne si la ligne actuelle est pleine
                if (hbox.getChildren().size() >= 3) {
                    imagesVbox.getChildren().add(hbox);
                    hbox = new HBox();
                    hbox.setSpacing(40); // Espace entre les cartes
                }
            }
            //imagesVbox.setSpacing(20);

            // Ajouter la dernière ligne si elle n'est pas pleine
            if (!hbox.getChildren().isEmpty()) {
                imagesVbox.getChildren().add(hbox);
            }
            imagesVbox.setSpacing(20);

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des images depuis la base de données : " + e.getMessage());
        }
    }




    @FXML
    void initialize() {
        MyConnection db = MyConnection.getInstance();
        conn = db.getCnx();
        //imagesVbox.setSpacing(40);

        displayAllProducts();
        try {
            // Ouvrir une connexion HTTP
            URL url = new URL(API_URL + "&count=" + NUM_IMAGES);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lire la réponse de l'API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Extraire les URL des images à partir de la réponse JSON
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imageUrl = jsonObject.getJSONObject("urls").getString("regular");

                // Charger et afficher l'image dans ImageView
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(IMAGE_WIDTH); // Fixer la largeur de l'image
                imageView.setFitHeight(IMAGE_HEIGHT); // Fixer la hauteur de l'image
                imageView.setPreserveRatio(false); // Désactiver le maintien du ratio d'aspect
                imageContainer.getChildren().add(imageView);
            }

            // Fermer la connexion
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }

