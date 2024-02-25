package com.example.demo.Controllers;

import com.example.demo.Main;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PageClientController {

    private int column;
    private int row;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private ListView<String> suggestionsListView;

    @FXML
    private GridPane imagesGrid;

    @FXML
    private VBox chosenFruitCard;

    @FXML
    private ImageView fruitImg;

    @FXML
    private Label fruitNameLabel;

    @FXML
    private Label fruitPriceLabel;
    @FXML
    private TextField searchResultTextField;


    @FXML
    private void searchProducts() {
        String searchQuery = searchResultTextField.getText();
        try {
            // Requête SQL pour récupérer les produits correspondant à la requête de recherche
            String sql = "SELECT image FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            clearProductDisplay();

            // Afficher les produits correspondant à la recherche
            displayProducts(resultSet);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
    }

    private ObservableList<String> categories = FXCollections.observableArrayList(
            "Tous les produits",
            "sans lactose",
            "sans gluten",
            "sans hh",
            "sans bb "
    );

    @FXML
    private ChoiceBox<String> categorieChoiceBox;

    private List<Produit> fruits = new ArrayList<>();
    private Connection conn;

    @FXML
    void initialize() {
        MyConnection db = MyConnection.getInstance();
        // Obtenez la connexion à la base de données à partir de myDataBase
        conn = db.getCnx();

        // Initialiser le ChoiceBox avec les catégories
        categorieChoiceBox.setItems(categories);
        imagesGrid.setHgap(20);


        // Afficher tous les produits au démarrage sans filtre de catégorie
        displayAllProducts();

        // Ajouter un écouteur sur la ChoiceBox pour détecter les changements de sélection de catégorie
        categorieChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Tous les produits")) {
                // Afficher tous les produits
                displayAllProducts();
            } else {
                // Réinitialiser l'affichage des produits pour afficher uniquement les produits de la catégorie sélectionnée
                displayProductsByCategory(newValue);
            }
        });
        // Initialise la liste des suggestions avec une liste vide
        suggestionsListView.setItems(FXCollections.observableArrayList());

        // Ajoute un écouteur sur le champ de recherche pour détecter les changements de texte
        searchResultTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Exécute une recherche dans la base de données et met à jour les suggestions
                updateSuggestions(newValue);
            } else {
                // Efface la liste des suggestions si le champ de recherche est vide
                suggestionsListView.getItems().clear();
            }
        });

    }

    private void displayAllProducts() {
        try {
            // Requête SQL pour récupérer toutes les URL des images depuis la base de données
            String sql = "SELECT image FROM produit";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            clearProductDisplay();

            // Afficher tous les produits
            displayProducts(resultSet);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des images depuis la base de données : " + e.getMessage());
        }
    }

    private void displayProductsByCategory(String category) {
        try {
            // Requête SQL pour récupérer toutes les URL des images depuis la base de données pour la catégorie sélectionnée
            String sql = "SELECT image FROM produit WHERE categorie=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            clearProductDisplay();

            // Afficher les produits de la catégorie sélectionnée
            displayProducts(resultSet);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des images depuis la base de données : " + e.getMessage());
        }
    }

    private void displayProducts(ResultSet resultSet) throws SQLException {
        chosenFruitCard.setVisible(false); // Cache la carte au démarrage
        chosenFruitCard.setManaged(false); // Désactive la gestion de la carte pour ne pas occuper d'espace

        while (resultSet.next()) {
            String imageUrl = resultSet.getString("image");
            Produit produit = fetchProductDetailsFromDatabase(imageUrl);
            Image image = new Image(imageUrl, 200, 200, true, true);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);

            Button detailButton = new Button("Détails");
            detailButton.setOnAction(event -> {
                System.out.println("Afficher les détails du produit : ");
            });
            detailButton.setStyle("-fx-background-color: #56ab2f; -fx-background-radius: 100; -fx-padding: 10px; ");

            imageView.setOnMouseClicked(event -> handleImageClick(produit));

            imagesGrid.add(imageView, column, row);
            imagesGrid.add(detailButton, column, row + 1);

            // Incrémenter les indices de colonne et de ligne
            column++;
            if (column > 2) {
                column = 0;
                row += 2;
            }
        }
        // Afficher un message si aucune image n'est trouvée
        if (!resultSet.first()) {
            System.out.println("Aucune image trouvée dans la base de données.");
        }
    }

    private Produit fetchProductDetailsFromDatabase(String imageUrl) {
        Produit produit = null;
        try {
            String sql = "SELECT * FROM produit WHERE image=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, imageUrl);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String marque = resultSet.getString("marque");
                float prix = resultSet.getFloat("prix");

                produit = new Produit(marque, prix, imageUrl);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return produit;
    }

    private void handleImageClick(Produit produit) {
        setChosenFruit(produit);
        chosenFruitCard.setVisible(true);
        chosenFruitCard.setManaged(true);
    }

    private void setChosenFruit(Produit produit) {
        if (produit != null && produit.getImage() != null) {
            fruitNameLabel.setText(produit.getMarque());
            fruitPriceLabel.setText(produit.getPrix() + Main.CURRENCY);
            // Vérifier si l'URL de l'image n'est pas null avant de créer l'objet Image
            if (produit.getImage() != null) {
                Image image = new Image(produit.getImage(), 200, 200, true, true);
                fruitImg.setImage(image);
                chosenFruitCard.setStyle("   -fx-background-radius: 30;");
            } else {
                System.out.println("L'URL de l'image est null.");
            }
        } else {
            System.out.println("Le produit est null ou l'URL de l'image est null.");
        }
    }
    private void updateSuggestions(String searchQuery) {
        try {
            String sql = "SELECT marque FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            ObservableList<String> suggestions = FXCollections.observableArrayList();
            while (resultSet.next()) {
                suggestions.add(resultSet.getString("marque"));
            }
            suggestionsListView.setItems(suggestions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearProductDisplay() {
        imagesGrid.getChildren().clear();
        column = 0;
        row = 0;
    }
}
