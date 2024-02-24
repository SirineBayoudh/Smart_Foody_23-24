package com.example.demo.Controllers;
import java.awt.Desktop;
import java.io.*;

import com.itextpdf.text.*;

import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.util.converter.FloatStringConverter;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class DashProduitController implements Initializable {
    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    int ref = 0;


    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;
    @FXML
    private Label labelTotalProduits;

    @FXML
    private TableView<Produit> table;

    @FXML
    private TextField tfCategorie;

    @FXML
    private TextField tfCritere;

    @FXML
    private TextField tfMarque;
    @FXML
    private VBox vboxFormulaire;
    @FXML
    private ComboBox<String> categoryFilterComboBox;
    @FXML
    private CheckBox checkboxCritere1;

    @FXML
    private CheckBox checkboxCritere2;

    @FXML
    private CheckBox checkboxCritere3;

    @FXML
    private CheckBox checkboxCritere4;

    @FXML
    private Spinner<Float> tfPrix;
    @FXML
    private TextField tfImage;
    @FXML
    private TableColumn<Produit, String> colCategorie;

    @FXML
    private TableColumn<Produit, String> colCritere;

    @FXML
    private TableColumn<Produit, String> colMarque;

    @FXML
    private TableColumn<Produit, Float> colPrix;

    @FXML
    private TableColumn<Produit, Integer> colRef;
    @FXML
    private TableColumn<Produit, String> colImage;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private LineChart<String, Number> linechart2;
    @FXML
    private Label labelSommePrixProduit;

    private float getTotalPrixProduits() {
        String query = "SELECT SUM(Prix) AS totalPrix FROM produit";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getFloat("totalPrix");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0f; // Retourne 0 si aucun produit trouvé ou en cas d'erreur
    }


    private void updateLineChart() {
        String query = "SELECT categorie, MAX(prix) AS MaxPrix FROM produit GROUP BY categorie";
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categorie = resultSet.getString("categorie");
                double maxPrix = resultSet.getFloat("MaxPrix");
                data.add(new XYChart.Data<>(categorie, maxPrix));
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Catgorie");
                yAxis.setLabel("Prix");
                linechart2.getYAxis().setAutoRanging(false);
                linechart2.setStyle("-fx-text-fill: #56ab2f");
                ((NumberAxis) linechart2.getYAxis()).setLowerBound(0);
                ((NumberAxis) linechart2.getYAxis()).setUpperBound(50);
                ((NumberAxis) linechart2.getYAxis()).setTickUnit(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>(data);
        linechart2.getData().clear();
        linechart2.getData().add(series);
    }

    @FXML
    void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString());
        }
    }

    private ObservableList<Produit> getProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String query = "SELECT * from produit";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Produit st = new Produit();
                st.setRef(rs.getInt("Ref"));
                st.setMarque(rs.getString("Marque"));
                st.setCategorie(rs.getString("Categorie"));
                float prix = rs.getFloat("Prix");
                if (!rs.wasNull()) {
                    st.setPrix(prix);
                }
                st.setImage(rs.getString("Image"));
                st.setCritere(rs.getString("Critere"));
                produits.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return produits;
    }

    public void showProduits() {
        ObservableList<Produit> list = getProduits();
        table.setItems(list);
        colRef.setCellValueFactory(new PropertyValueFactory<Produit, Integer>("ref"));
        colMarque.setCellValueFactory(new PropertyValueFactory<Produit, String>("marque"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<Produit, String>("categorie"));
        colPrix.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getPrix()).asObject());
        colImage.setCellValueFactory(new PropertyValueFactory<Produit, String>("image"));
        colCritere.setCellValueFactory(new PropertyValueFactory<Produit, String>("critere"));

    }

    @FXML
    void createProduit(ActionEvent event) throws SQLException {
        vboxFormulaire.setVisible(true);
        Number prixValue = tfPrix.getValue();
        Float prixFloatValue = null;

        if (prixValue != null) {
            prixFloatValue = prixValue.floatValue();
        } else {
            showAlert("Veuillez saisir un prix.");
            return;
        }

        String insert = "INSERT INTO produit(Marque, Categorie, Prix, Critere, Image) VALUES (?, ?, ?, ?, ?)";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            String imageUrl = tfImage.getText();
            String marque = tfMarque.getText();
            if (marque.isEmpty()) {
                showAlert("Veuillez saisir une marque.");
                return;
            } else if (!marque.matches("[a-zA-Z]+")) {
                showAlert("La marque ne doit contenir que des lettres.");
                return;
            }
            String categorie = tfCategorie.getText();
            if (categorie.isEmpty()) {
                showAlert("Veuillez saisir une catégorie.");
                return;
            } else if (!categorie.matches("[a-zA-Z]+")) {
                showAlert("La catégorie ne doit contenir que des lettres.");
                return;
            }
            // Récupérer les critères sélectionnés depuis les CheckBox
            StringBuilder critereBuilder = new StringBuilder();
            if (checkboxCritere1.isSelected()) {
                critereBuilder.append(checkboxCritere1.getText()).append(",");
            }
            if (checkboxCritere2.isSelected()) {
                critereBuilder.append(checkboxCritere2.getText()).append(",");
            }
            if (checkboxCritere3.isSelected()) {
                critereBuilder.append(checkboxCritere3.getText()).append(",");
            }
            if (checkboxCritere4.isSelected()) {
                critereBuilder.append(checkboxCritere4.getText()).append(",");
            }
            // Supprimer la dernière virgule et l'espace s'il y en a
            String critere = critereBuilder.toString().trim();
            if (critere.isEmpty()) {
                showAlert("Veuillez sélectionner au moins un critère.");
                return;
            }

            if (isImageUrlUnique(imageUrl)) {
                showAlert("L'URL de l'image doit être unique.");
                return;
            } else if (imageUrl.isEmpty()) {
                showAlert("Le champ est vide. Veuillez sélectionner une image.");
                return;
            }
            st.setString(1, marque);
            st.setString(2, categorie);
            st.setFloat(3, prixFloatValue);
            st.setString(4, critere);
            st.setString(5, imageUrl);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit ajouté avec succès !");
                showProduits();
                tfMarque.clear();
                tfCategorie.clear();
                //tfPrix.getValueFactory().setValue(0,0);
                // Ne pas effacer les CheckBox pour permettre à l'utilisateur de conserver sa sélection
                // tfCritere.clear();
                tfImage.clear();
                //btnModifier.setVisible(true);
            } else {
                System.out.println("Échec de l'ajout du produit !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        updateLineChart();
        updateTotalProduitsLabel();
        updateCategoryFilter();
    }


    private void updateTotalProduitsLabel() {
        getData(); // Vous n'avez plus besoin de capturer la valeur retournée
        int totalProduits = getTotalProduits();
        labelTotalProduits.setText("Total des produits : " + totalProduits);
    }

    private boolean isImageUrlUnique(String imageUrl) throws SQLException {
        String query = "SELECT COUNT(*) FROM produit WHERE Image = ?";
        PreparedStatement statement = con.getCnx().prepareStatement(query);
        statement.setString(1, imageUrl);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void getData() {
        Produit produit = table.getSelectionModel().getSelectedItem();
        if (produit != null) {
            ref = produit.getRef();
            tfMarque.setText(produit.getMarque());
            //tfCritere.setText(produit.getCritere());
            // Obtenez la liste des critères de produit
            String critere = produit.getCritere();

            // Séparez les critères en fonction du délimiteur (par exemple, une virgule)
            String[] critereArray = critere.split(", ");

            // Cochez les cases à cocher correspondantes en fonction des critères
            for (String c : critereArray) {
                switch (c) {
                    case "sans gluten":
                        checkboxCritere1.setSelected(true);
                        break;
                    case "sans glucose":
                        checkboxCritere2.setSelected(true);
                        break;
                    case "sans lactose":
                        checkboxCritere3.setSelected(true);
                        break;
                    case "protein":
                        checkboxCritere4.setSelected(true);
                        break;
                    // Ajoutez d'autres cas pour chaque critère si nécessaire
                }
            }
            tfCategorie.setText(produit.getCategorie());
            btnAjouter.setDisable(true);
            btnModifier.setDisable(false);
            btnSupprimer.setDisable(false);
            tfPrix.setDisable(true);
            tfImage.setDisable(true);
        }
    }

    @FXML
    void modifierProduit(ActionEvent event) {
        String update = "UPDATE produit SET Marque = ?, Categorie = ?, Prix = ?, Critere = ? WHERE ref = ?";
        con = MyConnection.getInstance();
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de modification");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir modifier ce produit ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    st = con.getCnx().prepareStatement(update);
                    st.setString(1, tfMarque.getText());
                    st.setString(2, tfCategorie.getText());

                    // Récupérer la valeur du prix du Spinner en tant que Float
                    Number prixValue = tfPrix.getValue();
                    Float prixFloatValue = prixValue != null ? Float.parseFloat(prixValue.toString()) : null;
                    if (prixFloatValue == null) {
                        showAlert("Veuillez saisir un prix valide.");
                        return;
                    }
                    st.setFloat(3, prixFloatValue);

                    // Construire la chaîne de critères en fonction des cases cochées
                    StringBuilder critereBuilder = new StringBuilder();
                    if (checkboxCritere1.isSelected()) {
                        critereBuilder.append("sans gluten").append(",");
                    }
                    if (checkboxCritere2.isSelected()) {
                        critereBuilder.append("sans glucose").append(",");
                    }
                    if (checkboxCritere3.isSelected()) {
                        critereBuilder.append("sans lactose").append(",");
                    }
                    if (checkboxCritere4.isSelected()) {
                        critereBuilder.append("protein").append(",");
                    }
                    //st.setString(4, critereBuilder);
                    st.setString(4, critereBuilder.toString());

                    st.setInt(5, ref);

                    int rowsAffected = st.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Produit modifié avec succès !");
                        showProduits();
                        tfMarque.clear();
                        tfCategorie.clear();
                        //tfPrix.getValueFactory().setValue(0f);
                        tfImage.clear();
                        checkboxCritere1.setSelected(false); // Décocher la première case à cocher
                        checkboxCritere2.setSelected(false); // Décocher la deuxième case à cocher
                        checkboxCritere3.setSelected(false);
                        checkboxCritere4.setSelected(false);
                    } else {
                        System.out.println("Échec de la modification du produit !");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                updateLineChart();
            }
        });

        btnAjouter.setDisable(false);
        updateCategoryFilter();
    }

    @FXML
    void supprimerProduit(ActionEvent event) {
        String delete = "delete from produit where ref = ?";
        con = MyConnection.getInstance();
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");
        confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            st = con.getCnx().prepareStatement(delete);
                            st.setInt(1, ref);
                            int rowsAffected = st.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Produit supprimé avec succès !");
                                showProduits();
                                tfMarque.clear();
                                //tfPrix.clear();
                                checkboxCritere1.setSelected(false); // Décocher la première case à cocher
                                checkboxCritere2.setSelected(false); // Décocher la deuxième case à cocher
                                checkboxCritere3.setSelected(false);
                                checkboxCritere4.setSelected(false);
                                tfCategorie.clear();
                            } else {
                                System.out.println("Échec de la suppression du produit !");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        updateLineChart();
                    }
                }
        );
        updateTotalProduitsLabel();
        btnAjouter.setDisable(false);
        updateCategoryFilter();
    }

    @FXML
    void filterByCategory(ActionEvent event) {
        String selectedCategory = categoryFilterComboBox.getValue();
        ObservableList<Produit> filteredList = FXCollections.observableArrayList();
        if ("Tout".equals(selectedCategory)) {
            filteredList.addAll(getProduits()); // Charger tous les produits sans filtre
        } else {
            for (Produit produit : getProduits()) {
                if (produit.getCategorie().equals(selectedCategory) || selectedCategory == null) {
                    filteredList.add(produit);
                }
            }
        }
        table.setItems(filteredList);
    }

    private void updateCategoryFilter() {
        ObservableList<String> categories = getCategoriesFromDatabase();
        categoryFilterComboBox.setItems(categories);
    }

    private ObservableList<String> getCategoriesFromDatabase() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.add("Tout"); // Ajout de l'option "Tout"
        String query = "SELECT DISTINCT Categorie FROM produit";
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categorie = resultSet.getString("Categorie");
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private int getTotalProduits() {
        String query = "SELECT COUNT(*) AS total FROM produit";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showProduits();
        updateLineChart();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnSupprimer.setDisable(true);
        ObservableList<String> categories = getCategoriesFromDatabase();
        categoryFilterComboBox.setItems(categories);
        int totalProduits = getTotalProduits();
        labelTotalProduits.setText("Total des produits :\n " + totalProduits);
        labelSommePrixProduit.setText("Total des prix des produits :\n" + getTotalPrixProduits());


    }

    @FXML
    void exporterFichierPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (exporterProduitsVersPDF(filePath)) {
                // Affichage d'un message de confirmation à l'utilisateur
                showAlert(Alert.AlertType.INFORMATION, "Exportation réussie", "Les données ont été exportées avec succès vers " + filePath);

                // Ouvrir le fichier PDF
                openPDFFile(filePath);
            } else {
                // Affichage d'un message d'erreur à l'utilisateur
                showAlert(Alert.AlertType.ERROR, "Erreur d'exportation", "Une erreur s'est produite lors de l'exportation des données.");
            }
        }
    }

    private void openPDFFile(String filePath) {
        try {
            File file = new File(filePath);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Impossible d'ouvrir le fichier. Veuillez le faire manuellement.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du fichier PDF.");
        }
    }
        private boolean exporterProduitsVersPDF(String filePath) {
            Document document = null;
            try {
                FileOutputStream fileOut = new FileOutputStream(filePath);
                document = new Document();
                PdfWriter.getInstance(document, fileOut);
                document.open();

                ObservableList<Produit> produits = table.getItems();

                // Créer un tableau avec 6 colonnes pour les données des produits
                PdfPTable table = new PdfPTable(6);

                // Ajouter les en-têtes de colonne
                String[] headers = {"Ref", "Marque", "Catégorie", "Prix", "Critère", "Image"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setBackgroundColor(new BaseColor(136, 171, 47));
                    table.addCell(headerCell);
                }

                // Ajouter les données de chaque produit dans le tableau
                for (Produit produit : produits) {
                    table.addCell(String.valueOf(produit.getRef()));
                    table.addCell(produit.getMarque());
                    table.addCell(produit.getCategorie());
                    table.addCell(String.valueOf(produit.getPrix()));
                    table.addCell(produit.getCritere());
                    table.addCell(produit.getImage());
                }

                // Ajouter le tableau au document PDF
                document.add(table);

                System.out.println("Export PDF réussi.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'export PDF.");
                return false;
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    }
