package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Controllers.UserCrud;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
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

    @FXML
    private Button btn_ajout;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label verifCaptcha;

    public boolean captcha = false;

    private String generatedWord;

    Encryptor encryptor = new Encryptor();

    ComplexiteMdp complx = new ComplexiteMdp();

    private ArrayList<String> objectifList = new ArrayList<>();

    @FXML
    private Spinner<Double> spinnerTaille;

    @FXML
    private Spinner<Double> spinnerPoids;

    @FXML
    private Label IMC;

    @FXML
    private VBox register1;

    @FXML
    private AnchorPane register2;

    private void chargerOptionsObjectif() {
        Connection cnx = MyConnection.getInstance().getCnx();

        String req = "SELECT libelle FROM objectif";

        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                objectifList.add(rs.getString("libelle"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des options d'objectif : " + e.getMessage());
        }

        choixObjectif.setItems(FXCollections.observableArrayList(objectifList));

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

        chargerOptionsObjectif();
        choixObjectif.setOnAction(e -> objectifChoisi = choixObjectif.getValue());

        register1.setVisible(true);
        register2.setVisible(false);


        SpinnerValueFactory<Double> tailleValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 300.0, 170.0);
        SpinnerValueFactory<Double> poidsValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 500.0, 70.0);


        // Définir les SpinnerValueFactory pour les Spinners
        spinnerTaille.setValueFactory(tailleValueFactory);
        spinnerPoids.setValueFactory(poidsValueFactory);

        spinnerTaille.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());
        spinnerPoids.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());

        IMC.setText(String.valueOf(0));

        // Calculer l'IMC initial
        calculerIMC();

    }
    private void calculerIMC() {
        double taille = spinnerTaille.getValue() / 100.0; // Convertir la taille en mètres
        double poids = spinnerPoids.getValue();
        double imc = poids / (taille * taille);

        // Mettre à jour le label IMC avec le résultat
        IMC.setText(String.format("IMC : %.2f", imc));
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
    void verifierCaptcha(MouseEvent event) {
        // Générer un mot aléatoire composé de chiffres et de lettres
        generatedWord = generateRandomWord();

        // Créer une image avec le mot généré
        ImageView captchaImage = createCaptchaImage(generatedWord);

        // Champ de texte pour que l'utilisateur entre le mot du captcha
        TextField captchaInput = new TextField();
        captchaInput.setPromptText("Entrez le mot suivant");
        captchaInput.setEditable(true);

        // Bouton pour vérifier le captcha
        Button verifyButton = new Button("Vérifier");
        verifyButton.setOnAction(e -> {
            String userInput = captchaInput.getText();
            if (userInput.equals(generatedWord)) {
                captcha = true;
            } else {
                showAlert("Erreur: Captcha incorrect!");
                captcha = false;
            }
        });

        anchorPane.setPadding(new Insets(10));

        captchaImage.setFitWidth(180);
        captchaImage.setFitHeight(60);

        AnchorPane.setTopAnchor(captchaImage, -5.0);
        AnchorPane.setLeftAnchor(captchaImage, -10.0);

        captchaInput.setPrefWidth(170.0);
        AnchorPane.setTopAnchor(captchaInput, -5.0);
        AnchorPane.setLeftAnchor(captchaInput, 170.0);

        verifyButton.setMaxWidth(120.0);
        verifyButton.setMinHeight(30.0);
        AnchorPane.setTopAnchor(verifyButton, 35.0);
        AnchorPane.setRightAnchor(verifyButton, 30.0);

        anchorPane.getChildren().addAll( captchaImage, captchaInput, verifyButton);
    }

    @FXML
    void addUser(ActionEvent event)  {

        //Contrôle sur les champs vides
        if (tnom.getText().isEmpty() || tprenom.getText().isEmpty() || temail.getText().isEmpty() || tpwd.getText().isEmpty() || genreChoisi == null || tftel.getText().isEmpty()  || villeChoisie == null || tfrue.getText().isEmpty() || objectifChoisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        // Vérifiez si l'e-mail est au format requis ****@*****
        String emailPattern = "^.+@.+$";
        if (!temail.getText().matches(emailPattern)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format d'e-mail incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une adresse e-mail valide.");
            alert.showAndWait();
            return;
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

        if(!captcha){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Veuillez choisir correctement le code captcha");
            alert.showAndWait();
        }else {
            complx.Calcul(tpwd.getText());
            System.out.println(complx.getNb());

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
                register1.setVisible(false);
                register2.setVisible(true);
            }
        }
    }

    @FXML
    void addUserIMC(ActionEvent event) throws NoSuchAlgorithmException{
        Utilisateur user = new Utilisateur(tnom.getText(), tprenom.getText(), genreChoisi, temail.getText(), encryptor.encryptString(tpwd.getText()), Integer.parseInt(tftel.getText()), Role.Client.toString(), "", "", villeChoisie + ", " + tfrue.getText(), objectifChoisi, 0,spinnerTaille.getValue(),spinnerPoids.getValue());
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

    private String generateRandomWord() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private ImageView createCaptchaImage(String word) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image("https://dummyimage.com/200x50/000/fff&text=" + word)); // Ceci est un exemple pour générer une image dynamiquement avec le mot
        return imageView;
    }

    // Méthode pour afficher une alerte
    private void showAlert(String message) {
        Stage alertStage = new Stage();
        VBox alertLayout = new VBox(10);
        alertLayout.getChildren().add(new Label(message));
        Scene scene = new Scene(alertLayout, 200, 100);
        alertStage.setScene(scene);
        alertStage.show();
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

