package com.example.demo.Controllers;

import javafx.scene.control.Alert;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
public class EmailPwd {

    public static void main(String[] args) {

        // SMTP server details
        String host = "smtp.gmail.com";
        int port = 587; // Change this to your SMTP server port
        String email = "smartfoody.2024@gmail.com"; // SMTP username
        String password = "smartfoody2324"; // SMTP password

        // Set mail properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        // Create a Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Réinitialisation de mot de passe");
            message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : http://example.com/reset");

            // Send message
            Transport.send(message);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Un e-mail de réinitialisation de mot de passe a été envoyé à votre adresse e-mail.");
            alert.showAndWait();

        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'envoi de l'e-mail de réinitialisation de mot de passe.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
