package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterStockController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnSave;

    @FXML
    private TextField tMarque;

    @FXML
    private TextField tQnt;

    @FXML
    private TextField tRef;
    @FXML
    private ComboBox<String> refComboBox;
    @FXML
    private TextField tNom;
    private StockController stockController;


    public void setStockController(StockController stockController) {
        this.stockController = stockController;
    }

    @FXML
    void handleAddStock(ActionEvent event) {
        createStock(event);
        // calcul total
        int ref_produit = Integer.parseInt(tRef.getText());
        float totalValue = calculateTotalValue(ref_produit);

        // Update StockController
        if (stockController != null) {
            stockController.show(); // mis a jour table
            stockController.updateTotalValue(totalValue); // Update total value cout
        }

        // fermeture de  AjouterStockController window
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }



    private boolean isProductExists(int ref) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "SELECT * FROM produit WHERE ref = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, ref);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // Retourner true if product existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A-2 condition clique sur entrer puis appel fetchMarque pour récuperer marque du  produit
     **/
    @FXML
    private void onRefTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fetchMarque();
        }
    }

//    @FXML
//    private void fetchMarque() {
//        int ref = Integer.parseInt(tRef.getText());
//
//        try {
//            Connection connection = MyConnection.getInstance().getCnx();
//
//            // produit existe
//            if (isProductExists(ref)) {
//                // detail produit  (fetch)
//                String selectQuery = "SELECT marque FROM produit WHERE ref = ?";
//                try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
//                    selectStatement.setInt(1, ref);
//                    ResultSet resultSet = selectStatement.executeQuery();
//
//                    // if produit existe , set  'marque' value in the tMarque TextField
//                    if (resultSet.next()) {
//                        String marque = resultSet.getString("marque");
//                        tMarque.setText(marque);
//                    }
//                }
//            } else {
//                // supprimer tmarque textfield if produit n'existe pas
//                tMarque.clear();
//                System.out.println("Product does not exist. Please add the product first.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * A-3 methode ajout avec nb vendu initialiser à 0
     **/


    @FXML

    private void createStock(ActionEvent event) {
        if (validateInputs()) {
            try {
                String selectedRef = refComboBox.getValue();

                // Vérifiez si une référence est sélectionnée
                if (selectedRef != null && !selectedRef.isEmpty()) {
                    int ref = Integer.parseInt(selectedRef);
                    int quantite = Integer.parseInt(tQnt.getText());
                    String nom = tNom.getText();

                    Connection connection = MyConnection.getInstance().getCnx();

                    if (isProductExists(ref)) {
                        // Vérifiez l'existence de la référence dans le stock
                        if (!isStockEntryExists(ref)) {
                            // Produit existe, ajoutez une entrée de stock
                            String selectQuery = "SELECT marque FROM produit WHERE ref = ?";
                            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                                selectStatement.setInt(1, ref);
                                ResultSet resultSet = selectStatement.executeQuery();

                                // Si le produit existe, ajoutez une entrée de stock avec les détails du produit
                                if (resultSet.next()) {
                                    String marque = resultSet.getString("marque");

                                    String insertQuery = "INSERT INTO stock (ref_produit, marque, quantite, nom) VALUES (?, ?, ?, ?)";
                                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                        insertStatement.setInt(1, ref);
                                        insertStatement.setString(2, marque);
                                        insertStatement.setInt(3, quantite);
                                        insertStatement.setString(4, nom);
                                        insertStatement.executeUpdate();

                                        // Fermez la fenêtre
                                        Stage stage = (Stage) btnSave.getScene().getWindow();
                                        stage.close();

                                        showAlert("Stock ajouté avec succès !", "Succès", Alert.AlertType.INFORMATION);
                                    }
                                }
                            }
                        } else {
                            showAlert("Référence déjà existante.", "Référence existe", Alert.AlertType.WARNING);
                        }
                    } else {
                        showAlert("Produit inexistant, veuillez ajouter le produit.", "Produit introuvable", Alert.AlertType.WARNING);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur d'ajout du stock.", "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private boolean validateInputs() {
        if ( tQnt.getText().isEmpty() || tNom.getText().isEmpty()|| tMarque.getText().isEmpty()) {
            showAlert1("Erreur", "Les champs sont obligatoires.", Alert.AlertType.ERROR);
            return false;
        }

        try {
            // Validate if tRef, tQnt type  int

            Integer.parseInt(tQnt.getText());
            // Validate if tNom type String
            String nomValue = tNom.getText();
            if (!nomValue.matches("^[a-zA-Z]+$")) {
                showAlert1("Erreur", "Le nom doit contenir uniquement des lettres.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert1("Erreur", "Les champs doivent être respecter leurs Types.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }


    private void showAlert1(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showAlert(String message, String title, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isStockEntryExists(int ref) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "SELECT * FROM stock" +
                    " WHERE ref_produit = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, ref);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); //Retourner true if stok entry existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***********************Fin ajout stock **************************/


    @FXML
    private void Annuler(ActionEvent event) {
        clear();
    }

    private void clear() {
        tRef.setText(null);
        tMarque.setText(null);
        tQnt.setText(null);
        btnSave.setDisable(false);
    }

    @FXML
    void initialize() {
        loadProductReferences();
        refComboBox.setOnAction(event -> {
            // Appelez la méthode pour récupérer la marque lorsqu'une référence est sélectionnée
            fetchMarque();
        });

    }
    @FXML
    private void fetchMarque() {
        String selectedRef = refComboBox.getValue();

        // Vérifiez si une référence est sélectionnée
        if (selectedRef != null && !selectedRef.isEmpty()) {
            int ref = Integer.parseInt(selectedRef);

            try {
                Connection connection = MyConnection.getInstance().getCnx();

                // Produit existe
                if (isProductExists(ref)) {
                    // Détail du produit (fetch)
                    String selectQuery = "SELECT marque FROM produit WHERE ref = ?";
                    try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                        selectStatement.setInt(1, ref);
                        ResultSet resultSet = selectStatement.executeQuery();

                        // Si le produit existe, définissez la valeur 'marque' dans le champ tMarque
                        if (resultSet.next()) {
                            String marque = resultSet.getString("marque");
                            tMarque.setText(marque);
                        }
                    }
                } else {
                    // Supprimez le champ tMarque si le produit n'existe pas
                    tMarque.clear();
                    System.out.println("Le produit n'existe pas. Veuillez d'abord ajouter le produit.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    private float calculateTotalValue(int ref_produit) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String selectQuery = "SELECT p.prix, s.quantite " +
                    "FROM produit p INNER JOIN stock s ON p.ref = s.ref_produit " +
                    "WHERE s.ref_produit = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, ref_produit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // test produit existe dans table stock
                    if (resultSet.next()) {
                        // Retrancher les informations du produits
                        float prix = resultSet.getFloat("prix");
                        int quantite = resultSet.getInt("quantite");

                        // calculer total
                        return prix * quantite;
                    } else {
                        System.out.println("Produit ave réference  " + ref_produit + " n'existe pas  dans le stock.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // produit not found , error , case prend valeur 0.0
        return 0.0f;
    }

    private void loadProductReferences() {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "SELECT ref FROM produit";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                // Nettoyez d'abord le ComboBox
                refComboBox.getItems().clear();

                // Ajoutez chaque référence au ComboBox
                while (resultSet.next()) {
                    String reference = resultSet.getString("ref");
                    refComboBox.getItems().add(reference);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
