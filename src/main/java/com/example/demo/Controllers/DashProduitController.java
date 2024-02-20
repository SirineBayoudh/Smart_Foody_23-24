package com.example.demo.Controllers;

import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


import java.io.File;
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
    private VBox vboxFormulaire;
    @FXML
    private ComboBox<String> categoryFilterComboBox;

    @FXML
    private TextField tfPrix;
    @FXML
    private TextField tfImage;
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
    private LineChart<String, Number> lineChart;

    private void updateLineChart() {
        String query = "SELECT categorie, MAX(prix) AS MaxPrix FROM produit GROUP BY categorie";
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categorie = resultSet.getString("categorie");
                double maxPrix = resultSet.getFloat("MaxPrix");
                data.add(new XYChart.Data<>(categorie, maxPrix));
                lineChart.getYAxis().setAutoRanging(false);
                lineChart.setStyle("-fx-text-fill: #56ab2f");
                ((NumberAxis) lineChart.getYAxis()).setLowerBound(0);
                ((NumberAxis) lineChart.getYAxis()).setUpperBound(50);
                ((NumberAxis) lineChart.getYAxis()).setTickUnit(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>(data);
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }
    @FXML
    void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString());
        }
    }
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
        vboxFormulaire.setVisible(true);
        String insert = "insert into produit(Marque,Categorie,Prix,Critere,Image) values(?,?,?,?,?)";
        con = MyConnection.getInstance();
        try{
            st = con.getCnx().prepareStatement(insert);
            String imageUrl = tfImage.getText();
            String marque = tfMarque.getText();
            if (marque.isEmpty()) {
                showAlert("Veuillez saisir une marque.");
                return;
            } else if (!marque.matches("[a-zA-Z]+")) {
                showAlert("La marque ne doit contenir que des lettres.");
                return;
            }
            String categorie = tfCategorie.getText();
            if (categorie.isEmpty()) {
                showAlert("Veuillez saisir une catégorie.");
                return;
            } else if (!categorie.matches("[a-zA-Z]+")) {
                showAlert("La catégorie ne doit contenir que des lettres.");
                return;
            }
            String critere = tfCritere.getText();
            if (critere.isEmpty()) {
                showAlert("Veuillez saisir un critère.");
                return;
            } else if (!critere.matches("[a-zA-Z]+")) {
                showAlert("Le critère ne doit contenir que des lettres.");
                return;
            }
            String prixText = tfPrix.getText();
             if (prixText.isEmpty()) {
                showAlert("Veuillez saisir un prix.");
                return;
            } else if (!prixText.matches("\\d+(\\.\\d+)?")) {
                 showAlert("Le prix doit être un nombre décimal (float).");
                 return;
             }
            if (isImageUrlUnique(imageUrl)) {
                showAlert("L'URL de l'image doit être unique.");
                return;
            }else if (imageUrl.isEmpty()){
                showAlert("le champs est vide . Veuillez selectionner une image.");
                return;
            }
            st.setString(1,tfMarque.getText());
            st.setString(2,tfCategorie.getText());
            st.setString(3, String.valueOf(Float.parseFloat(tfPrix.getText())));
            st.setString(4,tfCritere.getText());
            st.setString(5,tfImage.getText());
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit ajouté avec succès !");
                showProduits();
                tfMarque.clear();
                tfCategorie.clear();
                tfPrix.clear();
                tfCritere.clear();
                tfImage.clear();
                btnModifier.setVisible(false);
            } else {
                System.out.println("Échec de l'ajout du produit !");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        updateLineChart();
    }
    private boolean isImageUrlUnique(String imageUrl) throws SQLException {
        String query = "SELECT COUNT(*) FROM produit WHERE Image = ?";
        PreparedStatement statement = con.getCnx().prepareStatement(query);
        statement.setString(1, imageUrl);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
        @FXML
    void getData(MouseEvent event) {
            Produit produit = table.getSelectionModel().getSelectedItem();
            if (produit != null) {
                ref = produit.getRef();
                tfMarque.setText(produit.getMarque());
                float prix = produit.getPrix();
                String prixString = String.valueOf(prix);
                tfPrix.setText(prixString);
                tfCritere.setText(produit.getCritere());
                btnAjouter.setDisable(true);
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
            }
    }

    @FXML
    void modifierProduit(ActionEvent event) {
        String update = "update produit set Marque = ? ,Prix = ?,Critere = ?   where ref = ?";
        con = MyConnection.getInstance();
        try{
            st = con.getCnx().prepareStatement(update);
            st.setString(1,tfMarque.getText());
            st.setString(2, String.valueOf(Float.parseFloat(tfPrix.getText())));
            st.setString(3,tfCritere.getText());
            st.setInt(4,ref);
            String marque = tfMarque.getText();
            if(!marque.matches("[a-zA-Z]+")) {
                showAlert("La marque ne doit contenir que des lettres.");
                return;
            }
            String critere = tfCritere.getText();
            if (!critere.matches("[a-zA-Z]+")) {
                showAlert("Le critère ne doit contenir que des lettres.");
                return;
            }
            String prixText = tfPrix.getText();
            if (!prixText.matches("\\d+(\\.\\d+)?")) {
                showAlert("Le prix doit être un nombre décimal (float).");
                return;
            }
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit modifié avec succès !");
                showProduits();
                tfMarque.clear();
                tfPrix.clear();
                tfCritere.clear();
            } else {
                System.out.println("Échec de la modification du produit !");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        updateLineChart();
    }

    @FXML
    void supprimerProduit(ActionEvent event) {
        String delete = "delete from produit where ref = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(delete);
            st.setInt(1, ref);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit supprimé avec succès !");
                showProduits();
            } else {
                System.out.println("Échec de la suppression du produit !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        updateLineChart();
        }
    @FXML
    void filterByCategory(ActionEvent event) {
        String selectedCategory = categoryFilterComboBox.getValue();
        ObservableList<Produit> filteredList = FXCollections.observableArrayList();
        for (Produit produit : table.getItems()) {
            if (produit.getCategorie().equals(selectedCategory)) {
                filteredList.add(produit);
            }
        }
        table.setItems(filteredList);
    }
    private ObservableList<String> getCategoriesFromDatabase() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT Categorie FROM produit";
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categorie = resultSet.getString("Categorie");
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showProduits();
        updateLineChart();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnSupprimer.setDisable(true);
        ObservableList<String> categories = getCategoriesFromDatabase();
        categoryFilterComboBox.setItems(categories);
    }
}
