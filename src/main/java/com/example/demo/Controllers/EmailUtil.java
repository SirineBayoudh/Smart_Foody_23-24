package com.example.demo.Controllers;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    public static void fonctionMail(String destinataire, String sujet, String contenu, String cheminImage) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("smartfoody.2024@gmail.com", "tjds daep armh encc");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("smartfoody.2024@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject(sujet);

        // Création de la partie texte du message
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(contenu, "text/html");

        // Création de la partie image du message
        MimeBodyPart imagePart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(cheminImage);
        imagePart.setDataHandler(new DataHandler(fds));
        imagePart.setHeader("Content-ID", "<image>");

        // Création d'un Multipart pour regrouper les parties du message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(imagePart);

        // Définition du contenu multipart comme contenu du message
        message.setContent(multipart);

        // Envoi du message
        Transport.send(message);
    }
}