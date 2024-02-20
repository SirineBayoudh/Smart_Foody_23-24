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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Comparator;
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
    @FXML
    private BarChart<String, Number> barchart;
    @FXML
    private Pane pane_1;
    @FXML
    private Label countAllStock;
    @FXML
    private ScatterChart<String, Number> scatterChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Trecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });

        clickpane.setOnMouseClicked(event -> handlePaneClick(event));
        show();
        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
        updateTotalStockCount();
    }
    public static StockController getInstance() {
        if (instance == null) {
            instance = new StockController();
        }
        return instance;
    }

    public void checkStockAndDisplayLowestQuantity() {

        ObservableList<Stock> stockData = displayAllStock();

        // petite quantite
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

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/demo/css/style_dash.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
    public static Stock findLowestStock(ObservableList<Stock> stockData) {
        Stock lowestStock = null;
        int minQuantity = Integer.MAX_VALUE;

        for (Stock stock : stockData) {
            int quantity = stock.getQuantite();
            if (quantity < minQuantity) {
                minQuantity = quantity;
                lowestStock = new Stock();
                lowestStock.setId_s(stock.getId_s());
                lowestStock.setQuantite(stock.getQuantite());
                lowestStock.setNbVendu(stock.getNbVendu());

            }
        }
        return lowestStock;
    }
    public int getLowestStockId() {
        return lowestStockId;
    }
    @FXML
    private void handlePaneClick(MouseEvent event) {
        // annuler select in the TableView when the Pane is clicked
        stockTableView.getSelectionModel().clearSelection();

        // Clear other fields
        Qntfield.setText(null);
        Idfield.setText(null);
        Refield.setText(null);

        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
    }


    /******** Get stock data from the database**********/
//    public ObservableList<Stock> displayAllStock() {
//        ObservableList<Stock> stockData = FXCollections.observableArrayList();
//
//        try {
//            Connection connection = MyConnection.getInstance().getCnx();
//
//            String selectQuery = "SELECT * FROM stock";
//
//            try (Statement statement = connection.createStatement();
//                 ResultSet resultSet = statement.executeQuery(selectQuery)) {
//
//                while (resultSet.next()) {
//                    int id_s = resultSet.getInt("id_s");
//                    String nom=resultSet.getString("nom");
//                    int ref = resultSet.getInt("ref_produit");
//                    String marque = resultSet.getString("marque");
//                    int quantite = resultSet.getInt("quantite");
//                    int nb_vendu = resultSet.getInt("nb_vendu");
//
//                    Produit produit = new Produit(ref, marque, "category", 0); // You may need to adjust this based on your Produit class
//                    float cout = calculateTotalValue(ref); // Calculate cout
//
//                    Stock stockEntry = new Stock(id_s, produit, quantite,nom, nb_vendu, cout);
//                    stockData.add(stockEntry);
//                }
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return stockData;
//    }

    public ObservableList<Stock> displayAllStock() {
        ObservableList<Stock> stockData = FXCollections.observableArrayList();

        try {
            Connection connection = MyConnection.getInstance().getCnx();

            String selectQuery = "SELECT s.id_s, s.nom, p.ref AS ref_produit, p.marque, lc.id_lc, lc.quantite AS quantite_commandee, s.quantite, s.nbVendu " +
                    "FROM stock s " +
                    "JOIN produit p ON s.ref_produit = p.ref " +
                    "LEFT JOIN ligne_commande lc ON p.ref = lc.ref_produit";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {

                while (resultSet.next()) {
                    int id_s = resultSet.getInt("id_s");
                    String nom = resultSet.getString("nom");
                    int refProduit = resultSet.getInt("ref_produit");
                    String marque = resultSet.getString("marque");
                    int idLigneCommande = resultSet.getInt("id_lc");
                    int quantiteCommandee = resultSet.getInt("quantite_commandee");
                    int quantiteStock = resultSet.getInt("quantite");
                    int nb_vendu = resultSet.getInt("nbVendu");
                    // Mettez à jour nb_vendu avec la quantité commandée
                    mettreAJourNbVendu(idLigneCommande);

                    Produit produit = new Produit(refProduit, marque, "category", 0); // Assurez-vous de fournir les valeurs correctes
                    float cout = calculateTotalValue(refProduit);

                    Stock stockEntry = new Stock(id_s, produit, quantiteStock, nom, nb_vendu, cout);
                    stockData.add(stockEntry);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockData;
    }

    public void mettreAJourNbVendu(int idLigneCommande) {
        String updateQuery = "UPDATE stock s " +
                "JOIN ligne_commande lc ON s.ref_produit = lc.ref_produit " +
                "SET s.nbVendu = lc.quantite " +
                "WHERE lc.id_lc = ?";

        try {
            Connection connection = MyConnection.getInstance().getCnx();
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            preparedStatement.setInt(1, idLigneCommande);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Updated nbVendu for id_s " + idLigneCommande);
                // Now fetch and print the updated nbVendu from the stock entry
                printUpdatedNbVendu(connection, idLigneCommande);
            } else {
                System.out.println("No rows were updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printUpdatedNbVendu(Connection connection, int idLigneCommande) {
        String selectQuery = "SELECT s.id_s, s.nbVendu " +
                "FROM stock s " +
                "JOIN ligne_commande lc ON s.ref_produit = lc.ref_produit " +
                "WHERE lc.id_lc = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, idLigneCommande);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int id_s = resultSet.getInt("id_s");
                int nbVendu = resultSet.getInt("nbVendu");
                System.out.println("Updated nbVendu value for id_s " + id_s + ": " + nbVendu);
            } else {
                System.out.println("Failed to fetch updated nbVendu value.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public float calculateTotalValue(int ref_produit) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();

            String selectQuery = "SELECT p.prix, s.quantite " +
                    "FROM produit p INNER JOIN stock s ON p.ref = s.ref_produit " +
                    "WHERE s.ref_produit = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, ref_produit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

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
            updateBarChart(list);
            updateScatterChart(list);


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
            show(); // Update the table
            updateTotalStockCount();
        });
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error du chargement Ajouter_stock.fxml");
    }
}
    public void updateTotalValue(float totalValue) {
        tTotal.setText(String.valueOf(totalValue));
    }
    @FXML
    void deleteStock(ActionEvent event) {
        Stock selectedStock = stockTableView.getSelectionModel().getSelectedItem();

        if (selectedStock == null) {
            // alert lors d'absence d'un element selectionner
            showAlert("Veuillez sélectionner un élément.");
        } else {
            stockTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    id_s = newSelection.getId_s();
                }
            });

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes sur  de supprimer le stock?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String delete = "DELETE FROM stock WHERE id_s = ?";
                    Connection connection = MyConnection.getInstance().getCnx();
                    try {
                        PreparedStatement st = connection.prepareStatement(delete);
                        st.setInt(1, id_s);
                        st.executeUpdate();
                        show();
                        updateTotalStockCount();
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

    if (event.getClickCount() == 1) {
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

            showAlert("Veuillez sélectionner un élément.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Vous êtes sur de modifier le stock ? ");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (!isInteger(Qntfield.getText())) {
                        showAlert("La quantité doit être un entier.");
                        return;
                    }
                    String update = "update stock set quantite = ? where id_s = ?";
                    Connection connection = MyConnection.getInstance().getCnx();

                    try {
                        PreparedStatement st = connection.prepareStatement(update);
                        st.setInt(1, Integer.parseInt(Qntfield.getText())); // Convert the String to int
                        st.setInt(2, Integer.parseInt(Idfield.getText())); //

                        st.executeUpdate();
                        show();

                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();

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

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/com/example/demo/css/style_dash.css").toExternalForm()
            );
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }


    /*****************Recherche************/
    private void filterData(String searchText) {

        ObservableList<Stock> dataList = displayAllStock();

        ObservableList<Stock> filteredList = FXCollections.observableArrayList();


        for (Stock stock : dataList) {
            String brand = stock.getNom().toLowerCase();
            searchText = searchText.toLowerCase().trim();


            if (brand.contains(searchText)) {
                filteredList.add(stock);
            }
        }

        // Clear the TableView and data filtered
        stockTableView.getItems().clear();
        stockTableView.getItems().addAll(filteredList);
    }
    /*****************************Stat *******************************/
    private void updateBarChart(ObservableList<Stock> stockData) {
        barchart.getData().clear();
        stockData.sort(Comparator.comparingInt(Stock::getId_s));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Stock stock : stockData) {
            try {
                int quantity = stock.getQuantite();
                String id_s = String.valueOf(stock.getId_s());

                // Utilisez BarChart.Data pour spécifier les valeurs des axes X et Y
                BarChart.Data<String, Number> data = new BarChart.Data<>(id_s, quantity);
                series.getData().add(data);

                // Si vous avez besoin de personnaliser l'apparence des barres, vous pouvez le faire ici
                Node bar = data.getNode();
                // Ajoutez ici votre personnalisation de la barre (si nécessaire)
            } catch (NumberFormatException e) {
                System.err.println("Invalid reference format: " + stock.getId_s());
            }
        }

        barchart.getData().add(series);
        barchart.setTitle("Quantités de produit par stock");
        barchart.getXAxis().setLabel("Id stock");
        barchart.getYAxis().setLabel("Quantité");
    }

    /******** Count nb stock **********/
    private void updateTotalStockCount() {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String countQuery = "SELECT COUNT(id_s) AS totalStockCount FROM stock";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(countQuery)) {

                if (resultSet.next()) {
                    int totalStockCount = resultSet.getInt("totalStockCount");

                    // Mettez à jour l'étiquette avec le nouveau nombre total
                    countAllStock.setText(" " + totalStockCount);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateScatterChart(ObservableList<Stock> stockData) {
        scatterChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Stock stock : stockData) {
            try {
                int quantite = stock.getQuantite();
                int nbVendu = stock.getNbVendu();
                String referenceCategory = String.valueOf(stock.getProduitRef());

                // Utilisez ScatterChart.Data pour spécifier les valeurs des axes X et Y
                ScatterChart.Data<String, Number> data = new ScatterChart.Data<>(referenceCategory, nbVendu);
                series.getData().add(data);

            } catch (NumberFormatException e) {
                System.err.println("Invalid reference format: " + stock.getProduitRef());
            }
        }

        scatterChart.getData().add(series);
        scatterChart.setTitle("Nombre vendu  par produit ");
        scatterChart.getXAxis().setLabel("Référence du produit");
        scatterChart.getYAxis().setLabel("Nombre vendu");
    }


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