package com.example.demo.Controllers;

import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCrud implements ICrud<Utilisateur>{

    static Connection cnx;

    private GestionUserController gestionUserController;

    public void setGestionUserController(GestionUserController gestionUserController) {
        this.gestionUserController = gestionUserController;
    }

    public UserCrud() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouterEntite(Utilisateur u) {
        String requete = "INSERT INTO utilisateur(nom,prenom,genre,email,mot_de_passe,num_tel,role,matricule,attestation,adresse,objectif,tentative) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setString(1, u.getNom());
            pst.setString(2,u.getPrenom());
            pst.setString(3, u.getGenre());
            pst.setString(4, u.getEmail());
            pst.setString(5,u.getMot_de_passe());
            pst.setInt(6,u.getNum_tel());
            pst.setString(7,u.getRole());
            pst.setInt(8,u.getMatricule());
            pst.setString(9,u.getAttestation());
            pst.setString(10,u.getAdresse());
            pst.setString(11, u.getObjectif());
            pst.setInt(12,u.getTentative());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifierEntite(Utilisateur u) {
        String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,mot_de_passe=?,num_tel=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, u.getNom());
            pst.setString(2,u.getPrenom());
            pst.setString(3, u.getGenre());
            pst.setString(4, u.getEmail());
            pst.setString(5,u.getMot_de_passe());
            pst.setInt(6,u.getNum_tel());
            pst.setString(7,u.getRole());
            pst.setInt(8,u.getMatricule());
            pst.setString(9,u.getAttestation());
            pst.setString(10,u.getAdresse());
            pst.setString(11, u.getObjectif());
            pst.setInt(12, u.getId_utilisateur());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimerEntite(Utilisateur u) {
        String req = "DELETE FROM `utilisateur` WHERE `id_utilisateur`=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, u.getId_utilisateur());
            int nb = pst.executeUpdate();
            if (nb > 0) {
                System.out.println("L'utilisateur a été supprimé avec succès.");
            } else {
                System.out.println("Aucun utilisateur n'a été supprimé pour cet ID.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Utilisateur> afficherEntite() {
        List<Utilisateur> ListU = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";
        try{
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while(rs.next()){
                Utilisateur usr = new Utilisateur();
                usr.setId_utilisateur(rs.getInt(1));
                usr.setNom(rs.getString(2));
                usr.setPrenom(rs.getString(3));
                usr.setGenre(rs.getString(4));
                usr.setEmail(rs.getString(5));
                usr.setMot_de_passe(rs.getString(6));
                usr.setNum_tel(rs.getInt(7));
                usr.setRole(rs.getString(8));
                usr.setMatricule(rs.getInt(9));
                usr.setAttestation(rs.getString(10));
                usr.setAdresse(rs.getString(11));
                usr.setObjectif(rs.getString(12));
                ListU.add(usr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ListU;
    }

    public static boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Si count est supérieur à 0, cela signifie qu'un utilisateur avec cet e-mail existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Utilisateur> listerParNom(String nom){
        List<Utilisateur> usersList= new ArrayList<>();
        String req = "SELECT * FROM `utilisateur` WHERE `nom` = ?; ";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, nom.toString());
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                Utilisateur usr = new Utilisateur();
                usr.setId_utilisateur(rs.getInt(1));
                usr.setNom(rs.getString(2));
                usr.setPrenom(rs.getString(3));
                usr.setGenre(rs.getString(4));
                usr.setEmail(rs.getString(5));
                usr.setMot_de_passe(rs.getString(6));
                usr.setNum_tel(rs.getInt(7));
                usr.setRole(rs.getString(8));
                usr.setMatricule(rs.getInt(9));
                usr.setAttestation(rs.getString(10));
                usr.setAdresse(rs.getString(11));
                usr.setObjectif(rs.getString(12));
                usersList.add(usr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return usersList;
    }

    public List<Utilisateur> listeClients(String nom){
        List<Utilisateur> usersList= new ArrayList<>();
        String req = "SELECT * FROM `utilisateur` WHERE `role` = `client` ; ";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, nom.toString());
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                Utilisateur usr = new Utilisateur();
                usr.setId_utilisateur(rs.getInt(1));
                usr.setNom(rs.getString(2));
                usr.setPrenom(rs.getString(3));
                usr.setGenre(rs.getString(4));
                usr.setEmail(rs.getString(5));
                usr.setMot_de_passe(rs.getString(6));
                usr.setNum_tel(rs.getInt(7));
                usr.setRole(rs.getString(8));
                usr.setMatricule(rs.getInt(9));
                usr.setAttestation(rs.getString(10));
                usr.setAdresse(rs.getString(11));
                usr.setObjectif(rs.getString(12));
                usersList.add(usr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return usersList;
    }
}
