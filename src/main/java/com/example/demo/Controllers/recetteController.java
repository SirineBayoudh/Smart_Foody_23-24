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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {
    MyConnection con = null;
    PreparedStatement st = null;
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
    comparerObjectifUtilisateurAvecObjectif();

    }

    void comparerObjectifUtilisateurAvecObjectif() {
        // Requête pour sélectionner l'objectif de l'utilisateur
        String selectObjectifUtilisateur = "SELECT objectif FROM utilisateur WHERE id_utilisateur = ?";

        // Requête pour comparer l'objectif de l'utilisateur avec le libellé de la table objectif
        String comparerObjectif = "SELECT * FROM objectif WHERE libelle = ?";

        try {
            con = MyConnection.getInstance();

            // Récupérer l'objectif de l'utilisateur
            PreparedStatement stObjectifUtilisateur = con.getCnx().prepareStatement(selectObjectifUtilisateur);
            stObjectifUtilisateur.setInt(1, 1); // Remplacer 1 par l'ID de l'utilisateur
            ResultSet rsObjectifUtilisateur = stObjectifUtilisateur.executeQuery();

            if (rsObjectifUtilisateur.next()) {
                String objectifUtilisateur = rsObjectifUtilisateur.getString("objectif");
                System.out.println("Objectif de l'utilisateur : " + objectifUtilisateur);

                // Comparer l'objectif de l'utilisateur avec les libellés de la table objectif
                PreparedStatement stComparerObjectif = con.getCnx().prepareStatement(comparerObjectif);
                stComparerObjectif.setString(1, objectifUtilisateur);
                ResultSet rsComparerObjectif = stComparerObjectif.executeQuery();

                if (rsComparerObjectif.next()) {
                    // L'objectif de l'utilisateur correspond à un libellé dans la table objectif
                    String libelleObjectif = rsComparerObjectif.getString("Libelle");
                    System.out.println("L'objectif de l'utilisateur correspond au libellé : " + libelleObjectif);

                    // Maintenant, vous pouvez appeler la méthode pour afficher les produits correspondants
                    afficherProduitsAvecCriteresCommuns(objectifUtilisateur);
                } else {
                    // Aucun libellé correspondant trouvé dans la table objectif
                    System.out.println("Aucun libellé correspondant trouvé dans la table objectif.");
                }
            } else {
                // Aucun objectif trouvé pour l'utilisateur avec cet ID
                System.out.println("Aucun objectif trouvé pour cet utilisateur.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception
        }
    }

    void afficherProduitsAvecCriteresCommuns(String objectifUtilisateur) {
        // Récupérer les critères de l'objectif de l'utilisateur depuis la base de données
        String selectCriteresObjectif = "SELECT listCritere FROM objectif WHERE libelle = ?";
        List<String> critereObjectif = new ArrayList<>();

        try {
            con = MyConnection.getInstance();
            PreparedStatement stCriteresObjectif = con.getCnx().prepareStatement(selectCriteresObjectif);
            stCriteresObjectif.setString(1, objectifUtilisateur);
            ResultSet rsCriteresObjectif = stCriteresObjectif.executeQuery();

            while (rsCriteresObjectif.next()) {
                critereObjectif.add(rsCriteresObjectif.getString("listCritere"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception
        }

        // Construire la requête pour récupérer les produits correspondant aux critères de l'objectif de l'utilisateur
        String selectProduits = "SELECT * FROM produit WHERE ";
        List<String> conditions = new ArrayList<>();

        for (String critere : critereObjectif) {
            conditions.add("Critere LIKE '%" + critere + "%'");
        }

        selectProduits += String.join(" AND ", conditions);

        // Exécuter la requête pour récupérer les produits correspondants
        try {
            PreparedStatement stProduits = con.getCnx().prepareStatement(selectProduits);
            ResultSet rsProduits = stProduits.executeQuery();

            // Afficher les produits correspondants
            while (rsProduits.next()) {
                // Récupérer le nom, le prix et l'image de chaque produit
                String nomProduit = rsProduits.getString("Marque");
                double prixProduit = rsProduits.getDouble("Prix");
                String imagePath = rsProduits.getString("Image");

                // Afficher ou stocker ces informations selon vos besoins
                System.out.println("Produit trouvé - Nom: " + nomProduit + ", Prix: " + prixProduit);

                // Afficher l'image avec un bouton
                afficherImage(nomProduit, imagePath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception
        }
    }

    // Méthode pour afficher une image à partir d'un chemin d'accès
    void afficherImage(String nomProduit, String urlImage) {
        try {
            // Charger l'image
            Image image = new Image(urlImage);

            // Créer un ImageView pour afficher l'image
            ImageView imageView = new ImageView(image);

            // Créer un bouton pour afficher la recette
            Button button = new Button("Voir la recette");

            // Ajouter un événement de clic au bouton pour afficher la recette
            button.setOnAction(event -> {
                // Afficher les recettes pour ce produit
                getRecipesByIngredient(nomProduit);
            });

            // Définir la taille maximale de l'image (vous pouvez ajuster ces valeurs selon vos besoins)
            double maxWidth = 100; // Largeur maximale en pixels
            double maxHeight = 100; // Hauteur maximale en pixels

            // Redimensionner l'image si elle dépasse les dimensions maximales
            if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
                double widthRatio = image.getWidth() / maxWidth;
                double heightRatio = image.getHeight() / maxHeight;
                double scale = Math.min(widthRatio, heightRatio);
                imageView.setFitWidth(image.getWidth() / scale);
                imageView.setFitHeight(image.getHeight() / scale);
            }

            // Ajouter l'imageView et le bouton à votre interface utilisateur
            recommendedProductsVBox.getChildren().addAll(imageView, button);
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer l'exception
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



}
