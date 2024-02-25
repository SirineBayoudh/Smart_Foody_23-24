/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.Models;


/**
 *
 * @author omarb
 */
public class CommandeHolder {

    private Commande cmd;
    private final static CommandeHolder INSTANCE =new CommandeHolder();

    public CommandeHolder() {
    }

    public static CommandeHolder getInstance(){
        return INSTANCE;
    }



    public void setCommande (Commande c){
        this.cmd =c;
    }

    public Commande getCommande (){
        return  this.cmd;
    }

}
