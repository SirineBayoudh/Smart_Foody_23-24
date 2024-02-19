package com.example.demo.Controllers;

import com.example.demo.Models.Conseil;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

public class AjoutConseilController implements Initializable {

    @FXML
    private TextArea demandeLabel;
    @FXML
    private TextArea tDemande;
    @FXML
    private TextArea tNote;
    int id_conseil=0;
    @FXML
    private TableColumn<Conseil, Integer> colidconseil;
    @FXML
    private TableColumn<Conseil, java.util.Date> coldate;
    @FXML
    private TableColumn<Conseil, String> coldemande;
    @FXML
    private TableColumn<Conseil, Integer> colnote;
    @FXML
    private TableColumn<Conseil, String> colreponse;
    @FXML
    private TableColumn<Conseil, String> colstatut;
    @FXML
    private TableView<Conseil> table;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyConnection.getInstance();
        showConseils();
        // capturing id_conseil of selected item in the table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                id_conseil = newSelection.getId_conseil();
            }
        });
    }

    @FXML
    void ajoutConseil(ActionEvent event) {
        try {
            // Check if any of the input fields are empty
            if (demandeLabel.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tout les champs.");
                return; // Stop execution
            }
            String insert = "INSERT INTO conseil(id_conseil, id_client, matricule, statut, demande, reponse, date_conseil) VALUES (?, ?, ?, ?, ?, ?, ?)";
            con = MyConnection.instance.getCnx();
            st = con.prepareStatement(insert);
            st.setString(1, String.valueOf(6776));
            st.setString(2, String.valueOf(123123123));
            st.setInt(3, 321312312);
            st.setString(4, "en attente");
            st.setString(5, demandeLabel.getText());
            st.setString(6, "");
            LocalDate currentDate = LocalDate.now();
            Date date = Date.valueOf(currentDate);
            st.setObject(7, date);
            int rowsAffected = st.executeUpdate();
            showConseils();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Conseil ajouté avec succès.");
                sendEmail("yassiine.studies@gmail.com", "Smart Foody : Nouvelle demande reçue", demandeLabel.getText(), currentDate.toString());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout du conseil.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Entrée invalide.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date invalide.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : échec de l'ajout de conseil.");
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

    public ObservableList<Conseil> getConseils(){
        ObservableList<Conseil> conseils = FXCollections.observableArrayList();
        String query="select * from conseil WHERE id_client='123123123'";
        con = MyConnection.instance.getCnx();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while(rs.next()){
                Conseil st = new Conseil();
                st.setId_conseil(rs.getInt("id_conseil"));
                st.setStatut(rs.getString("statut"));
                st.setDemande(rs.getString("demande"));
                st.setReponse(rs.getString("reponse"));
                st.setNote(rs.getInt("note"));
                st.setDate_conseil(rs.getDate("date_conseil"));
                conseils.add(st)
                ;            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conseils;
    }

    public void showConseils(){
        ObservableList<Conseil> list = getConseils();
        table.setItems(list);
        colidconseil.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("id_conseil"));
        colstatut.setCellValueFactory(new PropertyValueFactory<Conseil,String>("statut"));
        coldemande.setCellValueFactory(new PropertyValueFactory<Conseil,String>("demande"));
        colreponse.setCellValueFactory(new PropertyValueFactory<Conseil,String>("reponse"));
        colnote.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("note"));
        coldate.setCellValueFactory(new PropertyValueFactory<Conseil, java.util.Date>("date_conseil"));
    }

    @FXML
    void getData(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Conseil selectedConseil = table.getSelectionModel().getSelectedItem();
            if (selectedConseil != null) {
                id_conseil = selectedConseil.getId_conseil();
                tDemande.setText(String.valueOf(selectedConseil.getDemande()));
                tNote.setText(String.valueOf(selectedConseil.getNote()));
            }
        }
        if (event.getClickCount() == 2) {
            Conseil selectedConseil = table.getSelectionModel().getSelectedItem();
            if (selectedConseil != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/DetailConseilWindow.fxml"));
                    Parent root = loader.load();
                    DetailWindowController controller = loader.getController();
                    controller.initData(selectedConseil);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void openChatBotWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ChatBot.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ChatBot");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateConseil(ActionEvent event) {
        try {
            if (tNote.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le champ.");
                return;
            }
            int noteValue = Integer.parseInt(tNote.getText());
            if (noteValue < 1 || noteValue > 5) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir une note entre 1 et 5.");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir mettre à jour ce conseil?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String update = "UPDATE conseil SET note = ? WHERE id_conseil = ?";
                    con = MyConnection.instance.getCnx();
                    try {
                        st = con.prepareStatement(update);
                        st.setString(1, tNote.getText());
                        st.setInt(2, id_conseil);
                        int rowsAffected = st.executeUpdate();
                        showConseils();
                        if (rowsAffected > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Conseil mis à jour avec succès.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du conseil.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("SQLException occurred: " + e.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : Échec de la mise à jour du conseil.");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est apparue.");
        }
    }
}
