package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AccueilDashboardController implements Initializable {

    @FXML
    private AnchorPane chart;

    @FXML
    private Label totalUsers;

    @FXML
    private BarChart<String, Number> barChart;

    GestionUserController gs = new GestionUserController();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalUsers();

        stackedBarChart();
    }

    public void totalUsers() {
        ObservableList<Utilisateur> listUsers = gs.getUtilisateurs();
        int nbUsers = listUsers.size();

        totalUsers.setText(String.valueOf(nbUsers - 1));
    }

    private void stackedBarChart() {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barChart.setTitle("Statistiques par objectif");

        xAxis.setLabel("Objectif");
        yAxis.setLabel("Nombre de clients");

        int BienEtre = 0;
        int PrisePoids = 0;
        int PertePoids = 0;
        int PriseMasseMus = 0;

        ObservableList<Utilisateur> listUsers = gs.getUtilisateurs();

        for (Utilisateur user : listUsers) {
            String objectif = user.getObjectif();
            if (objectif != null) {
                switch (objectif) {
                    case "Bien être":
                        BienEtre++;
                        break;
                    case "Prise de poids":
                        PrisePoids++;
                        break;
                    case "Perte de poids":
                        PertePoids++;
                        break;
                    case "Prise de masse musculaire":
                        PriseMasseMus++;
                        break;
                }
            }
        }

        XYChart.Series<String, Number> objectifSeries = new XYChart.Series<>();
        objectifSeries.getData().add(new XYChart.Data<>("Bien être", BienEtre));
        objectifSeries.getData().add(new XYChart.Data<>("Prise de poids", PrisePoids));
        objectifSeries.getData().add(new XYChart.Data<>("Perte de poids", PertePoids));
        objectifSeries.getData().add(new XYChart.Data<>("Prise de masse musculaire", PriseMasseMus));

        barChart.getData().add(objectifSeries);

        for (XYChart.Data<String, Number> data : objectifSeries.getData()) {
            switch (data.getXValue()) {
                case "Bien être":
                    data.getNode().getStyleClass().add("objectif1");
                    break;
                case "Prise de poids":
                    data.getNode().getStyleClass().add("objectif2");
                    break;
                case "Perte de poids":
                    data.getNode().getStyleClass().add("objectif3");
                    break;
                case "Prise de masse musculaire":
                    data.getNode().getStyleClass().add("objectif4");
                    break;
            }
        }

        barChart.getStylesheets().add(getClass().getResource("/com/example/demo/css/style_dash.css").toExternalForm());

    }

}
