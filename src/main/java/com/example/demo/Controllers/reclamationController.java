package com.example.demo.Controllers;

import com.example.demo.Models.Reclamation;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class reclamationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Reclamation, Integer> colIdReclamation;

    @FXML
    private TableColumn<Reclamation, Integer> colIdClient;

    @FXML
    private TableColumn<Reclamation, String> colDescription;

    @FXML
    private TableColumn<Reclamation, String> colTitre;

    @FXML
    private TableColumn<Reclamation, String> colStatut;

    @FXML
    private TableColumn<Reclamation, String> colType;

    @FXML
    private TableColumn<Reclamation, java.util.Date> colDateReclamation;

    @FXML
    private TableView<Reclamation> table;

    @FXML
    private TextArea tfDescription;

    @FXML
    private TextField tfIdClient;

    @FXML
    private TextField tfStatut;

    @FXML
    private TextField tfTitre;

    @FXML
    private TextField tfType;

    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    int id;

    @FXML
    void initialize() {
        showReclamations();
    }
    @FXML
    void addReclamation(ActionEvent event) {
          String insert = "INSERT INTO reclamation(id_client,description,titre,statut,type) VALUES (?,?,?,?,?)";
          con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            st.setInt(1, Integer.parseInt(tfIdClient.getText()));
            st.setString(2,tfDescription.getText());
            st.setString(3,tfTitre.getText());
            st.setString(4,tfStatut.getText());
            st.setString(5,tfType.getText());
            st.executeUpdate();
            showReclamations();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    void clear(){
        tfIdClient.setText(null);
        tfDescription.setText(null);
        tfTitre.setText(null);
        tfStatut.setText(null);
        tfType.setText(null);
        btnAdd.setDisable(true);
    }

    @FXML
    void clearReclamation(ActionEvent event) {
        clear();
    }

    @FXML
    void deleteReclamation(ActionEvent event) {
            String delete = "DELETE FROM reclamation where id_reclamation= ? ";
            con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(delete);
            st.setInt(1,id);
            st.executeUpdate();
            clear();
            showReclamations();
            btnAdd.setDisable(false);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateReclamation(ActionEvent event) {
            String update = "UPDATE `reclamation` SET `id_client`=?,`description`=?,`titre`=?,`statut`=?,`type`=?,`date_reclamation`=?  WHERE `id_reclamation`=?";
        con = MyConnection.getInstance();
        Date currentDate = new Date(System.currentTimeMillis());

        try {
            st = con.getCnx().prepareStatement(update);
            st.setInt(1, Integer.parseInt(tfIdClient.getText()));
            st.setString(2,tfDescription.getText());
            st.setString(3,tfTitre.getText());
            st.setString(4,tfStatut.getText());
            st.setString(5,tfType.getText());
            st.setString(6, String.valueOf(currentDate));
            st.setInt(7,id);
            st.executeUpdate();
            clear();
            showReclamations();
            btnAdd.setDisable(false);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void getData(MouseEvent event) {
            Reclamation reclamation = table.getSelectionModel().getSelectedItem();
            id = reclamation.getId_reclamation();
            tfIdClient.setText(String.valueOf(reclamation.getId_client()));
            tfDescription.setText(reclamation.getDescription());
            tfTitre.setText(reclamation.getTitre());
            tfStatut.setText(reclamation.getStatut());
            tfType.setText(reclamation.getType());
            btnAdd.setDisable(true); //Desactiver le button d'ajout
    }




    private ObservableList<Reclamation> getReclamation() {
        ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
        String query = "SELECT * from reclamation";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Reclamation st = new Reclamation();
                st.setId_reclamation(rs.getInt("id_reclamation"));
                st.setId_client(rs.getInt("id_client"));
                st.setDescription(rs.getString("description"));
                st.setTitre(rs.getString("titre"));
                st.setStatut(rs.getString("statut"));
                st.setType(rs.getString("type"));
                st.setDate_reclamation(rs.getDate("date_reclamation"));
                reclamations.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reclamations;
    }

    public void showReclamations() {
        ObservableList<Reclamation> list = getReclamation();
        table.setItems(list);
        colIdReclamation.setCellValueFactory(new PropertyValueFactory<>("id_reclamation"));
        colIdClient.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDateReclamation.setCellValueFactory(new PropertyValueFactory<>("date_reclamation"));
    }

}