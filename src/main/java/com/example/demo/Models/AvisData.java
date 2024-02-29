package com.example.demo.Models;

import java.util.Date;

//modèle temporaire pour stocker les données d'un avis
public class AvisData {
    private int id_avis;
    private String nom;
    private String prenom;
    private Date dateAvis;
    private int nbEtoiles;
    private String commentaire;

    private int id_client;

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public AvisData() {
    }

    public AvisData(String nom, String prenom, Date dateAvis, int nbEtoiles, String commentaire) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateAvis = dateAvis;
        this.nbEtoiles = nbEtoiles;
        this.commentaire = commentaire;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateAvis() {
        return dateAvis;
    }

    public void setDateAvis(Date dateAvis) {
        this.dateAvis = dateAvis;
    }

    public int getNbEtoiles() {
        return nbEtoiles;
    }

    public void setNbEtoiles(int nbEtoile) {
        this.nbEtoiles = nbEtoile;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getId_avis() {
        return id_avis;
    }

    public void setId_avis(int id_avis) {
        this.id_avis = id_avis;
    }

    @Override
    public String toString() {
        return "AvisData{" +
                "id_avis=" + id_avis +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateAvis=" + dateAvis +
                ", nbEtoiles=" + nbEtoiles +
                ", commentaire='" + commentaire + '\'' +
                ", id_client=" + id_client +
                '}';
    }
}