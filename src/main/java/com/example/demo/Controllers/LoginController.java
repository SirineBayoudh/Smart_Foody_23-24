package com.example.demo.Controllers;


import com.example.demo.Models.Role;
import com.example.demo.Models.Tentative;
import com.example.demo.Tools.MyConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public TextField temail;

    public PasswordField tpwd;

    @FXML
    private TextField tpwdshow;

    public Button btnCon;

    @FXML
    private ImageView eyeClosed;

    @FXML
    private ImageView eyeOpen;

    String password;

    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTemail(String temail) {
        this.temail.setText(temail);
    }

    public void setTpwd(String tpwd) {
        this.tpwd.setText(tpwd);
    }

    public void setTpwdshow(String tpwdshow) {
        this.tpwdshow.setText(tpwdshow);
    }

    Encryptor encryptor = new Encryptor();
    public void login(){
        Connection cnx = MyConnection.getInstance().getCnx();
        String req = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe =?";
        try{
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1,temail.getText());
            pst.setString(2,encryptor.encryptString(tpwd.getText()));
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                String reqUserId = "SELECT id_utilisateur FROM utilisateur WHERE email = ?";
                PreparedStatement pstUserId = cnx.prepareStatement(reqUserId);
                pstUserId.setString(1, temail.getText());
                ResultSet rsUserId = pstUserId.executeQuery();
                rsUserId.next();
                userId = rsUserId.getInt("id_utilisateur");
                MyConnection.getInstance().setUserId(userId);

                String reqRole = "SELECT role FROM utilisateur WHERE email = ?";
                PreparedStatement pstRole = cnx.prepareStatement(reqRole);
                pstRole.setString(1, temail.getText());
                ResultSet rsRole = pstRole.executeQuery();
                rsRole.next();
                String role = rsRole.getString("role");
                System.out.println(role);
                if(role.equalsIgnoreCase("Admin")){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/dashboard.fxml"));
                    try {
                        Parent root = loader.load();

                        dashboardController dash = loader.getController();

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();
                        //Pour fermer la fenêtre du login
                        Stage loginStage = (Stage) temail.getScene().getWindow();
                        loginStage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
                    try {
                        Parent root = loader.load();

                        NavbarreController nv = loader.getController();

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
            }else {

                String reqCheckEmail = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
                PreparedStatement pstEmail = cnx.prepareStatement(reqCheckEmail);
                pstEmail.setString(1, temail.getText());
                ResultSet rsCheckEmail = pstEmail.executeQuery();
                rsCheckEmail.next();
                int count = rsCheckEmail.getInt(1);
                if (count > 0) {
                    String reqUserId = "SELECT id_utilisateur FROM utilisateur WHERE email = ?";
                    PreparedStatement pstUserId = cnx.prepareStatement(reqUserId);
                    pstUserId.setString(1, temail.getText());
                    ResultSet rsUserId = pstUserId.executeQuery();
                    rsUserId.next();
                    int userId = rsUserId.getInt("id_utilisateur");

                    // Incrémenter le nombre de tentatives dans la classe Tentative
                    Tentative tentative = new Tentative();
                    tentative.setId_utilisateur(userId);
                    tentative.setNb_tentatives(tentative.getNb_tentatives() + 1);

                    System.out.println("Id:" + tentative.getId_utilisateur()+ " nb tentatives"+ tentative.getNb_tentatives());

                    Alert alert = new Alert(Alert.AlertType.WARNING,"Mot de passe erroné",ButtonType.OK);
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,"Aucun compte n'est associé à cette adresse email",ButtonType.OK);
                    alert.show();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCon.setOnAction(actionEvent -> login());
        tpwdshow.setVisible(false);
        eyeOpen.setVisible(false);
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
    void redirectToRegister(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/register.fxml"));
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

