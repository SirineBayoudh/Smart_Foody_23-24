package com.example.demo.Models;

public class Produit {
    private Integer ref;
    private String marque;
    private String categorie;
    private Float prix;
    private String image;
    private  String obj;
    private String critere;


    public Produit(String marque, Float prix, String image) {
        this.marque = marque;
        this.prix = prix;
        this.image = image;
    }

    public Produit(String marque, String categorie, String imageUrl) {
        this.marque = marque;
        this.categorie=categorie;
        this.image = image;
    }

    public String getMarque() {
        return marque;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
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

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getCritere() {
        return critere;
    }

    public void setCritere(String critere) {
        this.critere = critere;
    }

    public Produit(String marque, String categorie, Float prix, String image, String obj, String critere) {
        this.marque = marque;
        this.categorie = categorie;
        this.prix = prix;
        this.image = image;
        this.obj = obj;
        this.critere = critere;
    }
    public Produit(){

    }

    @Override
    public String toString() {
        return "Produit{" +
                "marque='" + marque + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix=" + prix +
                ", image='" + image + '\'' +
                ", obj='" + obj + '\'' +
                ", critere='" + critere + '\'' +
                '}';
    }
}
