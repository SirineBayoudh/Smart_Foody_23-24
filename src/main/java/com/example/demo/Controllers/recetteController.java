package com.example.demo.Controllers;

import com.example.demo.Models.Produit;
import com.example.demo.Models.Recette;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {
    @FXML
    private GridPane recommendedProductsGridPane;

    @FXML
    private TextField ingredientField;

    @FXML
    private ScrollPane recommendedProductsScrollPane;
    int column=0;
    int row=0;

    @FXML
    private VBox recommendedProductsVBox;


    @FXML
    private Label resultatLabel;
    private static final String API_KEY = "00ede7fd4ca64d31aad4408625c0947b";

    @FXML
    void initialize() {
        /*String objectifUtilisateur = getObjectifUtilisateur();
        if (objectifUtilisateur != null) {
            fetchAndDisplayRecommendedProducts(objectifUtilisateur);
        } else {
            System.out.println("Impossible de récupérer l'objectif de l'utilisateur.");
        }*/
       /* int userId = 1; // Remplacer 1 par l'ID de l'utilisateur actuellement connecté
        String userGoal = getCurrentUserGoal(userId);
        if (userGoal != null) {
            showRecommendedProducts(userGoal);
        } else {
            System.out.println("Impossible de récupérer l'objectif de l'utilisateur.");
        }*/
    }


    @FXML
    void search(ActionEvent event) {
        String ingredient = ingredientField.getText().trim();
        if (!ingredient.isEmpty()) {
            Task<List<JSONObject>> searchTask = new Task<List<JSONObject>>() {
                @Override
                protected List<JSONObject> call() throws Exception {
                    return getRecipesByIngredient(ingredient);
                }
            };
            searchTask.setOnSucceeded(e -> {
                List<JSONObject> recipes = searchTask.getValue();
                if (recipes.isEmpty()) {
                    resultatLabel.setText("No recipes found for this ingredient.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (JSONObject recipe : recipes) {
                        String title = recipe.getString("title");
                        String instructions = getCookingInstructionsForRecipe(recipe.getInt("id")); // Passer l'identifiant de la recette
                        sb.append("Title: ").append(title).append("\n");
                        sb.append("Instructions: ").append(instructions).append("\n");
                        sb.append("\n");
                    }
                    resultatLabel.setText(sb.toString());
                }
            });
            new Thread(searchTask).start();
        } else {
            resultatLabel.setText("Please enter an ingredient.");
        }
    }

    private List<JSONObject> getRecipesByIngredient(String ingredient) {
        List<JSONObject> recipes = new ArrayList<>();
        try {
            URL url = new URL("https://api.spoonacular.com/recipes/findByIngredients?ingredients=" +
                    URLEncoder.encode(ingredient, "UTF-8") + "&number=5&apiKey=" + API_KEY);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recipe = jsonArray.getJSONObject(i);
                recipes.add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
    }


    private String getCookingInstructionsForRecipe(int recipeId) {
        try {
            URL url = new URL("https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions?apiKey=" + API_KEY);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            StringBuilder instructions = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject step = jsonArray.getJSONObject(i);
                JSONArray steps = step.getJSONArray("steps");
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject stepDetail = steps.getJSONObject(j);
                    String stepInstruction = stepDetail.getString("step");
                    instructions.append(stepInstruction).append("\n");
                }
            }
            return instructions.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Instructions not available";
        }
    }

    /*private String getObjectifUtilisateur() {
        String objectif = null;
        String query = "SELECT objectif FROM utilisateur WHERE id_utilisateur = ?";
        try (PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            int idUtilisateur = 1; // Remplacer 1 par l'ID de l'utilisateur connecté
            statement.setInt(1, idUtilisateur);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    objectif = resultSet.getString("objectif");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return objectif;
    }

    private void fetchAndDisplayRecommendedProducts(String objectifUtilisateur) {
        List<Produit> produitsRecommandes = getRecommendedProducts(objectifUtilisateur);
        if (produitsRecommandes != null && !produitsRecommandes.isEmpty()) {
            for (Produit produit : produitsRecommandes) {
                String imageUrl = produit.getImage();
                System.out.println("Image URL from database: " + imageUrl); // Vérifier l'URL récupérée depuis la base de données
                ImageView imageView = createProductImageView(imageUrl);
                if (imageView != null) {

                    recommendedProductsGridPane.add(imageView, column, row);
                    column++;
                    if (column == 2) {
                        column = 0;
                        row++;
                    }
                } else {
                    System.out.println("ImageView is null for image URL: " + imageUrl);
                }
            }
        } else {
            resultatLabel.setText("Aucun produit recommandé trouvé.");
        }
    }

    private ImageView createProductImageView(String imageUrl) {
        ImageView imageView = new ImageView();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl);
                imageView.setImage(image);
                // Ajuster la taille de l'image
                imageView.setFitWidth(100); // Largeur souhaitée
                imageView.setFitHeight(100); // Hauteur souhaitée
                // Ajouter d'autres propriétés à l'ImageView si nécessaire
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image à partir de l'URL : " + imageUrl);
                e.printStackTrace();
            }
        } else {
            System.err.println("URL de l'image incorrecte : " + imageUrl);
        }
        return imageView;
    }*/

    /*private List<Produit> getRecommendedProducts(String objectifUtilisateur) {
        List<Produit> produitsRecommandes = new ArrayList<>();

        try {
            // Sélectionner tous les produits dont l'objectif correspond à celui de l'utilisateur
            String sql = "SELECT * FROM produit WHERE objectif = ?";
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(sql);
            statement.setString(1, objectifUtilisateur);
            System.out.println("ss");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String marque = resultSet.getString("Marque");
                String categorie = resultSet.getString("Categorie");
                String imageUrl = resultSet.getString("Image");

                Produit produit = new Produit(marque, categorie, imageUrl);
                produitsRecommandes.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // À améliorer pour une gestion plus robuste des exceptions
        }

        return produitsRecommandes;
    }*/

    /*private String getCurrentUserGoal(int userId) {
        String userGoal = null;
        String query = "SELECT Objectif FROM Utilisateur WHERE Id = ?";
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userGoal = resultSet.getString("Objectif");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userGoal;
    }
    void showRecommendedProducts() {
        String userGoal = getCurrentUserGoal(); // Obtenez l'objectif de l'utilisateur actuellement connecté

        ObservableList<Produit> recommendedProducts = FXCollections.observableArrayList();
        for (Produit produit : getRecommendedProducts()) {
            if (produit.getObjectif().equals(userGoal)) { // Comparer l'objectif du produit avec l'objectif de l'utilisateur
                recommendedProducts.add(produit);
            }
        }

        recommendedProductsGridPane.getChildren().clear(); // Effacer les anciens produits recommandés
        int row = 0;
        int col = 0;
        for (Produit produit : recommendedProducts) {
            Label nameLabel = new Label(produit.getMarque());
            Label goalLabel = new Label(produit.getObjectif());

            recommendedProductsGridPane.add(nameLabel, col, row);
            recommendedProductsGridPane.add(goalLabel, col + 1, row);

            // Augmenter la ligne ou la colonne pour afficher les produits suivants
            if (col == 1) {
                col = 0;
                row++;
            } else {
                col++;
            }
        }
    }*/

    private String getCurrentUserGoal(int userId) {
        String userGoal = null;
        String query = "SELECT Objectif FROM Utilisateur WHERE Id = ?";
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userGoal = resultSet.getString("Objectif");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userGoal;
    }

    /*void showRecommendedProducts(String userGoal) {
        ObservableList<Produit> recommendedProducts = FXCollections.observableArrayList();
        for (Produit produit : getRecommendedProducts()) {
            if (produit.getObjectif().equals(userGoal)) { // Comparer l'objectif du produit avec l'objectif de l'utilisateur
                recommendedProducts.add(produit);
            }
        }*/

       /* recommendedProductsGridPane.getChildren().clear(); // Effacer les anciens produits recommandés
        int row = 0;
        int col = 0;
        for (Produit produit : recommendedProducts) {
            Label nameLabel = new Label(produit.getMarque());
            Label goalLabel = new Label(produit.getObjectif());

            recommendedProductsGridPane.add(nameLabel, col, row);
            recommendedProductsGridPane.add(goalLabel, col + 1, row);

            // Augmenter la ligne ou la colonne pour afficher les produits suivants
            if (col == 1) {
                col = 0;
                row++;
            } else {
                col++;
            }
        }
    }

    // Cette méthode doit être implémentée pour récupérer les produits recommandés depuis la base de données
    private ObservableList<Produit> getRecommendedProducts() {
        // Implémenter la logique pour récupérer les produits recommandés depuis la base de données
        return FXCollections.observableArrayList();
    }*/
}
