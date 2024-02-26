package com.example.demo.Controllers;

import com.example.demo.Models.Commande;
import com.example.demo.Models.CommandeHolder;
import com.example.demo.Tools.MyConnection;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Image;

import java.io.*;

import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;



import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.Desktop;


public class CommandeController {


    @FXML
    private TextField searchField;

    @FXML
    private Pane clickpane;

    @FXML
    private Label cmd;

    @FXML
    private Label cmdlivre;

    @FXML
    private Label cmdnonlivre;

    @FXML
    private Label cmdencours;
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

    private CommandeHolder holder = CommandeHolder.getInstance();
    // Constructeur
    public CommandeController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void initialize() {
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
        dynamicSearch();
        List<Commande> commandesNonLivre = AfficherCommandes("Non Livre");
        cmdlivre.setText(String.valueOf(commandesNonLivre.size()));
        List<Commande> commandesLivre = AfficherCommandes("Livre");
        cmdnonlivre.setText(String.valueOf(commandesLivre.size()));
        List<Commande> commandesEncours = AfficherCommandes("en cours");
        cmdencours.setText(String.valueOf(commandesEncours.size()));




    }
    @FXML
    private void dynamicSearch()  {


        FilteredList<Commande> filteredData = new FilteredList<>(commandeList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(g -> {
                System.out.println(g.toString());

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                System.out.println(lowerCaseFilter);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Customize the format as needed
                String formattedDate = dateFormat.format(g.getDate_commande());

                if (formattedDate.toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;

                } else if (Integer.toString(g.getId_commande()).toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;

                } else {
                    return false;
                }

            });
        });
        SortedList<Commande> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(commandeTableView.comparatorProperty());
        commandeTableView.setItems(sortedData);

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
            System.out.println("etat" + query);

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
        commandeList.clear();
        commandeList.addAll(commandes);
        cmd.setText(String.valueOf(commandes.size()));
    }

    @FXML
    private void onPane2Clicked() {

        List<Commande> commandes = AfficherCommandes("Livre");
        commandeList.clear();
        commandeList.addAll(commandes);
    }

    @FXML
    private void onPane3Clicked() {
        List<Commande> commandes = AfficherCommandes("Non Livre");
        commandeList.clear();
        commandeList.addAll(commandes);

    }

    @FXML
    private void onPane4Clicked() {
        List<Commande> commandes = AfficherCommandes("en cours");
        commandeList.clear();
        commandeList.addAll(commandes);
    }

    /************** Modifier Question *******************/
    @FXML
    void detailsCommande(ActionEvent event) throws SQLException, IOException {
        Commande selectedCommande = commandeTableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            holder.setCommande(selectedCommande);


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/DetailsCommande.fxml"));
                Parent root = loader.load();
                clickpane.getChildren().clear();
                clickpane.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }

            //loadUsers();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Pas de Commande séléctionnée");
            alert.setContentText("S'il vous plait de séléctionner une commande");
            alert.showAndWait();
        }

    }

    @FXML
    void statButton(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/StatsCommande.fxml"));
            Parent root = loader.load();
            clickpane.getChildren().clear();
            clickpane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }
    @FXML
    public void ExportPDF(List<Commande> commandes) {
        String path = "Commandes.pdf";
        try {
            PdfWriter pdfWriter = new PdfWriter(path);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDocument);

            // Créer une table avec 2 colonnes pour le logo et le titre
            float[] columnWidths = {2, 8}; // Ajustez les proportions selon vos besoins
            Table headerTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            // Ajouter le logo de l'application
            String imagePath = "C:/Users/INFOTEC/Desktop/Smart_Foody_23-24/src/main/resources/com/example/demo/Images/trans_logo.png";
            Image logo = new Image(ImageDataFactory.create(imagePath)).setAutoScale(true);
            headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));

            // Ajouter le titre dans la deuxième cellule de la table
            Paragraph title = new Paragraph("Extrait des Commandes")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 128, 0)); // Vert
            headerTable.addCell(new Cell().add(title).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            // Ajouter la table d'en-tête au document
            document.add(headerTable);

            // Espacement entre l'en-tête et le tableau des commandes
            document.add(new Paragraph("\n"));

            // Créer un tableau avec 5 colonnes pour les données des commandes
            Table table = new Table(5);

            // Ajouter un en-tête pour chaque colonne
            table.addCell(new Cell().add("ID Commande").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.GREEN)); // Couleur légèrement différente pour l'exemple
            table.addCell(new Cell().add("Date").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.GREEN));
            table.addCell(new Cell().add("ID Client").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.GREEN));
            table.addCell(new Cell().add("Total").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.GREEN));
            table.addCell(new Cell().add("Remise").setTextAlignment(TextAlignment.CENTER).setBackgroundColor(Color.GREEN));

            // Ajouter les données des commandes dans le tableau
            for (Commande commande : commandes) {
                table.addCell(new Cell().add(String.valueOf(commande.getId_commande())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(commande.getDate_commande().toString()).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(String.valueOf(commande.getId_client())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(String.valueOf(commande.getTotal_commande())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(String.valueOf(commande.getRemise())).setTextAlignment(TextAlignment.CENTER));
            }

            // Ajouter le tableau au document
            document.add(table);

            // Fermer le document
            document.close();

            // Ouvrir le fichier PDF généré
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    void exportPDF(ActionEvent event) {
        List<Commande> allCommandes = commandeList; // Récupérez toutes les commandes à partir de votre TableView
        ExportPDF(allCommandes);
    }
}
