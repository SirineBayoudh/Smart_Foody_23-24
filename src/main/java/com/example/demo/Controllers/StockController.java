package com.example.demo.Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.demo.Models.Produit;
import com.example.demo.Models.Stock;
import com.example.demo.Tools.MyConnection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;


public class StockController implements Initializable , LanguageObserver{
    @FXML
    private TextField tst;
    @FXML
    private Button btnEuro;
    @FXML
    private Button btnDollar;

    @FXML
    private Button bntAnuuler;
    @FXML
    private TextField Trecherche;
    @FXML
    private Button btnsupprimer;
    @FXML private VBox Vboxupdate;
    @FXML
    private static Button btnupdate;
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
    private TableColumn<Stock, String> marqueColumn;
    @FXML
    private TableColumn<Stock, Integer> id_stockColumn;
    @FXML
    private TableColumn<Stock, String> NomColumn;


    @FXML
    private TableColumn<Stock, Integer> quantiteColumn;

    @FXML
    private TableColumn<Stock, Integer> nbVenduColumn;

    @FXML
    private TextField tRef;
    int id_s = 0;
    @FXML
    private TextField Idfield;

    @FXML
    private TextField Qntfield;

    @FXML
    private TextField Refield;
    @FXML
    private TableColumn<Stock, Integer> tTotal;
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
    @FXML
    private TextField tstTotal;

    @FXML
    private TextField tstadd;

    @FXML
    private TextField tstdelete;

    @FXML
    private TextField tstexport;

    @FXML
    private TextField tstsmodif;
    @FXML
    private Button btnExporterTout;

    @FXML
    private Text ref_pr;

    @FXML
    private Text qtvendu;
    @FXML
    private Text qtpr_stock;
    @FXML
    private Text id_st;
    @FXML
    private Text qtbarchart;

    @FXML
    private Text Titlebarchart;
    private static final String ACCOUNT_SID = "AC65cc060d1e8324522666575b59ffd53b";
    private static final String AUTH_TOKEN = "";
    private static final String FROM_PHONE_NUMBER = "+18607820963";
    private LanguageManager languageManager = LanguageManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        languageManager.addObserver(this);
        Trecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });

        clickpane.setOnMouseClicked(event -> handlePaneClick(event));
        show();
        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
        updateTotalStockCount();
        btnExporterTout.setOnAction(event -> exporterToutesLesDonnees());
        btnEuro.setOnAction(event -> {
            convertCostToEuro(event); // Appeler la méthode de conversion
        });
        btnDollar.setOnAction(event -> {
            convertCostToDollar(event); // Appeler la méthode de conversion
        });

    }

 /*********************Update stock **********/
     @FXML
      void UpdateStock(ActionEvent event) {
         Stock selectedStock = stockTableView.getSelectionModel().getSelectedItem();

         if (selectedStock == null) {
             showAlert("Veuillez sélectionner un élément.");
         } else {
             Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
             alert.setTitle("Confirmation Dialog");
             alert.setHeaderText(null);
             alert.setContentText("Vous êtes sûr de modifier le stock ? ");

             ButtonType customButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
             alert.getButtonTypes().setAll(customButtonType);

             // Get the OK button and apply custom styles
             Button okButton = (Button) alert.getDialogPane().lookupButton(customButtonType);
             okButton.setStyle("-fx-background-color: #56ab2f; -fx-text-fill: white;");

             // Remove the default icon
             alert.getDialogPane().setGraphic(null);

             alert.showAndWait().ifPresent(response -> {
                 if (response == customButtonType) {
                     if (!isInteger(Qntfield.getText())) {
                         showAlert("La quantité doit être un entier.");
                         return;
                     }
                     String update = "update stock set quantite = ? where id_s = ?";
                     Connection connection = MyConnection.getInstance().getCnx();

                     try {
                         int newQuantite = Integer.parseInt(Qntfield.getText());

                         PreparedStatement st = connection.prepareStatement(update);
                         st.setInt(1, newQuantite);
                         st.setInt(2, Integer.parseInt(Idfield.getText()));

                         st.executeUpdate();
                     if (newQuantite > selectedStock.getNbVendu()) {
                         // Remplacez YOUR_ACCESS_TOKEN par votre jeton d'accès Facebook
                         String accessToken = "EAANLAmpI2JEBO2XUNnnsmnZCWIxORTpZA3mQ3YFDhgo2qlRj6teQWDZAWWQadhU3zjg2LgwkCxzXLxxWEgFdFJUOIBZC6pOzVpwqtQBcb68Eezv7ZBUmMj5vVExZCxeHv3gxZBhUlvCt0b4yfebtZAj5yO0v9LtJbqupwlKOJpgrtBTXSWnP31YCRqOGaEFVSsAZD";
                         FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_19_0);

                         // Construire le message pour la publication sur Facebook
                         String facebookMessage = "Le stock du produit "+selectedStock.getProduitMarque()+ " est maintenant disponible  ";


                         // Publier sur Facebook directement depuis StockController
                         postStatusUpdate(facebookClient, facebookMessage);
                     }

                     show();

                 } catch (SQLException | NumberFormatException e) {
                     e.printStackTrace();
                 }
             }
         });
     }
 }

 /*************Delete stock**************/
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
             alert.setContentText("Vous êtes sûr de supprimer le stock?");
             ButtonType customButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
             alert.getButtonTypes().setAll(customButtonType);

             // Get the OK button and apply custom styles
             Button okButton = (Button) alert.getDialogPane().lookupButton(customButtonType);
             okButton.setStyle("-fx-background-color: #56ab2f; -fx-text-fill: white;");

             // Remove the default icon
             alert.getDialogPane().setGraphic(null);

             Optional<ButtonType> result = alert.showAndWait();

             if (result.isPresent() && result.get() == customButtonType) {
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
         }
     }



    /******** Get stock data from the database**********/

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

                    // Vérifier si la quantité est égale au nombre vendu
                    if (quantiteStock == nb_vendu) {
                        // Envoyer un SMS
                        sendSMS("+21692150166", "Stock terminé : " + stockEntry.getNom());
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockData;
    }



     /*********Fnct si je clique dans le pane j'annule les selectionner *********/
    @FXML
    private void handlePaneClick(MouseEvent event) {
        // annuler select in the TableView when the Pane is clicked
        stockTableView.getSelectionModel().clearSelection();
        tTotal.setText("cout");
       show();
        // Clear other fields
        Qntfield.setText(null);
        Idfield.setText(null);
        Refield.setText(null);

        Vboxupdate.visibleProperty().bind(stockTableView.getSelectionModel().selectedItemProperty().isNotNull());
    }



         /************pour mettre a jour le nb vendu , a partir de ligne commande responsable de l'exécution de la mise à jou**********/
        public void mettreAJourNbVendu(int idLigneCommande) {
        String updateQuery = "UPDATE stock s " +
                "JOIN ligne_commande lc ON s.ref_produit = lc.ref_produit " +
                "SET s.nbVendu = lc.quantite " +
                "WHERE lc.id_lc = ?";   //mettre à jour la colonne nbVendu de la table stock en fonction de la quantité (quantite) de la ligne de commande associée.

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
                System.out.println("aucune ligne a été modifié");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        /*************printUpdatedNbVendu pour afficher la nouvelle valeur de nbVendu.***************/
    private void printUpdatedNbVendu(Connection connection, int idLigneCommande) {
        String selectQuery = "SELECT s.id_s, s.nbVendu " +
                "FROM stock s " +
                "JOIN ligne_commande lc ON s.ref_produit = lc.ref_produit " +
                "WHERE lc.id_lc = ?";  //récupérer la nouvelle valeur de nbVendu à partir de la table stock après la mise à jour.

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
        /************calcul cout ***************/
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
                        System.out.println("valeur total des produit  " + ref_produit + ": " + totalValue);

                        // Update the total value in the stockk table
                        updateTotalValueInDatabase(ref_produit, totalValue);

                        return totalValue;
                    } else {
                        System.out.println("produit avec réference  " + ref_produit + " n'existe pas dans le stock .");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return a default value in case of an error or if the product is not found
        return 0.0f;
    }
       /**********update cout dans table***********/
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
            System.out.println("Table vide");
        }
    }

  /************Pour buton ajouter stock*************/
    @FXML
    void handleButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Ajouter_stock.fxml"));
            Parent root = loader.load();
            AjouterStockController ajouterStockController = loader.getController();
           //établir une référence entre deux contrôleurs
            ajouterStockController.setStockController(this); //prend en paramètre une référence au contrôleur actuel (this),

            Stage stage = new Stage();
            stage.setTitle("Ajouter stock");
            stage.setScene(new Scene(root));
            stage.setOnHidden((WindowEvent windowEvent) -> {
                show(); // Update the table
                updateTotalStockCount();// stat pane
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur du chargement Ajouter_stock.fxml");
        }
    }
    @FXML
    void getData(MouseEvent event) {
        Vboxupdate.visibleProperty().unbind();
        Vboxupdate.setVisible(event.getClickCount() == 1);

        if (event.getClickCount() == 1) {
            Stock selectedstock = stockTableView.getSelectionModel().getSelectedItem();
            Idfield.setText(String.valueOf(selectedstock.getId_s()));
            Refield.setText(String.valueOf(selectedstock.getProduitRef()));
            Qntfield.setText(String.valueOf(selectedstock.getQuantite()));
        } else if (event.getClickCount() == 2) {
            // Double-clic détecté, appeler la méthode pour exporter les données
            exportStockToExcel();
        }
    }

    public void updateTotalValue(float totalValue) {
        tTotal.setText(String.valueOf(totalValue));
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





    /**********using api facebook*************/

    private void postStatusUpdate(FacebookClient facebookClient, String message) {
        try {
            // Créer un paramètre avec le message
            Parameter messageParam = Parameter.with("message", message);

            // Publier la mise à jour du statut sur le fil d'actualité de l'utilisateur
            FacebookType response = facebookClient.publish("me/feed", FacebookType.class, messageParam);

            // Afficher l'ID du message nouvellement créé
            System.out.println("Posted message ID: " + response.getId());
        } catch (Exception e) {
            e.printStackTrace();
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();

            ButtonType customButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(customButtonType);

            // Get the OK button and apply custom styles
            Button okButton = (Button) alert.getDialogPane().lookupButton(customButtonType);
            okButton.setStyle("-fx-background-color: #56ab2f; -fx-text-fill: white;");

            // Remove the default icon
            alert.getDialogPane().setGraphic(null);

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

                // Si vous avez besoin de personnaliser l'apparence des points, vous pouvez le faire ici
                Node point = data.getNode();
                // Ajoutez ici votre personnalisation du point (si nécessaire)
            } catch (NumberFormatException e) {
                System.err.println("Invalid reference format: " + stock.getProduitRef());
            }
        }

        scatterChart.getData().add(series);

    }

/****************export excel****************/
private void exportStockToExcel() {
    Stock selectedStock = stockTableView.getSelectionModel().getSelectedItem();

    if (selectedStock != null) {
        String folderPath = "./ExcelFiles/";
        System.out.println("Chemin du fichier Excel : " + folderPath);
        String fileName = folderPath + "StockReport_" + selectedStock.getId_s() + ".xlsx";
        boolean success = ExcelExporter.exportStockToExcel(fileName, selectedStock);

        if (success) {
            showAlert("Export Excel réussi.");
        } else {
            showAlert("Erreur lors de l'export Excel.");
        }
    }
}

    @FXML
    private void exporterToutesLesDonnees() {
        // Récupérer la liste de tous les stocks à partir du TableView
        ObservableList<Stock> allStocks = stockTableView.getItems();

        // Spécifier le chemin du dossier ExcelFiles dans le même répertoire que votre application
        String folderPath = "./ExcelFiles/";

        // Générer un nom de fichier unique, par exemple, en ajoutant une horodatage
        String fileName = folderPath + "AllStocksReport_" + System.currentTimeMillis() + ".xlsx";

        // Appeler la méthode d'exportation avec le nom de fichier et la liste de stocks
        boolean success = ExcelExporter.exportAllStocksToExcel(fileName, allStocks);

        if (success) {
            showAlert("Export Excel réussi.");
        } else {
            showAlert("Erreur lors de l'export Excel.");
        }
    }

    /******************** devise **************************/

    @FXML
    void convertCostToEuro(ActionEvent event) {
        try {
            // Obtenez tous les éléments de la TableView
            ObservableList<Stock> allStocks = stockTableView.getItems();

            // Calculer le taux de change TND vers Euro
            double exchangeRateTNDToEuro = ExchangeRateApiUtil.getExchangeRate("TND", "EUR");

            // Parcourir tous les éléments et mettre à jour la colonne "cout"
            for (Stock stock : allStocks) {
                double coutEuro = stock.getCout() / exchangeRateTNDToEuro;
             stock.setCout((float) coutEuro) ;
            }
            tTotal.setText("Cout en Euro");
            // Mettez à jour la TableView pour refléter les modifications
            stockTableView.refresh();
            System.out.println(exchangeRateTNDToEuro);
            // Afficher une alerte ou un message pour informer de la conversion
            showAlert("Conversion en Euro", "Les coûts ont été convertis avec succès en Euro.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML

    void convertCostToDollar(ActionEvent event) {
        try {
            // Récupérez tous les éléments de la TableView
            ObservableList<Stock> allStocks = stockTableView.getItems();

            // Obtenez le taux de change de Dinar tunisien vers Dollar
            double exchangeRateTNDToUSD = ExchangeRateApiUtil.getExchangeRate("TND", "USD");

            // Parcourir tous les éléments et mettre à jour la colonne "cout"
            for (Stock stock : allStocks) {
                double coutUSD = stock.getCout() / exchangeRateTNDToUSD;
                stock.setCout((float) coutUSD);
            }
            tTotal.setText("Cout en Dollar");
            // Mettez à jour la TableView pour refléter les modifications
            stockTableView.refresh();
            System.out.println(exchangeRateTNDToUSD);
            // Afficher une alerte ou un message pour informer de la conversion
            showAlert("Conversion en Dollar", "Les coûts ont été convertis avec succès en Dollar.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }
    public void updateLabels() {
        id_stockColumn.setText(LanguageManager.getInstance().getText("id_stockColumn"));
        NomColumn.setText(LanguageManager.getInstance().getText("NomColumn"));
        refProduitColumn.setText(LanguageManager.getInstance().getText("refProduitColumn"));
        marqueColumn.setText(LanguageManager.getInstance().getText("marqueColumn"));
        quantiteColumn.setText(LanguageManager.getInstance().getText("quantiteColumn"));
        nbVenduColumn.setText(LanguageManager.getInstance().getText("nbVenduColumn"));
        tTotal.setText(LanguageManager.getInstance().getText("tTotal"));
        //btnupdate.setText(LanguageManager.getInstance().getText("btnupdate"));
        tstdelete.setText(LanguageManager.getInstance().getText("tstdelete"));
        tstadd.setText(LanguageManager.getInstance().getText("tstadd"));
        tstsmodif.setText(LanguageManager.getInstance().getText("tstsmodif"));
        tstexport.setText(LanguageManager.getInstance().getText("tstexport"));
        tstTotal.setText(LanguageManager.getInstance().getText("tstTotal"));
        /***************stat********************************/
        ref_pr.setText(LanguageManager.getInstance().getText("ref_pr"));
        qtvendu.setText(LanguageManager.getInstance().getText("qtvendu"));
        qtpr_stock.setText(LanguageManager.getInstance().getText("qtpr_stock"));
        id_st.setText(LanguageManager.getInstance().getText("id_st"));
        Titlebarchart.setText(LanguageManager.getInstance().getText("Titlebarchart"));
        qtbarchart.setText(LanguageManager.getInstance().getText("qtbarchart"));

        /**********************/
        //Trecherche.setText(LanguageManager.getInstance().getText("Trecherche"));
        updateMarqueColumnContent();
        updateNomColumnContent();
    }
    public void updateMarqueColumnContent() {
        // Suppose your TableColumn "marqueColumn" is already defined in your FXML or code
        // TableColumn<Stock, String> marqueColumn = ...

        marqueColumn.setCellValueFactory(cellData -> {
            Stock stock = cellData.getValue();
            return new SimpleStringProperty(stock.getProduitMarque());
        });

        marqueColumn.setCellFactory(column -> new TableCell<Stock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(LanguageManager.getInstance().getText(item));
                }
            }
        });
    }
    public void updateNomColumnContent() {
        // Suppose your TableColumn "nomColumn" is already defined in your FXML or code
        // TableColumn<Stock, String> nomColumn = ...

        NomColumn.setCellValueFactory(cellData -> {
            Stock stock = cellData.getValue();
            return new SimpleStringProperty(stock.getNom());
        });

        NomColumn.setCellFactory(column -> new TableCell<Stock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(LanguageManager.getInstance().getText(item));
                }
            }
        });
    }

    @Override
    public void onLanguageChanged() {
        updateLabels();
    }



    public void sendSMS(String toPhoneNumber, String message) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message twilioMessage = Message.creator(
                new PhoneNumber("+21692150166"), // To phone number
                new PhoneNumber(FROM_PHONE_NUMBER), // From Twilio phone number
                message
        ).create();

        System.out.println("SMS sent: " + twilioMessage.getSid());
    }
    public static StockController getInstance() {
        if (instance == null) {
            instance = new StockController();
        }
        return instance;
    }
}
































































