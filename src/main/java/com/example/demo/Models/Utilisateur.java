package com.example.demo.Models;

import java.util.Objects;

public class Utilisateur implements Comparable<Utilisateur>{
    private int id_utilisateur;
    private String nom,prenom,genre,email,mot_de_passe,role;
    private int num_tel,matricule;
    private String attestation,adresse,objectif;

    private int tentative;

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id) {
        this.id_utilisateur = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public int getNum_tel() {
        return num_tel;
    }

    public void setNum_tel(int num_tel) {
        this.num_tel = num_tel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getMatricule() {
        return matricule;
    }

    public void setMatricule(int matricule) {
        this.matricule = matricule;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public int getTentative() {
        return tentative;
    }

    public void setTentative(int tentative) {
        this.tentative = tentative;
    }

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String genre, String email, String mdp, int num_tel,String role, int matricule, String attestation) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.email = email;
        this.mot_de_passe = mdp;
        this.num_tel = num_tel;
        this.role = role;
        this.matricule = matricule;
        this.attestation = attestation;
    }

    public Utilisateur(String nom, String prenom, String genre, String email, String mdp,int num_tel, String role, String adresse, String objectif) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.email = email;
        this.mot_de_passe = mdp;
        this.num_tel = num_tel;
        this.role = role;
        this.adresse = adresse;
        this.objectif = objectif;
    }

    public Utilisateur(String nom, String prenom, String genre, String email, String mot_de_passe,int num_tel, String role, int matricule, String attestation, String adresse, String objectif) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.email = email;
        this.mot_de_passe = mot_de_passe;
        this.num_tel = num_tel;
        this.role = role;
        this.matricule = matricule;
        this.attestation = attestation;
        this.adresse = adresse;
        this.objectif = objectif;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id_utilisateur=" + id_utilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", genre='" + genre + '\'' +
                ", email='" + email + '\'' +
                ", mot_de_passe='" + mot_de_passe + '\'' +
                ", role='" + role + '\'' +
                ", num_tel=" + num_tel +
                ", matricule=" + matricule +
                ", attestation='" + attestation + '\'' +
                ", adresse='" + adresse + '\'' +
                ", objectif='" + objectif + '\'' +
                '}';
    }

    @Override
    public int compareTo(Utilisateur u) {
        return this.nom.compareTo(u.nom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(nom, that.nom) && Objects.equals(prenom, that.prenom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom);
    }
}
