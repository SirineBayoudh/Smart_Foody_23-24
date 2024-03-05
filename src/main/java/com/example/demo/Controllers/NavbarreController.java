package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NavbarreController implements Initializable {
    @FXML
    private BorderPane centerPane;

    @FXML
    private ImageView logoUser;

    @FXML
    private Button returnDash;

    Connection cnx = MyConnection.instance.getCnx();

    private int idUtilisateurConnecte = MyConnection.getInstance().getUserId();

    public void redirectToProfil(MouseEvent mouseEvent) throws SQLException {

        String req = "SELECT role FROM utilisateur WHERE id_utilisateur = ?";
        PreparedStatement pst = cnx.prepareStatement(req);
        pst.setString(1, String.valueOf(idUtilisateurConnecte));

        ResultSet rs = pst.executeQuery();
        rs.next();
        String role = rs.getString("role");

        if(role.equals("Client")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/profil.fxml"));
            try {
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                Stage loginStage = (Stage) logoUser.getScene().getWindow();
                loginStage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if (role.equals("Conseiller")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/profilConseiller.fxml"));
            try {
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                //Pour fermer la fenêtre du login
                Stage loginStage = (Stage) logoUser.getScene().getWindow();
                loginStage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    void redirectToDashboard(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/dashboard.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            //Pour fermer la fenêtre du login
            Stage loginStage = (Stage) logoUser.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
