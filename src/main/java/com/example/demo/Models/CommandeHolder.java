
package com.example.demo.Models;



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
