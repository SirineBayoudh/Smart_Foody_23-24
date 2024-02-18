package com.example.demo.Controllers;

import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashProduitController implements Initializable {
    MyConnection con =null;
    PreparedStatement st =null;
    ResultSet rs = null;
    int ref=0;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupTout;

    @FXML
    private Button btnSupprimer;

    @FXML
    private TableView<Produit> table;

    @FXML
    private TextField tfCategorie;

    @FXML
    private TextField tfCritere;

    @FXML
    private TextField tfMarque;

    @FXML
    private TextField tfPrix;
    @FXML
    private TextField tfImage;
    @FXML
    private TextField tfObjectif;

    @FXML
    private TableColumn<Produit, String> colCategorie;

    @FXML
    private TableColumn<Produit, String> colCritere;

    @FXML
    private TableColumn<Produit, String> colMarque;

    @FXML
    private TableColumn<Produit, Float> colPrix;

    @FXML
    private TableColumn<Produit, Integer> colRef;
    @FXML
    private TableColumn<Produit, String > colImage;
    @FXML
    private TableColumn<Produit, String> colObjectif;


    private ObservableList<Produit> getProduits(){
        ObservableList<Produit> produits = FXCollections.observableArrayList();
         String query = "SELECT * from produit";
         con = MyConnection.getInstance();
         try{
             st = con.getCnx().prepareStatement(query);
             rs =st.executeQuery();
             while (rs.next()){
                 Produit st = new Produit();
                 st.setRef(rs.getInt("Ref"));
                 st.setMarque(rs.getString("Marque"));
                 st.setCategorie(rs.getString("Categorie"));
                 st.setPrix(rs.getFloat("Prix"));
                 st.setImage(rs.getString("Image"));
                 st.setCritere(rs.getString("Critere"));
                 st.setObj(rs.getString("Objectif"));
                 produits.add(st);
             }
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
         return produits;
    }

    public void showProduits(){
        ObservableList<Produit> list = getProduits();
        table.setItems(list);
        colRef.setCellValueFactory(new PropertyValueFactory<Produit,Integer>("ref"));
        colMarque.setCellValueFactory(new PropertyValueFactory<Produit,String>("marque"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<Produit,String>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<Produit,Float>("prix"));
        colImage.setCellValueFactory(new PropertyValueFactory<Produit,String >("image"));
        colCritere.setCellValueFactory(new PropertyValueFactory<Produit,String>("critere"));
    }
    @FXML
    void createProduit(ActionEvent event) throws SQLException {
        String insert = "insert into produit(Marque,Categorie,Prix,Critere,Image,Objectif) values(?,?,?,?,?,?)";
        con = MyConnection.getInstance();
        try{
            st = con.getCnx().prepareStatement(insert);
            st.setString(1,tfMarque.getText());
            st.setString(2,tfCategorie.getText());
            st.setString(3, String.valueOf(Float.parseFloat(tfPrix.getText())));
            st.setString(4,tfCritere.getText());
            st.setString(5,tfImage.getText());
            st.setString(6,tfObjectif.getText());
            // Utilisez executeUpdate() pour les requêtes de modification
            int rowsAffected = st.executeUpdate();

            // Vérifiez si des lignes ont été affectées
            if (rowsAffected > 0) {
                // Affichez un message ou effectuez d'autres actions en cas de succès
                System.out.println("Produit ajouté avec succès !");

                // Actualisez la liste des produits affichée dans le TableView
                showProduits();
            } else {
                // Affichez un message ou effectuez d'autres actions en cas d'échec
                System.out.println("Échec de l'ajout du produit !");
            }
        }catch (SQLException e) {
            e.printStackTrace(); // Imprimez la trace de l'exception
            throw new RuntimeException(e); // Lancez l'exception avec l'exception d'origine
        }
    }
    @FXML
    void getData(MouseEvent event) {
    Produit produit = table.getSelectionModel().getSelectedItem();
    ref = produit.getRef();
    tfMarque.setText(produit.getMarque());
    tfCategorie.setText(produit.getCategorie());
    //tfPrix.setText(produit.getPrix());
        float prix = produit.getPrix();
        String prixString = String.valueOf(prix);
        tfPrix.setText(prixString);
        tfCritere.setText(produit.getCritere());
        btnAjouter.setDisable(true);
    }

    @FXML
    void modifierProduit(ActionEvent event) {
        String update = "update produit set Marque = ? ,Categorie = ? ,Prix = ?,Critere = ? ,Image = ? ,Objectif = ? where ref = ?";
        con = MyConnection.getInstance();
        try{
            st = con.getCnx().prepareStatement(update);
            st.setString(1,tfMarque.getText());
            st.setString(2,tfCategorie.getText());
            st.setString(3, String.valueOf(Float.parseFloat(tfPrix.getText())));
            st.setString(4,tfCritere.getText());
            st.setString(5,tfImage.getText());
            st.setString(6,tfObjectif.getText());
            st.setInt(7,ref);
            // Utilisez executeUpdate() pour les requêtes de modification
            int rowsAffected = st.executeUpdate();

            // Vérifiez si des lignes ont été affectées
            if (rowsAffected > 0) {
                // Affichez un message ou effectuez d'autres actions en cas de succès
                System.out.println("Produit modifié avec succès !");

                // Actualisez la liste des produits affichée dans le TableView
                showProduits();
            } else {
                // Affichez un message ou effectuez d'autres actions en cas d'échec
                System.out.println("Échec de la modification du produit !");
            }
        }catch (SQLException e) {
            e.printStackTrace(); // Imprimez la trace de l'exception
            throw new RuntimeException(e); // Lancez l'exception avec l'exception d'origine
        }
    }

    @FXML
    void suppToutProduit(ActionEvent event) {

    }

    @FXML
    void supprimerProduit(ActionEvent event) {
        String delete = "delete from produit where ref = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(delete);
            st.setInt(1, ref);

            // Utilisez executeUpdate() pour les requêtes de suppression
            int rowsAffected = st.executeUpdate();

            // Vérifiez si des lignes ont été affectées
            if (rowsAffected > 0) {
                // Affichez un message ou effectuez d'autres actions en cas de succès
                System.out.println("Produit supprimé avec succès !");

                // Actualisez la liste des produits affichée dans le TableView
                showProduits();
            } else {
                // Affichez un message ou effectuez d'autres actions en cas d'échec
                System.out.println("Échec de la suppression du produit !");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimez la trace de l'exception
            throw new RuntimeException(e); // Lancez l'exception avec l'exception d'origine
        }
        }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    showProduits();
    }
}
