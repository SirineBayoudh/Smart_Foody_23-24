package com.example.demo.Models;

public class Produit {
    private String ref;
    private String marque;
    private String categorie;
    private float prix;
    private String image;

    public Produit(String ref, String marque, String categorie, float prix, String image, String objectif, String critere) {
        this.ref = ref;
        this.marque = marque;
        this.categorie = categorie;
        this.prix = prix;
        this.image = image;
    }

    // Getters et Setters
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public float getPrix() { return prix; }
    public void setPrix(float prix) { this.prix = prix; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "Produit{" +
                "ref='" + ref +
                ", marque='" + marque +
                ", categorie='" + categorie +
                ", prix=" + prix +
                ", image='" + image +
                '}';
    }
}
