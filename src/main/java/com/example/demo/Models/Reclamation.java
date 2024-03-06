package com.example.demo.Models;

import java.util.Date;

public class Reclamation {
    private int id_reclamation;
    private int id_client;
    private String description;
    private String titre;
    private String statut;
    private String type;
    private Date date_reclamation;
    private int archive;

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public int getId_reclamation() {
        return id_reclamation;
    }

    public int getId_client() {
        return id_client;
    }

    public String getDescription() {
        return description;
    }

    public String getTitre() {
        return titre;
    }

    public String getStatut() {
        return statut;
    }

    public String getType() {
        return type;
    }

    public Date getDate_reclamation() {
        return date_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate_reclamation(Date date_reclamation) {
        this.date_reclamation = date_reclamation;
    }

    public Reclamation() {
    }
    public Reclamation(int id_reclamation, int id_client, String description, String titre, String statut, String type, Date date_reclamation, int archive) {
        this.id_reclamation = id_reclamation;
        this.id_client = id_client;
        this.description = description;
        this.titre = titre;
        this.statut = statut;
        this.type = type;
        this.date_reclamation = date_reclamation;
        this.archive = archive;
    }
    public Reclamation(int id_reclamation, int id_client, String description, String titre, String statut, String type, Date date_reclamation) {
        this.id_reclamation = id_reclamation;
        this.id_client = id_client;
        this.description = description;
        this.titre = titre;
        this.statut = statut;
        this.type = type;
        this.date_reclamation = date_reclamation;
    }

    public Reclamation(int id_client, String description, String titre, String type) {
        this.id_client = id_client;
        this.description = description;
        this.titre = titre;
        this.type = type;
    }
    public Reclamation(int id_client, String description, String titre,String statut, String type) {
        this.id_client = id_client;
        this.description = description;
        this.titre = titre;
        this.statut = statut;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id_reclamation=" + id_reclamation +
                ", id_client=" + id_client +
                ", description='" + description + '\'' +
                ", titre='" + titre + '\'' +
                ", statut='" + statut + '\'' +
                ", type='" + type + '\'' +
                ", date_reclamation=" + date_reclamation +
                '}';
    }
}
