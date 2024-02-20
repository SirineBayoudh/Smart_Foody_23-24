package com.example.demo.Models;

import java.util.Date;

public class Tentative {

    private int id_tentative,id_utilisateur,nb_tentatives;

    private Date date_tentative;

    public int getId_tentative() {
        return id_tentative;
    }

    public void setId_tentative(int id_tentative) {
        this.id_tentative = id_tentative;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public int getNb_tentatives() {
        return nb_tentatives;
    }

    public void setNb_tentatives(int nb_tentatives) {
        this.nb_tentatives = nb_tentatives;
    }

    public Date getDate_tentative() {
        return date_tentative;
    }

    public void setDate_tentative(Date date_tentative) {
        this.date_tentative = date_tentative;
    }

    public Tentative() {
    }

    public Tentative(int id_utilisateur, int nb_tentatives, Date date_tentative) {
        this.id_utilisateur = id_utilisateur;
        this.nb_tentatives = nb_tentatives;
        this.date_tentative = date_tentative;
    }
}
