package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterConseillerController implements Initializable {

    @FXML
    private TextField tfnomc;

    @FXML
    private TextField tfprenomc;

    @FXML
    private TextField tfemailc;

    @FXML
    private PasswordField tfmdpc;

    @FXML
    private ComboBox<String> choixGenrec;

    private String[] genre = {"Homme","Femme"};

    private String genreChoisi;

    @FXML
    private TextField tfnumtelc;

    @FXML
    private TextField tfmatricule;

    @FXML
    private TextField tfattestation;

    @FXML
    private Button btn_ajoutConseiller;

    @FXML
    private Button btn_refresh;

    private GestionUserController gestionUserController;

    public void setGestionUserController(GestionUserController gestionUserController) {
        this.gestionUserController = gestionUserController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choixGenrec.getItems().addAll(genre);
        choixGenrec.setOnAction(e -> genreChoisi = choixGenrec.getValue());

    }

    Encryptor encryptor = new Encryptor();
    @FXML
    void addConseiller(ActionEvent event) throws NoSuchAlgorithmException {

        if (tfnomc.getText().isEmpty() || tfprenomc.getText().isEmpty() || tfemailc.getText().isEmpty() || tfmdpc.getText().isEmpty() || genreChoisi == null || tfnumtelc.getText().isEmpty()  || tfmatricule.getText().isEmpty() || tfattestation.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        String emailPattern = "^.+@.+$";
        if (!tfemailc.getText().matches(emailPattern)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format d'e-mail incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une adresse e-mail valide.");
            alert.showAndWait();
            return;
        }

        //contrôle sur l'@ email existe déjà
        if (UserCrud.emailExists(tfemailc.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Adresse e-mail déjà utilisée");
            alert.setHeaderText(null);
            alert.setContentText("L'adresse e-mail est déjà associée à un compte existant.");
            alert.showAndWait();
            return;
        }

        try {
            int tel = Integer.parseInt(tfnumtelc.getText());
            if (tfnumtelc.getText().length() != 8) {
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

        try {
            int matricule = Integer.parseInt(tfmatricule.getText());
            if (tfmatricule.getText().length() != 6) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Format de matricule incorrect");
                alert.setHeaderText(null);
                alert.setContentText("Le champ matricule doit contenir exactement 6 chiffres.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format de matricule incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Le champ matricule ne doit contenir que des chiffres.");
            alert.showAndWait();
            return;
        }
        Connection cnx = MyConnection.getInstance().getCnx();

        Utilisateur u = new Utilisateur(tfnomc.getText(),tfprenomc.getText(),genreChoisi,tfemailc.getText(),encryptor.encryptString(tfmdpc.getText()), Integer.parseInt(tfnumtelc.getText()), Role.Conseiller.toString(),Integer.parseInt(tfmatricule.getText()),tfattestation.getText(),"","");
        String requete = "INSERT INTO utilisateur(nom,prenom,genre,email,mot_de_passe,num_tel,role,matricule,attestation,adresse,objectif,tentative) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setString(1, u.getNom());
            pst.setString(2,u.getPrenom());
            pst.setString(3, u.getGenre());
            pst.setString(4, u.getEmail());
            pst.setString(5,u.getMot_de_passe());
            pst.setInt(6,u.getNum_tel());
            pst.setString(7,u.getRole());
            pst.setInt(8,u.getMatricule());
            pst.setString(9,u.getAttestation());
            pst.setString(10,u.getAdresse());
            pst.setString(11, u.getObjectif());
            pst.setInt(12,u.getTentative());
            pst.executeUpdate();

            gestionUserController.afficherUtilisateurs();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conseiller ajouté");
        alert.setHeaderText(null);
        alert.setContentText("Conseiller ajouté avec succès");
        alert.showAndWait();

        Stage loginStage = (Stage) tfemailc.getScene().getWindow();
        loginStage.close();

    }

    @FXML
    private void refreshForm() {
        tfnomc.clear();
        tfprenomc.clear();
        tfemailc.clear();
        tfmdpc.clear();
        choixGenrec.getSelectionModel().clearSelection();
        tfnumtelc.clear();
        tfmatricule.clear();
        tfattestation.clear();
    }
}
