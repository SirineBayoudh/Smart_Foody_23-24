package com.example.demo.Controllers;


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
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
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

    private int id_user;

    public void setTemail(String temail) {
        this.temail.setText(temail);
    }

    public void setTpwd(String tpwd) {
        this.tpwd.setText(tpwd);
    }

    public void setTpwdshow(String tpwdshow) {
        this.tpwdshow.setText(tpwdshow);
    }

    public void login(){
        Connection cnx = MyConnection.getInstance().getCnx();
        String req = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe =?";
        try{
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1,temail.getText());
            pst.setString(2,tpwd.getText());
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
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
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING,"Login error",ButtonType.OK);
                alert.show();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

