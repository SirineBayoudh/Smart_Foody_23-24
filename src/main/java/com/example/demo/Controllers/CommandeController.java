package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeController {

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_2;

    @FXML
    private Pane pane_3;
    @FXML
    private TextField Trecherche;

    @FXML
    private Pane clickpane;

    @FXML
    private Label cmd;

    @FXML
    private Label cmdlivre;

    @FXML
    private Label cmdnonlivre;
    @FXML
    private TableView<Commande> commandeTableView;

    @FXML
    private TableColumn<Commande, Integer> id_commandeColumn;

    @FXML
    private TableColumn<Commande, String> etat;
    @FXML
    private TableColumn<Commande, Float> remise;

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
        date_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("date_commande"));
        id_clientColumn.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        total_commandeColumn.setCellValueFactory(new PropertyValueFactory<>("total_commande"));
        remise.setCellValueFactory(new PropertyValueFactory<>("remise"));
        etat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Set the items of the table view to the observable list
        commandeTableView.setItems(commandeList);

        // Load data into the observable list
        onPane1Clicked();
        List<Commande> commandesNonLivre = AfficherCommandes("Non Livre");
        cmdlivre.setText(String.valueOf(commandesNonLivre.size()));
        List<Commande> commandesLivre = AfficherCommandes("Livre");
        cmdnonlivre.setText(String.valueOf(commandesLivre.size()));


    }
    // la méthodes qui selectionne tous les commandes passe a partir de base
    private List<Commande> AfficherCommandes(String etat) {
        List<Commande> commandes = new ArrayList<>();
        try {
            // Création de la requête SELECT
            String query = "SELECT * FROM commande";
            if (etat != null) {
                query += " WHERE etat='" + etat + "'";
            }
            System.out.println("etat"+query);

            // Création de l'instruction et exécution de la requête
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Remplissage de la liste observable avec les données récupérées
            while (resultSet.next()) {
                Commande commande = new Commande();
                commande.setId_commande(resultSet.getInt("id_commande"));
                commande.setDate_commande(resultSet.getDate("date_commande"));
                commande.setId_client(resultSet.getInt("id_client"));
                commande.setTotal_commande(resultSet.getFloat("totalecommande"));
                commande.setRemise(resultSet.getFloat("remise"));
                commande.setEtat(resultSet.getString("etat"));

                commandes.add(commande);
            }

            // Fermeture des ressources
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    @FXML
    private void onPane1Clicked() {
        List<Commande> commandes = AfficherCommandes(null);
        ObservableList<Commande> observableCommandes = FXCollections.observableArrayList(commandes);
        commandeList.clear();
        commandeList.addAll(observableCommandes);
        cmd.setText(String.valueOf(commandes.size()));
    }

    @FXML
    private void onPane2Clicked() {
        List<Commande> commandes = AfficherCommandes("Non Livre");
        //cmdlivre.setText(String.valueOf(commandes.stream().filter(c -> "Non Livre".equals(c.getEtat())).count()));
        ObservableList<Commande> observableCommandes = FXCollections.observableArrayList(commandes);
        commandeList.clear();
        commandeList.addAll(observableCommandes);

    }

    @FXML
    private void onPane3Clicked() {
        List<Commande> commandes = AfficherCommandes("Livre");
        // cmdnonlivre.setText(String.valueOf(commandes.stream().filter(c -> "Livre".equals(c.getEtat())).count()));
        ObservableList<Commande> observableCommandes = FXCollections.observableArrayList(commandes);
        commandeList.clear();
        commandeList.addAll(observableCommandes);

    }

}
