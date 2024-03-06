package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBotController {

    @FXML
    private TextArea chatArea;
    @FXML
    private Button chatBtn;

    @FXML
    private void BienFaitMessage() {
        sendMessage("Bienfaits d'une alimentation équilibrée ?");
    }

    @FXML
    private void sendHealthyEatingTipsMessage() {
        sendMessage("Conseils pour une alimentation saine ?");
    }

    @FXML
    private void sendFoodPortionSizesMessage() {
        sendMessage("Tailles des portions alimentaires ?");
    }

    @FXML
    private void sendProteinSourcesMessage() {
        sendMessage("Sources de protéines saines ?");
    }

    @FXML
    private void sendHelloMessage() {
        sendMessage("Délai de livraison ?");
    }

    @FXML
    private void sendPaymentModesMessage() {
        sendMessage("Modes de paiement ?");
    }

    @FXML
    private void sendCustomMessage() {
        String message = chatArea.getText();
        if (message == null || message.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le champ.");
        } else {
            sendChatMessage(message);
            chatArea.setEditable(false);
            chatBtn.setDisable(true);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void sendChatMessage(String message) {
        clear();
        // Display user message
        appendToChatArea("Moi: " + message);

        // Process user message and generate response
        String response = chatGPT(message);

        // Display bot response
        appendToChatArea("AI BOT: " + response);
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = ""; // API key goes here
        String model = "gpt-3.5-turbo"; // current model of chatgpt api

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

    private void sendMessage(String message) {
        clear();
        // Display user message
        appendToChatArea("Moi: " + message);

        // Process user message and generate response
        String response = generateResponse(message);

        // Display bot response
        appendToChatArea("BOT: " + response);
        chatArea.setEditable(false);
        chatBtn.setDisable(true);
    }

    private void appendToChatArea(String message) {
        chatArea.appendText(message + "\n");
    }

    private String generateResponse(String message) {
        if (message.equalsIgnoreCase("Délai de livraison ?")) {
            return "3 jours";
        } else if (message.equalsIgnoreCase("Modes de paiement ?")) {
            return "en ligne ou en espèce";
        } else if (message.equalsIgnoreCase("Bienfaits d'une alimentation équilibrée ?")) {
            return "Une alimentation équilibrée peut favoriser la santé globale en fournissant à l'organisme les nutriments essentiels dont il a besoin, tels que les vitamines, les minéraux, les protéines et les fibres.";
        } else if (message.equalsIgnoreCase("Conseils pour une alimentation saine ?")) {
            return "Mangez une variété de fruits, de légumes, de céréales complètes et de protéines maigres. Limitez les aliments transformés et les sucres ajoutés.";
        }
        else if (message.equalsIgnoreCase("Tailles des portions alimentaires ?")) {
            return "Les tailles des portions alimentaires varient en fonction du type d'aliment, mais il est important de surveiller les portions pour maintenir un régime équilibré.";
        } else if (message.equalsIgnoreCase("Sources de protéines saines ?")) {
            return "Les sources de protéines saines incluent les viandes maigres, les poissons, les œufs, les légumineuses, les noix et les graines.";
        }
        return "";
    }

    @FXML
    void clear() {
        chatArea.setText(null);
        chatArea.setEditable(true);
        chatBtn.setDisable(false);
    }
}
