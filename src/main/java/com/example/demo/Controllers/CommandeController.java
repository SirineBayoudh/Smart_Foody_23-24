package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class CommandeController {

    @FXML
    private TableView<Commande> commandeTableView;

    @FXML
    private TableColumn<Commande, Integer> id_commandeColumn;

    @FXML
    private TableColumn<Commande, Integer> id_lcColumn;

    @FXML
    private TableColumn<Commande, Date> date_commandeColumn;

    @FXML
    private TableColumn<Commande, Integer> id_clientColumn;

    @FXML
    private TableColumn<Commande, Float> total_commandeColumn;

    @FXML
    private TableColumn<Commande, Integer> nbre_commandeColumn;

    // Liste observable pour stocker les commandes
    private ObservableList<Commande> commandeList = FXCollections.observableArrayList();

    // Connexion à la base de données
    private Connection cnx;

    // Constructeur
    public CommandeController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void initialize() {
        // Initialize the table columns
        id_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("id_commande"));
        id_lcColumn.setCellValueFactory(new PropertyValueFactory<>("id_lc"));
        date_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("date_commande"));
        id_clientColumn.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        total_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("total_commande"));
        nbre_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("nbrecmdParclient"));

        // Set the items of the table view to the observable list
        commandeTableView.setItems(commandeList);

        // Load data into the observable list
        AfficherCommandes();
    }
// la méthodes qui selectionne tous les commandes passe a partir de base
    private void AfficherCommandes() {
        try {
            // Création de la requête SELECT
            String query = "SELECT * FROM commande";

            // Création de l'instruction et exécution de la requête
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the existing list before loading new data
            commandeList.clear();

            // Remplissage de la liste observable avec les données récupérées
            while (resultSet.next()) {
                Commande commande = new Commande();
                commande.setId_commande(resultSet.getInt("id_commande"));
                commande.setId_lc(resultSet.getInt("id_lc"));
                commande.setDate_commande(resultSet.getDate("date_commande"));
                commande.setId_client(resultSet.getInt("id_client"));
                commande.setTotal_commande(resultSet.getFloat("totalecommande"));
                commande.setNbrecmdParclient(resultSet.getInt("nbrecmdParclient"));
                commandeList.add(commande);
            }

            // Fermeture des ressources
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
