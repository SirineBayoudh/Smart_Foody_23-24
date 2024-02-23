package com.example.demo.Controllers;

import com.example.demo.Models.ListCritere;
import com.example.demo.Models.Objectif;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private CheckBox tfCritere1;

    @FXML
    private CheckBox tfCritere2;

    @FXML
    private CheckBox tfCritere3;

    @FXML
    private CheckBox tfCritere4;

    @FXML
    private ComboBox<String> tfLibelle;

    private String[] objectif = {"Bien être", "prise de poids", "Perte de poids", "Prise de masse musculaire"};
    @FXML
    private TableColumn<Objectif, String> colCritere;

    @FXML
    private TableColumn<Objectif, Integer> colIdObj;

    @FXML
    private TableColumn<Objectif, String> colLibelle;


    @FXML
    void createObjectif(ActionEvent event) {
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
            } else {
                System.out.println("Échec de l'ajout de l'objectif !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        loadObjectifs();
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
                    } else {
                        afficherAlerte("Erreur", "Échec de la mise à jour de l'objectif !", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
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
    }

    private Optional<ButtonType> afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }


}
