package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarreController implements Initializable {
    @FXML
    private BorderPane centerPane;

    @FXML
    private ImageView logoUser;

    private int idUtilisateurConnecte;

    public int getIdUtilisateurConnecte() {
        return idUtilisateurConnecte;
    }

    public void setIdUtilisateurConnecte(int idUtilisateurConnecte) {
        this.idUtilisateurConnecte = idUtilisateurConnecte;
    }

    public void redirectToProfil(MouseEvent mouseEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/profil.fxml"));
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
