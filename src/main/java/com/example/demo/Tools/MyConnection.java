package com.example.demo.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    public String url="jdbc:mysql://localhost:3306/smart";
    public String login="root";
    public String pwd="";
    Connection cnx;
    public static MyConnection instance; //2eme étape cette ligne
    //1ere étape : public MyConnection => private MyConnection
    private MyConnection(){
        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            //System.out.println("Connexion établie!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Connection getCnx() {
        return cnx;
    }

    //3eme étape cette méthode
    public static MyConnection getInstance(){
        if(instance == null){
            instance = new MyConnection();
        }
        return instance;
    }
}
