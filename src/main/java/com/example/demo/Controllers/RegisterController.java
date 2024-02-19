package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Controllers.UserCrud;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField tnom;
    @FXML
    private TextField tprenom;

    @FXML
    private TextField temail;

    @FXML
    private PasswordField tpwd;

    @FXML
    private TextField tpwdshow;

    @FXML
    private ImageView eyeClosed;

    @FXML
    private ImageView eyeOpen;

    String password;

    @FXML
    private PasswordField tpwdconfirm;

    @FXML
    private TextField tpwdshowconfirm;

    @FXML
    private ImageView eyeClosedConfrm;

    @FXML
    private ImageView eyeOpenConfirm;

    String confirmPassword;

    @FXML
    private ComboBox<String> choixGenre;

    private String[] genre = {"Homme","Femme"};

    private String genreChoisi;

    @FXML
    private TextField tftel;

    @FXML
    private ComboBox<String> choixVille;

    private String[] ville = {"Ariana","Béja","Ben Arous","Bizerte","Gabès","Gafsa","Jendouba","Kairouan","Kasserine","Kébili","Le Kef","Mahdia","La Manouba","Médenine","Monastir","Nabeul","Sfax","Sidi Bouzid","Siliana","Sousse","Tataouine","Tozeur","Tunis","Zaghouan"};

    private String villeChoisie;

    @FXML
    private TextField tfrue;

    @FXML
    private ComboBox<String> choixObjectif;

    private String[] objectif = {"Bien être","prise de poids","Perte de poids","Prise de masse musculaire"};

    private String objectifChoisi;
    @FXML
    private Button btnInscri;


    public void setTemail(String temail) {
        this.temail.setText(temail);
    }

    public void setTpwd(String tpwd) {
        this.tpwd.setText(tpwd);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tpwdshow.setVisible(false);
        eyeOpen.setVisible(false);

        tpwdshowconfirm.setVisible(false);
        eyeOpenConfirm.setVisible(false);

        choixGenre.getItems().addAll(genre);
        choixGenre.setOnAction(e -> genreChoisi = choixGenre.getValue());

        choixVille.getItems().addAll(ville);
        choixVille.setOnAction(e -> villeChoisie = choixVille.getValue());

        choixObjectif.getItems().addAll(objectif);
        choixObjectif.setOnAction(e -> objectifChoisi = choixObjectif.getValue());
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
    void HidePasswordConfirmOnAction(KeyEvent event) {
        confirmPassword = tpwdconfirm.getText();
        tpwdshowconfirm.setText(confirmPassword);
    }

    @FXML
    void ShowPasswordConfirmOnAction(KeyEvent event) {
        confirmPassword = tpwdshowconfirm.getText();
        tpwdconfirm.setText(confirmPassword);
    }

    @FXML
    void Close_Eye_Confirm_OnClick(MouseEvent event) {
        tpwdshowconfirm.setVisible(true);
        eyeOpenConfirm.setVisible(true);
        tpwdconfirm.setVisible(false);
        eyeClosedConfrm.setVisible(false);
    }

    @FXML
    void Open_Eye_Confirm_OnClick(MouseEvent event) {
        tpwdconfirm.setVisible(true);
        eyeClosedConfrm.setVisible(true);
        tpwdshowconfirm.setVisible(false);
        eyeOpenConfirm.setVisible(false);
    }

    @FXML
    void addUser(ActionEvent event) {

        //Contrôle sur les champs vides
        if (tnom.getText().isEmpty() || tprenom.getText().isEmpty() || temail.getText().isEmpty() || tpwd.getText().isEmpty() || villeChoisie == null || tftel.getText().isEmpty()  || tfrue.getText().isEmpty() || objectifChoisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        // Vérifiez si l'e-mail est au format requis ****@esprit.tn
        String emailPattern = "^[A-Za-z0-9._%+-]+@esprit\\.tn$";
        if (!temail.getText().matches(emailPattern)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format d'e-mail incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une adresse e-mail valide au format ****@esprit.tn.");
            alert.showAndWait();
            return; // Arrête l'exécution de la méthode si le format de l'e-mail est incorrect
        }

        //contrôle sur l'@ email existe déjà
        if (UserCrud.emailExists(temail.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Adresse e-mail déjà utilisée");
            alert.setHeaderText(null);
            alert.setContentText("L'adresse e-mail est déjà associée à un compte existant.");
            alert.showAndWait();
            return;
        }

        //contrôle sur le mot de passe = confirm mot de passe
        if (!tpwd.getText().equals(tpwdconfirm.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de mot de passe");
            alert.setHeaderText(null);
            alert.setContentText("Le mot de passe et la confirmation du mot de passe ne correspondent pas.");
            alert.showAndWait();
            return;
        }

        //verifier que la longuer du num = 8 et que le champ comporte que des chiffres
        try {
            int tel = Integer.parseInt(tftel.getText());
            if (tftel.getText().length() != 8) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Format de numéro de téléphone incorrect");
                alert.setHeaderText(null);
                alert.setContentText("Le numéro de téléphone doit contenir exactement 8 chiffres.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format de numéro de téléphone incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Le numéro de téléphone ne doit contenir que des chiffres.");
            alert.showAndWait();
            return;
        }

        Utilisateur user = new Utilisateur(tnom.getText(),tprenom.getText(),genreChoisi,temail.getText(),tpwd.getText(), Integer.parseInt(tftel.getText()),Role.Client.toString(),0,"",villeChoisie + ", " + tfrue.getText(),objectifChoisi);
        UserCrud usc = new UserCrud();
        usc.ajouterEntite(user);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            //Pour fermer la fenêtre du login
            Stage loginStage = (Stage) temail.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void redirectToLogin(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            //Pour fermer la fenêtre du login
            Stage loginStage = (Stage) temail.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

