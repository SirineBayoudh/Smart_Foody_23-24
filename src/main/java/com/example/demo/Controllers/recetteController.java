package com.example.demo.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class recetteController {

    @FXML
    private TextField ingredientField;

    @FXML
    private Label resultatLabel;

    @FXML
    private Button searchButton;
    private static final String API_KEY = "00ede7fd4ca64d31aad4408625c0947b";

    @FXML
    void search(ActionEvent event) {
        String ingredient = ingredientField.getText().trim();
        if (!ingredient.isEmpty()) {
            Task<List<String>> searchTask = new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    return getRecipesByIngredient(ingredient);
                }
            };
            searchTask.setOnSucceeded(e -> {
                List<String> recipes = searchTask.getValue();
                if (recipes.isEmpty()) {
                    resultatLabel.setText("No recipes found for this ingredient.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String recipe : recipes) {
                        sb.append(recipe).append("\n");
                    }
                    resultatLabel.setText(sb.toString());
                }
            });
            new Thread(searchTask).start();
        } else {
            resultatLabel.setText("Please enter an ingredient.");
        }
    }

    private List<String> getRecipesByIngredient(String ingredient) {
        List<String> recipes = new ArrayList<>();
        try {
            String apiKey = "00ede7fd4ca64d31aad4408625c0947b";
            URL url = new URL("https://api.spoonacular.com/recipes/findByIngredients?ingredients=" +
                    URLEncoder.encode(ingredient, "UTF-8") + "&apiKey=" + apiKey);

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
                String title = recipe.getString("title");
                recipes.add(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
    }
}
