package com.example.demo.Controllers;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void envoyerEmail(String destinataire, String sujet, String contenu) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Ignorer la vérification SSL pour le serveur SMTP de Gmail - À utiliser avec prudence!
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("smartfoody.2024@gmail.com", "tjds daep armh encc"); // Remplacez par votre mot de passe d'application
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("smartfoody.2024@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject(sujet);
        message.setText(contenu);

        Transport.send(message);
    }
}
