package com.example.demo.Controllers;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.example.demo.Models.Alerte;
import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class AlerteController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Alerte> AlerteTableView;

    @FXML
    private TableColumn<Alerte, Integer> DateColoumn;

    @FXML
    private TextField Trecherche;

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
    private Label labelTotalStock;

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_11;

    @FXML
    private Pane pane_111;

    @FXML
    void UpdateAlert(ActionEvent event) {

    }

    @FXML
    void deleteAlerte(ActionEvent event) {

    }

    @FXML
    void getData(MouseEvent event) {

    }

    @FXML
    void handlePaneClick(MouseEvent event) {

    }

    @FXML
    void initialize() {
        loadAlerts();
        DateColoumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        id_alerteColumn.setCellValueFactory(new PropertyValueFactory<>("id_alerte"));
        idstockCoulmn.setCellValueFactory(new PropertyValueFactory<>("id_stock"));
    }

    public static void insertAlert(int id_stock, java.util.Date date) {

        try{
            Connection connection = MyConnection.getInstance().getCnx();
            // Assuming you have a table named "alerts" with columns "id_stock" and "alert_date"
            String query = "INSERT INTO alerte (id_stock, date_alerte) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_stock);
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately based on your application's needs
        }
    }

    private void loadAlerts() {
        // Fetch alerts from the database
        List<Alerte> alerts = getAlertsFromDatabase();

        // Update the AlerteTableView with the fetched alerts
        AlerteTableView.getItems().setAll(alerts);
    }

    private List<Alerte> getAlertsFromDatabase() {
        // Fetch alerts from the database using JDBC
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

                    // Create Alerte object and add to the list
                    Alerte alerte = new Alerte(id_alerte, id_stock ,date);
                    alerts.add(alerte);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately based on your application's needs
        }

        return alerts;
    }
}
