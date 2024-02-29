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
import javafx.scene.control.*;
import javafx.scene.text.Text;
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
        stage.setHeight(720);
        stage.setWidth(1200);
        stage.setTitle("Smart foody");
        stage.setScene(scene);
        stage.show();
        AlerteController alerteController = new AlerteController();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();  //Initialise un planificateur Quartz Scheduler.
        scheduler.start();

        JobDetail job = newJob(BankTransferJob.class)  // cree une tâche qui sera exécutée périodiquement.
                .withIdentity("bank-transfer")
                .build();

        JobDataMap jobDataMap = job.getJobDataMap();   // Récupère la JobDataMap associée à la tâche
        jobDataMap.put("alerteController", alerteController);  //permettra à la tâche d'accéder à l'AlerteController lors de son exécution.

        Trigger cronTrigger = newTrigger()  //déclencheur (Trigger) qui spécifie le calendrier pour l'exécution de la tâche
                .withIdentity("cron-trigger")
                .startNow()
                .withSchedule(simpleSchedule().simpleSchedule().withIntervalInSeconds(600).repeatForever())
                .build();

        scheduler.scheduleJob(job, cronTrigger);  //Planifie la tâche (BankTransferJob) avec le déclencheur dans le planificateur.
    }

    public static class    BankTransferJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            // Use JobDataMap to retrieve the AlerteController instance
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();  //Récupère la JobDataMap de la tâche pour accéder aux données associées à la tâche.
            AlerteController alerteController = (AlerteController) jobDataMap.get("alerteController"); //permet à la tâche d'accéder à l'AlerteController.

            if (alerteController != null) {  //Vérifie si l'AlerteController a été correctement récupéré.
                StockController stockController = StockController.getInstance();
                Stock lowestStock = stockController.findLowestStock(stockController.displayAllStock());

                if (lowestStock != null) {
                    String message = "Le stock avec l'ID " + lowestStock.getId_s() + " contient la plus petite quantité : " + lowestStock.getQuantite();
                    alerteController.insertAlert(lowestStock.getId_s(), new Date(), message,false);
                    System.out.println(lowestStock.getNbVendu());
                    System.out.println(lowestStock.getId_s());

                    System.out.println(lowestStock.getQuantite());
                 //pour exécuter l'affichage de la notification sur le thread JavaFX.
                    Platform.runLater(() -> showNotification("Stock Notification", message, Alert.AlertType.INFORMATION));


                } else {
                    Platform.runLater(() -> showNotification("Stock Notification", "Pas de stock .", Alert.AlertType.WARNING));
                }
            } else {
                System.err.println("Alerte Controller is null. Make sure it's properly set before scheduling the job.");
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
                alert.setContentText("");  // Set an empty text to clear default styling

                Text contentText = new Text(message);
                contentText.setStyle("-fx-fill:#66b343; -fx-alignment: center;-fx-font-size: 15;");
                alert.getDialogPane().setContent(contentText);

                alert.getDialogPane().setGraphic(null);

                //dialogPane.setStyle("-fx-background-color: linear-gradient(#a9ff00, #00ff00);");
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
        }
    }
