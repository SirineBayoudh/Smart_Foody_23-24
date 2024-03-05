package com.example.demo.Models;

import java.util.ArrayList;
import java.util.List;

public class Objectif {
    private int id_obj;
    private String libelle;
    private List<ListCritere> listCritere;

    public Objectif(int id_obj, String libelle, List<ListCritere>  listCritere) {
        this.id_obj = id_obj;
        this.libelle = libelle;
        this.listCritere = listCritere;
    }

    public Objectif() {
        this.listCritere = new ArrayList<>();
    }

    public Objectif(int id_obj, String libelle) {
        this.id_obj = id_obj;
        this.libelle = libelle;
        this.listCritere = new ArrayList<>();
    }

    public void setListCritere(List<ListCritere> listCritere) {
        this.listCritere = listCritere;
    }

    public int getId_obj() {
        return id_obj;
    }

    public void setId_obj(int id_obj) {
        this.id_obj = id_obj;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<ListCritere> getListCritere() {
        return listCritere;
    }

    public void addCritere(ListCritere critere) {
        listCritere.add(critere);
    }

    public String getListCritereAsString() {
        StringBuilder sb = new StringBuilder();
        for (ListCritere critere : listCritere) {
            sb.append(critere.toString()).append(", ");
        }
        // Supprimer la virgule et l'espace supplémentaires à la fin
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }

    @Override
    public String toString() {
        return "Objectif{" +
                "id_obj=" + id_obj +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
