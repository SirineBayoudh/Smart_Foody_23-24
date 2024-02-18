package com.example.demo;

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
        stage.setScene(scene);
        stage.show();


        MyConnection mc = MyConnection.getInstance();
        Utilisateur u1 = new Utilisateur("Ben Hamida","Nourhene","femme","nourhene@esprit.tn","nourhene123", Role.Client.toString(),0,"","menzah1","perdre");
        Utilisateur u2 = new Utilisateur("Selmi","Amine","homme","amine@esprit.tn","amine123",Role.Conseiller.toString(),14578962,"aaaaa","","");

        UserCrud ucd = new UserCrud();

        //************* Ajout *****************
        //ucd.ajouterEntite(u2);

        //************* Modification ************
        //u1.setId_utilisateur(1);
        //u1.setAdresse("el menzah 1");
        //ucd.modifierEntite(u1);


    }
}