package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ajoutReclamationController {

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnEnvoi;

    @FXML
    private TextArea tfDesc;

    @FXML
    private TextField tfMail;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfTitre;

    @FXML
    private ComboBox<String> tfType;

    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    int id = 15;

    @FXML
    void initialize() {
        ObservableList<String> list = FXCollections.observableArrayList("Réclamation", "Demande d'information", "Remerciement", "Demande de Collaboration");
        tfType.setItems(list);
        recuperValeur();
    }

    void clear(){
        tfTitre.setText(null);
        tfDesc.setText(null);
        //tfType.setItems(null);
        btnEnvoi.setDisable(true);
    }

     void recuperValeur(){
         String recup = "SELECT nom,prenom,email FROM utilisateur where id_utilisateur=? ";
         con = MyConnection.getInstance();
         try {
             st = con.getCnx().prepareStatement(recup);
             st.setInt(1, id);
             ResultSet resultSet = st.executeQuery();

             if (resultSet.next()) {
                 String nom = resultSet.getString("nom");
                 String prenom = resultSet.getString("prenom");
                 String email = resultSet.getString("email");

                 // Afficher les détails dans les champs de texte
                 tfNom.setText(nom + " " + prenom);
                 tfMail.setText(email);
             }

         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
     }
    @FXML
    void ajoutRec(ActionEvent event) {
        String insert = "INSERT INTO reclamation(id_client,description,titre,type) VALUES (?,?,?,?)";

        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            st.setInt(1, id);
            st.setString(2, tfDesc.getText());
            st.setString(3, tfTitre.getText());
            st.setString(4, tfType.getValue());
            st.executeUpdate();
            clear();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void annulerAjout(ActionEvent event) {
        clear();
        btnEnvoi.setDisable(false);
    }
}