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
    private Button btnSupTout;

    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnSelectImg;
    @FXML
    private TextField searchField;

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

    // Déclarez une liste observable pour stocker les suggestions
    private ObservableList<String> suggestions = FXCollections.observableArrayList();

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
    private LineChart<String, Number> lineChart;
    @FXML
    private ListView<String> suggestionList;

    // Méthode pour mettre à jour le LineChart avec les catégories et les prix les plus élevés
    private void updateLineChart() {
        String query = "SELECT categorie, MAX(prix) AS MaxPrix FROM produit GROUP BY categorie";
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categorie = resultSet.getString("categorie"); // Utilisez le nom de la colonne tel qu'il apparaît dans votre requête SQL
                double maxPrix = resultSet.getFloat("MaxPrix"); // Accédez à la colonne avec le nom "MaxPrix"
                data.add(new XYChart.Data<>(categorie, maxPrix));
                lineChart.getYAxis().setAutoRanging(false);
                ((NumberAxis) lineChart.getYAxis()).setLowerBound(0);
                ((NumberAxis) lineChart.getYAxis()).setUpperBound(50);
                ((NumberAxis) lineChart.getYAxis()).setTickUnit(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Effacez les données existantes et ajoutez les nouvelles données au LineChart
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
            // Récupérez l'URL de l'image sélectionnée et affichez-la dans le champ texte
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
            // Validation de la marque
            // Validation de la marque
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

            // Validation du critère
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
        String update = "update produit set Marque = ? ,Categorie = ? ,Prix = ?,Critere = ? ,Image = ?  where ref = ?";
        con = MyConnection.getInstance();
        try{
            st = con.getCnx().prepareStatement(update);
            st.setString(1,tfMarque.getText());
            st.setString(2,tfCategorie.getText());
            st.setString(3, String.valueOf(Float.parseFloat(tfPrix.getText())));
            st.setString(4,tfCritere.getText());
            st.setString(5,tfImage.getText());
            st.setInt(6,ref);
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
        updateLineChart();
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
        updateLineChart();
        }

    @FXML
    void handleSearchFieldKeyPress(KeyEvent event) {
        String query = "SELECT marque FROM produit WHERE marque LIKE ?";
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, searchField.getText() + "%"); // Ajoutez '%' pour rechercher les noms de marque commençant par la lettre saisie
            ResultSet resultSet = statement.executeQuery();

            suggestionList.getItems().clear();
            while (resultSet.next()) {
                String suggestion = resultSet.getString("marque");
                suggestions.add(suggestion);
            }
            suggestionList.getItems().addAll(suggestions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        showProduits();
        updateLineChart();

    }
}
