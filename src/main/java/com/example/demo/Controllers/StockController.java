package com.example.demo.Controllers;


import com.example.demo.Models.Produit;
import com.example.demo.Models.Stock;
import com.example.demo.Tools.MyConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class StockController implements Initializable{

    @FXML
    private Button bntAnuuler;
    @FXML
    private TextField Trecherche;
    @FXML
    private Button btnsupprimer;
    @FXML
    private VBox Vboxupdate;
    @FXML
    private Button btnupdate;
    @FXML
    private Button btnSave;

    @FXML
    private TextField tMarque;

    @FXML
    private TextField tNb;

    @FXML
    private TextField tQnt;
    @FXML
    private TableView<Stock> stockTableView;
    @FXML
    private VBox VboxAjouter;

    @FXML
    private TableColumn<Stock, Integer> refProduitColumn;

    @FXML
    private TableColumn<Stock, Integer> marqueColumn;
    @FXML
    private TableColumn<Stock, Integer> id_stockColumn;
    @FXML
    private TableColumn<Stock, Integer>   NomColumn;


    @FXML
    private TableColumn<Stock, Integer> quantiteColumn;

    @FXML
    private TableColumn<Stock, Integer> nbVenduColumn;
    @FXML
    private TextField tRef;
 int id_s=0;
    @FXML
    private TextField Idfield;

    @FXML
    private TextField Qntfield;

    @FXML
    private TextField Refield;
    @FXML
    private TableColumn<?, ?> tTotal;
    @FXML
    private Pane clickpane;
    private int lowestStockId;

    private static StockController instance;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Trecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filter the data based on the entered text
            filterData(newValue);
        });

        clickpane.setOnMouseClicked(event -> handlePaneClick(event));
        //property of the delete and update buttons to the absence of selection in the table
      //  btnsupprimer.disableProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNull());
       // btnupdate.disableProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNull());
        show();
        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
        //VboxAjouter.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNull());
    }
    public static StockController getInstance() {
        if (instance == null) {
            instance = new StockController();
        }
        return instance;
    }

    public void checkStockAndDisplayLowestQuantity() {
        // Assuming stockData is a list of Stock objects
        ObservableList<Stock> stockData = displayAllStock();

        // Find the stock with the lowest quantity
        Stock lowestStock = findLowestStock(stockData);

        if (lowestStock != null) {
            lowestStockId = lowestStock.getId_s();
            String message = "le  stock : "+lowestStock.getId_s()+"quantité" + lowestStock.getQuantite();
            runLater(() -> showNotification("Stock Notification", message, Alert.AlertType.INFORMATION));
        } else {
            runLater(() -> showNotification("Stock Notification", "No stock data available.", Alert.AlertType.WARNING));
        }
    }
    private void runLater(Runnable runnable) {
        Platform.runLater(runnable);
    }

    private void showNotification(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Customize the alert style (optional)
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/demo/css/style_dash.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");

        // Show the alert
        alert.showAndWait();
    }
    private Stock findLowestStock(ObservableList<Stock> stockData) {
        Stock lowestStock = null;
        int minQuantity = Integer.MAX_VALUE;

        for (Stock stock : stockData) {
            int quantity = stock.getQuantite();
            if (quantity < minQuantity) {
                minQuantity = quantity;
                lowestStock = new Stock();
                lowestStock.setId_s(stock.getId_s());
                lowestStock.setQuantite(stock.getQuantite());

            }
        }

        return lowestStock;
    }
    public int getLowestStockId() {
        return lowestStockId;
    }
    @FXML
    private void handlePaneClick(MouseEvent event) {
        // Clear the selection in the TableView when the Pane is clicked
        stockTableView.getSelectionModel().clearSelection();

        // Clear other fields or perform additional actions if needed
        Qntfield.setText(null);
        Idfield.setText(null);
        Refield.setText(null);

        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
    }


    /******** Get stock data from the database**********/
    public ObservableList<Stock> displayAllStock() {
        ObservableList<Stock> stockData = FXCollections.observableArrayList();

        try {
            Connection connection = MyConnection.getInstance().getCnx();
            // Select all columns from the stockk table
            String selectQuery = "SELECT * FROM stock";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {
                // Iterate through the result set and create Stock objects
                while (resultSet.next()) {
                    int id_s = resultSet.getInt("id_s");
                    String nom=resultSet.getString("nom");
                    int ref = resultSet.getInt("ref_produit");
                    String marque = resultSet.getString("marque");
                    int quantite = resultSet.getInt("quantite");
                    int nb_vendu = resultSet.getInt("nb_vendu");

                    Produit produit = new Produit(ref, marque, "category", 0); // You may need to adjust this based on your Produit class
                    float cout = calculateTotalValue(ref); // Calculate cout

                    Stock stockEntry = new Stock(id_s, produit, quantite,nom, nb_vendu, cout);
                    stockData.add(stockEntry);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockData;
    }
    public float calculateTotalValue(int ref_produit) {
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
                        float totalValue = prix * quantite;

                        // Print the result
                        System.out.println("Total value for product with ref " + ref_produit + ": " + totalValue);

                        // Update the total value in the stockk table
                        updateTotalValueInDatabase(ref_produit, totalValue);

                        // If you want to update the UI, you can use a label or some other UI component here
                        // For example, if you have a label named totalLabel, you can update it like this:
                        // totalLabel.setText(String.valueOf(totalValue));

                        return totalValue;
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

    private void updateTotalValueInDatabase(int ref_produit, float totalValue) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();

            // Update the total value in the stockk table
            String updateQuery = "UPDATE stock SET cout = ? WHERE ref_produit = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setFloat(1, totalValue);
                updateStatement.setInt(2, ref_produit);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void show() {
        ObservableList<Stock> list = displayAllStock();
        if (stockTableView != null) {
            stockTableView.setItems(list);
            refProduitColumn.setCellValueFactory(new PropertyValueFactory<>("produitRef"));
            marqueColumn.setCellValueFactory(new PropertyValueFactory<>("produitMarque"));
            quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            nbVenduColumn.setCellValueFactory(new PropertyValueFactory<>("nbVendu"));
            id_stockColumn.setCellValueFactory(new PropertyValueFactory<>("id_s"));
            NomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            tTotal.setCellValueFactory(new PropertyValueFactory<>("cout"));
        } else {
            System.out.println("TableView is null");
        }
    }


@FXML
void handleButtonAction(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Ajouter_stock.fxml"));
        Parent root = loader.load();
        AjouterStockController ajouterStockController = loader.getController();
        // Pass the StockController instance
        ajouterStockController.setStockController(this);

        Stage stage = new Stage();
        stage.setTitle("Ajouter stock");
        stage.setScene(new Scene(root));
        stage.setOnHidden((WindowEvent windowEvent) -> {
            // This code will be executed when the window is closed
            show(); // Update the table
        });
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error loading Ajouter_stock.fxml");
    }
}
    public void updateTotalValue(float totalValue) {
        // Update your UI element to display the total value
        tTotal.setText(String.valueOf(totalValue));
    }
    @FXML
    void deleteStock(ActionEvent event) {
        Stock selectedStock = stockTableView.getSelectionModel().getSelectedItem();

        if (selectedStock == null) {
            // Display an alert indicating that no item is selected
            showAlert("Veuillez sélectionner un élément.");
        } else {
            stockTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    id_s = newSelection.getId_s();
                }
            });
            // Create an alert dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes sur  de supprimer le stock?");

            // Show the alert and wait for the user's response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // User clicked OK, proceed with delete
                    String delete = "DELETE FROM stock WHERE id_s = ?";
                    Connection connection = MyConnection.getInstance().getCnx();
                    try {
                        PreparedStatement st = connection.prepareStatement(delete);
                        st.setInt(1, id_s);
                        st.executeUpdate();
                        show();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

/******************************Update*******************/
@FXML
void getData(MouseEvent event) {

    Vboxupdate.visibleProperty().unbind();
    Vboxupdate.setVisible(event.getClickCount() == 1);

    if (event.getClickCount() == 1) { // Check for single-click
        Stock selectedstock= stockTableView.getSelectionModel().getSelectedItem();
        Idfield.setText(String.valueOf(selectedstock.getId_s()));
        Refield.setText(String.valueOf(selectedstock.getProduitRef()));
        Qntfield.setText(String.valueOf(selectedstock.getQuantite()));
    }
}
    @FXML
    void UpdateStock(ActionEvent event) {
        Stock selectedStock = stockTableView.getSelectionModel().getSelectedItem();

        if (selectedStock == null) {
            // Display an alert indicating that no item is selected
            showAlert("Veuillez sélectionner un élément.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes sur de modifier le stock ? ");

            // Show the alert and wait for the user's response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (!isInteger(Qntfield.getText())) {
                        showAlert("La quantité doit être un entier.");
                        return; // Exit the method if the quantity is not a valid integer
                    }
                    String update = "update stock set quantite = ? where id_s = ?";
                    Connection connection = MyConnection.getInstance().getCnx();

                    try {
                        PreparedStatement st = connection.prepareStatement(update);
                        st.setInt(1, Integer.parseInt(Qntfield.getText())); // Convert the String to int
                        st.setInt(2, Integer.parseInt(Idfield.getText())); // Assuming Idfield is a TextField for the id_s

                        st.executeUpdate();
                        show();

                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        // Handle the exception appropriately
                    }
                }

            });
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
        private void showAlert(String message){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Apply a custom style to the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/com/example/demo/css/style_dash.css").toExternalForm()
            );
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }


    /*****************Recherche************/
    private void filterData(String searchText) {
        // Get the current list of Conseil objects from the TableView
        ObservableList<Stock> dataList = displayAllStock();

        // Create a filtered list to hold Stock objects matching the search criteria
        ObservableList<Stock> filteredList = FXCollections.observableArrayList();

        // Iterate through the dataList and add Stock objects that match the search criteria
        for (Stock stock : dataList) {
            String brand = stock.getProduitMarque().toLowerCase();
            searchText = searchText.toLowerCase().trim();

            // Check if the brand name contains the searchText as a substring
            if (brand.contains(searchText)) {
                filteredList.add(stock);
            }
        }

        // Clear the TableView and add the filtered data
        stockTableView.getItems().clear();
        stockTableView.getItems().addAll(filteredList);
    }
    /*****************************Cout*******************************/


}



































































/******************Annuler a partir du stock*********************/

//    @FXML
//    void Annuler_select(ActionEvent event) {
//        if (stockTableView.getSelectionModel().isEmpty()) {
//            // Display a message indicating that no item is selected
//            System.out.println("Veuillez sélectionner un élément.");
//        } else {
//            Annulerstock();
//        }
//    }
//
//    void Annulerstock() {
//        Qntfield.setText(null);
//        Idfield.setText(null);
//        Refield.setText(null);
//        stockTableView.getSelectionModel().clearSelection();
//
//        // Unbind disable properties before setting them
//
//
//        // Rebind disable properties if needed
//
//
//        // Show/hide the Vboxupdate based on selection
//        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
//    }
/**********************************************************************************/
//    public float calculateTotalValue(int ref_produit) {
//        try {
//            Connection connection = MyConnection.getInstance().getCnx();
//
//            // Select product information from both produit and stockk tables
//            String selectQuery = "SELECT p.prix, s.quantite " +
//                    "FROM produit p INNER JOIN stockk s ON p.ref = s.ref_produit " +
//                    "WHERE s.ref_produit = ?";
//
//            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
//                preparedStatement.setInt(1, ref_produit);
//
//                try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                    // Check if the product entry exists in the stockk table
//                    if (resultSet.next()) {
//                        // Retrieve product information
//                        float prix = resultSet.getFloat("prix");
//                        int quantite = resultSet.getInt("quantite");
//
//                        // Calculate the total value
//                        float totalValue = prix * quantite;
//
//                        // Print the result
//                        System.out.println("Total value for product with ref " + ref_produit + ": " + totalValue);
//
//                        // If you want to update the UI, you can use a label or some other UI component here
//                        // For example, if you have a label named totalLabel, you can update it like this:
//                        // totalLabel.setText(String.valueOf(totalValue));
//
//                        return totalValue;
//                    } else {
//                        System.out.println("Product with ref " + ref_produit + " not found in stockk table.");
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Return a default value in case of an error or if the product is not found
//        return 0.0f;
//    }
/*********************************************************************/
//    @FXML
//    void handleButtonAction(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ajouter_stock.fxml"));
//            Parent root = loader.load();
//
//            Stage stage = new Stage();
//            stage.setTitle("Ajouter stock");
//            stage.setScene(new Scene(root));
//            stage.setOnHidden((WindowEvent windowEvent) -> {
//                // This code will be executed when the window is closed
//                show(); // Update the table
//            });
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Error loading Ajouter_stock.fxml");
//        }
//    }