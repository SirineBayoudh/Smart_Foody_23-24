package com.example.demo.Models;




import java.util.ArrayList;
import java.util.List;

public class Panier {
    private int id_panier;
    private List<Produit> produits;
    private float prixTotal;
    private float prixRemise;

    public Panier() {
        this.produits = new ArrayList<>();
        this.prixTotal = 0;
        this.prixRemise = 0;
    }

    public void ajouterProduit(Produit produit) {
        produits.add(produit);
        recalculerPrixTotal();
    }

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

    // Getters et Setters
    public List<Produit> getProduits() { return produits; }
    public void setProduits(List<Produit> produits) { this.produits = produits; }
    public float getPrixTotal() { return prixTotal; }
    public float getPrixRemise() { return prixRemise; }
    public void setPrixRemise(float prixRemise) {
        this.prixRemise = prixRemise;
        recalculerPrixTotal(); // Vous voudrez peut-être recalculer le total si la remise affecte le prix total
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id_panier=" + id_panier +
                ", produits=" + produits +
                ", prixTotal=" + prixTotal +
                ", prixRemise=" + prixRemise +
                '}';
    }
}
