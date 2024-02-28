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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {
    MyConnection con = null;
    PreparedStatement st = null;
    @FXML
    private GridPane recommendedProductsGridPane;

    ScrollPane scrollPane = new ScrollPane();



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

                // Créer une HBox pour contenir les cartes horizontalement
                HBox hboxProduits = new HBox();
                hboxProduits.setSpacing(20); // Espacement horizontal entre les cartes

                while (rsProduits.next()) {
                    // Récupérer les informations sur le produit
                    String imageUrl = rsProduits.getString("image");
                    String marque = rsProduits.getString("marque");
                    String prix = rsProduits.getString("prix");

                    // Ajouter la carte à la HBox
                    hboxProduits.getChildren().add(createProductCard(imageUrl, marque, prix));
                }

                // Créer un ScrollPane pour permettre le glissement horizontal
                ScrollPane scrollPane = new ScrollPane(hboxProduits);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Désactiver la barre de défilement horizontale

                // Définir la couleur de fond du ScrollPane
                scrollPane.setStyle("-fx-background-color: #56ab2f;");


                // Ajouter les boutons de défilement
                Button previousButton = new Button("<");
                previousButton.setStyle("-fx-background-color: #56ab2f; -fx-text-fill: white;");
                Button nextButton = new Button(">");
                nextButton.setStyle("-fx-background-color: #56ab2f; -fx-text-fill: white;");

                previousButton.setOnAction(event -> {
                    hboxProduits.setTranslateX(hboxProduits.getTranslateX() + 100); // Décalage vers la gauche
                });

                nextButton.setOnAction(event -> {
                    hboxProduits.setTranslateX(hboxProduits.getTranslateX() - 100); // Décalage vers la droite
                });

                // Ajouter les boutons à la VBox des produits recommandés
                recommendedProductsVBox.getChildren().addAll(previousButton, scrollPane, nextButton);

            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception
            }
        } else {
            System.out.println("Aucun critère trouvé pour l'objectif de l'utilisateur.");
        }
    }


    // Méthode pour créer une carte de produit avec image, marque, prix et boutons
    private StackPane createProductCard(String imageUrl, String marque, String prix) {
        // Créer une VBox pour contenir les informations du produit
        VBox productInfo = new VBox(5); // Espacement vertical entre les éléments
        productInfo.setAlignment(Pos.BOTTOM_CENTER); // Alignement central des éléments
        // Appliquer le style CSS au texte de la marque et du prix
        Label labelMarque = new Label("Marque: " + marque);
        labelMarque.setStyle("-fx-text-fill: #56ab2f;");

        Label labelPrix = new Label("Prix: " + prix);
        labelPrix.setStyle("-fx-text-fill: #56ab2f;");

// Ajouter les labels avec les styles à la VBox des informations du produit
        productInfo.getChildren().addAll(labelMarque, labelPrix);
        // Créer des boutons pour ajouter au panier et voir les détails
        Button btnAjouterPanier = new Button("Ajouter au panier");
        Button btnDetails = new Button("Détails");
        btnAjouterPanier.setStyle("-fx-background-color: #56ab2f; -fx-background-radius: 30; -fx-text-fill: white;");
        btnDetails.setStyle("-fx-background-color: #56ab2f; -fx-background-radius: 30; -fx-text-fill: white;");

        // Ajouter des actions aux boutons
        btnAjouterPanier.setOnAction(event -> {
            // Action à effectuer lors du clic sur "Ajouter au panier"
            // Par exemple, ajouter le produit au panier
        });

        btnDetails.setOnAction(event -> {
            // Action à effectuer lors du clic sur "Détails"
            // Par exemple, afficher une fenêtre modale avec les détails du produit
        });

        // Ajouter les boutons à la VBox des informations du produit
        productInfo.getChildren().addAll(btnDetails,btnAjouterPanier);


        StackPane cardPane = new StackPane();
        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitWidth(150); // Taille de l'image
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        StackPane.setAlignment(imageView, Pos.TOP_CENTER);
        cardPane.getChildren().addAll(imageView, productInfo);
        cardPane.setStyle("-fx-background-color: white; -fx-background-radius: 30; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-border-color: #56ab2f;-fx-border-radius: 30;-fx-border-width: 2px;");
        cardPane.setPrefWidth(220); // Fixer la largeur de la carte
        cardPane.setPrefHeight(300);
        return cardPane;
    }


    @FXML
    void search(ActionEvent event) {
        String ingredient = ingredientField.getText().trim();
        if (!ingredient.isEmpty()) {
            Task<List<JSONObject>> searchTask = new Task<>() {
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
                    displayRecipesWithPagination(recipes);
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
                    // Replace each dot with a dot followed by a newline
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

    // Méthode pour afficher les recettes avec pagination
    private void displayRecipesWithPagination(List<JSONObject> recipes) {
        int recipesPerPage = 1;
        int numPages = (int) Math.ceil((double) recipes.size() / recipesPerPage);
        int currentPage = 0;
        int[] currentPageHolder = new int[]{currentPage};

        Runnable displayPage = () -> {
            resultatLabel.setText(""); // Effacer le contenu précédent du label

            int startIndex = currentPageHolder[0] * recipesPerPage;
            int endIndex = Math.min(startIndex + recipesPerPage, recipes.size());

            for (int i = startIndex; i < endIndex; i++) {
                JSONObject recipe = recipes.get(i);
                String title = recipe.getString("title");
                String instructions = getCookingInstructionsForRecipe(recipe.getInt("id"));
                StringBuilder recipeText = new StringBuilder();
                recipeText.append("Title: ").append(title).append("\n").append("\n").append("\n");
                recipeText.append("Instructions: ").append(instructions);

                if (i < endIndex - 1) {
                    recipeText.append("\n");
                }

                resultatLabel.setText(resultatLabel.getText() + recipeText.toString());
            }
        };

        displayPage.run();

        Button prevButton = new Button("<");
        prevButton.setStyle("-fx-background-color: white; -fx-text-fill: #56ab2f;");
        prevButton.setOnAction(e -> {
            if (currentPageHolder[0] > 0) {
                currentPageHolder[0]--;
                displayPage.run();
            }
        });

        Button nextButton = new Button(">");
        nextButton.setStyle("-fx-background-color: white; -fx-text-fill: #56ab2f;");
        nextButton.setOnAction(e -> {
            if (currentPageHolder[0] < numPages - 1) {
                currentPageHolder[0]++;
                displayPage.run();
            }
        });

        HBox paginationBox = new HBox(10); // Espacement de 10 pixels entre les boutons

// Ajouter d'abord le bouton "Previous"
        paginationBox.getChildren().add(prevButton);

// Ajouter ensuite le bouton "Next"
        paginationBox.getChildren().add(nextButton);

// Ajouter le conteneur au contenu du label
        resultatLabel.setGraphic(paginationBox);
        //resultatLabel.setPrefWidth(600); // Définir la largeur souhaitée en pixels

    }


}