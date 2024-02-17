package com.example.demo.Controllers;

import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCrud implements ICrud<Utilisateur>{

    Connection cnx;

    public UserCrud() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouterEntite(Utilisateur u) {
        String requete = "INSERT INTO utilisateur(nom,prenom,genre,email,mot_de_passe,role,matricule,attestation,adresse,objectif) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setString(1, u.getNom());
            pst.setString(2,u.getPrenom());
            pst.setString(3, u.getGenre());
            pst.setString(4, u.getEmail());
            pst.setString(5,u.getMot_de_passe());
            pst.setString(6,u.getRole());
            pst.setInt(7,u.getMatricule());
            pst.setString(8,u.getAttestation());
            pst.setString(9,u.getAdresse());
            pst.setString(10, u.getObjectif());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifierEntite(Utilisateur u) {
        String req = "UPDATE utilisateur SET nom=?,prenom=?,genre=?,email=?,mot_de_passe=?,role=?,matricule=?,attestation=?,adresse=?,objectif=? WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, u.getNom());
            pst.setString(2,u.getPrenom());
            pst.setString(3, u.getGenre());
            pst.setString(4, u.getEmail());
            pst.setString(5,u.getMot_de_passe());
            pst.setString(6,u.getRole());
            pst.setInt(7,u.getMatricule());
            pst.setString(8,u.getAttestation());
            pst.setString(9,u.getAdresse());
            pst.setString(10, u.getObjectif());
            pst.setInt(11, u.getId_utilisateur());
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
                usr.setRole(rs.getString(7));
                usr.setMatricule(rs.getInt(8));
                usr.setAttestation(rs.getString(9));
                usr.setAdresse(rs.getString(10));
                usr.setObjectif(rs.getString(11));
                ListU.add(usr);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ListU;
    }
}
