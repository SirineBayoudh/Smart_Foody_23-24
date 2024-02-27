package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifierConseillerController implements Initializable {

    @FXML
    private TextField tfnomc;

    @FXML
    private TextField tfprenomc;

    @FXML
    private TextField tfemailc;

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

    public Boolean existe = false;

    public String matriculeC;

    public String attestationC;

    Encryptor encryptor = new Encryptor();

    private GestionUserController gestionUserController;

    public void setGestionUserController(GestionUserController gestionUserController) {
        this.gestionUserController = gestionUserController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choixGenrec.getItems().addAll(genre);
        choixGenrec.setOnAction(e -> genreChoisi = choixGenrec.getValue());

        tfprenomc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                tfmatricule.setText("CNS-" + newValue);
            } else {
                tfmatricule.clear();
            }
        });

    }

    public void initData(Utilisateur u) {
        this.u = u;
        remplirChamps(u);
    }

    @FXML
    void choisirAttestationOnClick(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier d'attestation");

        // filtre pour les types de fichiers
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);


        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            tfattestation.setText(selectedFile.getAbsolutePath());

            String contenuFichier = effectuerOCR(selectedFile);

            if (contenuFichier.contains("Conseiller") || contenuFichier.contains("Nutritionniste")) {
                existe = true;
            } else {
                existe = false;
            }
        }
    }
    private String effectuerOCR(File file) {
        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\ESPRIT\\Semestre 2\\PI\\API\\Tess4J\\tessdata");
        try {
            return instance.doOCR(file);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void updateConseiller(ActionEvent event) throws SQLException {

        id = GestionUserController.getId();

        if (tfnomc.getText().isEmpty() || tfprenomc.getText().isEmpty() || tfemailc.getText().isEmpty() || choixGenrec.getValue().isEmpty() || tfnumtelc.getText().isEmpty()  || tfmatricule.getText().isEmpty() || tfattestation.getText().isEmpty()) {
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

        String reqMatricule = "SELECT matricule,attestation FROM utilisateur WHERE id_utilisateur= ?";
        PreparedStatement pstMatricule = cnx.prepareStatement(reqMatricule);
        pstMatricule.setInt(1, id);
        ResultSet rsMatricule = pstMatricule.executeQuery();
        rsMatricule.next();
        matriculeC = rsMatricule.getString("matricule");
        attestationC = rsMatricule.getString("attestation");

        if(!tfattestation.getText().equals(attestationC)){
            if(existe){
                modif();
            } else {
                showAlert("Mots non trouvés", "Les mots 'Conseiller' ou 'Nutritionniste' ne sont pas présents dans le fichier d'attestation.");
            }
        }else {
            modif();
        }

    }

    Connection  cnx = MyConnection.getInstance().getCnx();

    public void modif(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de modification");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir modifier le conseiller ayant la matricule " + matriculeC +" ?");

        ButtonType buttonTypeOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOui) {
            String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,num_tel=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
            try {
                PreparedStatement pst = cnx.prepareStatement(req);
                pst.setString(1, tfnomc.getText());
                pst.setString(2,tfprenomc.getText());
                pst.setString(3, choixGenrec.getValue());
                pst.setString(4, tfemailc.getText());
                pst.setInt(5,Integer.parseInt(tfnumtelc.getText()));
                pst.setString(6,Role.Conseiller.toString());
                pst.setString(7,tfmatricule.getText());
                pst.setString(8,tfattestation.getText());
                pst.setString(9,"");
                pst.setString(10, "");
                pst.setInt(11, id);
                pst.executeUpdate();

                gestionUserController.tableUser.getItems().setAll(gestionUserController.getUtilisateurs());

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }

        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert2.setTitle("Modification réussie");
        alert2.setHeaderText(null);
        alert2.setContentText("Les informations du conseiller ont été mises à jour avec succès.");
        alert2.showAndWait();

        Stage loginStage = (Stage) tfemailc.getScene().getWindow();
        loginStage.close();
    }

    @FXML
    private void refreshForm() {
        tfnomc.clear();
        tfprenomc.clear();
        tfemailc.clear();
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
            choixGenrec.setValue(user.getGenre());
            tfnumtelc.setText(String.valueOf(user.getNum_tel()));
            tfmatricule.setText(user.getMatricule());
            tfattestation.setText(user.getAttestation());
        }
    }
}
