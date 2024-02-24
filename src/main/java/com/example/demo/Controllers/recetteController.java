package com.example.demo.Controllers;

import com.example.demo.Main;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {
    MyConnection con = null;
    PreparedStatement st = null;
    @FXML
    private GridPane recommendedProductsGridPane;
    @FXML
    private VBox chosenFruitCard;

    @FXML
    private ImageView fruitImg;

    @FXML
    private Label fruitNameLabel;

    @FXML
    private Label fruitPriceLabel;

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
    private static final String API_KEY = "e701aafe487049789b44f77762dfec88";

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

            if (rsCriteresObjectif.next()) {
                String listCritere = rsCriteresObjectif.getString("listCritere");
                critereObjectif.addAll(Arrays.asList(listCritere.split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception
        }

        // Construire la requête pour sélectionner les produits correspondant à au moins un critère de l'objectif
        StringBuilder selectProduitsBuilder = new StringBuilder("SELECT * FROM produit WHERE ");
        List<String> conditions = new ArrayList<>();

        for (String critere : critereObjectif) {
            conditions.add("Critere LIKE '%" + critere.trim() + "%'");
        }

        if (!conditions.isEmpty()) {
            selectProduitsBuilder.append("(").append(String.join(" OR ", conditions)).append(")");

            try {
                PreparedStatement stProduits = con.getCnx().prepareStatement(selectProduitsBuilder.toString());
                ResultSet rsProduits = stProduits.executeQuery();

                // Créer un conteneur pour les images horizontales
                HBox hboxProduits = new HBox();
                hboxProduits.setSpacing(40);

                // Compteur pour suivre le nombre de produits ajoutés
                int produitCounter = 0;

                // Afficher les produits correspondants
                while (rsProduits.next()) {
                    // Récupérer le nom, le prix et l'image de chaque produit
                    String nomProduit = rsProduits.getString("Marque");
                    double prixProduit = rsProduits.getDouble("Prix");
                    String imagePath = rsProduits.getString("Image");

                    // Créer un ImageView pour afficher l'image
                    ImageView imageView = new ImageView(new Image(imagePath));
                    imageView.setOnMouseClicked(event -> {
                        // Récupérer les détails du produit à partir de la base de données ou de toute autre source
                        Produit produit = fetchProductDetailsFromDatabase(imagePath,con.getCnx());

                        // Afficher les détails du produit
                        setChosenFruit(produit);
                    });

                    // Définir la taille maximale de l'image
                    imageView.setFitWidth(180);
                    imageView.setFitHeight(180);

                    // Ajouter l'imageView à la HBox
                    hboxProduits.getChildren().add(imageView);

                    // Incrémenter le compteur de produits
                    produitCounter++;

                    // Si nous avons ajouté 5 produits, ajouter la HBox au VBox et réinitialiser la HBox
                    if (produitCounter == 5) {
                        recommendedProductsVBox.getChildren().add(hboxProduits);
                        hboxProduits = new HBox();
                        produitCounter = 0; // Réinitialiser le compteur de produits
                    }
                }

                // Ajouter la dernière HBox si elle contient moins de 5 produits
                if (!hboxProduits.getChildren().isEmpty()) {
                    recommendedProductsVBox.getChildren().add(hboxProduits);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception
            }
        } else {
            System.out.println("Aucun critère trouvé pour l'objectif de l'utilisateur.");
        }
    }




    // Méthode pour afficher une image à partir d'un chemin d'accès
    void afficherImage(String urlImage) {
        try {
            // Charger l'image
            Image image = new Image(urlImage);

            // Créer un ImageView pour afficher l'image
            ImageView imageView = new ImageView(image);
            Button button = new Button("Voir la recette");

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

            // Ajouter un événement de clic à l'imageView pour afficher la recette du produit
            imageView.setOnMouseClicked(event -> {
                // Récupérer le nom du produit à partir de l'URL ou de toute autre source si nécessaire
                String nomProduit = "Nom du produit";

                // Afficher les recettes pour ce produit
                getRecipesByIngredient(nomProduit);
            });

            // Ajouter l'imageView à votre interface utilisateur
            recommendedProductsVBox.getChildren().add(imageView);
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
            for (int i = 0; i < 4; i++) {
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
                    // Remplacer chaque point par un point suivi d'un retour à la ligne
                    stepInstruction = stepInstruction.replaceAll("\\.", ".\n");
                    instructions.append(stepInstruction).append("\n");
                    if (j == 0) {
                        instructions.append("**").append(stepInstruction).append("\n");
                    } else {
                        instructions.append(stepInstruction).append("\n");
                    }
                }
            }
            return instructions.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Instructions not available";
        }
    }

    private void setChosenFruit(Produit produit) {
        if (produit != null && produit.getImage() != null) {
            fruitNameLabel.setText(produit.getMarque());
            fruitPriceLabel.setText(produit.getPrix() + Main.CURRENCY);
            // Vérifier si l'URL de l'image n'est pas null avant de créer l'objet Image
            if (produit.getImage() != null) {
                javafx.scene.image.Image image = new Image(produit.getImage(), 200, 200, true, true);
                fruitImg.setImage(image);
                chosenFruitCard.setStyle("   -fx-background-radius: 30;");
            } else {
                System.out.println("L'URL de l'image est null.");
            }
        } else {
            System.out.println("Le produit est null ou l'URL de l'image est null.");
        }
    }
    private Produit fetchProductDetailsFromDatabase(String imageUrl, Connection connection) {
        Produit produit = null;
        try {
            String sql = "SELECT * FROM produit WHERE image=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, imageUrl);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String marque = resultSet.getString("marque");
                float prix = resultSet.getFloat("prix");

                produit = new Produit(marque, prix, imageUrl);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return produit;
    }





}