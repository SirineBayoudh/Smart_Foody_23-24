package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

public class AjoutConseilController implements Initializable {

    @FXML
    private TextArea demandeLabel;

    Connection con = null;
    PreparedStatement st = null;

    @FXML
    void ajoutConseil(ActionEvent event) {
        try {
            // Check if any of the input fields are empty
            if (demandeLabel.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the fields.");
                return; // Stop execution
            }

            String insert = "INSERT INTO conseil(id_conseil, id_client, matricule, statut, demande, reponse, date_conseil) VALUES (?, ?, ?, ?, ?, ?, ?)";
            con = MyConnection.instance.getCnx();
            st = con.prepareStatement(insert);
            st.setString(1, String.valueOf(9970023));
            st.setString(2, String.valueOf(123123123));
            st.setInt(3, 321312312);
            st.setString(4, "en attente");
            st.setString(5, demandeLabel.getText());
            st.setString(6, "");
            LocalDate currentDate = LocalDate.now();
            Date date = Date.valueOf(currentDate);
            st.setObject(7, date);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Conseil added successfully.");
                sendEmail("yassiine.studies@gmail.com", "Smart Foody : Nouvelle demande reçue", demandeLabel.getText(), currentDate.toString());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add conseil.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date format.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: Failed to add conseil.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void sendEmail(String to, String subject, String demande, String dateConseil) {
        // SMTP server configuration
        String host = "smtp.gmail.com"; // SMTP server hostname
        String username = "yassinetrabelsi110@gmail.com"; // SMTP username
        String password = "qhkh yezb ynvm jghf"; // SMTP password

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Session object to authenticate the sender
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Construct email body
            String body = "Demande: " + demande + "\n" +
                    "Date Conseil: " + dateConseil;

            message.setText(body);

            // Send the email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyConnection.getInstance();
        //noteChoiceBox.getSelectionModel().selectFirst();
    }
}
