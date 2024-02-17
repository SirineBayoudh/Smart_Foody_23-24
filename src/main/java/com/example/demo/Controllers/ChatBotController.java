package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ChatBotController {

    @FXML
    private TextArea chatArea;

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
        sendMessage("Other question");
    }

    private void sendMessage(String message) {
        // Display user message
        appendToChatArea("You: " + message);

        // Process user message and generate response
        String response = generateResponse(message);

        // Display bot response
        appendToChatArea("BOT: " + response);
    }

    private void appendToChatArea(String message) {
        chatArea.appendText(message + "\n");
    }

    private String generateResponse(String message) {
        // Simple chatbot response generation
        if (message.equalsIgnoreCase("Délai de livration ?")) {
            return "3 jours";
        } else if (message.equalsIgnoreCase("Modes de paiement ?")) {
            return "en ligne ou en espèce";
        } else {
            return "I'm sorry, please contact the administrator for additional information.";
        }
    }
}
