package com.example.demo.Models;


public class LigneCommande {
    private int idLc;
    private int idCommande;
    private int quantite;
    private String refProduit;

    public LigneCommande(int idLc, int idCommande, int quantite, String refProduit) {
        this.idLc = idLc;
        this.idCommande = idCommande;
        this.quantite = quantite;
        this.refProduit = refProduit;
    }

    // Getters et setters
    public int getIdLc() {
        return idLc;
    }

    public void setIdLc(int idLc) {
        this.idLc = idLc;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
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
