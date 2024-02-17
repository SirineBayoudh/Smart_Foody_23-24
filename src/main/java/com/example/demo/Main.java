package com.example.demo;

import com.example.demo.Controllers.AlerteController;
import com.example.demo.Controllers.StockController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Scene scene = new Scene(root);
        stage.setHeight(700);
        stage.setWidth(1200);
        stage.setTitle("Smart foody");
        stage.setScene(scene);
        stage.show();
        AlerteController alerteController = new AlerteController();
        // Schedule the job to run every 5 seconds
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        JobDetail job = newJob(BankTransferJob.class)
                .withIdentity("bank-transfer")
                .build();

        // Get the JobDataMap from the JobDetail
        JobDataMap jobDataMap = job.getJobDataMap();

        // Put AlerteController instance into the JobDataMap
        jobDataMap.put("alerteController", alerteController);

        Trigger cronTrigger = newTrigger()
                .withIdentity("cron-trigger")
                .startNow()
                .withSchedule(simpleSchedule().simpleSchedule().withIntervalInSeconds(60 ).repeatForever())
                .build();

        scheduler.scheduleJob(job, cronTrigger);
    }

    public static class BankTransferJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            // Use JobDataMap to retrieve the AlerteController instance
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            AlerteController alerteController = (AlerteController) jobDataMap.get("alerteController");

            if (alerteController != null) {
                // Your logic with alerteController
                StockController stockController = StockController.getInstance();
                stockController.checkStockAndDisplayLowestQuantity();
                int lowestStockId = stockController.getLowestStockId();

                Date currentDate = new Date();  // Use a proper way to get the current date and time

                // Call the insertAlert method in AlerteController
                alerteController.insertAlert(lowestStockId, currentDate);


            } else {
                System.err.println("AlerteController is null. Make sure it's properly set before scheduling the job.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
