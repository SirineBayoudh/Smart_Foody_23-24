package com.example.demo;




//import com.example.demo.Controllers.AlerteController;

import com.example.demo.Controllers.AlerteController;
import com.example.demo.Controllers.LanguageManager;
import com.example.demo.Controllers.StockController;
import com.example.demo.Models.Stock;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.Locale;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Scene scene = new Scene(root);
        stage.setHeight(700);
        stage.setWidth(1300);
        stage.setTitle("Smart foody");
        stage.setScene(scene);
        stage.show();
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
                .withSchedule(simpleSchedule().simpleSchedule().withIntervalInSeconds(600).repeatForever())
                .build();

        scheduler.scheduleJob(job, cronTrigger);
    }

    public static class    BankTransferJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            // Use JobDataMap to retrieve the AlerteController instance
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            AlerteController alerteController = (AlerteController) jobDataMap.get("alerteController");

            if (alerteController != null) {
                StockController stockController = StockController.getInstance();
                Stock lowestStock = stockController.findLowestStock(stockController.displayAllStock());

                if (lowestStock != null) {
                    String message = "Le stock avec l'ID " + lowestStock.getId_s() + " contient la plus petite quantité : " + lowestStock.getQuantite();
                    alerteController.insertAlert(lowestStock.getId_s(), new Date(), message,false);
                    System.out.println(lowestStock.getNbVendu());
                    System.out.println(lowestStock.getId_s());
                    //
                    System.out.println(lowestStock.getQuantite());
                    //
                    // affichage de notification
                    Platform.runLater(() -> showNotification("Stock Notification", message, Alert.AlertType.INFORMATION));

                    // Check if nbVendu equals quantite

                } else {
                    Platform.runLater(() -> showNotification("Stock Notification", "No stock data available.", Alert.AlertType.WARNING));
                }
            } else {
                System.err.println("AlerteController is null. Make sure it's properly set before scheduling the job.");
            }
        }

            public static void main (String[]args){
                launch(args);
            }

            private static void showNotification (String title, String message, Alert.AlertType alertType){
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }
        }
    }
