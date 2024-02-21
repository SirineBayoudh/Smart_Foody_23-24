package com.example.demo.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    public String url="jdbc:mysql://localhost:3306/smart" ;
    public String login="root";
    public String pwd="" ;
    Connection cnx;

    private int userId;
    public static MyConnection instance ;
    private MyConnection()
    {
        try {
            cnx= DriverManager.getConnection(url,login,pwd);
            System.out.println("Connexion établie ! ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Connection getCnx() {
        return cnx;
    }
    public static MyConnection getInstance(){
        if(instance == null){
            instance=new MyConnection() ;
        }
        return instance ;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
