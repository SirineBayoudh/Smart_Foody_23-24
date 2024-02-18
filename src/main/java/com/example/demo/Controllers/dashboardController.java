package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    @FXML
    private Pane  innerPane;
    @FXML
    private Button btn_home;
    @FXML
    private BorderPane centerPane;
    @FXML
    private Text tt;

    private boolean isClicked = false;

    @FXML
    private void userClicked() {
        if (isClicked) {
            // Réinitialiser l'état normal si déjà cliqué
            tt.setFill(Color.valueOf("#faf6f6"));
            isClicked = false;
        } else {
            // Changer le fond lors du clic
            tt.setFill(Color.rgb(250, 246, 246, 0.5));
            // Vous pouvez ajuster les valeurs RGBA selon vos besoins
            isClicked = true;
        }
    }

    @FXML
    private void resetState() {
        // Réinitialiser l'état normal
        tt.setFill(Color.valueOf("#faf6f6"));
        isClicked = false;
    }

    @FXML
    public void initialize() {
        // Ajouter un gestionnaire d'événements générique pour réinitialiser l'état
        Parent root = tt.getParent();  // ou récupérez la référence au conteneur principal
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            // Vérifier si l'événement provient d'un bouton
            if (event.getTarget() instanceof Button) {
                // Réinitialiser l'état normal
                resetState();
            }
        });
    }
    @FXML
    private void dashboard() {
        centerPane.setCenter(innerPane);
        btn_home.getStyleClass().add("btn_home");

    }

    @FXML

    private void commande() {
        loadPage("/com/example/demo/Commande_dash.fxml");
    }
    @FXML
    private void user() {
        loadPage("/com/example/demo/utilisateur.fxml");
    }
    private void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page));
            centerPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}
