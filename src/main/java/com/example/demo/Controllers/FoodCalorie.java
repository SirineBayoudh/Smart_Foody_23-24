package com.example.demo.Controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodCalorie {

    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition?query=";
    private static final String API_KEY = "";

    public String getFoodCalories(String foodName) throws IOException {

        try {
            URL url = new URL(API_URL + foodName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", API_KEY);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response to get calorie information
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();
                JsonArray foods = jsonResponse.getAsJsonArray("items");

                if (foods.size() > 0) {
                    JsonObject food = foods.get(0).getAsJsonObject();
                    JsonElement name = food.get("name");
                    JsonElement calories = food.get("calories");

                    return "Aliment: " + name.getAsString() + ", Calories: " + calories.getAsString();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune information calorique trouvée pour : "+ foodName);
                    return "";
                }
            } else {
                System.out.println("Failed to get calorie information. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
