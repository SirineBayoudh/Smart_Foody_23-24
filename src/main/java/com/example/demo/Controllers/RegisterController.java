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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @FXML
    private ImageView verif1;

    @FXML
    private ImageView error1;

    @FXML
    private Label majus;

    @FXML
    private ImageView verif2;

    @FXML
    private ImageView error2;

    @FXML
    private Label minuscule;

    @FXML
    private ImageView verif3;

    @FXML
    private ImageView error3;

    @FXML
    private Label special;

    @FXML
    private ImageView verif4;

    @FXML
    private ImageView error4;

    @FXML
    private Label longueur;

    @FXML
    private ImageView verifConfirm1;

    @FXML
    private ImageView errorConfirm1;

    @FXML
    private Label majusConfirm;

    @FXML
    private ImageView verifConfirm2;

    @FXML
    private ImageView errorConfirm2;

    @FXML
    private Label minusculeConfirm;

    @FXML
    private ImageView verifConfirm3;

    @FXML
    private ImageView errorConfirm3;

    @FXML
    private Label specialConfirm;

    @FXML
    private ImageView verifConfirm4;

    @FXML
    private ImageView errorConfirm4;

    @FXML
    private Label longueurConfirm;

    @FXML
    private Label indicatif;

    Connection cnx = MyConnection.getInstance().getCnx();

    private void chargerOptionsObjectif() {

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


        spinnerTaille.setValueFactory(tailleValueFactory);
        spinnerPoids.setValueFactory(poidsValueFactory);

        spinnerTaille.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());
        spinnerPoids.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());

        IMC.setText(String.valueOf(0));

        calculerIMC();

        errorConfirm1.setVisible(false);
        errorConfirm2.setVisible(false);
        errorConfirm3.setVisible(false);
        errorConfirm4.setVisible(false);

        majusConfirm.setVisible(false);
        minusculeConfirm.setVisible(false);
        specialConfirm.setVisible(false);
        longueurConfirm.setVisible(false);

        /*String ipAddress = "";
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(ipAddress);*/

        try {

            // API FREEGEOIP pour le nom du pays à partir de l'adresse ip
            URL url2 = new URL("https://freegeoip.app/json/" + "197.2.48.207"); //102.129.65.0 france 

            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String countryName = response.toString().split("\"country_name\":\"")[1].split("\"")[0];
            System.out.println("Pays: " + countryName);

            conn.disconnect();

            // API RESTCOUNTRIES pour l'indicatif du pays à partir du nom du pays

            URL url3 = new URL("https://restcountries.com/v2/name/" + countryName);
            HttpURLConnection conn2 = (HttpURLConnection) url3.openConnection();
            conn2.setRequestMethod("GET");

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
            StringBuilder response2 = new StringBuilder();
            while ((line = reader2.readLine()) != null) {
                response2.append(line);
            }

            // Analyse de la réponse JSON pour obtenir l'indicatif téléphonique
            String phoneCode = response2.toString().split("\"callingCodes\":\\[\"")[1].split("\"")[0];
            System.out.println("Indicatif téléphonique: " + phoneCode);

            reader2.close();
            conn2.disconnect();

            indicatif.setText("+" + phoneCode);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void calculerIMC() {
        double taille = spinnerTaille.getValue() / 100.0; // Convertir la taille en mètres
        double poids = spinnerPoids.getValue();
        double imc = poids / (taille * taille);

        IMC.setText(String.format(" %.2f", imc));
    }

    @FXML
    void HidePasswordOnAction(KeyEvent event) {
        password = tpwd.getText();
        tpwdshow.setText(password);
        checkForUpperCaseLetter(password);
        checkForLowerCaseLetter(password);
        checkSpecial(password);
        checkLength(password);
    }

    @FXML
    void ShowPasswordOnAction(KeyEvent event) {
        password = tpwdshow.getText();
        tpwd.setText(password);
        checkForUpperCaseLetter(password);
        checkForLowerCaseLetter(password);
        checkSpecial(password);
        checkLength(password);
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
        /*checkForUpperCaseLetterConfirm(confirmPassword);
        checkForLowerCaseLetterConfirm(confirmPassword);
        checkSpecialConfirm(confirmPassword);
        checkLengthConfirm(confirmPassword);*/
    }

    @FXML
    void ShowPasswordConfirmOnAction(KeyEvent event) {
        confirmPassword = tpwdshowconfirm.getText();
        tpwdconfirm.setText(confirmPassword);
        /*checkForUpperCaseLetterConfirm(confirmPassword);
        checkForLowerCaseLetterConfirm(confirmPassword);
        checkSpecialConfirm(confirmPassword);
        checkLengthConfirm(confirmPassword);*/
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

    private void checkForUpperCaseLetter(String password) {
        boolean containsUpperCase = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                containsUpperCase = true;
                break;
            }
        }
        if (containsUpperCase) {
            majus.setStyle("-fx-text-fill: green;");
            verif1.setVisible(true);
            error1.setVisible(false);
        } else {
            majus.setStyle("");
            verif1.setVisible(false);
            error1.setVisible(true);
        }
    }

    private void checkForLowerCaseLetter(String password) {
        boolean containsLowerCase = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                containsLowerCase = true;
                break;
            }
        }
        if (containsLowerCase) {
            minuscule.setStyle("-fx-text-fill: green;");
            verif2.setVisible(true);
            error2.setVisible(false);
        } else {
            minuscule.setStyle("");
            verif2.setVisible(false);
            error2.setVisible(true);
        }
    }

    private void checkSpecial(String password) {
    Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
    Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (specialCharMatcher.find()) {
            special.setStyle("-fx-text-fill: green;");
            verif3.setVisible(true);
            error3.setVisible(false);
        } else {
            special.setStyle("");
            verif3.setVisible(false);
            error3.setVisible(true);
        }

    }

    private void checkLength(String password){
        if (password.length() >= 8) {
            longueur.setStyle("-fx-text-fill: green;");
            verif4.setVisible(true);
            error4.setVisible(false);
        } else {
            longueur.setStyle("");
            verif4.setVisible(false);
            error4.setVisible(true);
        }
    }

    private void checkForUpperCaseLetterConfirm(String password) {
        boolean containsUpperCase = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                containsUpperCase = true;
                break;
            }
        }
        if (containsUpperCase) {
            majusConfirm.setStyle("-fx-text-fill: green;");
            verifConfirm1.setVisible(true);
            errorConfirm1.setVisible(false);
        } else {
            majusConfirm.setStyle("");
            verifConfirm1.setVisible(false);
            errorConfirm1.setVisible(true);
        }
    }

    private void checkForLowerCaseLetterConfirm(String password) {
        boolean containsLowerCase = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                containsLowerCase = true;
                break;
            }
        }
        if (containsLowerCase) {
            minusculeConfirm.setStyle("-fx-text-fill: green;");
            verifConfirm2.setVisible(true);
            errorConfirm2.setVisible(false);
        } else {
            minusculeConfirm.setStyle("");
            verifConfirm2.setVisible(false);
            errorConfirm2.setVisible(true);
        }
    }

    private void checkSpecialConfirm(String password) {
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (specialCharMatcher.find()) {
            specialConfirm.setStyle("-fx-text-fill: green;");
            verifConfirm3.setVisible(true);
            errorConfirm3.setVisible(false);
        } else {
            specialConfirm.setStyle("");
            verifConfirm3.setVisible(false);
            errorConfirm3.setVisible(true);
        }

    }

    private void checkLengthConfirm(String password){
        if (password.length() >= 8) {
            longueurConfirm.setStyle("-fx-text-fill: green;");
            verifConfirm4.setVisible(true);
            errorConfirm4.setVisible(false);
        } else {
            longueurConfirm.setStyle("");
            verifConfirm4.setVisible(false);
            errorConfirm4.setVisible(true);
        }
    }

    @FXML
    void verifierCaptcha(MouseEvent event) {

        generatedWord = generateRandomWord();

        ImageView captchaImage = createCaptchaImage(generatedWord);

        TextField captchaInput = new TextField();
        captchaInput.setPromptText("Entrez le mot suivant");
        captchaInput.setEditable(true);

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
    void addUser(ActionEvent event) throws IOException {

        indicatif.setVisible(true);

        if (tnom.getText().isEmpty() || tprenom.getText().isEmpty() || temail.getText().isEmpty() || tpwd.getText().isEmpty() || genreChoisi == null || tftel.getText().isEmpty()  || villeChoisie == null || tfrue.getText().isEmpty() || objectifChoisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        String emailPattern = "^.+@.+$";
        if (!temail.getText().matches(emailPattern)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Format d'e-mail incorrect");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une adresse e-mail valide.");
            alert.showAndWait();
            return;
        }

        if (UserCrud.emailExists(temail.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Adresse e-mail déjà utilisée");
            alert.setHeaderText(null);
            alert.setContentText("L'adresse e-mail est déjà associée à un compte existant.");
            alert.showAndWait();
            return;
        }

        if (!tpwd.getText().equals(tpwdconfirm.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de mot de passe");
            alert.setHeaderText(null);
            alert.setContentText("Le mot de passe et la confirmation du mot de passe ne correspondent pas.");
            alert.showAndWait();
            return;
        }

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

                verif1.setVisible(false);
                verif2.setVisible(false);
                verif3.setVisible(false);
                verif4.setVisible(false);
                majus.setVisible(false);
                minuscule.setVisible(false);
                special.setVisible(false);
                longueur.setVisible(false);

                indicatif.setVisible(false);
            }
        }
    }

    @FXML
    void addUserIMC(ActionEvent event) throws NoSuchAlgorithmException{

        indicatif.setVisible(false);

        Utilisateur user = new Utilisateur(tnom.getText(), tprenom.getText(), genreChoisi, temail.getText(), encryptor.encryptString(tpwd.getText()), Integer.parseInt(tftel.getText()), Role.Client.toString(), "", "", villeChoisie + ", " + tfrue.getText(), objectifChoisi, 0,spinnerTaille.getValue(),spinnerPoids.getValue());
        UserCrud usc = new UserCrud();
        usc.ajouterEntite(user);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
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

            Stage loginStage = (Stage) temail.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

