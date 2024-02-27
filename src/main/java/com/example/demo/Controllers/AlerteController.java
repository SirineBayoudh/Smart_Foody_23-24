package com.example.demo.Controllers;

import com.example.demo.Models.Alerte;
import com.example.demo.Tools.MyConnection;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class AlerteController implements LanguageObserver {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private PieChart nbAlert;

    @FXML
    private LineChart<String, Number> nbIdstock;


    @FXML
    private TableView<Alerte> AlerteTableView;

    @FXML
    private TableColumn<Alerte, Integer> DateColoumn;

    @FXML
    private TextField TrechercheAlerte;

    @FXML
    private Button btnsupprimer;

    @FXML
    private Button btnupdate;

    @FXML
    private Pane clickpane;

    @FXML
    private TableColumn<Alerte, Integer> id_alerteColumn;

    @FXML
    private TableColumn<Alerte, Integer> idstockCoulmn;
    @FXML
    private TableColumn<Alerte, String> DescriptionCoulumn;
    @FXML
    private TableColumn<Alerte, Boolean> TypeCoulumn;
    @FXML
    private Label labelTotalStock;

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_11;

    @FXML
    private Pane pane_111;
    @FXML
    private Text nbalert;

    @FXML
    private Label idAlerteCountLabel;

    @FXML
    private Text nbalertstock;

    @FXML
    private Text stokid;
    @FXML
    private Text paneTitre;
    private PieChart.Data unreadData;
    private PieChart.Data readData;

    private LanguageManager languageManager = LanguageManager.getInstance();
    @FXML
    void initialize() {
        languageManager.addObserver(this);
        loadAlerts();
        DateColoumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        id_alerteColumn.setCellValueFactory(new PropertyValueFactory<>("id_alerte"));
        idstockCoulmn.setCellValueFactory(new PropertyValueFactory<>("id_stock"));
        DescriptionCoulumn.setCellValueFactory(new PropertyValueFactory<>("description_alerte"));
        TypeCoulumn.setCellValueFactory(new PropertyValueFactory<>("typeString"));
        TrechercheAlerte.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAlerts(newValue);
        });
        updatePieChart(AlerteTableView.getItems());
        updateLineChart(getAlertCountsByStock(AlerteTableView.getItems()));
               //stat pane nb des alertes
        int alertCount = AlerteTableView.getItems().size();
        idAlerteCountLabel.setText(String.valueOf(alertCount));
        setupPagination(AlerteTableView.getItems());
        applyRowStyles();

    }


    @FXML
    void UpdateAlert(ActionEvent event) {

    }

    @FXML
    void deleteAlerte(ActionEvent event) {

    }
    @FXML
    void handlePaneClick(MouseEvent event) {

    }


    public static void insertAlert(int id_stock, Date date, String description, boolean isRead) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();

            String query = "INSERT INTO alerte (id_stock, date_alerte, description_alerte, Type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_stock);
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime()));
                preparedStatement.setString(3, description);

                // Set the value based on the boolean parameter
                preparedStatement.setBoolean(4, isRead);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAlerts() {
        deleteReadAlertsOlderThan10Days();

        List<Alerte> alerts = getAlertsFromDatabase();
        AlerteTableView.getItems().setAll(alerts);
    }

    private List<Alerte> getAlertsFromDatabase() {

        List<Alerte> alerts = new ArrayList<>();

        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "SELECT * FROM alerte";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id_alerte = resultSet.getInt("id_alerte");
                    int id_stock = resultSet.getInt("id_stock");
                    String description_alerte = resultSet.getString("description_alerte");
                    Date date = resultSet.getDate("date_alerte");
                    Boolean type = resultSet.getBoolean("Type");

                    // Convert boolean type to string representation
                    String typeString = (type != null && type) ? "lue" : "non lue";

                    System.out.println("typeString: " + typeString);
                    Alerte alerte = new Alerte(id_alerte, id_stock, description_alerte, date, type);
                    alerte.setTypeString(typeString);
                    alerts.add(alerte);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }



    public void Alerte(MouseEvent mouseEvent) {
    }
    @FXML

    void rechercherAlerte(ActionEvent event) {
        String searchText = TrechercheAlerte.getText();
        filterAlerts(searchText);

    }

    private void filterAlerts(String searchText) {
        List<Alerte> dataList = getAlertsFromDatabase();

        List<Alerte> filteredList = FXCollections.observableArrayList();

        for (Alerte alerte : dataList) {
            String idStock = String.valueOf(alerte.getId_stock());
            idStock = idStock.toLowerCase();
            searchText = searchText.toLowerCase().trim();

            if (idStock.contains(searchText)) {
                filteredList.add(alerte);
            }
        }

        // Clear the TableView and add the filtered data
        AlerteTableView.getItems().setAll(filteredList);
    }
    @FXML
    void getData(MouseEvent event) {
        if (event.getClickCount() == 2) { // Single-click
            Alerte selectedAlert = AlerteTableView.getSelectionModel().getSelectedItem();
            if (selectedAlert != null && !selectedAlert.isType()) { // If it's not already lue
                selectedAlert.setType(true); // Mark as lue in the local object
                selectedAlert.setTypeString("lue"); // Update the typeString as well

                // Update the database to mark it as lue
                updateAlertTypeInDatabase(selectedAlert.getId_alerte(), true);

                // Refresh the TableView or update the specific row
                AlerteTableView.refresh();
                updatePieChart(AlerteTableView.getItems());

                //update les textfield piechart when i click (ligne table)
                onLanguageChanged();
            }
        }
    }
    private void updateAlertTypeInDatabase(int alertId, boolean isRead) {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "UPDATE alerte SET Type = ? WHERE id_alerte = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, isRead);
                preparedStatement.setInt(2, alertId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLabels() {
        // Update other labels in StockController...
        // You can customize this based on your needs
        // Update btnupdate text based on language
      id_alerteColumn.setText(LanguageManager.getInstance().getText("id_alerteColumn"));
      idstockCoulmn.setText(LanguageManager.getInstance().getText("idstockCoulmn"));
        DescriptionCoulumn.setText(LanguageManager.getInstance().getText("DescriptionCoulumn"));
        TypeCoulumn.setText(LanguageManager.getInstance().getText("TypeCoulumn"));
        DateColoumn.setText(LanguageManager.getInstance().getText("DateColoumn"));
        stokid.setText(LanguageManager.getInstance().getText("stokid"));
        nbalert.setText(LanguageManager.getInstance().getText("nbalert"));
        nbalertstock.setText(LanguageManager.getInstance().getText("nbalertstock"));
        paneTitre.setText(LanguageManager.getInstance().getText("paneTitre"));
    }

    @Override
    public void onLanguageChanged() {
        updateLabels();
        unreadData.setName(LanguageManager.getInstance().getText("Alertes_Non_lues"));
        readData.setName(LanguageManager.getInstance().getText("Alertes_lues"));
    }

    private void deleteReadAlertsOlderThan10Days() {
        try {
            Connection connection = MyConnection.getInstance().getCnx();
            String query = "DELETE FROM alerte WHERE Type = 1 AND date_alerte < DATE_SUB(NOW(), INTERVAL 10 DAY)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updatePieChart(List<Alerte> alerts) {
        int unreadCount = 0;
        int readCount = 0;

        for (Alerte alert : alerts) {
            if (!alert.isType()) {
                unreadCount++;
            } else {
                readCount++;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Utilisez des ID pour référencer les données
        unreadData = new PieChart.Data("", unreadCount);
        readData = new PieChart.Data("", readCount);
        try {
            unreadData.setName(LanguageManager.getInstance().getText("Alertes_Non_lues") + ": " + unreadCount);
            readData.setName(LanguageManager.getInstance().getText("Alertes_lues") + ": " + readCount);
        } catch (MissingResourceException e) {
            System.out.println("Clé manquante : " + e.getKey());
        }
        pieChartData.addAll(unreadData, readData);

        nbAlert.setData(pieChartData);
    }
    private Map<Integer, Integer> getAlertCountsByStock(List<Alerte> alerts) {
        Map<Integer, Integer> alertCounts = new HashMap<>();

        for (Alerte alert : alerts) {
            int idStock = alert.getId_stock();
            alertCounts.put(idStock, alertCounts.getOrDefault(idStock, 0) + 1);
        }

        return alertCounts;
    }

    private void updateLineChart(Map<Integer, Integer> alertCounts) {
        ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();

        XYChart.Series<String, Number> series = new XYChart.Series<>();


        for (Map.Entry<Integer, Integer> entry : alertCounts.entrySet()) {
            String idStock = String.valueOf(entry.getKey());
            series.getData().add(new XYChart.Data<>(idStock, entry.getValue()));
        }

        lineChartData.add(series);

        // Set the data to the existing LineChart with fx:id "nbIdstock"
        nbIdstock.getData().setAll(lineChartData);



    }
    private static final int ITEMS_PER_PAGE = 12;

    @FXML
    private Pagination pagination;


    private void setupPagination(ObservableList<Alerte> alerteData) {
        int pageCount = (alerteData.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        pagination.setPageCount(pageCount);

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (newIndex.intValue() >= 0 && newIndex.intValue() < pageCount) {
                int startIndex = newIndex.intValue() * ITEMS_PER_PAGE;
                int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, alerteData.size());

                if (startIndex >= 0 && endIndex >= 0 && startIndex <= endIndex) {
                    ObservableList<Alerte> pageData = FXCollections.observableArrayList(
                            alerteData.subList(startIndex, endIndex));
                    AlerteTableView.setItems(pageData);
                }
            }
        });
    }


    private void applyRowStyles() {
        AlerteTableView.setRowFactory(tv -> {
            TableRow<Alerte> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (Boolean.TRUE.equals(newValue.isType())) {
                        row.setStyle("-fx-background-color: #bbddab; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                    } else {
                        row.setStyle("-fx-background-color: #ff9999; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                    }
                } else {
                    row.setStyle(""); // Réinitialiser le style par défaut si la ligne est vide
                }
            });
            return row;
        });
    }

}
