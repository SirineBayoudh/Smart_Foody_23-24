package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Controllers.UserCrud;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
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
        Utilisateur user = new Utilisateur(tnom.getText(),tprenom.getText(),genreChoisi,temail.getText(),tpwd.getText(), Role.Client.toString(),0,"",villeChoisie + ", " + tfrue.getText(),objectifChoisi);
        UserCrud usc = new UserCrud();
        usc.ajouterEntite(user);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"Client ajoutée avec succès", ButtonType.OK);
        alert.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        /*try {
            Parent root = loader.load();
            LoginController lc = loader.getController();
            lc.setTemail(temail.getText());
            lc.setTpwd(tpwd.getText());
            temail.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

}

