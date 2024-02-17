package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private TextField tNom;
    private StockController stockController;

    // Add a method to set the StockController
    public void setStockController(StockController stockController) {
        this.stockController = stockController;
    }

    @FXML
    void handleAddStock(ActionEvent event) {
        // Logic to add a new stock

        // Call createStock method
        createStock(event);

        // Calculate total value
        int ref_produit = Integer.parseInt(tRef.getText());
        float totalValue = calculateTotalValue(ref_produit);

        // Update StockController
        if (stockController != null) {
            stockController.show(); // Refresh the table
            stockController.updateTotalValue(totalValue); // Update total value
        }

        // Close the AjouterStockController window
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    // Add a new method to close the window
    private void closeWindow() {
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
                return resultSet.next(); // Returns true if the product exists
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

    @FXML
    private void fetchMarque() {
        int ref = Integer.parseInt(tRef.getText());

        try {
            Connection connection = MyConnection.getInstance().getCnx();

            // Check if the product exists
            if (isProductExists(ref)) {
                // Fetch the product details
                String selectQuery = "SELECT marque FROM produit WHERE ref = ?";
                try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                    selectStatement.setInt(1, ref);
                    ResultSet resultSet = selectStatement.executeQuery();

                    // If the product is found, set the 'marque' value in the tMarque TextField
                    if (resultSet.next()) {
                        String marque = resultSet.getString("marque");
                        tMarque.setText(marque);
                    }
                }
            } else {
                // Clear the tMarque TextField if the product doesn't exist
                tMarque.clear();
                System.out.println("Product does not exist. Please add the product first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A-3 methode ajout avec nb vendu initialiser à 0
     **/


    @FXML
    private void createStock(ActionEvent event) {
        if (validateInputs()) {
            try {
                int ref = Integer.parseInt(tRef.getText());
                int quantite = Integer.parseInt(tQnt.getText());
                String nom = tNom.getText();
                int nbVendu = 0;

                Connection connection = MyConnection.getInstance().getCnx();

                // Check if the product exists
                if (isProductExists(ref)) {
                    // Check if a stock entry with the same reference already exists
                    if (!isStockEntryExists(ref)) {
                        // Fetch the product details
                        String selectQuery = "SELECT marque FROM produit WHERE ref = ?";
                        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                            selectStatement.setInt(1, ref);
                            ResultSet resultSet = selectStatement.executeQuery();

                            // If the product is found, proceed with adding the stock entry
                            if (resultSet.next()) {
                                String marque = resultSet.getString("marque"); // Retrieve the 'marque' value from the result set

                                // Add the stock entry with the fetched product details
                                String insertQuery = "INSERT INTO stock (ref_produit, marque, quantite,nom, nb_vendu) VALUES (?,?, ?, ?, ?)";
                                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                    insertStatement.setInt(1, ref);
                                    insertStatement.setString(2, marque);
                                    insertStatement.setInt(3, quantite);
                                    insertStatement.setString(4, nom);
                                    insertStatement.setInt(5, nbVendu);
                                    insertStatement.executeUpdate();

                                    // Close the window
                                    Stage stage = (Stage) btnSave.getScene().getWindow();
                                    stage.close();

                                    showAlert("Stock entry added successfully!", "Success", Alert.AlertType.INFORMATION);
                                }
                            }
                        }
                    } else {
                        showAlert("Stock entry with the same reference already exists.", "Duplicate Entry", Alert.AlertType.WARNING);
                    }
                } else {
                    showAlert("Product does not exist. Please add the product first.", "Product Not Found", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error occurred while adding stock entry.", "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private boolean validateInputs() {
        if (tRef.getText().isEmpty() || tQnt.getText().isEmpty()) {
            showAlert1("Erreur", "Les champs Ref et Quantité sont obligatoires.", Alert.AlertType.ERROR);
            return false;
        }

        try {
            // Validate if tRef is a valid integer
            Integer.parseInt(tRef.getText());

            // Validate if tQnt is a valid integer
            Integer.parseInt(tQnt.getText());
        } catch (NumberFormatException e) {
            showAlert1("Erreur", "Les champs Ref et Quantité doivent être des entiers valides.", Alert.AlertType.ERROR);
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
                return resultSet.next(); // Returns true if the stock entry exists
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


    }


    private float calculateTotalValue(int ref_produit) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();

            // Select product information from both produit and stockk tables
            String selectQuery = "SELECT p.prix, s.quantite " +
                    "FROM produit p INNER JOIN stock s ON p.ref = s.ref_produit " +
                    "WHERE s.ref_produit = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, ref_produit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Check if the product entry exists in the stockk table
                    if (resultSet.next()) {
                        // Retrieve product information
                        float prix = resultSet.getFloat("prix");
                        int quantite = resultSet.getInt("quantite");

                        // Calculate the total value
                        return prix * quantite;
                    } else {
                        System.out.println("Product with ref " + ref_produit + " not found in stockk table.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return a default value in case of an error or if the product is not found
        return 0.0f;
    }


}
