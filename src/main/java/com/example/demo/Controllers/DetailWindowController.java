package com.example.demo.Controllers;

import com.example.demo.Models.Conseil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DetailWindowController {

    @FXML
    private TextField statutLabel;
    @FXML
    private TextArea demandeLabel;
    @FXML
    private TextArea reponseLabel;
    @FXML
    private TextField noteLabel;
    @FXML
    private TextField dateConseilLabel;

    public void initData(Conseil conseil) {
        //idLabel.setText(String.valueOf(conseil.getId_conseil()));
        //clientLabel.setText(String.valueOf(conseil.getId_client()));
        //matriculeLabel.setText(String.valueOf(conseil.getMatricule()));
        statutLabel.setText(conseil.getStatut());
        demandeLabel.setText(conseil.getDemande());
        reponseLabel.setText(conseil.getReponse());
        noteLabel.setText(String.valueOf(conseil.getNote()));
        dateConseilLabel.setText(conseil.getDate_conseil().toString());
    }
}
