package com.example.demo.Controllers;

import com.example.demo.Main;
import com.example.demo.Models.ListCritere;
import com.example.demo.Models.Objectif;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class objectifController {
    MyConnection con = null;
    PreparedStatement st = null;
    @FXML
    private TableView<Objectif> ObjectifTableView;
    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;
    @FXML
    private CheckBox tfCritere1;

    @FXML
    private CheckBox tfCritere2;

    @FXML
    private CheckBox tfCritere3;

    @FXML
    private CheckBox tfCritere4;

    @FXML
    private ComboBox<String> tfLibelle;

    private String[] objectif = {"Bien être", "Prise de poids", "Perte de poids", "Prise de masse musculaire"};
    @FXML
    private TableColumn<Objectif, String> colCritere;

    @FXML
    private TableColumn<Objectif, Integer> colIdObj;

    @FXML
    private TableColumn<Objectif, String> colLibelle;
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private ComboBox<String> critereFilterCombobox;

    @FXML
    void exportFichierPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (exporterObjectifsVersPDF(filePath)) {
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
    private boolean exporterObjectifsVersPDF(String filePath) {
        Document document = null;
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            document = new Document();
            PdfWriter.getInstance(document, fileOut);
            document.open();

            ObservableList<Objectif> objectifs = ObjectifTableView.getItems();

            // Créer un tableau avec 3 colonnes pour les données des objectifs
            PdfPTable table = new PdfPTable(3);

            // Ajouter les en-têtes de colonne
            String[] headers = {"ID", "Libellé", "Critères"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(new BaseColor(136, 171, 47));
                table.addCell(headerCell);
            }

            // Ajouter les données de chaque objectif dans le tableau
            for (Objectif objectif : objectifs) {
                table.addCell(String.valueOf(objectif.getId_obj()));
                table.addCell(objectif.getLibelle());
                table.addCell(objectif.getListCritereAsString());
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

    @FXML
    void filterByCritere(ActionEvent event) {
        String selectedCritere = critereFilterCombobox.getValue();
        ObservableList<Objectif> filteredList = FXCollections.observableArrayList();

        if ("Tout".equals(selectedCritere)) {
            filteredList.addAll(getObjectifs()); // Charger tous les objectifs sans filtre
        } else {
            ListCritere critereEnum = ListCritere.valueOf(selectedCritere); // Convertir la chaîne en énumération

            for (Objectif objectif : getObjectifs()) {
                if (objectif.getListCritere().contains(critereEnum)) {
                    filteredList.add(objectif);
                }
            }
        }

        // Mettre à jour le TableView avec la liste filtrée d'objectifs
        ObjectifTableView.setItems(filteredList);
    }

    private ObservableList<Objectif> getObjectifs() {
        ObservableList<Objectif> objectifs = FXCollections.observableArrayList();
        // Remplacez ce code avec votre propre logique pour récupérer les objectifs depuis la base de données
        // ou depuis une autre source de données
        try {
            String query = "SELECT id_obj, libelle, listCritere FROM objectif";
            con = MyConnection.getInstance();
            st = con.getCnx().prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String libelle = rs.getString("libelle");
                String listCritereString = rs.getString("listCritere");
                Integer id_obj = rs.getInt("id_obj");

                // Convertir la chaîne de critères en une liste d'énumérations ListCritere
                List<ListCritere> listCritere = mapCritereStringToList(listCritereString.split(", "));

                // Créer un nouvel objet Objectif avec la liste de critères
                objectifs.add(new Objectif(id_obj, libelle, listCritere));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return objectifs;
    }

    private ObservableList<XYChart.Series<String, Number>> updateLineChart() {
        String query = "SELECT libelle, listCritere FROM objectif ";
        ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = MyConnection.getInstance().getCnx().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String libelle = resultSet.getString("libelle");
                String critereString = resultSet.getString("listCritere");
                int critereCount = calculateCheckedCriteria(critereString);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(libelle);
                series.getData().add(new XYChart.Data<>(libelle, critereCount));
                data.add(series);
            }
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(4);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

        private int calculateCheckedCriteria(String listCritere) {
        // Compteur pour stocker le nombre de critères cochés
        int count = 0;

        // Vérifier chaque critère et incrémenter le compteur si présent dans la chaîne listCritere
        for (ListCritere critere : ListCritere.values()) {
            if (listCritere.contains(critere.name())) {
                count++;
            }
        }

        return count;
    }


    @FXML
    void createObjectif(ActionEvent event) {
        if (tfLibelle.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un libellé !", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si au moins un critère est sélectionné
        if (!tfCritere1.isSelected() && !tfCritere2.isSelected() && !tfCritere3.isSelected() && !tfCritere4.isSelected()) {
            afficherAlerte("Erreur", "Veuillez sélectionner au moins un critère !", Alert.AlertType.ERROR);
            return;
        }
        String insert = "INSERT INTO objectif(libelle, listCritere) VALUES(?, ?)";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);

            // Récupérer le libellé sélectionné
            String libelle = tfLibelle.getValue();

            // Créez une liste pour stocker les critères sélectionnés
            List<String> criteres = new ArrayList<>();

            // Ajoutez les critères sélectionnés à la liste
            if (tfCritere1.isSelected()) {
                criteres.add("sans_gluten");
            }
            if (tfCritere2.isSelected()) {
                criteres.add("sans_glucose");
            }
            if (tfCritere3.isSelected()) {
                criteres.add("sans_lactose");
            }
            if (tfCritere4.isSelected()) {
                criteres.add("protein");
            }

            // Convertissez la liste en une chaîne séparée par des virgules
            String listCritere = String.join(", ", criteres);

            // Assurez-vous d'avoir exactement le nombre de paramètres attendus dans votre requête SQL
            st.setString(1, libelle);
            st.setString(2, listCritere);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Objectif ajouté avec succès !");
                resetForm();
            } else {
                System.out.println("Échec de l'ajout de l'objectif !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        loadObjectifs();
        lineChart.getData().clear(); // Supprimer les données existantes
        lineChart.getData().addAll(updateLineChart()); // Ajouter les nouvelles données mises à jour

    }

    @FXML
    void initialize() {
        // Assurez-vous que la ComboBox contient les éléments objectif
        tfLibelle.getItems().addAll(objectif);

        // Ajoutez un gestionnaire d'événements pour capturer la sélection de l'utilisateur
        tfLibelle.setOnAction(event -> {
            String libelle = tfLibelle.getValue();
            System.out.println("Objectif sélectionné : " + libelle);
        });
        critereFilterCombobox.getItems().add("Tout");
        critereFilterCombobox.getItems().addAll(Arrays.stream(ListCritere.values())
                .map(Enum::toString)
                .collect(Collectors.toList()));


        // Ajoutez un gestionnaire d'événements pour capturer la sélection de l'utilisateur
        critereFilterCombobox.setOnAction(event -> {
            filterByCritere(event); // Appeler la méthode de filtrage lorsque l'utilisateur sélectionne un critère
        });
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        // Ajouter un listener pour le TableView pour activer les boutons "Modifier" et "Supprimer" lorsque
        // une ligne est sélectionnée
        ObjectifTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Activer les boutons "Modifier" et "Supprimer"
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
                // Désactiver le bouton "Ajouter"
                btnAjouter.setDisable(true);
            } else {
                // Aucune ligne sélectionnée, désactiver les boutons "Modifier" et "Supprimer"
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
                // Activer le bouton "Ajouter"
                btnAjouter.setDisable(false);
            }
        });
        colIdObj.setCellValueFactory(new PropertyValueFactory<>("id_obj"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colCritere.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getListCritereAsString()));
        ObjectifTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Récupérez les données de l'objectif sélectionné et affichez-les dans le formulaire
                tfLibelle.setValue(newSelection.getLibelle());

                // Vous devrez remplacer cette logique pour déterminer quels critères sont sélectionnés
                // et les cocher dans le formulaire
                // Par exemple :
                tfCritere1.setSelected(newSelection.getListCritere().contains(ListCritere.sans_glucose));
                tfCritere2.setSelected(newSelection.getListCritere().contains(ListCritere.sans_gluten));
                tfCritere3.setSelected(newSelection.getListCritere().contains(ListCritere.sans_lactose));
                tfCritere4.setSelected(newSelection.getListCritere().contains(ListCritere.protein));
                // Répétez pour les autres critères...
            }
        });
        loadObjectifs();
        ObservableList<XYChart.Series<String, Number>> data = updateLineChart();
        lineChart.getData().addAll(data);

    }

    private void loadObjectifs() {
        List<Objectif> objectifs = new ArrayList<>();

        try {
            String query = "SELECT id_obj, libelle, listCritere FROM objectif";
            con = MyConnection.getInstance();
            st = con.getCnx().prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String libelle = rs.getString("libelle");
                String listCritereString = rs.getString("listCritere");
                Integer id_obj = rs.getInt("id_obj");

                // Convertir la chaîne de critères en une liste d'énumérations ListCritere
                List<ListCritere> listCritere = mapCritereStringToList(listCritereString.split(", "));

                // Créer un nouvel objet Objectif avec la liste de critères
                objectifs.add(new Objectif(id_obj, libelle, listCritere));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        ObservableList<Objectif> observableObjectifs = FXCollections.observableArrayList();
        observableObjectifs.addAll(objectifs);
        ObjectifTableView.setItems(observableObjectifs);
        //updateLineChart();
    }

    private List<ListCritere> mapCritereStringToList(String[] critereArray) {
        List<ListCritere> listCritere = new ArrayList<>();
        for (String critere : critereArray) {
            // Parcourir toutes les constantes de ListCritere et vérifier la correspondance
            for (ListCritere lc : ListCritere.values()) {
                if (lc.toString().equals(critere)) {
                    listCritere.add(lc); // Ajouter la constante d'énumération correspondante
                    break;
                }
            }
        }
        return listCritere;
    }

    @FXML
    void updateObjectif(ActionEvent event) {
        // Récupérer l'objectif sélectionné dans le TableView
        Objectif selectedObjectif = ObjectifTableView.getSelectionModel().getSelectedItem();

        if (selectedObjectif == null) {
            // Aucun objectif sélectionné, affichez un message d'avertissement à l'utilisateur
            // ou prenez d'autres mesures appropriées
            return;
        }

        // Afficher une alerte pour confirmer la modification
        Optional<ButtonType> result = afficherAlerte("Confirmation de modification", "Voulez-vous vraiment modifier cet objectif ?", Alert.AlertType.CONFIRMATION);

        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                // L'utilisateur a confirmé la modification, procédez à la modification de l'objectif

                // Récupérer les nouvelles valeurs des champs de l'objectif
                String newLibelle = tfLibelle.getValue(); // Par exemple, si tfLibelle est un champ de texte pour le libellé

                // Récupérer les nouveaux critères sélectionnés
                List<ListCritere> newCriteres = new ArrayList<>();
                if (tfCritere1.isSelected()) {
                    newCriteres.add(ListCritere.sans_glucose);
                }
                if (tfCritere2.isSelected()) {
                    newCriteres.add(ListCritere.sans_gluten);
                }
                if (tfCritere3.isSelected()) {
                    newCriteres.add(ListCritere.sans_lactose);
                }
                if (tfCritere4.isSelected()) {
                    newCriteres.add(ListCritere.protein);
                }
                // Répéter pour les autres critères...

                // Mettre à jour l'objectif sélectionné avec les nouvelles valeurs
                selectedObjectif.setLibelle(newLibelle);
                selectedObjectif.setListCritere(newCriteres);

                // Mettre à jour l'objectif dans la base de données en exécutant une requête SQL UPDATE
                try {
                    String updateQuery = "UPDATE objectif SET libelle = ?, listCritere = ? WHERE id_obj = ?";
                    st = con.getCnx().prepareStatement(updateQuery);
                    st.setString(1, newLibelle);
                    st.setString(2, String.join(", ", newCriteres.stream().map(Enum::toString).collect(Collectors.toList()))); // Convertir les critères en une chaîne séparée par des virgules
                    st.setInt(3, selectedObjectif.getId_obj());
                    int rowsAffected = st.executeUpdate();

                    if (rowsAffected > 0) {
                        afficherAlerte("Succès", "Objectif mis à jour avec succès !", Alert.AlertType.INFORMATION);
                        // Rafraîchir la TableView après la mise à jour
                        loadObjectifs();
                        resetForm();
                    } else {
                        afficherAlerte("Erreur", "Échec de la mise à jour de l'objectif !", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        loadObjectifs();
        lineChart.getData().clear(); // Supprimer les données existantes
        lineChart.getData().addAll(updateLineChart()); // Ajouter les nouvelles données mises à jour
    }


    @FXML
    void supprimerObjectif(ActionEvent event) {
        // Récupérer l'objectif sélectionné dans le TableView
        Objectif selectedObjectif = ObjectifTableView.getSelectionModel().getSelectedItem();

        if (selectedObjectif == null) {
            // Aucun objectif sélectionné, affichez un message d'avertissement à l'utilisateur
            // ou prenez d'autres mesures appropriées
            return;
        }

        // Afficher une alerte pour confirmer la suppression
        Optional<ButtonType> result = afficherAlerte("Confirmation de suppression", "Voulez-vous vraiment supprimer cet objectif ?", Alert.AlertType.CONFIRMATION);

        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                // L'utilisateur a confirmé la suppression, procédez à la suppression de l'objectif

                // Supprimer l'objectif de la base de données
                try {
                    String deleteQuery = "DELETE FROM objectif WHERE id_obj = ?";
                    st = con.getCnx().prepareStatement(deleteQuery);
                    st.setInt(1, selectedObjectif.getId_obj());
                    int rowsAffected = st.executeUpdate();
                    if (rowsAffected > 0) {
                        afficherAlerte("Succès", "Objectif supprimé avec succès !", Alert.AlertType.INFORMATION);
                        // Rafraîchir la TableView après la suppression
                        loadObjectifs();
                        resetForm();
                    } else {
                        afficherAlerte("Erreur", "Échec de la suppression de l'objectif !", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        loadObjectifs();
        lineChart.getData().clear(); // Supprimer les données existantes
        lineChart.getData().addAll(updateLineChart()); // Ajouter les nouvelles données mises à jour
    }

    private Optional<ButtonType> afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();

    }

    private void resetForm() {
        tfLibelle.setValue(null);
        tfCritere1.setSelected(false);
        tfCritere2.setSelected(false);
        tfCritere3.setSelected(false);
        tfCritere4.setSelected(false);
        // Réinitialiser d'autres champs du formulaire si nécessaire
    }



}
