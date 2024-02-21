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

public class ModifierConseillerController implements Initializable {

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
    private Button btn_modifierConseiller;

    @FXML
    private Button btn_refresh;

    private Utilisateur u;

    private int id;

    Encryptor encryptor = new Encryptor();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choixGenrec.getItems().addAll(genre);
        choixGenrec.setOnAction(e -> genreChoisi = choixGenrec.getValue());

    }

    public void initData(Utilisateur u) {
        this.u = u;
        remplirChamps(u);
    }

    @FXML
    void updateConseiller(ActionEvent event) {

        id = GestionUserController.getId();

        if (tfnomc.getText().isEmpty() || tfprenomc.getText().isEmpty() || tfemailc.getText().isEmpty() || tfmdpc.getText().isEmpty() || choixGenrec.getValue().isEmpty() || tfnumtelc.getText().isEmpty()  || tfmatricule.getText().isEmpty() || tfattestation.getText().isEmpty()) {
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
            alert.setContentText("Veuillez saisir une adresse e-mail valide au format ****@esprit.tn.");
            alert.showAndWait();
            return; // Arrête l'exécution de la méthode si le format de l'e-mail est incorrect
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

        Connection  cnx = MyConnection.getInstance().getCnx();

        String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,mot_de_passe=?,num_tel=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, tfnomc.getText());
            pst.setString(2,tfprenomc.getText());
            pst.setString(3, choixGenrec.getValue());
            pst.setString(4, tfemailc.getText());
            pst.setString(5,encryptor.encryptString(tfmdpc.getText()));
            pst.setInt(6,Integer.parseInt(tfnumtelc.getText()));
            pst.setString(7,Role.Conseiller.toString());
            pst.setInt(8,Integer.parseInt(tfmatricule.getText()));
            pst.setString(9,tfattestation.getText());
            pst.setString(10,"");
            pst.setString(11, "");
            pst.setInt(12, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modification réussie");
        alert.setHeaderText(null);
        alert.setContentText("Les informations de l'utilisateur ont été mises à jour avec succès.");
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
    public void remplirChamps(Utilisateur user) {
        // Assurez-vous que l'utilisateur n'est pas null
        if (user != null) {
            // Remplir les champs avec les données de l'utilisateur
            tfnomc.setText(user.getNom());
            tfprenomc.setText(user.getPrenom());
            tfemailc.setText(user.getEmail());
            tfmdpc.setText(user.getMot_de_passe());
            choixGenrec.setValue(user.getGenre());
            tfnumtelc.setText(String.valueOf(user.getNum_tel()));
            tfmatricule.setText(String.valueOf(user.getMatricule()));
            tfattestation.setText(user.getAttestation());
        }
    }
}
