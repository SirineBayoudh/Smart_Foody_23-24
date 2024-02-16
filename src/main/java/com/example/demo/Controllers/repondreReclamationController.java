package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class repondreReclamationController  {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnEnnvoi;

    @FXML
    private TextArea tfDesc;

    @FXML
    private TextField tfMail;

    @FXML
    private TextField tfNom;

    @FXML
    private TextArea tfReponse;

    @FXML
    private TextField tfTitre;

    @FXML
    private TextField tfType;

    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    int id = 15;

    @FXML
    void initialize() {
        recuperValeur();
    }
    @FXML
    void reponseRec(ActionEvent event) {
        String a_jour = "UPDATE reclamation SET statut = 'repondu' WHERE id_client=?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(a_jour);
             st.setInt(1,id);
             st.executeUpdate();
             clear();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    void recuperValeur(){
        String recup = "SELECT u.nom, u.prenom, u.email, r.titre, r.type, r.description " +
                "FROM utilisateur u " +
                "INNER JOIN reclamation r ON u.id_utilisateur = r.id_client " +
                "WHERE u.id_utilisateur = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(recup);
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String titre = resultSet.getString("titre");
                String type = resultSet.getString("type");
                String description = resultSet.getString("description");

                // Afficher les détails dans les champs de texte
                tfNom.setText(nom + " " + prenom);
                tfMail.setText(email);
                tfTitre.setText(titre);
                tfType.setText(type);
                tfDesc.setText(description);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void clear(){
        tfNom.setText(null);
        tfMail.setText(null);
        tfTitre.setText(null);
        tfDesc.setText(null);
        tfType.setText(null);
        tfDesc.setText(null);
        tfReponse.setText(null);
    }


}