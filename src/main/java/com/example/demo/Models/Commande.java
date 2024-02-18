package com.example.demo.Models;

import java.util.Date;

public class Commande {
    private int id_commande;
    private int id_lc;
    private Date date_commande;
    private int id_client;
    private float total_commande;
    private int nbrecmdParclient;

    public int getId_commande() {
        return id_commande;
    }

    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
    }

    public int getId_lc() {
        return id_lc;
    }

    public void setId_lc(int id_lc) {
        this.id_lc = id_lc;
    }

    public Date getDate_commande() {
        return date_commande;
    }

    public void setDate_commande(Date date_commande) {
        this.date_commande = date_commande;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public float getTotal_commande() {
        return total_commande;
    }

    public void setTotal_commande(float total_commande) {
        this.total_commande = total_commande;
    }

    public int getNbrecmdParclient() {
        return nbrecmdParclient;
    }

    public void setNbrecmdParclient(int nbrecmdParclient) {
        this.nbrecmdParclient = nbrecmdParclient;
    }

    public Commande() {
    }

    public Commande(int id_commande, int id_lc, Date date_commande, int id_client, float total_commande, int nbrecmdParclient) {
        this.id_commande = id_commande;
        this.id_lc = id_lc;
        this.date_commande = date_commande;
        this.id_client = id_client;
        this.total_commande = total_commande;
        this.nbrecmdParclient = nbrecmdParclient;
    }
}
