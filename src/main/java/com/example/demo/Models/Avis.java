package com.example.demo.Models;

public class Avis {
    int id_avis;
    int ref_produit;
    int nb_etoiles;
    String commentaire;

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

    public Avis() {
    }

    public Avis(int id_avis, int ref_produit, int nb_etoiles, String commentaire) {
        this.id_avis = id_avis;
        this.ref_produit = ref_produit;
        this.nb_etoiles = nb_etoiles;
        this.commentaire = commentaire;
    }

    public Avis(int ref_produit,int nb_etoiles, String commentaire) {
        this.ref_produit = ref_produit;
        this.nb_etoiles = nb_etoiles;
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "Avis{" +
                "id_avis=" + id_avis +
                ", ref_produit=" + ref_produit +
                ", nb_etoiles=" + nb_etoiles +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}
