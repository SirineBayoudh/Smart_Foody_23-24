/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.Tools;


import com.example.demo.Models.Commande;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ServiceCommande implements IServiceCommande<Commande> {

    private Connection cnx;

    public ServiceCommande() {
        cnx = MyConnection.getInstance().getCnx();
    }



    @Override
    public void deleteOne(int id) throws SQLException {
        String req = "DELETE FROM `commande` WHERE `id`=?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("commande supprimée !");

    }


    @Override
    public void deleteOne(Commande t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    @Override
    public String usernameById(int id) {
        String name = "Dead";
        String lastname="";

        try {
            String req = "SELECT nom,prenom FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("nom");
                lastname = rs.getString("prenom");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceCommande.class.getName()).log(Level.SEVERE, null, ex);
        }

        return name+" "+lastname;

    }

    @Override
    public Integer nbrCommandeByClient (int clientId){
        String query = "SELECT COUNT(*) AS commande FROM commande WHERE id_client = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, clientId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int nbrCommande = rs.getInt("commande");
                    System.out.println("Nombre de commandes pour le client " + clientId + " : " + nbrCommande);
                    return nbrCommande;
                } else {
                    System.out.println("Aucune commande trouvée pour le client " + clientId);
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nombre de commandes pour le client " + clientId +
                    " : " + e.getMessage());
            return -1; // or throw an exception
        }

    }



    @Override
    public List<Integer> selectByLikes() throws SQLException {

        List<Commande> temp = new ArrayList<>();

        Map<Integer, Integer> countMap = new HashMap<>();
        Map<Integer, Float> totalCommandeMap = new HashMap<>();

        String req = "SELECT * FROM commande ";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int idClient = rs.getInt("id_client");
            countMap.put(idClient, countMap.getOrDefault(idClient, 0) + 1);
            totalCommandeMap.put(idClient, totalCommandeMap.getOrDefault(idClient, 0f) + rs.getFloat("totalecommande"));
        }

        List<Map.Entry<Integer, Integer>> sortedCountList = new ArrayList<>(countMap.entrySet());
        sortedCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<Map.Entry<Integer, Float>> sortedTotalCommandeList = new ArrayList<>(totalCommandeMap.entrySet());
        sortedTotalCommandeList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

// Get the top 3 clients based on the count of commandes
        List<Integer> top3ClientsByCount = sortedCountList.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

// Get the top 3 clients based on the sum of total_commande
        List<Integer> top3ClientsByTotalCommande = sortedTotalCommandeList.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

// Compare the two lists and select the top 3 clients
        List<Integer> top3Clients = Stream.concat(top3ClientsByCount.stream(), top3ClientsByTotalCommande.stream())
                .distinct()
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("Top 3 clients: " + top3Clients);



        return top3Clients;

    }



}
