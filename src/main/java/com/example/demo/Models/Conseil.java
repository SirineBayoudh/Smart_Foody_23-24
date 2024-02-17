package com.example.demo.Models;

import java.sql.Date;

public class Conseil {
    private int id_conseil;
    private int id_client;
    private int matricule;
    private String statut;
    private String demande;
    private String reponse;
    private int note;
    private Date date_conseil;

    public int getId_conseil() {
        return id_conseil;
    }

    public void setId_conseil(int id_conseil) {
        this.id_conseil = id_conseil;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public int getMatricule() {
        return matricule;
    }

    public void setMatricule(int matricule) {
        this.matricule = matricule;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDemande() {
        return demande;
    }

    public void setDemande(String demande) {
        this.demande = demande;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Date getDate_conseil() {
        return date_conseil;
    }

    public void setDate_conseil(Date date_conseil) {
        this.date_conseil = date_conseil;
    }
}
