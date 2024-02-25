package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
    @FXML
    private TextField searchResultTextField;

    @FXML
    private ListView<String> suggestionsListView;
    @FXML
    private void searchProducts() {
        String searchQuery = searchResultTextField.getText();
        try {
            // Requête SQL pour récupérer les produits correspondant à la requête de recherche
            String sql = "SELECT image FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            clearProductDisplay();
            List<String> suggestions = new ArrayList<>();
            while (resultSet.next()) {
                String suggestion = resultSet.getString("marque");
                suggestions.add(suggestion);
            }

            // Afficher les produits correspondant à la recherche
            displayProducts(resultSet);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
    }
    private void displayProducts(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            // Récupérer l'URL de l'image du produit à partir du résultat de la requête
            String imageUrl = resultSet.getString("image");

            // Créer une ImageView pour afficher l'image du produit
            Image image = new Image(imageUrl);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150); // Définir la largeur de l'image
            imageView.setFitHeight(150); // Définir la hauteur de l'image

            // Ajouter l'ImageView à votre conteneur d'affichage des produits
            // Par exemple, si vous avez un VBox nommé 'productDisplayVBox':
            imagesVbox.getChildren().add(imageView);
        }
    }
    private void clearProductDisplay() {
        imagesVbox.getChildren().clear();
    }
    private List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        try {
            // Requête SQL pour récupérer les suggestions basées sur la saisie de l'utilisateur
            String sql = "SELECT marque FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + input + "%");
            ResultSet resultSet = statement.executeQuery();

            // Ajouter les suggestions à la liste
            while (resultSet.next()) {
                String suggestion = resultSet.getString("marque");
                suggestions.add(suggestion);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des suggestions : " + e.getMessage());
        }
        return suggestions;
    }
    private void displaySuggestions(List<String> suggestions) {
        // Ajouter les suggestions à la liste
        ObservableList<String> items = FXCollections.observableArrayList(suggestions);
        suggestionsListView.setItems(items);

        // Gérer la sélection d'une suggestion
        suggestionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le champ de recherche avec la suggestion sélectionnée
            searchResultTextField.setText(newValue);

            // Effectuer la recherche dans la base de données avec la suggestion sélectionnée
            searchProducts();
        });
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
        // Initialise la liste des suggestions avec une liste vide
        suggestionsListView.setItems(FXCollections.observableArrayList());
        suggestionsListView.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        // Ajoute un écouteur sur le champ de recherche pour détecter les changements de texte
        searchResultTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Exécute une recherche dans la base de données et met à jour les suggestions
                updateSuggestions(newValue);
            } else {
                // Efface la liste des suggestions si le champ de recherche est vide
                suggestionsListView.getItems().clear();
            }
        });
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
            // Initialise la liste des suggestions avec une liste vide
            suggestionsListView.setItems(FXCollections.observableArrayList());

            // Ajoute un écouteur sur le champ de recherche pour détecter les changements de texte
            searchResultTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    // Exécute une recherche dans la base de données et met à jour les suggestions
                    updateSuggestions(newValue);
                } else {
                    // Efface la liste des suggestions si le champ de recherche est vide
                    suggestionsListView.getItems().clear();
                }
            });

            // Fermer la connexion
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSuggestions(String searchQuery) {
        try {
            String sql = "SELECT marque FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            ObservableList<String> suggestions = FXCollections.observableArrayList();
            while (resultSet.next()) {
                suggestions.add(resultSet.getString("marque"));
            }
            suggestionsListView.setItems(suggestions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

