package com.example.demo.Controllers;

import com.example.demo.Models.Avis;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class avisController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Avis, String> colCommentaire;

    @FXML
    private TableColumn<Avis, Integer> colIdAvis;

    @FXML
    private TableColumn<Avis, Integer> colNotes;

    @FXML
    private TableColumn<Avis, Integer> colRefProduit;

    @FXML
    private TableView<Avis> table;

    @FXML
    private TextArea tfCommentaire;

    @FXML
    private TextField tfIdAvis;

    @FXML
    private TextField tfNotes;

    @FXML
    private TextField tfRef;

    private MyConnection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    private int id;

    @FXML
    void initialize() {
        showAvis();
    }

    void clear() {
        tfIdAvis.clear();
        tfRef.clear();
        tfNotes.clear();
        tfCommentaire.clear();
        btnAdd.setDisable(false);
    }

    @FXML
    void addReclamation(ActionEvent event) {
        String insert = "INSERT INTO avis(id_avis,ref_produit,nb_etoiles,commentaire) VALUES (?,?,?,?)";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            st.setInt(1, Integer.parseInt(tfIdAvis.getText()));
            st.setInt(2, Integer.parseInt(tfRef.getText()));
            st.setInt(3, Integer.parseInt(tfNotes.getText()));
            st.setString(4, tfCommentaire.getText());
            st.executeUpdate();
            showAvis();
            clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearReclamation(ActionEvent event) {
        clear();
    }

    @FXML
    void deleteReclamation(ActionEvent event) {
        String delete = "DELETE FROM avis WHERE id_avis = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
            clear();
            showAvis();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void getData(javafx.scene.input.MouseEvent event) {
        Avis avis = table.getSelectionModel().getSelectedItem();
        id = avis.getId_avis();
        tfIdAvis.setText(String.valueOf(avis.getId_avis()));
        tfRef.setText(String.valueOf(avis.getRef_produit()));
        tfNotes.setText(String.valueOf(avis.getNb_etoiles()));
        tfCommentaire.setText(avis.getCommentaire());
        btnAdd.setDisable(true);
    }
    @FXML
    void updateReclamation(ActionEvent event) {
        String update = "UPDATE `avis` SET `ref_produit`=?,`nb_etoiles`=?,`commentaire`=?  WHERE `id_avis`=?";
        con = MyConnection.getInstance();

        try {
            st = con.getCnx().prepareStatement(update);
            st.setString(1,tfRef.getText());
            st.setString(2,tfNotes.getText());
            st.setString(3,tfCommentaire.getText());
            st.setInt(4, Integer.parseInt(tfIdAvis.getText()));
            st.executeUpdate();
            clear();
            showAvis();
            btnAdd.setDisable(false);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ObservableList<Avis> getAvis() {
        ObservableList<Avis> avis = FXCollections.observableArrayList();
        String query = "SELECT * from avis";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Avis st = new Avis();
                st.setId_avis(rs.getInt("id_avis"));
                st.setRef_produit(rs.getInt("ref_produit"));
                st.setNb_etoiles(rs.getInt("nb_etoiles"));
                st.setCommentaire(rs.getString("commentaire"));
                avis.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return avis;
    }

    public void showAvis() {
        ObservableList<Avis> list = getAvis();
        table.setItems(list);
        colIdAvis.setCellValueFactory(new PropertyValueFactory<>("id_avis"));
        colRefProduit.setCellValueFactory(new PropertyValueFactory<>("ref_produit"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("nb_etoiles"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
    }

}