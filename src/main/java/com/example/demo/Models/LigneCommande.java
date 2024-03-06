package com.example.demo.Models;


public class LigneCommande {
    private int idLc;
    private int idPanier;
    private int quantite;
    private String refProduit;

    public LigneCommande(int idLc, int idPanier, int quantite, String refProduit, int idCommande) {
        this.idLc = idLc;
        this.idPanier = idPanier;
        this.quantite = quantite;
        this.refProduit = refProduit;
    }

    // Getters et setters


    public int getIdPanier() {
        return idPanier;
    }

    public void setIdPanier(int idPanier) {
        this.idPanier = idPanier;
    }

    public int getIdLc() {
        return idLc;
    }

    public void setIdLc(int idLc) {
        this.idLc = idLc;
    }


    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getRefProduit() {
        return refProduit;
    }

    public void setRefProduit(String refProduit) {
        this.refProduit = refProduit;
    }
}
