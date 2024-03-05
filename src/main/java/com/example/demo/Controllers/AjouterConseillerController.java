package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


import javafx.util.StringConverter;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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

    @FXML
    private ImageView choixAttestation;

    public Boolean existe = false;

    Encryptor encryptor = new Encryptor();

    ComplexiteMdp complx = new ComplexiteMdp();

    Random random = new Random();

    private GestionUserController gestionUserController;

    public void setGestionUserController(GestionUserController gestionUserController) {
        this.gestionUserController = gestionUserController;
    }

    //private ObservableList<String> emailSuggestions = FXCollections.observableArrayList("@gmail.com", "@hotmail.com", "@yahoo.com"); // Ajoutez d'autres suggestions au besoin

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choixGenrec.getItems().addAll(genre);
        choixGenrec.setOnAction(e -> genreChoisi = choixGenrec.getValue());

        tfprenomc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int randomNumber = random.nextInt(100);
                String formattedNumber = String.format("%02d", randomNumber);

                tfmatricule.setText("CNS-" + newValue + "-" + formattedNumber);
            } else {
                tfmatricule.clear();
            }
        });

        /*tfemailc.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.AT) {
                // Si l'utilisateur appuie sur la touche @, ajouter automatiquement "@gmail.com"
                tfemailc.appendText("gmail.com");

                // Déplacer le curseur à la position juste avant "gmail.com" pour permettre à l'utilisateur de continuer à saisir
                tfemailc.positionCaret(tfemailc.getText().length() - "gmail.com".length());

                // Consommer l'événement pour éviter qu'il ne soit traité par d'autres écouteurs
                event.consume();
            }
        });*/

        /*tfemailc.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                // Vérifier si la nouvelle valeur se termine par "@" et ne contient pas déjà "@gmail.com"
                if (newValue.endsWith("@") && !newValue.endsWith("@gmail.com")) {
                    tfemailc.setText(newValue + "gmail.com"); // Ajouter automatiquement "@gmail.com"
                    tfemailc.positionCaret(newValue.length());
                }
            }
        });*/

        /*ComboBox<String> emailComboBox = new ComboBox<>(emailSuggestions);
        emailComboBox.setLayoutX(tfemailc.getLayoutX() + tfemailc.getWidth() + 100);
        emailComboBox.setLayoutY(tfemailc.getLayoutY() + tfemailc.getHeight() + 50);


        ContextMenu emailSuggestionsMenu = new ContextMenu();
        emailSuggestions.forEach(suggestion -> {
            MenuItem item = new MenuItem(suggestion);
            item.setOnAction(event -> {
                String emailText = tfemailc.getText().split("@")[0] + suggestion;
                tfemailc.setText(emailText);
                tfemailc.positionCaret(emailText.length());
            });
            emailSuggestionsMenu.getItems().add(item);
        });


        tfemailc.setContextMenu(emailSuggestionsMenu);


        tfemailc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("@")) {
                emailSuggestionsMenu.show(tfemailc, tfemailc.getLayoutX() + tfemailc.getWidth(), tfemailc.getLayoutY());
            } else {
                emailSuggestionsMenu.hide();
            }
        });
        */

    }


    @FXML
    void choisirAttestationOnClick(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier d'attestation");

        // filtre pour les types de fichiers
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null); //ouvre boite de selection fichier
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

        if (existe == true) {
            complx.Calcul(tfmdpc.getText());

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

                Utilisateur u = new Utilisateur(tfnomc.getText(),tfprenomc.getText(),genreChoisi,tfemailc.getText(),encryptor.encryptString(tfmdpc.getText()), Integer.parseInt(tfnumtelc.getText()), Role.Conseiller.toString(),tfmatricule.getText(),tfattestation.getText(),"","",0,0.0,0.0);
                String requete = "INSERT INTO utilisateur(nom,prenom,genre,email,mot_de_passe,num_tel,role,matricule,attestation,adresse,objectif,tentative,taille,poids) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try{
                    PreparedStatement pst = cnx.prepareStatement(requete);
                    pst.setString(1, u.getNom());
                    pst.setString(2,u.getPrenom());
                    pst.setString(3, u.getGenre());
                    pst.setString(4, u.getEmail());
                    pst.setString(5,u.getMot_de_passe());
                    pst.setInt(6,u.getNum_tel());
                    pst.setString(7,u.getRole());
                    pst.setString(8,u.getMatricule());
                    pst.setString(9,u.getAttestation());
                    pst.setString(10,u.getAdresse());
                    pst.setString(11, u.getObjectif());
                    pst.setInt(12,u.getTentative());
                    pst.setDouble(13,u.getTaille());
                    pst.setDouble(14, u.getPoids());
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

        }else {
            showAlert("Attestation non valide", "L'attestation est non valide.");
        }

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
