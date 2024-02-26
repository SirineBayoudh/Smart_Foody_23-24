package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfilConseillerController implements Initializable {

    @FXML
    private BorderPane centerPane;

    @FXML
    private AnchorPane infosForm;

    @FXML
    private AnchorPane pwdForm;

    @FXML
    private Label infos;

    @FXML
    private Label infosmdp;

    @FXML
    private Label prenomUser;

    @FXML
    private TextField tfnomp;

    @FXML
    private TextField tfprenomp;

    @FXML
    private TextField tfemailp;


    @FXML
    private ComboBox<String> choixGenrep;

    private String[] genre = {"Homme","Femme"};

    private String genreChoisi;

    @FXML
    private TextField tfnumtelp;


    @FXML
    private Button btn_modif;

    @FXML
    private ImageView logoutIcon;

    private int idUtilisateurConnecte;

    @FXML
    private PasswordField ancienMDP;

    @FXML
    private TextField ancienMDPshow;

    String ancienPwd;

    @FXML
    private ImageView eyeClosedA;

    @FXML
    private ImageView eyeOpenA;

    @FXML
    private PasswordField nouveauMDP;

    @FXML
    private TextField nouveauMDPshow;

    String nouveauPwd;

    @FXML
    private ImageView eyeClosedN;

    @FXML
    private ImageView eyeOpenN;

    @FXML
    private Button modifierMDP;

    @FXML
    private TextField tfmatricule;

    @FXML
    private TextField tfattestation;

    Encryptor encryptor = new Encryptor();
    ComplexiteMdp complx = new ComplexiteMdp();
    Connection cnx = MyConnection.getInstance().getCnx();
    @FXML
    void infosUser(MouseEvent event) {
        infosForm.setVisible(true);
        pwdForm.setVisible(false);
    }

    @FXML
    void mdpUser(MouseEvent event) {
        pwdForm.setVisible(true);
        infosForm.setVisible(false);
    }

    @FXML
    void HidePasswordOnActionA(KeyEvent event) {
        ancienPwd = ancienMDP.getText();
        ancienMDPshow.setText(ancienPwd);
    }

    @FXML
    void ShowPasswordOnActionA(KeyEvent event) {
        ancienPwd = ancienMDPshow.getText();
        ancienMDP.setText(ancienPwd);
    }

    @FXML
    void Close_Eye_OnClickA(MouseEvent event) {
        ancienMDPshow.setVisible(true);
        eyeOpenA.setVisible(true);
        ancienMDP.setVisible(false);
        eyeClosedA.setVisible(false);
    }

    @FXML
    void Open_Eye_OnClickA(MouseEvent event) {
        ancienMDP.setVisible(true);
        eyeClosedA.setVisible(true);
        ancienMDPshow.setVisible(false);
        eyeOpenA.setVisible(false);
    }

    @FXML
    void HidePasswordOnActionN(KeyEvent event) {
        nouveauPwd = nouveauMDP.getText();
        nouveauMDPshow.setText(nouveauPwd);
    }

    @FXML
    void ShowPasswordOnActionN(KeyEvent event) {
        nouveauPwd = nouveauMDPshow.getText();
        nouveauMDP.setText(nouveauPwd);
    }

    @FXML
    void Close_Eye_OnClickN(MouseEvent event) {
        nouveauMDPshow.setVisible(true);
        eyeOpenN.setVisible(true);
        nouveauMDP.setVisible(false);
        eyeClosedN.setVisible(false);
    }

    @FXML
    void Open_Eye_OnClickN(MouseEvent event) {
        nouveauMDP.setVisible(true);
        eyeClosedN.setVisible(true);
        nouveauMDPshow.setVisible(false);
        eyeOpenN.setVisible(false);
    }

    @FXML
    void choisirAttestationOnClick(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier d'attestation");

        // filtre pour les types de fichiers
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Afficher la boîte de dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            tfattestation.setText(selectedFile.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choixGenrep.getItems().addAll(genre);
        choixGenrep.setOnAction(e -> genreChoisi = choixGenrep.getValue());

        idUtilisateurConnecte = MyConnection.getInstance().getUserId();
        remplirChampsUtilisateur(idUtilisateurConnecte);

        tfprenomp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                tfmatricule.setText("CNS-" + newValue);
            } else {
                tfmatricule.clear();
            }
        });

        infosForm.setVisible(true);
        pwdForm.setVisible(false);

        ancienMDPshow.setVisible(false);
        eyeOpenA.setVisible(false);

        nouveauMDPshow.setVisible(false);
        eyeOpenN.setVisible(false);
    }


    public void remplirChampsUtilisateur(int idUtilisateur) {
        Connection cnx = MyConnection.getInstance().getCnx();
        String req = "SELECT * FROM utilisateur WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, idUtilisateur);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                prenomUser.setText(rs.getString(3));
                tfnomp.setText(rs.getString(2));
                tfprenomp.setText(rs.getString(3));
                choixGenrep.setValue(rs.getString(4));
                tfemailp.setText(rs.getString(5));
                tfnumtelp.setText(rs.getString(7));
                tfmatricule.setText(rs.getString(9));
                tfattestation.setText(rs.getString(10));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void modifierProfil(ActionEvent event) {

        if (tfnomp.getText().isEmpty() || tfprenomp.getText().isEmpty() || tfemailp.getText().isEmpty() || choixGenrep.getValue().isEmpty() || tfnumtelp.getText().isEmpty()  || tfmatricule.getText().isEmpty() || tfattestation.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        String emailPattern = "^.+@.+$";
        if (!tfemailp.getText().matches(emailPattern)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format d'e-mail incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une adresse e-mail valide au format ****@esprit.tn.");
            alert.showAndWait();
            return; // Arrête l'exécution de la méthode si le format de l'e-mail est incorrect
        }

        try {
            int tel = Integer.parseInt(tfnumtelp.getText());
            if (tfnumtelp.getText().length() != 8) {
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

        Connection cnx = MyConnection.getInstance().getCnx();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de modification");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir modifier votre profil ?");

        ButtonType buttonTypeOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOui) {
            String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,num_tel=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
            try {
                PreparedStatement pst = cnx.prepareStatement(req);
                pst.setString(1, tfnomp.getText());
                pst.setString(2,tfprenomp.getText());
                pst.setString(3, choixGenrep.getValue());
                pst.setString(4, tfemailp.getText());
                pst.setInt(5,Integer.parseInt(tfnumtelp.getText()));
                pst.setString(6, Role.Conseiller.toString());
                pst.setString(7,tfmatricule.getText());
                pst.setString(8,tfattestation.getText());
                pst.setString(9,"");
                pst.setString(10, "");
                pst.setInt(11, idUtilisateurConnecte);
                pst.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert2.setTitle("Modification réussie");
        alert2.setHeaderText(null);
        alert2.setContentText("Vos informations ont été mises à jour avec succès.");
        alert2.showAndWait();

    }

    @FXML
    void modifierMDP(ActionEvent event) throws SQLException, NoSuchAlgorithmException {

        String reqMDP = "SELECT mot_de_passe FROM utilisateur WHERE id_utilisateur = ?";
        PreparedStatement pstMDP = cnx.prepareStatement(reqMDP);
        pstMDP.setString(1, String.valueOf(MyConnection.getInstance().getUserId()));
        ResultSet rsMDP = pstMDP.executeQuery();
        rsMDP.next();
        String mdp = rsMDP.getString("mot_de_passe");

        // Comparer l'ancien mot de passe saisi avec le mot de passe actuel de l'utilisateur
        if (!encryptor.encryptString(ancienMDP.getText()).equals(mdp)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mot de passe incorrect");
            alert.setHeaderText(null);
            alert.setContentText("L'ancien mot de passe saisi est incorrect.");
            alert.showAndWait();
            return;
        }

        if (ancienMDP.getText().isEmpty() || nouveauMDP.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        complx.Calcul(nouveauMDP.getText());

        if(complx.getNb() <6){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Mot de passe faible");
            alert.showAndWait();
            complx.setNb(0);
        }else if (complx.getNb() >= 6 && complx.getNb() < 12){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Mot de passe moyen");
            alert.showAndWait();
            complx.setNb(0);
        } else if (complx.getNb() == 12){

            Connection cnx = MyConnection.getInstance().getCnx();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de modification");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir modifier votre profil ?");

            ButtonType buttonTypeOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeOui) {
                String req = "UPDATE utilisateur SET mot_de_passe=? WHERE id_utilisateur=?";
                try {
                    PreparedStatement pst = cnx.prepareStatement(req);
                    pst.setString(1, encryptor.encryptString(nouveauMDP.getText()));
                    pst.setInt(2, idUtilisateurConnecte);
                    pst.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }

            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Modification réussie");
            alert2.setHeaderText(null);
            alert2.setContentText("Vos informations ont été mises à jour avec succès.");
            alert2.showAndWait();
        }



    }

    @FXML
    void choisirAttestation(MouseEvent event) {

    }
    @FXML
    void logout(MouseEvent event) {

        MyConnection.getInstance().setUserId(0);

        // Redirection vers l'écran de connexion ou autre écran d'accueil
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            // Fermeture des autres fenêtres ouvertes, si nécessaire
            Stage currentStage = (Stage) logoutIcon.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
