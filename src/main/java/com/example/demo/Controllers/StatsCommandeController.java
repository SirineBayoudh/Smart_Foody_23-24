/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.demo.Controllers;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.example.demo.Models.CommandeHolder;
import com.example.demo.Tools.ServiceCommande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class StatsCommandeController implements Initializable {
    @FXML
    private Pane clickpane;
    @FXML
    private BorderPane borderPane;

    private ServiceCommande quest;
    private CommandeHolder holder = CommandeHolder.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            pieChart(null);
        } catch (SQLException ex) {
            Logger.getLogger(StatsCommandeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    private void pieChart(ActionEvent event) throws SQLException {
        quest = new ServiceCommande();

        // create Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                quest.selectByLikes().stream()
                        .map(d -> new PieChart.Data(quest.usernameById(d)+" : "
                                +quest.nbrCommandeByClient(d) +" commandes" , quest.nbrCommandeByClient(d)))
                        .collect(Collectors.toList())
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Clients Fideles");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        //add bar chart to borderPane
        borderPane.setCenter(pieChart);
        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setOnMouseEntered(ev -> {
                borderPane.setCursor(Cursor.HAND);
            });
            data.getNode().setOnMouseExited(ev -> {
                borderPane.setCursor(Cursor.DEFAULT);
            });
            Node node = data.getNode();
            Tooltip tooltip = new Tooltip();
            // set the style of the tooltip
            tooltip.setStyle("-fx-font-size: 16px;");

            tooltip.setText(String.format("%d commandes", (int) data.getPieValue()));
            Tooltip.install(node, tooltip);

            data.getNode().setOnMouseClicked(e -> {
                try {
                    afficherQuestion(e, Integer.parseInt(data.getName().substring(14) ));
                } catch (SQLException ex) {
                    Logger.getLogger(StatsCommandeController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(StatsCommandeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    @FXML
    void afficherQuestion(MouseEvent event, int id) throws SQLException, IOException {


    }

    @FXML
    void switchButton(ActionEvent event) throws IOException {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Commande_dash.fxml"));
            Parent root = loader.load();
            clickpane.getChildren().clear();
            clickpane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

}
