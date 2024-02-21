package com.example.demo.Models;

public class Produit {
    private int ref;
    private String marque;
    private String categorie;
    private float prix;
    private  String image ;
    private String critere ;

    private String objectif ;

    public Produit() {
    }

    public Produit(int ref, String marque, String categorie, float prix) {
        this.ref = ref;
        this.marque = marque;
        this.categorie = categorie;
        this.prix = prix;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCritere() {
        return critere;
    }

    public void setCritere(String critere) {
        this.critere = critere;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }
}
