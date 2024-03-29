package com.example.demo.Controllers;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;


import com.example.demo.Main;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PageAccueilController {
    private static final String ACCESS_KEY = "8SwFxNJG53NadG8LlDRnWphLiLd56udZIqAY6v9DE2Y";
    private static final String SEARCH_QUERY = "healthy%20food"; // Modifier cette requête pour rechercher des images de produits alimentaires
    private static final String API_URL = "https://api.unsplash.com/photos/random?query=" + SEARCH_QUERY + "&client_id=" + ACCESS_KEY;
    private static final int NUM_IMAGES = 20; // Nombre d'images à afficher
    private static final int IMAGE_WIDTH = 750; // Largeur fixe des images
    private static final int IMAGE_HEIGHT = 350;
    @FXML
    private HBox imageContainer;
    @FXML
    private VBox imagesVbox;
    private Connection conn;
    private int currentPageIndex = 0;
    private int totalPageCount = 0;
    private final int itemsPerPage = 8;
    double totalWidth = 0.0;
    @FXML
    private ChoiceBox<String> categorieChoiceBox;
    @FXML
    private ImageView fruitImageView;

    @FXML
    private ImageView grainImageView;
    @FXML
    private ImageView laitImageView;
    @FXML
    private ImageView vegImageView;
    @FXML
    private ImageView AllProductImageView;


    @FXML
    void handleAllProductImageClick(MouseEvent event) {
       displayProductsByPage(0);
    }

    @FXML
    void handleFruitImageClick(MouseEvent event) {
        displayProductsByCategory("fruit");
    }

    @FXML
    void handleGrainImageClick(MouseEvent event) {
        displayProductsByCategory("grain");
    }

    @FXML
    void handleLaitierImageClick(MouseEvent event) {
        displayProductsByCategory("laitier");
    }

    @FXML
    void handleVegImageClick(MouseEvent event) {
        displayProductsByCategory("legume");
    }
    @FXML
    private TextField searchResultTextField;

    @FXML
    private ListView<String> suggestionsListView;
    private ObservableList<String> criteres = FXCollections.observableArrayList(
            "Tous",
            "sans_lactose",
            "sans_gluten",
            "sans_glucose",
            "protein"
    );
    @FXML
    void goToPreviousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            displayProductsByPage(currentPageIndex);
        }
    }

    @FXML
    void goToNextPage() {
        int totalProductsCount = getTotalProductsCount();
        int totalPages = (int) Math.ceil((double) totalProductsCount / itemsPerPage);

        if (currentPageIndex < totalPages - 1) {
            currentPageIndex++;
            displayProductsByPage(currentPageIndex);
        }
    }
    private int getTotalProductsCount() {
        try {
            String sql = "SELECT COUNT(*) AS total FROM produit";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void displayProductsByPage(int pageIndex) {
        try {
            // Calculer l'offset
            int offset = pageIndex * itemsPerPage;

            // Requête SQL pour récupérer les produits de la page spécifiée
            String sql = "SELECT marque, prix, image FROM produit LIMIT ?, ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, offset);
            statement.setInt(2, itemsPerPage);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            imagesVbox.getChildren().clear();

            // Afficher les produits de la page spécifiée
            displayProducts(resultSet, offset, itemsPerPage);

            // Mise à jour de l'indice de la page actuelle
            currentPageIndex = pageIndex;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits depuis la base de données : " + e.getMessage());
        }
    }



    @FXML
    private void searchProducts() {
        String searchQuery = searchResultTextField.getText();
        try {
            // Requête SQL pour récupérer les produits correspondant à la requête de recherche
            String sql = "SELECT marque, image, prix FROM produit WHERE marque LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            clearProductDisplay();

            // Afficher les produits correspondant à la recherche
            displayProducts(resultSet, 0, itemsPerPage); // Utilisez les paramètres pour spécifier la pagination
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
    }


    private void displayProducts(ResultSet resultSet, int offset, int limit) throws SQLException {
        try {
            // Effacer l'affichage actuel des produits
            imagesVbox.getChildren().clear();

            // Créer une HBox pour chaque ligne d'images
            HBox hbox = new HBox();
            hbox.setSpacing(40); // Espace entre les cartes

            int count = 0; // Compteur pour le nombre de produits affichés sur la page actuelle

            while (resultSet.next() && count < limit) {
                String imageUrl = resultSet.getString("image");
                String marque = resultSet.getString("marque");
                String prix = resultSet.getString("prix");

                // Créer une ImageView pour afficher l'image du produit
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150); // Définir la largeur de l'image
                imageView.setFitHeight(150); // Définir la hauteur de l'image

                // Créer une VBox pour contenir les informations du produit
                VBox productInfo = new VBox(5); // Espacement vertical entre les éléments
                productInfo.setAlignment(Pos.BOTTOM_CENTER); // Alignement central des éléments
                // Appliquer le style CSS au texte de la marque et du prix
                Label labelMarque = new Label("Marque: " + marque);
                labelMarque.setStyle("-fx-text-fill: #56ab2f;");
                Label labelPrix = new Label("Prix: " + prix + " dt"); // Ajout de 'dt' au prix
                labelPrix.setStyle("-fx-text-fill: #56ab2f;");
                // Ajouter les labels avec les styles à la VBox des informations du produit
                productInfo.getChildren().addAll(labelMarque, labelPrix);

                // Créer des boutons pour ajouter au panier et voir les détails
                Button btnAjouterPanier = new Button("Ajouter au panier");
                Button btnDetails = new Button("Détails");
                applyButtonStyles(btnAjouterPanier, btnDetails);

                // Ajouter des actions aux boutons
                btnAjouterPanier.setOnAction(event -> {
                    // Action à effectuer lors du clic sur "Ajouter au panier"
                    // Par exemple, ajouter le produit au panier
                });

                btnDetails.setOnAction(event -> {
                    // Action à effectuer lors du clic sur "Détails"
                    // Par exemple, afficher une fenêtre modale avec les détails du produit
                });

                // Ajouter les boutons à la VBox des informations du produit
                productInfo.getChildren().addAll(btnDetails, btnAjouterPanier);

                // Créer une carte pour encapsuler l'imageView et les informations du produit
                StackPane cardPane = createCardPane(imageView, marque, prix + " dt");
                cardPane.setPrefWidth(220);
                cardPane.setPrefHeight(300);

                // Ajouter la carte contenant l'image à la ligne actuelle
                hbox.getChildren().add(cardPane);
                count++;

                // Si la ligne est pleine ou s'il n'y a plus de produits à afficher
                if (count >= 4 || resultSet.isLast()) {
                    // Ajouter la ligne à la VBox
                    imagesVbox.getChildren().add(hbox);

                    // Créer une nouvelle ligne si ce n'est pas le dernier produit
                    if (!resultSet.isLast()) {
                        hbox = new HBox();
                        hbox.setSpacing(40); // Espace entre les cartes
                    }
                    count = 0; // Réinitialiser le compteur pour la nouvelle ligne
                } else {
                    System.out.println("L'URL de l'image est invalide : " + imageUrl);
                }
            }
            imagesVbox.setSpacing(20);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des produits depuis la base de données : " + e.getMessage());
        }
    }



    private StackPane createCardPane(ImageView imageView, String marque, String prix) {
        // Créer une VBox pour contenir les informations du produit
        VBox productInfo = new VBox(5); // Espacement vertical entre les éléments
        productInfo.setAlignment(Pos.BOTTOM_CENTER); // Alignement central des éléments
        // Appliquer le style CSS au texte de la marque et du prix
        Label labelMarque = new Label("Marque: " + marque);
        labelMarque.setStyle("-fx-text-fill: #56ab2f;");

        Label labelPrix = new Label("Prix: " + prix);
        labelPrix.setStyle("-fx-text-fill: #56ab2f;");

// Ajouter les labels avec les styles à la VBox des informations du produit
        productInfo.getChildren().addAll(labelMarque, labelPrix);
        // Créer des boutons pour ajouter au panier et voir les détails
        Button btnAjouterPanier = new Button("Ajouter au panier");
        Button btnDetails = new Button("Détails");
        applyButtonStyles(btnAjouterPanier, btnDetails);

        // Ajouter des actions aux boutons
        btnAjouterPanier.setOnAction(event -> {
            // Action à effectuer lors du clic sur "Ajouter au panier"
            // Par exemple, ajouter le produit au panier
        });

        btnDetails.setOnAction(event -> {
            // Action à effectuer lors du clic sur "Détails"
            // Par exemple, afficher une fenêtre modale avec les détails du produit
        });

        // Ajouter les boutons à la VBox des informations du produit
        productInfo.getChildren().addAll(btnDetails,btnAjouterPanier);


        // Créer une StackPane pour contenir l'image et les informations du produit
        StackPane cardPane = new StackPane();
        StackPane.setAlignment(imageView, Pos.TOP_CENTER);
        cardPane.getChildren().addAll(imageView, productInfo);
        cardPane.setStyle("-fx-background-color: white; -fx-background-radius: 30; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-border-color: #56ab2f;-fx-border-radius: 30;-fx-border-width: 2px;");
        cardPane.setPrefWidth(150); // Fixer la largeur de la carte
        cardPane.setPrefHeight(150); // Fixer la hauteur de la carte

        // Ajuster la position de la VBox des informations du produit
        StackPane.setMargin(productInfo, new Insets(10, 0, 0, 0)); // Ajouter une marge au-dessus des informations du produit

        return cardPane;
    }




    private void clearProductDisplay() {
        imagesVbox.getChildren().clear();
    }
    private void displayProductsByCategory(String category) {
        try {
            // Requête SQL pour récupérer toutes les informations des produits de la catégorie spécifiée depuis la base de données
            String sql = "SELECT marque, prix, image FROM produit WHERE categorie = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            // Effacer l'affichage actuel des produits
            imagesVbox.getChildren().clear();

            // Créer une HBox pour chaque ligne d'images
            HBox hbox = new HBox();
            hbox.setSpacing(40); // Espace entre les cartes

            // Ajouter les images et les informations à chaque HBox et gérer les lignes
            while (resultSet.next()) {
                String imageUrl = resultSet.getString("image");
                String marque = resultSet.getString("marque");
                String prix = resultSet.getString("prix");
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150); // Fixer la largeur de l'image
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(false);

                // Créer une carte blanche pour chaque produit
                StackPane cardPane = createCardPane(imageView, marque, prix + " dt");
                cardPane.setPrefWidth(220);
                cardPane.setPrefHeight(300);

                // Ajouter la carte à la HBox
                hbox.getChildren().add(cardPane);

                // Créer une nouvelle ligne si la ligne actuelle est pleine
                if (hbox.getChildren().size() >= 4) {
                    imagesVbox.getChildren().add(hbox);
                    hbox = new HBox();
                    hbox.setSpacing(40); // Espace entre les cartes
                }
            }

            // Ajouter la dernière ligne si elle n'est pas pleine
            if (!hbox.getChildren().isEmpty()) {
                imagesVbox.getChildren().add(hbox);
            }

            imagesVbox.setSpacing(20);

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des produits depuis la base de données : " + e.getMessage());
        }
    }
    private void addHoverAnimation(ImageView imageView) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), imageView);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), imageView);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        imageView.setOnMouseEntered(event -> scaleIn.play());
        imageView.setOnMouseExited(event -> scaleOut.play());
    }


    @FXML
    void initialize() {
        MyConnection db = MyConnection.getInstance();
        conn = db.getCnx();
        if (conn == null) {
            System.err.println("La connexion à la base de données n'est pas établie.");
            return;
        }
        addHoverAnimation(AllProductImageView);
        addHoverAnimation(fruitImageView);
        addHoverAnimation(vegImageView);
        addHoverAnimation(laitImageView);
        addHoverAnimation(grainImageView);
        categorieChoiceBox.setItems(criteres);
        fetchAndDisplayImagesFromAPI();

        // Ajouter un écouteur d'événement pour le choix de filtre
        categorieChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Appliquer le filtre sur les produits selon le critère sélectionné
                switch (newValue) {
                    case "sans_glucose":
                        applyFilter("sans_glucose", 0);
                        break;
                    case "sans_gluten":
                        applyFilter("sans_gluten", 0);
                        break;
                    case "sans_lactose":
                        applyFilter("sans_lactose", 0);
                        break;
                    case "protein":
                        applyFilter("protein", 0);
                        break;
                    default:
                        // Si aucun filtre n'est sélectionné, afficher tous les produits
                        displayProductsByPage(0);
                        break;
                }
            }
        });

        // Initialise la liste des suggestions avec une liste vide
        suggestionsListView.setItems(FXCollections.observableArrayList());
        suggestionsListView.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

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

        // Affiche les produits par page
        displayProductsByPage(0);


    }
    private void fetchAndDisplayImagesFromAPI() {
        try {
            // Ouvrir une connexion HTTP
            URL url = new URL(API_URL + "&count=" + NUM_IMAGES);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lire la réponse de l'API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Extraire les URL des images à partir de la réponse JSON
            JSONArray jsonArray = new JSONArray(response.toString());

            // Créer une timeline pour l'animation
            Timeline timeline = new Timeline();
            // Durée totale de l'animation
            double animationDuration = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                final int index = i;
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String imageUrl = jsonObject.getJSONObject("urls").getString("regular");

                // Charger l'image dans ImageView
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(IMAGE_WIDTH); // Fixer la largeur de l'image
                imageView.setFitHeight(IMAGE_HEIGHT); // Fixer la hauteur de l'image
                imageView.setPreserveRatio(false); // Désactiver le maintien du ratio d'aspect
                //imageView.setTranslateX(IMAGE_WIDTH * i); // Position initiale de l'image

                // Ajouter l'image à imageContainer
                imageContainer.getChildren().add(imageView);
                // Positionner l'image à côté de la précédente
                if (index > 0) {
                    totalWidth += IMAGE_WIDTH-900; // Ajouter la largeur de l'image précédente
                    imageView.setTranslateX(totalWidth); // Positionner l'image en fonction de la largeur totale
                } else {
                    totalWidth = 0.0; // Réinitialiser la largeur totale pour la première image
                }

                // Ajouter la durée de délai à l'animation totale
                animationDuration += 10; // Délai de 3 secondes pour chaque image

                // Créer une KeyValue pour l'animation
                KeyValue keyValue = new KeyValue(imageView.translateXProperty(), -IMAGE_WIDTH * i - IMAGE_WIDTH);
                // Créer un KeyFrame pour l'animation
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(animationDuration), keyValue);
                // Ajouter le KeyFrame à la timeline
                timeline.getKeyFrames().add(keyFrame);
            }

            // Régler le cycle de la timeline
            timeline.setCycleCount(Timeline.INDEFINITE);
            // Lancer la timeline
            timeline.play();

            // Fermer la connexion
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilter(String filter, int pageIndex) {
        try {
            // Calculer l'offset
            int offset = pageIndex * itemsPerPage;

            // Requête SQL pour récupérer les produits filtrés de la page spécifiée
            String sql = "SELECT marque, prix, image FROM produit WHERE critere LIKE ? LIMIT ?, ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + filter + "%");
            statement.setInt(2, offset);
            statement.setInt(3, itemsPerPage);
            ResultSet resultSet = statement.executeQuery();

            // Afficher les produits filtrés
            displayProducts(resultSet, offset, itemsPerPage);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'application du filtre : " + e.getMessage());
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
    private void applyButtonStyles(Button btnAjouterPanier, Button btnDetails) {
        btnAjouterPanier.setStyle("-fx-background-color: #56ab2f; -fx-background-radius: 30; -fx-text-fill: white;");
        btnDetails.setStyle("-fx-background-color: #56ab2f; -fx-background-radius: 30; -fx-text-fill: white;");
    }

}

