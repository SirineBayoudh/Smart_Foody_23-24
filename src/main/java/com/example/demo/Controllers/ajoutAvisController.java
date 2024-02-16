package com.example.demo.Controllers;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ajoutAvisController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnSu;

    @FXML
    private TextArea tfCom;

    @FXML
    private TextField tfNote;
    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    int ref = 120;


    @FXML
    void initialize() {

    }

    @FXML
    void donnerAvis(ActionEvent event) {
        con = MyConnection.getInstance();
        String inserer = "INSERT INTO avis (ref_produit, nb_etoiles, commentaire) VALUES (?, ?, ?)";
        try {
            st = con.getCnx().prepareStatement(inserer);
            st.setInt(1, ref);
            st.setInt(2, Integer.parseInt(tfNote.getText()));
            st.setString(3, tfCom.getText()); // Utiliser setString pour un champ de texte
            st.executeUpdate();
            clear();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    void clear(){
        tfNote.setText(null);
        tfCom.setText(null);
    }

}