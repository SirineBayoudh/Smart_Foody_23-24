package com.example.demo;

import com.example.demo.Controllers.Encryptor;
import com.example.demo.Controllers.UserCrud;
import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Smart foody");
        //stage.setHeight(700);
        //stage.setWidth(1200);
        stage.setScene(scene);
        stage.show();

        Encryptor encryptor = new Encryptor();

        MyConnection mc = MyConnection.getInstance();
        //String password = "Admin123";
        //Utilisateur u1 = new Utilisateur("Ben Hamida","Nourhene","femme","nourhene@esprit.tn","nourhene123", Role.Client.toString(),0,"","menzah1","perdre");
        //Utilisateur u2 = new Utilisateur("DevAholics","Admin","homme","admin@esprit.tn",encryptor.encryptString(password),98654145,Role.Admin.toString(),0,"","","");

        UserCrud ucd = new UserCrud();

        //************* Ajout *****************
        //ucd.ajouterEntite(u2);

        //************* Modification ************
        //u1.setId_utilisateur(1);
        //u1.setAdresse("el menzah 1");
        //ucd.modifierEntite(u1);

        //String password = "nourhene";
        //System.out.println(encryptor.encryptString(password));
        //Utilisateur u2 = new Utilisateur("ben","Mohamed","homme","mohamed@esprit.tn",encryptor.encryptString(password),1478569,Role.Client.toString(),0,"","zfjn","ezdg");
        //ucd.ajouterEntite(u2);


    }
}