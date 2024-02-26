package com.example.demo.Controllers;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    // Méthode pour envoyer un e-mail simple ou HTML sans image intégrée
    public static void envoyerEmail(String destinataire, String sujet, String contenu, boolean isHtml) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Utilisez vos propres identifiants
                return new PasswordAuthentication("smartfoody.2024@gmail.com", "tjds daep armh encc");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("smartfoody.2024@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject(sujet);
        if (isHtml) {
            message.setContent(contenu, "text/html; charset=utf-8");
        } else {
            message.setText(contenu);
        }

        Transport.send(message);
    }

    // Surcharge pour envoyer un e-mail avec une image intégrée
    public static void envoyerEmailAvecImageInline(String destinataire, String sujet, String htmlBody, String imagePath1, String cid1, String imagePath2, String cid2) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Remplacez par vos informations d'authentification pour le serveur SMTP
                return new PasswordAuthentication("smartfoody.2024@gmail.com", "tjds daep armh encc");
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("smartfoody.2024@gmail.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
        message.setSubject(sujet);

        // Créer le contenu multipart
        MimeMultipart multipart = new MimeMultipart("related");

        // Partie HTML du corps de l'email
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlBody, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        // Ajouter la première image inline
        messageBodyPart = new MimeBodyPart();
        DataSource fds1 = new FileDataSource(imagePath1);
        messageBodyPart.setDataHandler(new DataHandler(fds1));
        messageBodyPart.setHeader("Content-ID", "<" + cid1 + ">");
        multipart.addBodyPart(messageBodyPart);

        // Ajouter la deuxième image inline
        messageBodyPart = new MimeBodyPart();
        DataSource fds2 = new FileDataSource(imagePath2);
        messageBodyPart.setDataHandler(new DataHandler(fds2));
        messageBodyPart.setHeader("Content-ID", "<" + cid2 + ">");
        multipart.addBodyPart(messageBodyPart);

        // Définir le multipart comme contenu du message
        message.setContent(multipart);

        // Envoyer l'email
        Transport.send(message);
    }
}
