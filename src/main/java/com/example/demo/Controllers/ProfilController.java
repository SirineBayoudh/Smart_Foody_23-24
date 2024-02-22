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
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfilController implements Initializable {

    @FXML
    private TextField nomUser;

    @FXML
    private TextField tfnomp;

    @FXML
    private TextField tfprenomp;

    @FXML
    private TextField tfemailp;

    @FXML
    private PasswordField tfmdpp;

    @FXML
    private ComboBox<String> choixGenrep;

    private String[] genre = {"Homme","Femme"};

    private String genreChoisi;

    @FXML
    private TextField tfnumtelp;

    @FXML
    private ComboBox<String> choixVillep;

    private String[] ville = {"Ariana","Béja","Ben Arous","Bizerte","Gabès","Gafsa","Jendouba","Kairouan","Kasserine","Kébili","Le Kef","Mahdia","La Manouba","Médenine","Monastir","Nabeul","Sfax","Sidi Bouzid","Siliana","Sousse","Tataouine","Tozeur","Tunis","Zaghouan"};

    private String villeChoisie;

    @FXML
    private TextField tfruep;

    @FXML
    private ComboBox<String> choixObjectifp;

    private String[] objectif = {"Bien être","prise de poids","Perte de poids","Prise de masse musculaire"};

    private String objectifChoisi;

    @FXML
    private Button btn_modif;

    @FXML
    private ImageView logoutIcon;

    private int idUtilisateurConnecte;

    public TextField getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser.setText(nomUser);
    }

    Encryptor encryptor = new Encryptor();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choixGenrep.getItems().addAll(genre);
        choixGenrep.setOnAction(e -> genreChoisi = choixGenrep.getValue());

        choixVillep.getItems().addAll(ville);
        choixVillep.setOnAction(e -> villeChoisie = choixVillep.getValue());

        choixObjectifp.getItems().addAll(objectif);
        choixObjectifp.setOnAction(e -> objectifChoisi = choixObjectifp.getValue());

        idUtilisateurConnecte = MyConnection.getInstance().getUserId();
        remplirChampsUtilisateur(idUtilisateurConnecte);

        System.out.println(idUtilisateurConnecte);

    }


    public void remplirChampsUtilisateur(int idUtilisateur) {
        Connection cnx = MyConnection.getInstance().getCnx();
        String req = "SELECT * FROM utilisateur WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, idUtilisateur);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nomUser.setText(rs.getString(2));
                tfnomp.setText(rs.getString(2));
                tfprenomp.setText(rs.getString(3));
                choixGenrep.setValue(rs.getString(4));
                tfemailp.setText(rs.getString(5));
                tfmdpp.setText(rs.getString(6));
                tfnumtelp.setText(rs.getString(7));

                String adresse = rs.getString(11);
                String[] parts = adresse.split(",");
                if (parts.length == 2) {
                    choixVillep.setValue(parts[0]);
                    tfruep.setText(parts[1]);
                }
                choixObjectifp.setValue(rs.getString(12));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void modifierProfil(ActionEvent event) {

        if (tfnomp.getText().isEmpty() || tfprenomp.getText().isEmpty() || tfemailp.getText().isEmpty() || tfmdpp.getText().isEmpty() || choixGenrep.getValue().isEmpty() || tfnumtelp.getText().isEmpty()  || choixVillep.getValue().isEmpty() ||  tfruep.getText().isEmpty() || choixObjectifp.getValue().isEmpty()) {
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
            String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,mot_de_passe=?,num_tel=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
            try {
                PreparedStatement pst = cnx.prepareStatement(req);
                pst.setString(1, tfnomp.getText());
                pst.setString(2,tfprenomp.getText());
                pst.setString(3, choixGenrep.getValue());
                pst.setString(4, tfemailp.getText());
                pst.setString(5,encryptor.encryptString(tfmdpp.getText()));
                pst.setInt(6,Integer.parseInt(tfnumtelp.getText()));
                pst.setString(7, Role.Conseiller.toString());
                pst.setInt(8,0);
                pst.setString(9,"");
                pst.setString(10,choixVillep.getValue() + "," + tfruep);
                pst.setString(11, choixObjectifp.getValue());
                pst.setInt(12, idUtilisateurConnecte);
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
