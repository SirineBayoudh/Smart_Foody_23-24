package com.example.demo.Controllers;


import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.demo.Models.Avis;
import com.example.demo.Models.Reclamation;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class avisController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BarChart<String, Integer> BarChartAvis;

    @FXML
    private PieChart PiChartAvis;

    @FXML
    private Button btnSupprimer;

    @FXML
    private TableColumn<Avis, String> colCommentaire;

    @FXML
    private TableColumn<Avis, java.util.Date> colDateAvis;

    @FXML
    private TableColumn<Avis, Integer> colIdAvis;

    @FXML
    private TableColumn<Avis, Integer> colIdClient;

    @FXML
    private TableColumn<Avis, Integer> colNotes;

    @FXML
    private TableColumn<Avis, Integer> colRefProduit;

    @FXML
    private TableView<Avis> table1;

    @FXML
    private TextArea tfCommentaire;

    @FXML
    private TextField tfIdAvis;
    @FXML
    private TextField tfDateAvis;

    @FXML
    private Label tfNomProduit;

    @FXML
    private TextField tfNotes;

    @FXML
    private TextField tfRef;

    @FXML
    private TextField tfrecherche;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private MyConnection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    private int id;

    @FXML
    void initialize() {
        showAvis();
        afficherCharteAvis();

        // Initialisation de la bareCharte
        if(xAxis != null) {
            xAxis.setLabel("Nombre d'étoiles");
        } else {
            System.err.println("xAxis is null, initialization failed!");
        }
    }


    @FXML
    void getData(MouseEvent event) {
        //Remettre à null avant chqaue clique
            BarChartAvis.getData().clear();

            Avis avis = table1.getSelectionModel().getSelectedItem();
            id = avis.getId_avis();
            tfIdAvis.setText(String.valueOf(avis.getId_avis()));
            tfRef.setText(String.valueOf(avis.getRef_produit()));
            tfNotes.setText(String.valueOf(avis.getNb_etoiles()));
            tfCommentaire.setText(avis.getCommentaire());
            tfDateAvis.setText(String.valueOf(avis.getDate_avis()));
            btnSupprimer.setDisable(false);
            afficheBarCharte(avis.getRef_produit());

        // Exécutez la requête SQL pour obtenir la marque du produit
        String req = "SELECT marque FROM produit WHERE ref=?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(req);
            st.setInt(1, avis.getRef_produit());
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                String marque = rs.getString("marque");
                tfNomProduit.setText(marque);
            } else {
                tfNomProduit.setText("Marque inconnue");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void rechercherAvis(MouseEvent event) {
        // Requête SQL pour récupérer les détails de l'avis selon l'ID
        String req = "SELECT * FROM avis WHERE id_avis=? ";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(req);
            if (!tfrecherche.getText().isEmpty() && isInteger(tfrecherche.getText())) {
                st.setInt(1, Integer.parseInt(tfrecherche.getText()));
                rs = st.executeQuery();
                ObservableList<Avis> avis = FXCollections.observableArrayList();
                while (rs.next()) {
                    Avis st = new Avis();
                    st.setId_avis(rs.getInt("id_avis"));
                    st.setRef_produit(rs.getInt("ref_produit"));
                    st.setId_client(rs.getInt("id_client"));
                    st.setNb_etoiles(rs.getInt("nb_etoiles"));
                    st.setCommentaire(rs.getString("commentaire"));
                    st.setDate_avis(rs.getDate("date_avis"));
                    avis.add(st); // Ajouter l'avis à la liste

                }
                 table1.setItems(avis);
            } else {
                // Si l'entrée n'est pas un nombre valide
                afficherAlerte("Recherche","Veuillez saisir un identifiant existant");
                showAvis();

            }
        } catch (SQLException e) {
            afficherAlerte("Recherche","Veuillez saisir un Identifiant");
        }
        tfrecherche.setText(null);
    }

    @FXML
    void supprimerAvis(ActionEvent event) {
        String delete = "DELETE FROM avis WHERE id_avis = ?";
        con = MyConnection.getInstance();

        //Alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre avis ?");
        alert.setContentText("Veuillez confirmer votre action.");
        ButtonType boutonOui = new ButtonType("Oui");
        ButtonType boutonNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(boutonOui, boutonNon);
        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                try {
                    st = con.getCnx().prepareStatement(delete);
                    st.setInt(1, id);
                    st.executeUpdate();
                    clear();
                    showAvis();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (reponse == boutonNon) {
                // Action à effectuer si l'utilisateur choisit "Non"
                System.out.println("L'utilisateur a choisi Non");
                // Ajoutez votre code ici
            }
        });
        afficherCharteAvis();
        BarChartAvis.getData().clear();

    }

    private ObservableList<Avis> getAvis() {
        ObservableList<Avis> avis = FXCollections.observableArrayList();
        String query = "SELECT * from avis";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Avis st = new Avis();
                st.setId_avis(rs.getInt("id_avis"));
                st.setRef_produit(rs.getInt("ref_produit"));
                st.setId_client(rs.getInt("id_client"));
                st.setNb_etoiles(rs.getInt("nb_etoiles"));
                st.setCommentaire(rs.getString("commentaire"));
                st.setDate_avis(rs.getDate("date_avis"));
                avis.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return avis;
    }

    public void showAvis() {
        ObservableList<Avis> list = getAvis();
        table1.setItems(list);
        colIdAvis.setCellValueFactory(new PropertyValueFactory<>("id_avis"));
        colRefProduit.setCellValueFactory(new PropertyValueFactory<>("ref_produit"));
        colIdClient.setCellValueFactory(new PropertyValueFactory<>("id_avis"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("nb_etoiles"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        colDateAvis.setCellValueFactory(new PropertyValueFactory<>("date_avis"));
    }

    private void afficherCharteAvis() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        con = MyConnection.getInstance();
        try {
            String avisQuery = "SELECT ref_produit, COUNT(*) as count FROM avis GROUP BY ref_produit";
            st = con.getCnx().prepareStatement(avisQuery);
            ResultSet queryOutput = st.executeQuery();
            while (queryOutput.next()) {
                String refProduit = queryOutput.getString("ref_produit");
                int count = queryOutput.getInt("count");
                pieChartData.add(new PieChart.Data(refProduit + " ", count));
            }
            PiChartAvis.setData(pieChartData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void afficheBarCharte(int ref) {
            con = MyConnection.getInstance();
            try {
                // Préparez la requête pour récupérer le nombre d'avis pour chaque nombre d'étoiles
                String avisQuery = "SELECT nb_etoiles, COUNT(*) as count FROM avis WHERE ref_produit = ? GROUP BY nb_etoiles";
                st = con.getCnx().prepareStatement(avisQuery);
                st.setInt(1, ref);

                ResultSet queryOutput = st.executeQuery();

                // Créez une liste pour stocker le nombre d'avis pour chaque nombre d'étoiles
                ObservableList<Integer> avisCounts = FXCollections.observableArrayList(0, 0, 0, 0, 0);

                // Remplissez la liste avec les nombres d'avis récupérés de la base de données
                while (queryOutput.next()) {
                    int nbEtoiles = queryOutput.getInt("nb_etoiles");
                    int count = queryOutput.getInt("count");
                    avisCounts.set(nbEtoiles - 1, count); // Soustrayez 1 pour correspondre à l'index de la liste
                }

                // Créez une série de données pour le graphique
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                for (int i = 0; i < avisCounts.size(); i++) {
                    series.getData().add(new XYChart.Data<>(String.valueOf(i + 1) + " étoiles \n", avisCounts.get(i)));
                }

                // Ajoutez la série de données au graphique
                BarChartAvis.getData().add(series);

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }


    void clear() {
        tfIdAvis.clear();
        tfRef.clear();
        tfNotes.clear();
        tfCommentaire.clear();
        tfDateAvis.clear();
        btnSupprimer.setDisable(true);
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une confirmation
    private void afficherConfirmation(String titre, String header, String message) {
        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle(titre);
        confirmation.setHeaderText(header);
        confirmation.setContentText(message);
        confirmation.showAndWait();
    }

}















/*

import com.example.demo.Models.Avis;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class avisController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Avis, String> colCommentaire;

    @FXML
    private TableColumn<Avis, Integer> colIdAvis;

    @FXML
    private TableColumn<Avis, Integer> colNotes;

    @FXML
    private TableColumn<Avis, Integer> colRefProduit;

    @FXML
    private TableView<Avis> table;

    @FXML
    private TextArea tfCommentaire;

    @FXML
    private TextField tfIdAvis;

    @FXML
    private TextField tfNotes;

    @FXML
    private TextField tfRef;

    private MyConnection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    private int id;

    @FXML
    void initialize() {
        showAvis();
    }

    void clear() {
        tfIdAvis.clear();
        tfRef.clear();
        tfNotes.clear();
        tfCommentaire.clear();
        btnAdd.setDisable(false);
    }

    @FXML
    void addReclamation(ActionEvent event) {
        String insert = "INSERT INTO avis(id_avis,ref_produit,nb_etoiles,commentaire) VALUES (?,?,?,?)";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            st.setInt(1, Integer.parseInt(tfIdAvis.getText()));
            st.setInt(2, Integer.parseInt(tfRef.getText()));
            st.setInt(3, Integer.parseInt(tfNotes.getText()));
            st.setString(4, tfCommentaire.getText());
            st.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Avis ajouté avec succès!", ButtonType.OK);
            alert.show();
            showAvis();
            clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearReclamation(ActionEvent event) {
        clear();
    }

    @FXML
    void deleteReclamation(ActionEvent event) {
        String delete = "DELETE FROM avis WHERE id_avis = ?";
        con = MyConnection.getInstance();

        //Alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre avis ?");
        alert.setContentText("Veuillez confirmer votre action.");
        ButtonType boutonOui = new ButtonType("Oui");
        ButtonType boutonNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(boutonOui, boutonNon);
        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                try {
                    st = con.getCnx().prepareStatement(delete);
                    st.setInt(1, id);
                    st.executeUpdate();
                    clear();
                    showAvis();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (reponse == boutonNon) {
                // Action à effectuer si l'utilisateur choisit "Non"
                System.out.println("L'utilisateur a choisi Non");
                // Ajoutez votre code ici
            }
        });

    }

    @FXML
    void getData(javafx.scene.input.MouseEvent event) {
        Avis avis = table.getSelectionModel().getSelectedItem();
        id = avis.getId_avis();
        tfIdAvis.setText(String.valueOf(avis.getId_avis()));
        tfRef.setText(String.valueOf(avis.getRef_produit()));
        tfNotes.setText(String.valueOf(avis.getNb_etoiles()));
        tfCommentaire.setText(avis.getCommentaire());
        btnAdd.setDisable(true);
    }
    @FXML
    void updateReclamation(ActionEvent event) {
        String update = "UPDATE `avis` SET `ref_produit`=?,`nb_etoiles`=?,`commentaire`=?  WHERE `id_avis`=?";
        con = MyConnection.getInstance();

        //Alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir Modifier votre avis ?");
        alert.setContentText("Veuillez confirmer votre action.");
        ButtonType boutonOui = new ButtonType("Oui");
        ButtonType boutonNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(boutonOui, boutonNon);
        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
        try {
            st = con.getCnx().prepareStatement(update);
            st.setString(1,tfRef.getText());
            st.setString(2,tfNotes.getText());
            st.setString(3,tfCommentaire.getText());
            st.setInt(4, Integer.parseInt(tfIdAvis.getText()));
            st.executeUpdate();
            clear();
            showAvis();
            btnAdd.setDisable(false);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } }
            else if (reponse == boutonNon) {
                // Action à effectuer si l'utilisateur choisit "Non"
                System.out.println("L'utilisateur a choisi Non");
                // Ajoutez votre code ici
            }
        });
    }

    private ObservableList<Avis> getAvis() {
        ObservableList<Avis> avis = FXCollections.observableArrayList();
        String query = "SELECT * from avis";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Avis st = new Avis();
                st.setId_avis(rs.getInt("id_avis"));
                st.setRef_produit(rs.getInt("ref_produit"));
                st.setNb_etoiles(rs.getInt("nb_etoiles"));
                st.setCommentaire(rs.getString("commentaire"));
                avis.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return avis;
    }

    public void showAvis() {
        ObservableList<Avis> list = getAvis();
        table.setItems(list);
        colIdAvis.setCellValueFactory(new PropertyValueFactory<>("id_avis"));
        colRefProduit.setCellValueFactory(new PropertyValueFactory<>("ref_produit"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("nb_etoiles"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
    }

}

 */