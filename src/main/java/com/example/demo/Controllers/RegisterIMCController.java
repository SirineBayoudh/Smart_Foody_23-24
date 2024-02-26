package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterIMCController implements Initializable {

    @FXML
    private Spinner<Double> spinnerTaille;

    @FXML
    private Spinner<Double> spinnerPoids;

    @FXML
    private Label IMC;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Double> tailleValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 300.0, 170.0);
        SpinnerValueFactory<Double> poidsValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 500.0, 70.0);


        // Définir les SpinnerValueFactory pour les Spinners
        spinnerTaille.setValueFactory(tailleValueFactory);
        spinnerPoids.setValueFactory(poidsValueFactory);

        spinnerTaille.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());
        spinnerPoids.valueProperty().addListener((observable, oldValue, newValue) -> calculerIMC());

        IMC.setText(String.valueOf(0));

        // Calculer l'IMC initial
        calculerIMC();
    }

    private void calculerIMC() {
        double taille = spinnerTaille.getValue() / 100.0; // Convertir la taille en mètres
        double poids = spinnerPoids.getValue();
        double imc = poids / (taille * taille);

        // Mettre à jour le label IMC avec le résultat
        IMC.setText(String.format("IMC : %.2f", imc));
    }
}
