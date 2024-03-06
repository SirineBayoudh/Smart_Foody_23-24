package com.example.demo.Models;

public class LigneCommande {
    private int id_lc;
    private int id_commande;
    private int quantite ;
    private int  ref_produit ;

    public LigneCommande(int id_lc, int id_commande, int quantite, int ref_produit) {
        this.id_lc = id_lc;
        this.id_commande = id_commande;
        this.quantite = quantite;
        this.ref_produit = ref_produit;
    }

    public int getId_lc() {
        return id_lc;
    }

    public void setId_lc(int id_lc) {
        this.id_lc = id_lc;
    }

    public int getId_commande() {
        return id_commande;
    }

    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getRef_produit() {
        return ref_produit;
    }

    public void setRef_produit(int ref_produit) {
        this.ref_produit = ref_produit;
    }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id_lc=" + id_lc +
                ", id_commande=" + id_commande +
                ", quantite=" + quantite +
                ", ref_produit=" + ref_produit +
                '}';
    }
}
