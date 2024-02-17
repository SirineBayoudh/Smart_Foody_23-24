package com.example.demo.Models;


public class Stock {
    private int id_s;
    private Produit produit;
    private int quantite;

    private String Nom ;
    private int nbVendu;
    private  float cout ;
    public Stock(int id_s, Produit produit, int quantite, String nom, int nbVendu, float cout) {
        this.id_s = id_s;
        this.produit = produit;
        this.quantite = quantite;
        Nom = nom;
        this.nbVendu = nbVendu;
        this.cout = cout;
    }
    public Stock(int id_s, Produit produit, int quantite, int nbVendu, float cout) {
        this.id_s = id_s;
        this.produit = produit;
        this.quantite = quantite;
        this.nbVendu = nbVendu;
        this.cout = cout;
    }

    public Stock() {
    }

    public Stock(int id_s, Produit produit, int quantite, int nbVendu) {
        this.id_s = id_s;
        this.produit = produit;
        this.quantite = quantite;
        this.nbVendu = nbVendu;
    }



    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }
    public float getCout() {
        return cout;
    }

    public void setCout(float cout) {
        this.cout = cout;
    }

    public int getId_s() {
        return id_s;
    }

    public void setId_s(int id_s) {
        this.id_s = id_s;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getNbVendu() {
        return nbVendu;
    }

    public void setNbVendu(int nbVendu) {
        this.nbVendu = nbVendu;
    }


    public String getProduitRef() {
        return String.valueOf(produit.getRef());
    }

    public String getProduitMarque() {
        return produit.getMarque();
    }
    @Override
    public String toString() {
        return "Stock{" +
                "id_s=" + id_s +
                ", produit=" + produit +
                ", quantite=" + quantite +
                ", nbVendu=" + nbVendu +
                '}';
    }
}