package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ChatBotController {

    @FXML
    private TextArea chatArea;

    @FXML
    private void sendCaloriesInFoodMessage() {
        sendMessage("Calories dans les aliments ?");
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
        sendMessage("Délai de livration ?");
    }

    @FXML
    private void sendPaymentModesMessage() {
        sendMessage("Modes de paiement ?");
    }

    @FXML
    private void sendElseMessage() {
        sendMessage("Autre question");
    }

    private void sendMessage(String message) {
        // Display user message
        appendToChatArea("Moi: " + message);

        // Process user message and generate response
        String response = generateResponse(message);

        // Display bot response
        appendToChatArea("BOT: " + response);
    }

    private void appendToChatArea(String message) {
        chatArea.appendText(message + "\n");
    }

    private String generateResponse(String message) {
        if (message.equalsIgnoreCase("Délai de livraison ?")) {
            return "3 jours";
        } else if (message.equalsIgnoreCase("Modes de paiement ?")) {
            return "en ligne ou en espèce";
        } else if (message.equalsIgnoreCase("Calories dans les aliments ?")) {
            return "Le nombre de calories dans les aliments dépend de leur composition et de leur taille de portion.";
        } else if (message.equalsIgnoreCase("Conseils pour une alimentation saine ?")) {
            return "Mangez une variété de fruits, de légumes, de céréales complètes et de protéines maigres. Limitez les aliments transformés et les sucres ajoutés.";
        }
        else if (message.equalsIgnoreCase("Tailles des portions alimentaires ?")) {
            return "Les tailles des portions alimentaires varient en fonction du type d'aliment, mais il est important de surveiller les portions pour maintenir un régime équilibré.";
        } else if (message.equalsIgnoreCase("Sources de protéines saines ?")) {
            return "Les sources de protéines saines incluent les viandes maigres, les poissons, les œufs, les légumineuses, les noix et les graines.";
        } else {
            return "Je suis désolé, veuillez contacter l'administrateur pour obtenir des informations supplémentaires.";
        }
    }
}
