package com.example.demo.Models;

import java.util.Date;

public class Commande {
    private int id_commande;

    private Date date_commande;
    private int id_client;
    private float total_commande;
    private float total_commande_devise;
    private float remise;
    private String clientUsername;
    private String etat;
    private String address;

    public String getAddress() {
        return address;
    }

    public int getId_commande() {
        return id_commande;
    }

    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
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

    public float getTotal_commande_devise() {
        return total_commande_devise;
    }

    public void setTotal_commande_devise(float total_commande_devise) {
        this.total_commande_devise = total_commande_devise;
    }

    public float getRemise() {
        return remise;
    }

    public void setRemise(float remise) {
        this.remise = remise;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public Commande() {
    }

    public Commande(int id_commande, Date date_commande, int id_client, float total_commande) {
        this.id_commande = id_commande;
        this.date_commande = date_commande;
        this.id_client = id_client;
        this.total_commande = total_commande;
    }

    public Commande(int id_commande, Date date_commande, int id_client, float total_commande, float remise, String etat) {
        this.id_commande = id_commande;
        this.date_commande = date_commande;
        this.id_client = id_client;
        this.total_commande = total_commande;
        this.remise = remise;
        this.etat = etat;
    }



    public Commande(int id_commande, Date date_commande, int id_client, float total_commande, float total_commande_devise, float remise, String clientUsername, String etat, String address) {
        this.id_commande = id_commande;
        this.date_commande = date_commande;
        this.id_client = id_client;
        this.total_commande = total_commande;
        this.total_commande_devise = total_commande_devise;
        this.remise = remise;
        this.clientUsername = clientUsername;
        this.etat = etat;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id_commande=" + id_commande +
                ", date_commande=" + date_commande +
                ", id_client=" + id_client +
                ", total_commande=" + total_commande +
                ", total_commande_devise=" + total_commande_devise +
                ", remise=" + remise +
                ", clientUsername='" + clientUsername + '\'' +
                ", etat='" + etat + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public void setAddress(String address) {
        this.address=address;
    }
}
