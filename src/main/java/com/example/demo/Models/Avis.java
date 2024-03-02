package com.example.demo.Models;

import java.util.Date;

public class Avis {
   private int id_avis;

    private int ref_produit;
    private int nb_etoiles;
    private String commentaire;

    private int id_client;
    private Date date_avis;

    private int signaler;

    public int getSignaler() {
        return signaler;
    }

    public Avis(int id_avis, int ref_produit, int nb_etoiles, String commentaire, int id_client, Date date_avis, int signaler) {
        this.id_avis = id_avis;
        this.ref_produit = ref_produit;
        this.nb_etoiles = nb_etoiles;
        this.commentaire = commentaire;
        this.id_client = id_client;
        this.date_avis = date_avis;
        this.signaler = signaler;
    }

    public void setSignaler(int signaler) {
        this.signaler = signaler;
    }

    public int getId_avis() {
        return id_avis;
    }

    public int getRef_produit() {
        return ref_produit;
    }

    public int getNb_etoiles() {
        return nb_etoiles;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public int getId_client() {
        return id_client;
    }

    public Date getDate_avis() {
        return date_avis;
    }

    public void setId_avis(int id_avis) {
        this.id_avis = id_avis;
    }

    public void setRef_produit(int ref_produit) {
        this.ref_produit = ref_produit;
    }

    public void setNb_etoiles(int nb_etoiles) {
        this.nb_etoiles = nb_etoiles;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public void setDate_avis(Date date_avis) {
        this.date_avis = date_avis;
    }



    public Avis() {
    }

    public Avis(int id_avis, int ref_produit,int id_client, int nb_etoiles, String commentaire, Date date_avis) {
        this.id_avis = id_avis;
        this.ref_produit = ref_produit;
        this.id_client = id_client;
        this.nb_etoiles = nb_etoiles;
        this.commentaire = commentaire;
        this.date_avis = date_avis;
    }

    public Avis(int ref_produit,int id_client,int nb_etoiles, String commentaire) {
        this.ref_produit = ref_produit;
        this.nb_etoiles = nb_etoiles;
        this.commentaire = commentaire;
        this.id_client = id_client;
    }

    @Override
    public String toString() {
        return "Avis{" +
                "id_avis=" + id_avis +
                ", ref_produit=" + ref_produit +
                ", nb_etoiles=" + nb_etoiles +
                ", commentaire='" + commentaire + '\'' +
                ", id_client=" + id_client +
                ", date_avis=" + date_avis +
                ", signaler=" + signaler +
                '}';
    }
}
