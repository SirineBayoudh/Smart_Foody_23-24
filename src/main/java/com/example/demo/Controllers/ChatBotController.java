package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class ChatBotController {

    @FXML
    private TextArea chatArea;

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
        clear();
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
        } else if (message.equalsIgnoreCase("Bienfaits d'une alimentation équilibrée ?")) {
            return "Une alimentation équilibrée peut favoriser la santé globale en fournissant à l'organisme les nutriments essentiels dont il a besoin, tels que les vitamines, les minéraux, les protéines et les fibres.";
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

    @FXML
    void clear() {
        chatArea.setText(null);
    }
}
