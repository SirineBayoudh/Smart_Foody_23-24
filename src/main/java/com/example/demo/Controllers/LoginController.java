package com.example.demo.Controllers;


import com.example.demo.Models.Role;
import com.example.demo.Models.Tentative;
import com.example.demo.Tools.MyConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LoginController implements Initializable {

    public TextField temail;

    public PasswordField tpwd;

    @FXML
    private TextField tpwdshow;

    public Button btnCon;

    @FXML
    private ImageView eyeClosed;

    @FXML
    private ImageView eyeOpen;

    String password;

    private int userId;

    Encryptor encryptor = new Encryptor();

    public int nbTentatives= 0;

    public Boolean bloque = false;

    public String prenom;

    public String codeEnvoye;

    ComplexiteMdp complx = new ComplexiteMdp();

    Connection cnx = MyConnection.getInstance().getCnx();
    public void login(){

        String req = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe =?";
        try{
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1,temail.getText());
            pst.setString(2,encryptor.encryptString(tpwd.getText()));
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                String reqUserId = "SELECT id_utilisateur FROM utilisateur WHERE email = ?";
                PreparedStatement pstUserId = cnx.prepareStatement(reqUserId);
                pstUserId.setString(1, temail.getText());
                ResultSet rsUserId = pstUserId.executeQuery();
                rsUserId.next();
                userId = rsUserId.getInt("id_utilisateur");
                MyConnection.getInstance().setUserId(userId);


                String reqRole = "SELECT role FROM utilisateur WHERE email = ?";
                PreparedStatement pstRole = cnx.prepareStatement(reqRole);
                pstRole.setString(1, temail.getText());
                ResultSet rsRole = pstRole.executeQuery();
                rsRole.next();
                String role = rsRole.getString("role");

                if(role.equalsIgnoreCase("Admin")){

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/dashboard.fxml"));
                    try {
                        Parent root = loader.load();

                        dashboardController dash = loader.getController();

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();

                        Stage loginStage = (Stage) temail.getScene().getWindow();
                        loginStage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    if(!bloque){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
                        try {
                            Parent root = loader.load();

                            NavbarreController nv = loader.getController();

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.show();
                            //Pour fermer la fenêtre du login
                            Stage loginStage = (Stage) temail.getScene().getWindow();
                            loginStage.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        Alert alert = new Alert(Alert.AlertType.WARNING,"Votre compte est bloqué",ButtonType.OK);
                        alert.show();
                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                        scheduler.schedule(() -> {
                            // Débloquer le compte après une minute
                            bloque = false;
                            System.out.println("Le compte est maintenant débloqué.");
                        }, 1, TimeUnit.MINUTES);
                    }

                }
            }else {

                String reqCheckEmail = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
                PreparedStatement pstEmail = cnx.prepareStatement(reqCheckEmail);
                pstEmail.setString(1, temail.getText());
                ResultSet rsCheckEmail = pstEmail.executeQuery();
                rsCheckEmail.next();

                int count = rsCheckEmail.getInt(1);
                if (count > 0) {
                    String reqUserId = "SELECT id_utilisateur FROM utilisateur WHERE email = ?";
                    PreparedStatement pstUserId = cnx.prepareStatement(reqUserId);
                    pstUserId.setString(1, temail.getText());
                    ResultSet rsUserId = pstUserId.executeQuery();
                    rsUserId.next();
                    userId = rsUserId.getInt("id_utilisateur");

                    nbTentatives++;

                    System.out.println("Id:" + userId+ " nb tentatives"+ nbTentatives);

                    Alert alert = new Alert(Alert.AlertType.WARNING,"Mot de passe erroné",ButtonType.OK);
                    alert.show();
                    if(nbTentatives == 3 ){
                        /*String requete = "UPDATE utilisateur SET tentative=? WHERE id_utilisateur=?";
                        try{
                            PreparedStatement pstTentative = cnx.prepareStatement(requete);
                            pstTentative.setInt(1, nbTentatives);
                            pstTentative.setInt(2,userId);
                            pstTentative.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }*/
                        bloque = true;
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,"Aucun compte n'est associé à cette adresse email",ButtonType.OK);
                    alert.show();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCon.setOnAction(actionEvent -> login());
        tpwdshow.setVisible(false);
        eyeOpen.setVisible(false);
    }

    @FXML
    void HidePasswordOnAction(KeyEvent event) {
        password = tpwd.getText();
        tpwdshow.setText(password);
    }

    @FXML
    void ShowPasswordOnAction(KeyEvent event) {
        password = tpwdshow.getText();
        tpwd.setText(password);
    }

    @FXML
    void Close_Eye_OnClick(MouseEvent event) {
        tpwdshow.setVisible(true);
        eyeOpen.setVisible(true);
        tpwd.setVisible(false);
        eyeClosed.setVisible(false);
    }

    @FXML
    void Open_Eye_OnClick(MouseEvent event) {
        tpwd.setVisible(true);
        eyeClosed.setVisible(true);
        tpwdshow.setVisible(false);
        eyeOpen.setVisible(false);
    }

    @FXML
    void redirectToRegister(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/register.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage loginStage = (Stage) temail.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void forgotPassword(MouseEvent event) throws SQLException {

        String subject = "Reset Password";
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Mot de passe oublié");
        emailDialog.setHeaderText("Veuillez entrer votre adresse e-mail pour réinitialiser votre mot de passe :");
        emailDialog.setContentText("Adresse e-mail :");

        emailDialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/example/demo/css/style_email.css").toExternalForm()
        );

        Optional<String> emailResult = emailDialog.showAndWait();
        if (emailResult.isPresent()) {
            String email = emailResult.get();

            String reqSend = "SELECT id_utilisateur,prenom,mot_de_passe FROM utilisateur WHERE email = ?";
            PreparedStatement psetSend = cnx.prepareStatement(reqSend);
            psetSend.setString(1, email);
            ResultSet rsSend = psetSend.executeQuery();
            if (rsSend.next()) {
                prenom = rsSend.getString("prenom");

                sendResetPasswordEmail(email, subject,prenom);

                TextInputDialog codeAndPasswordDialog = new TextInputDialog();
                codeAndPasswordDialog.setTitle("Réinitialiser le mot de passe");
                codeAndPasswordDialog.setHeaderText("Veuillez entrer le code reçu par e-mail et votre nouveau mot de passe :");
                codeAndPasswordDialog.setContentText("Code reçu par e-mail :");

                codeAndPasswordDialog.getDialogPane().getStylesheets().add(
                        getClass().getResource("/com/example/demo/css/style_email.css").toExternalForm()
                );

                TextField codeField = new TextField();
                codeField.setPromptText("code");

                PasswordField newPasswordField = new PasswordField();
                newPasswordField.setPromptText("Nouveau mot de passe");


                VBox content = new VBox();
                content.getChildren().addAll(codeField);
                content.getChildren().addAll(newPasswordField);
                codeAndPasswordDialog.getDialogPane().setContent(content);

                Optional<String> codeAndPasswordResult = codeAndPasswordDialog.showAndWait();

                if (codeAndPasswordResult.isPresent()) {
                    while (true) {
                        String code = codeField.getText();
                        String newPassword = newPasswordField.getText();

                        if (codeEnvoye.equals(code)) {

                            complx.Calcul(newPassword);

                            if (complx.getNb() < 6) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setHeaderText(null);
                                alert.setContentText("Mot de passe faible");
                                alert.showAndWait();
                                complx.setNb(0);
                            } else if (complx.getNb() >= 6 && complx.getNb() < 12) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setHeaderText(null);
                                alert.setContentText("Mot de passe moyen");
                                alert.showAndWait();
                                complx.setNb(0);
                            } else if (complx.getNb() == 12) {
                                String req = "UPDATE utilisateur SET mot_de_passe=? WHERE email=?";
                                try {
                                    PreparedStatement pst = cnx.prepareStatement(req);
                                    pst.setString(1, encryptor.encryptString(newPassword));
                                    pst.setString(2, email);
                                    pst.executeUpdate();
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }

                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Réinitialisation réussie");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Votre mot de passe a été réinitialisé avec succès !");
                                successAlert.showAndWait();

                                break;
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Réinitialisation échouée");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Le code saisi est incorrect ou la réinitialisation a échoué. Veuillez réessayer.");
                                errorAlert.showAndWait();
                            }
                        }
                        codeAndPasswordResult = codeAndPasswordDialog.showAndWait();
                        if (!codeAndPasswordResult.isPresent()) {
                            break;
                        }
                    }

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Aucun utilisateur trouvé avec cette adresse e-mail");
                alert.showAndWait();
            }
        }
    }
    private void sendResetPasswordEmail(String to, String subject,String prenom) {

        codeEnvoye = generateRandomCode();

        // SMTP server configuration
        String host = "smtp.gmail.com";
        String email = "smartfoody.2024@gmail.com";
        String password = "ssjb kizu idhi ksyn"; // SMTP password
        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        // Session object to authenticate the sender
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            String body = "<html>"
                    + "<body>"
                    + "<p style='color: #000;font-size:18px;padding-left: 20px;font-weight:bold'>Bonjour, " + prenom + "</p>"
                    + "<div style='width: 500px;padding: 20px;'>"
                    + "<h2 style='text-transform: uppercase; color: #000;'>VOUS AVEZ DEMANDÉ LA MODIFICATION DE VOTRE MOT DE PASSE</h2>"
                    + "<p style='color: #000;font-size:14px;'> Veuillez copier le code suivant :</p>"
                    + "<button style='background-color: #4CAF50; /* Green */\n" +
                    "  border: none;\n" +
                    "  color: white;\n" +
                    "  padding: 15px 32px;\n" +
                    "  text-align: center;\n" +
                    "  text-decoration: none;\n" +
                    "  display: inline-block;\n" +
                    "  font-size: 16px;'>" + codeEnvoye + "</button>"
                    + "<p style='color: #000;font-size:14px;'>Visitez notre page Facebook pour plus d'informations : "
                    + "<a href='https://www.facebook.com/smartfoody.tn' style='color: green;'>Smart Foody</a></p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomCode() {

        int length = 6;

        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random random = new Random();

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

}

