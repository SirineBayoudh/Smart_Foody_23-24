package com.example.demo.Models;




import java.util.ArrayList;
import java.util.List;

public class Panier {
    private int id_panier;
    private int id_client;
    private float prixTotal;
    private float prixRemise;

    public Panier() {
        this.prixTotal = 0;
        this.prixRemise = 0;
    }


/*
    public void supprimerProduit(String ref) {
        produits.removeIf(produit -> produit.getRef().equals(ref));
        recalculerPrixTotal();
    }

    public void afficherProduits() {
        for (Produit produit : produits) {
            System.out.println(produit);
        }
    }

    private void recalculerPrixTotal() {
        prixTotal = 0;
        for (Produit produit : produits) {
            prixTotal += produit.getPrix();
        }
    }
*/

    // Getters et Setters


    public int getId_panier() {
        return id_panier;
    }

    public void setId_panier(int id_panier) {
        this.id_panier = id_panier;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public void setPrixTotal(float prixTotal) {
        this.prixTotal = prixTotal;
    }

    public float getPrixTotal() { return prixTotal; }
    public float getPrixRemise() { return prixRemise; }
    public void setPrixRemise(float prixRemise) {
        this.prixRemise = prixRemise;
        //recalculerPrixTotal(); // Vous voudrez peut-être recalculer le total si la remise affecte le prix total
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id_panier=" + id_panier +
                ", prixTotal=" + prixTotal +
                ", prixRemise=" + prixRemise +
                '}';
    }
}
