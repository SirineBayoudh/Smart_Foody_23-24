package com.example.demo.Controllers;

import com.example.demo.Models.Produit;
import com.example.demo.Models.Recette;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {

    @FXML
    private TextField ingredientField;
    @FXML
    private ListView<Produit> recommendedProductsListView;

    @FXML
    private Label resultatLabel;
    private static final String API_KEY = "00ede7fd4ca64d31aad4408625c0947b";

    @FXML
    void initialize() {
        // Récupérer l'objectif de l'utilisateur
        String objectifUtilisateur = getObjectifUtilisateur();
        String critereProduit = "sans glucose";

        // Si l'objectif de l'utilisateur est récupéré avec succès
        if (objectifUtilisateur != null) {
            // Afficher les produits recommandés en fonction de l'objectif de l'utilisateur
            fetchAndDisplayRecommendedProducts(objectifUtilisateur,critereProduit);
        } else {
            System.out.println("ss");
        }
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

    private String getObjectifUtilisateur() {
        String objectif = null;
        String query = "SELECT objectif FROM utilisateur WHERE id_utilisateur = ?";

        try (PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            int idUtilisateur = 1;
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

    private void fetchAndDisplayRecommendedProducts(String objectifUtilisateur, String critereProduit) {
        ObservableList<Produit> produitsRecommandes = FXCollections.observableArrayList();

        // Requête SQL pour récupérer les produits recommandés en fonction de l'objectif de l'utilisateur et du critère des produits
        String query = "SELECT * FROM produit WHERE id_utilisateur IN (SELECT id_utilisateur  FROM utilisateur WHERE objectif = ?)";
        // Si un critère est spécifié, ajoutez-le à la requête SQL
        if (critereProduit != null && !critereProduit.isEmpty()) {
            query += " AND critere = ?";
        }

        try (PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            // Définir le paramètre pour l'objectif de l'utilisateur
            statement.setString(1, objectifUtilisateur);
            // Si un critère est spécifié, définissez également le paramètre pour le critère
            if (critereProduit != null && !critereProduit.isEmpty()) {
                statement.setString(2, critereProduit);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Produit produit = new Produit();
                    produit.setMarque(resultSet.getString("marque"));
                    produit.setCategorie(resultSet.getString("categorie"));
                    // Ajouter le produit recommandé à la liste observable
                    produitsRecommandes.add(produit);
                }
            }
            // Afficher les produits recommandés dans une autre ListView ou tout autre composant de votre interface utilisateur
            recommendedProductsListView.setItems(produitsRecommandes);
        } catch (SQLException e) {
            // Gérer les exceptions
            e.printStackTrace();
        }
    }



}
