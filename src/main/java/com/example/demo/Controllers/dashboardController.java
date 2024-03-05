package com.example.demo.Controllers;

import com.example.demo.Models.Stock;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.Locale;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class dashboardController implements Initializable , LanguageObserver {
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
    private Pane innerPane;
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
        try {
            AlerteController alerteController = new AlerteController();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = newJob(BankTransferJob.class)
                    .withIdentity("bank-transfer")
                    .build();

            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("alerteController", alerteController);

            Trigger cronTrigger = newTrigger()
                    .withIdentity("cron-trigger")
                    .startNow()
                    .withSchedule(simpleSchedule().withIntervalInSeconds(600).repeatForever())
                    .build();

            scheduler.scheduleJob(job, cronTrigger);
        } catch (SchedulerException e) {
            // Log or print the exception and handle it accordingly
            e.printStackTrace();
        }
    }

    public static class BankTransferJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            // Use JobDataMap to retrieve the AlerteController instance
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            AlerteController alerteController = (AlerteController) jobDataMap.get("alerteController");

            if (alerteController != null) {
                StockController stockController = StockController.getInstance();
                ObservableList<Stock> allStock = stockController.displayAllStock();

                Stock lowestMarginStock = stockController.findLowestMarginStock(allStock);

                if (lowestMarginStock != null) {
                    String message = "Le stock de " + lowestMarginStock.getNom() +
                            " contient la plus petite quantité : " + lowestMarginStock.getQuantite() ;

                    alerteController.insertAlert(lowestMarginStock.getId_s(), new Date(), message, false);
                    System.out.println(lowestMarginStock.getNbVendu());
                    System.out.println(lowestMarginStock.getNom());
                    System.out.println(lowestMarginStock.getQuantite());
                    // pour exécuter l'affichage de la notification sur le thread JavaFX.
                    Platform.runLater(() -> showNotification("Stock Notification", message, Alert.AlertType.INFORMATION));
                } else {
                    Platform.runLater(() -> showNotification("Stock Notification", "Pas de stock .", Alert.AlertType.WARNING));
                }
            } else {
                System.err.println("Alerte Controller is null. Make sure it's properly set before scheduling the job.");
            }
        }
    }

    private static void showNotification(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setContentText("");  // Set an empty text to clear default styling

        Text contentText = new Text(message);
        contentText.setStyle("-fx-fill:#66b343; -fx-alignment: center;-fx-font-size: 15;");
        alert.getDialogPane().setContent(contentText);

        alert.getDialogPane().setGraphic(null);

        // dialogPane.setStyle("-fx-background-color: linear-gradient(#a9ff00, #00ff00);");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(400);  // Set your desired minimum width

        // Apply  inline styles to the "OK" button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: " +
                "linear-gradient(#f0ff35, #a9ff00), " +
                "radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%); " +
                "-fx-background-radius: 6,5; " +
                "-fx-background-insets: 0,1; " +
                "-fx-effect: dropshadow(three-pass-box, rgb(255,255,255), 5, 0.0, 0, 1); " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-weight: bold;");

        alert.showAndWait();
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
