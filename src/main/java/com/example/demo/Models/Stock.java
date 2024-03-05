package com.example.demo.Models;
import javafx.beans.property.*;

public class Stock {
    private int id_s;
    private Produit produit;
    private int quantite;

    private String Nom ;
    private int nbVendu;
    private final FloatProperty cout = new SimpleFloatProperty();

    public Stock(int id_s, Produit produit, int quantite, String nom, int nbVendu, float cout) {
        this.id_s = id_s;
        this.produit = produit;
        this.quantite = quantite;
        this.Nom = nom;
        this.nbVendu = nbVendu;
        this.cout.set(cout); // Initialisez la propriété observable cout avec la valeur fournie
    }

    // Ajoutez les méthodes getters et setters uniquement pour la propriété cout

    public FloatProperty coutProperty() {
        return cout;
    }

    private LigneCommande ligneCommande;

//    public Stock(int id_s, Produit produit, int quantite, String nom, int nbVendu, float cout) {
//        this.id_s = id_s;
//        this.produit = produit;
//        this.quantite = quantite;
//        Nom = nom;
//        this.nbVendu = nbVendu;
//        this.cout = cout;
//    }
//    public Stock(int id_s, Produit produit, int quantite, int nbVendu, float cout) {
//        this.id_s = id_s;
//        this.produit = produit;
//        this.quantite = quantite;
//        this.nbVendu = nbVendu;
//        this.cout = cout;
//    }

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
        return cout.get();
    }

    public void setCout(float cout) {
        this.cout.set(cout);
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

    public LigneCommande getLigneCommande() {
        return ligneCommande;
    }

    public void setLigneCommande(LigneCommande ligneCommande) {
        this.ligneCommande = ligneCommande;
    }

    // Add a method to retrieve id_lc associated with a stock entry
    public int getIdLigneCommande() {
        return (ligneCommande != null) ? ligneCommande.getId_lc() : 0;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id_s=" + id_s +
                ", produit=" + produit +
                ", quantite=" + quantite +
                ", Nom='" + Nom + '\'' +
                ", nbVendu=" + nbVendu +
                ", cout=" + cout +
                ", ligneCommande=" + ligneCommande +
                '}';
    }
    private String coutWithSymbol; // Nouvelle propriété pour stocker le coût avec le symbole de l'euro

    // ... Autres méthodes

    public void setCoutWithSymbol(String coutWithSymbol) {
        this.coutWithSymbol = coutWithSymbol;
    }

    public String getCoutWithSymbol() {
        return coutWithSymbol;
    }

    public int calculateMargin() {
        return getQuantite() - getNbVendu();
    }


}