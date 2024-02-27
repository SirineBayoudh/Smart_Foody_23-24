package com.example.demo.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class dashboardController implements Initializable , LanguageObserver{
    private StockController stockController; // Instance of StockController

    // Setter for StockController
    public void setStockController(StockController stockController) {
        this.stockController = stockController;
    }
    private AlerteController alerteController; // Instance of StockController

    // Setter for StockController
    public void setAlerteController(AlerteController alerteController) {
        this.alerteController = alerteController;
    }
    @FXML
    private Button btn1;

    @FXML
    private Button btn11;

    @FXML
    private Button btn2;

    @FXML
    private Button btn21;

    @FXML
    private Button btn22;

    @FXML
    private Button btn221;

    @FXML
    private Button btn2211;

    @FXML
    private Button btn3;

    @FXML
    private Pane  innerPane;
    @FXML
    private Button btn_home;
    @FXML
    private Button btn;
    @FXML
    private RadioButton englishRadioButton;
    @FXML
    private RadioButton frenchRadioButton;
    @FXML
    private BorderPane centerPane;
    @FXML
    private Text tt;

    private boolean isClicked = false;

    private Locale selectedLocale;  // Added field to store the selected locale

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedLocale = Locale.FRENCH;  // Set a default locale
        languageManager.initialize(selectedLocale);
        languageManager.addObserver(this);
        updateLabels();
    }


    @FXML
    public void initialize() {

    }
    @FXML
    private void dashboard() {
        centerPane.setCenter(innerPane);
        btn_home.getStyleClass().add("btn_home");

    }
 public void clear(){
     btn.setTextFill(Color.WHITE);
 }

    @FXML
    private void stock() {
        //btn.setStyle("-fx-text-fill: WHITE;-fx-font-size: 16px;-fx-border-width: 2px;-fx-font-weight: bold; -fx-border-radius: 20px;");
        btn.setTextFill(Color.BLACK);
   saveSelectedLocale();
               loadPage("/com/example/demo/stock.fxml");
    }
    @FXML
    private void alerte() {
       clear();
       saveSelectedLocale();

                loadPage("/com/example/demo/Alerte.fxml");
    }

    private void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page));
            centerPane.setCenter(root);

            // After loading the page, set the language manager's locale to the saved locale
            languageManager.initialize(selectedLocale);

            // Update labels in other sections if needed
            if (stockController != null) {
                stockController.updateLabels();
            }
            if (alerteController != null) {
                alerteController.updateLabels();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private LanguageManager languageManager = LanguageManager.getInstance();


    public  void updateLabels() {
        btn_home.setText(languageManager.getText("btn_home"));
        btn.setText(languageManager.getText("btn"));
        btn2.setText(languageManager.getText("btn2"));
        btn3.setText(languageManager.getText("btn3"));
        btn21.setText(languageManager.getText("btn21"));
        btn22.setText(languageManager.getText("btn22"));
        btn221.setText(languageManager.getText("btn221"));
        btn1.setText(languageManager.getText("btn1"));
        btn11.setText(languageManager.getText("btn11"));
        btn2211.setText(languageManager.getText("btn2211"));
        // Mettez à jour d'autres libellés du dashboardController...
    }
    @FXML
    private void onFrenchSelected(ActionEvent event) {
        selectedLocale = Locale.FRENCH;
        languageManager.initialize(selectedLocale);
        updateLabels();
        // Update labels in StockController and AlerteController
        if (stockController != null) {
            stockController.updateLabels();
        }
        if (alerteController != null) {
            alerteController.updateLabels();
        }
        englishRadioButton.setSelected(false);
    }

    @FXML
    private void onEnglishSelected(ActionEvent event) {
        selectedLocale = Locale.ENGLISH;
        languageManager.initialize(selectedLocale);
        updateLabels();
        // Update labels in StockController and AlerteController
        if (stockController != null) {
            stockController.updateLabels();
        }
        if (alerteController != null) {
            alerteController.updateLabels();
        }
        frenchRadioButton.setSelected(false);
    }



    private void saveSelectedLocale() {
        selectedLocale = languageManager.getCurrentLocale();
    }





    @Override
    public void onLanguageChanged() {
        updateLabels();
    }
}
