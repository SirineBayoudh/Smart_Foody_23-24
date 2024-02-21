package com.example.demo.Controllers;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static void main(String[] args) {

        // SMTP server details
        String host = "smtp.gmail.com";
        int port = 587; // Change this to your SMTP server port
        String username = "smartfoody.2024@gmail.com"; // SMTP username
        String password = "apsp ytkl paob kzge"; // SMTP password

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
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress("your_email@example.com"));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("recipient@example.com"));

            // Set Subject: header field
            message.setSubject("Testing JavaMail API");

            // Set the actual message
            message.setText("This is a test email sent using JavaMail API.");

            // Send message
            Transport.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}

