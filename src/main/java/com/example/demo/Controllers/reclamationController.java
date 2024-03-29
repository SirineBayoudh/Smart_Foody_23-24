package com.example.demo.Controllers;

import com.example.demo.Models.Reclamation;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.cell.PropertyValueFactory;


import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.Desktop;
public class reclamationController{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    // Déclaration des éléments de l'interface
    @FXML
    private ProgressIndicator collabRaduis;

    @FXML
    private TextField collabPourcentage;

    @FXML
    private Label collabPersonnes;

    @FXML
    private ProgressIndicator infoRaduis;

    @FXML
    private TextField infoPourcentage;

    @FXML
    private Label infoPersonnes;

    @FXML
    private ProgressIndicator recRaduis;

    @FXML
    private TextField RecPourcentage;

    @FXML
    private Label recPersonnes;

    @FXML
    private ProgressIndicator autresRaduis;

    @FXML
    private TextField AutrePourcentage;

    @FXML
    private Label autresPersonnes;

    @FXML
    private ProgressIndicator remerRaduis;

    @FXML
    private TextField RemerPourcentage;

    @FXML
    private Label remerpersonnes;

    @FXML
    private TableView<Reclamation> table1;

    @FXML
    private TableView<Reclamation> table11;

    @FXML
    private TableColumn<Reclamation, Integer> colIdReclamation1;

    @FXML
    private TableColumn<Reclamation, Integer> colIdClient1;

    @FXML
    private TableColumn<Reclamation, String> colDescription1;

    @FXML
    private TableColumn<Reclamation, String> colTitre1;

    @FXML
    private TableColumn<Reclamation, String> colStatut1;

    @FXML
    private TableColumn<Reclamation, String> colType1;

    @FXML
    private TableColumn<Reclamation, java.util.Date> colDateReclamation1;

    @FXML
    private TableColumn<Reclamation, Integer> colIdReclamation11;

    @FXML
    private TableColumn<Reclamation, Integer> colIdClient11;

    @FXML
    private TableColumn<Reclamation, String> colDescription11;

    @FXML
    private TableColumn<Reclamation, String> colTitre11;

    @FXML
    private TableColumn<Reclamation, String> colStatut11;

    @FXML
    private TableColumn<Reclamation, String> colType11;

    @FXML
    private TableColumn<Reclamation, java.util.Date> colDateReclamation11;

    @FXML
    private Button btnArchives;

    @FXML
    private Button btnRetourListe;

    @FXML
    private Button btnEnvoyerMail;

    @FXML
    private Button btnExcel;

    @FXML
    private ImageView Archivebtn;

    @FXML
    private Pane ensembleArchives;

    @FXML
    private Pane ensembleReclamations;

    @FXML
    private TextField nbNotif;

    @FXML
    private TextArea tfDescription;

    @FXML
    private TextField tfMail;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfRechercher;
    @FXML
    private TextField tfId_reclamation;

    @FXML
    private TextArea tfReponse;

    @FXML
    private TextField tfTitre;

    @FXML
    private TextField tfType;

    @FXML
    private Label nbArchives;

    @FXML
    private ImageView rechercheIcon;

    @FXML
    private ImageView micro;

    // Connexion à la base de données
    private MyConnection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    // Déclarer un indicateur booléen pour suivre l'état du tri
    boolean triParTypeActif = false;



    @FXML
    void initialize() {
        // Initialisation des éléments de l'interface
        nombreNotification();
        nombreMessageArchive();
        showReclamations();
        showReclamationsArchivées();
        setProgressCollab();
        setProgressInfo();
        setProgressRec();
        setProgressRemer();
        setProgressAutres();
    }

    @FXML
    void getDataTableArchive(MouseEvent event) {
        // Récupération des données lorsqu'une ligne de la table est sélectionnée
        ObservableList<Reclamation> list = FXCollections.observableArrayList();
        Reclamation reclamation = table11.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Quelle action voulez-vous effectuer ?");
        alert.setContentText("Veuillez confirmer votre choix.");
        ButtonType boutonArch = new ButtonType("Desarchiver");
        ButtonType boutonAnn = new ButtonType("Annuler");
        alert.getButtonTypes().setAll(boutonArch, boutonAnn);
        alert.showAndWait().ifPresent(reponse -> {
           if(reponse == boutonArch){
                String req = "UPDATE reclamation SET archive=0 WHERE id_reclamation=?";
                con = MyConnection.getInstance();
                try {
                    st = con.getCnx().prepareStatement(req);
                    st.setInt(1,reclamation.getId_reclamation());
                    st.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
               nombreNotification();
               nombreMessageArchive();
            }else{
                tfTitre.setText(null);
                tfType.setText(null);
                tfDescription.setText(null);
                tfNom.setText(null);
                tfMail.setText(null);
            }
        });
        showReclamationsArchivées();
    }

    @FXML
    void getData(MouseEvent event) {
        // Récupération des données lorsqu'une ligne de la table est sélectionnée
        Reclamation reclamation = table1.getSelectionModel().getSelectedItem();
        if (reclamation != null) {
            // Vérifier si l'ID client existe dans la table utilisateur
            String reqExistence = "SELECT * FROM utilisateur WHERE id_utilisateur=?";
            con = MyConnection.getInstance();
            try {
                PreparedStatement stExistence = con.getCnx().prepareStatement(reqExistence);
                stExistence.setInt(1, reclamation.getId_client());
                ResultSet rsExistence = stExistence.executeQuery();
                if (rsExistence.next()) {
                    // L'ID client existe, procéder à la récupération des informations de réclamation
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Quelle action voulez-vous effectuer ?");
                    alert.setContentText("Veuillez confirmer votre action.");
                    ButtonType boutonOui = new ButtonType("Repondre");
                    ButtonType boutonArch = new ButtonType("Archiver");
                    ButtonType boutonAnn = new ButtonType("Annuler");


                    // Vérifier le statut avant d'ajouter le bouton "Repondre"
                    if (!reclamation.getStatut().equals("Repondu")) {
                        alert.getButtonTypes().setAll(boutonOui, boutonArch, boutonAnn);
                    } else {
                        alert.getButtonTypes().setAll(boutonArch, boutonAnn);
                    }

                    alert.showAndWait().ifPresent(reponse -> {
                        if (reponse == boutonOui) {
                            // Vérifier le statut avant de permettre de répondre
                            if (!reclamation.getStatut().equals("Repondu")) {
                                // Récupération du nom et de l'email du client à partir de son ID
                                String req = "SELECT nom, prenom, email FROM utilisateur WHERE id_utilisateur=?";
                                try {
                                    st = con.getCnx().prepareStatement(req);
                                    st.setInt(1, reclamation.getId_client());
                                    ResultSet rs = st.executeQuery();
                                    if (rs.next()) {
                                        tfTitre.setText(reclamation.getTitre());
                                        tfType.setText(reclamation.getType());
                                        tfDescription.setText(reclamation.getDescription());
                                        tfId_reclamation.setText(String.valueOf(reclamation.getId_reclamation()));
                                        String nom = rs.getString("nom");
                                        String prenom = rs.getString("prenom");
                                        String email = rs.getString("email");
                                        tfNom.setText(nom + "  " + prenom);
                                        tfMail.setText(email);
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                // Afficher un message d'erreur si le statut est déjà "Repondu"
                                afficherAlerte("Erreur", "Cette réclamation a déjà été répondue.");
                            }
                        } else if (reponse == boutonArch) {
                            // Archiver la réclamation
                            String req = "UPDATE reclamation SET archive=1 WHERE id_reclamation=?";
                            try {
                                st = con.getCnx().prepareStatement(req);
                                st.setInt(1, reclamation.getId_reclamation());
                                st.executeUpdate();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            nombreNotification();
                            nombreMessageArchive();
                        } else {
                            clear();
                        }
                    });
                    showReclamations();
                } else {
                    // L'ID client n'existe pas dans la table utilisateur
                    afficherAlerte("Erreur", "ID client introuvable dans la table utilisateur.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    void AfficherListeArchives(ActionEvent event) {
                switchForm(event);
    }
    @FXML
    void AfficherListeNormal(ActionEvent event) {
              switchForm(event);
    }
    @FXML
    void trieTable(MouseEvent event) {
        // Vérifier l'état du tri
        if (triParTypeActif) {
            // Si le tri est actif, annuler le tri en rechargeant les réclamations non triées
            showReclamations();
            // Mettre à jour l'état du tri
            triParTypeActif = false;
        } else {
            // Si le tri n'est pas actif, activer le tri par type
            // Requête SQL pour trier les réclamations par type
            String req = "SELECT * FROM reclamation WHERE archive=0 AND statut = 'Attente'";
            con = MyConnection.getInstance();
            try {
                st = con.getCnx().prepareStatement(req);
                rs = st.executeQuery();
                ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
                while (rs.next()) {
                    Reclamation reclamation = new Reclamation();
                    reclamation.setId_reclamation(rs.getInt("id_reclamation"));
                    reclamation.setId_client(rs.getInt("id_client"));
                    reclamation.setDescription(rs.getString("description"));
                    reclamation.setTitre(rs.getString("titre"));
                    reclamation.setStatut(rs.getString("statut"));
                    reclamation.setType(rs.getString("type"));
                    reclamation.setDate_reclamation(rs.getDate("date_reclamation"));
                    reclamations.add(reclamation);
                }
                table1.setItems(reclamations);

                // Mettre à jour l'état du tri
                triParTypeActif = true;
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur lors du tri par type");
                alert.show();
            }
        }
    }
    @FXML
    void envoyerMail(ActionEvent event) {
        // Vérifier que les champs ne sont pas vides
        if (tfTitre.getText().isEmpty() || tfType.getText().isEmpty() || tfDescription.getText().isEmpty() || tfNom.getText().isEmpty() || tfMail.getText().isEmpty()) {
            // Afficher un message d'alerte si l'un des champs est vide
            afficherAlerte("Champs manquants", "Veuillez remplir tous les champs avant d'envoyer le courriel.");
        } else {
            if (tfReponse == null || tfReponse.getText().isEmpty()) {
                afficherAlerte("Réponse manquante", "Veuillez saisir une réponse avant d'envoyer le courriel.");
            } else {
                try {
                    // Récupération du contenu de tfReponse
                    String reponse = tfReponse.getText();
                    String Nom = tfNom.getText();

// Construction du contenu HTML avec le texte, le chemin de l'image et le contenu de tfReponse
                    String corpsMailHTML ="<p style=\"font-style: regular; font-size: 14px;\">Bonjour Mr/Mme "+ Nom +"!<br/>" + reponse + "</p>" + "<p><span style=\"font-weight: bold; font-style: italic;\">Bonne journée !</span><br/>" +
                            "<span style=\"font-style: italic;\">Nous vous remercions pour l'intérêt que vous portez à Smart Foody. <br/>" +
                            "Veuillez recevoir nos salutations.</span></p>" ;

// Construction du pied de page du mail
                    String piedPageHTML = "<p style=\"font-size: 14px;\">Smart Foody &copy; 2024</p>" +
                            "<p style=\"font-size: 14px;\">123 Rue Principale, Ville, Pays</p>" +
                            "<p style=\"font-size: 14px;\">Téléphone: +123456789 | Email: contact@smartfoody.com</p>" +
                            "<p style=\"font-size: 14px;\">Suivez-nous sur <a href=\"https://www.facebook.com/smartfoody.tn\">Facebook</a></p>";

// Concaténation du corps du mail et du pied de page dans le contenu final du mail
                    String contenuHTML = corpsMailHTML + piedPageHTML;
                    // Appel de la méthode fonctionMail pour envoyer l'email avec le contenu HTML et le chemin de l'image
                    EmailUtil.fonctionMail(tfMail.getText(), tfTitre.getText(), contenuHTML, "src/main/resources/com/example/demo/ImagesGestionReclamations/grand logo.png");

                    // Le reste de votre code pour mettre à jour le statut de la réclamation et afficher les notifications
                    String req = "UPDATE reclamation SET statut = 'Repondu' WHERE id_reclamation = ?";
                    con = MyConnection.getInstance();
                    st = con.getCnx().prepareStatement(req);
                    st.setInt(1, Integer.parseInt(tfId_reclamation.getText()));
                    st.executeUpdate();

                    afficherConfirmation("Courriel envoyé", "Réponse de Smart Foody", "Le courriel a été envoyé avec succès.");
                    clear();
                    nombreNotification();
                    showReclamations();
                    showReclamationsArchivées();
                    nombreNotification();
                } catch (SQLException | MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    @FXML
    void rechercherRec(MouseEvent event) {
        // Vérification que tfRechercher est non null
        if (tfRechercher != null) {
            // Requête SQL pour récupérer les détails de la réclamation selon l'ID
            String req = "SELECT * FROM reclamation WHERE id_reclamation=? ";
            con = MyConnection.getInstance();
            try {
                st = con.getCnx().prepareStatement(req);
                if (isInteger(tfRechercher.getText())) {
                    st.setInt(1, Integer.parseInt(tfRechercher.getText()));
                    rs = st.executeQuery();
                    ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
                    ObservableList<Reclamation> reclamationsArchives = FXCollections.observableArrayList();
                    while (rs.next()) {
                        Reclamation reclamation = new Reclamation();
                        reclamation.setId_reclamation(rs.getInt("id_reclamation"));
                        reclamation.setId_client(rs.getInt("id_client"));
                        reclamation.setDescription(rs.getString("description"));
                        reclamation.setTitre(rs.getString("titre"));
                        reclamation.setStatut(rs.getString("statut"));
                        reclamation.setType(rs.getString("type"));
                        reclamation.setDate_reclamation(rs.getDate("date_reclamation"));

                        int etat = rs.getInt("archive");

                        // Afficher les résultats dans la table
                        if (etat == 0) {
                            reclamations.add(reclamation);
                        } else {
                            reclamationsArchives.add(reclamation);
                        }
                    }
                    // Définir les éléments dans la table non archivée
                    table1.setItems(reclamations);
                    // Définir les éléments dans la table archivée
                    table11.setItems(reclamationsArchives);
                } else {
                    // Si l'entrée n'est pas un nombre valide
                    afficherAlerte("Recherche", "Veuillez saisir un identifiant existant");
                    // Recharge la liste des réclamations non archivées
                    showReclamations();
                    // Recharge la liste des réclamations archivées
                    showReclamationsArchivées();
                }
            } catch (SQLException e) {
                afficherAlerte("Recherche", "Veuillez saisir un Identifiant");
            }
            tfRechercher.setText(null);
        } else {
            showReclamations();
        }
    }



        void nombreNotification() {
        // Calcul du nombre de réclamations en attente et affichage dans le champ correspondant
        String nbNot = "SELECT COUNT(*) AS nombre_reclamations_attente FROM reclamation WHERE statut = 'Attente' AND archive = 0";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(nbNot);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    int nombreReclamationsAttente = resultSet.getInt("nombre_reclamations_attente");
                    nbNotif.setText(String.valueOf(nombreReclamationsAttente));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void nombreMessageArchive() {
        // Calcul du nombre de réclamations archivées
        String nbNot = "SELECT COUNT(*) AS nombre_reclamations_attente FROM reclamation WHERE archive = 1";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(nbNot);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    Integer nombreReclamationsArchive = resultSet.getInt("nombre_reclamations_attente");
                    if (nombreReclamationsArchive == null) {
                        nombreReclamationsArchive = 0;
                    }
                    nbArchives.setText(String.valueOf(nombreReclamationsArchive));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<Reclamation> getReclamation(int val) {
        // Récupération des réclamations en fonction de leur statut (0: non archivées, 1: archivées)
        ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
        String query = "SELECT * FROM reclamation WHERE archive = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            st.setInt(1, val);
            rs = st.executeQuery();
            while (rs.next()) {
                Reclamation st = new Reclamation();
                st.setId_reclamation(rs.getInt("id_reclamation"));
                st.setId_client(rs.getInt("id_client"));
                st.setDescription(rs.getString("description"));
                st.setTitre(rs.getString("titre"));
                st.setStatut(rs.getString("statut"));
                st.setType(rs.getString("type"));
                st.setDate_reclamation(rs.getDate("date_reclamation"));
                reclamations.add(st);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reclamations;
    }

    public void showReclamations() {
        // Affichage des réclamations non archivées dans la table
        ObservableList<Reclamation> list = getReclamation(0);
        table1.setItems(list);
        colIdReclamation1.setCellValueFactory(new PropertyValueFactory<>("id_reclamation"));
        colIdClient1.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        colDescription1.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTitre1.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colStatut1.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colType1.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDateReclamation1.setCellValueFactory(new PropertyValueFactory<>("date_reclamation"));
    }

    public void showReclamationsArchivées() {
        // Affichage des réclamations archivées dans la table
        ObservableList<Reclamation> list = getReclamation(1);
        table11.setItems(list);
        colIdReclamation11.setCellValueFactory(new PropertyValueFactory<>("id_reclamation"));
        colIdClient11.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        colDescription11.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTitre11.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colStatut11.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colType11.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDateReclamation11.setCellValueFactory(new PropertyValueFactory<>("date_reclamation"));
    }

    //calcul des pourcentages pours les types
    public void setProgressRec() {
        // Calcul du pourcentage de réclamations de type "Réclamation" et mise à jour de l'interface
        double value = pourcentageCalcul("Réclamation");
        if (recRaduis != null) {
            recRaduis.setProgress(value / 100);
        }
        recPersonnes.setText(String.valueOf(nbPersonnePourType("Réclamation"))+" Personnes");
    }
    public void setProgressRemer() {
        // Calcul du pourcentage de réclamations de type "Remerciement" et mise à jour de l'interface
        double value = pourcentageCalcul("Remerciement");
       if (remerRaduis != null){
           remerRaduis.setProgress(value/100);
       }
        remerpersonnes.setText(String.valueOf(nbPersonnePourType("Remerciement"))+" Personnes");
    }
    public void setProgressInfo() {
        // Calcul du pourcentage de réclamations de type "Demande d'information" et mise à jour de l'interface
        float value = pourcentageCalcul("Demande d'information");
        if (infoRaduis != null) {
            infoRaduis.setProgress(value / 100);
        }
        infoPersonnes.setText(String.valueOf(nbPersonnePourType("Demande d'information")) + " Personnes");
    }

    public void setProgressAutres() {
        // Calcul du pourcentage de réclamations de type "Autres" et mise à jour de l'interface
        double value = pourcentageCalcul("Autres");
        if(autresRaduis != null) {
            autresRaduis.setProgress(value / 100);
        }
        autresPersonnes.setText(String.valueOf(nbPersonnePourType("Autres"))+" Personnes");
    }

    public void setProgressCollab() {
        // Calcul du pourcentage de réclamations de type "Demande de Collaboration" et mise à jour de l'interface
        double value = pourcentageCalcul("Demande de Collaboration");
        if(collabRaduis != null) {
            collabRaduis.setProgress(value / 100);
        }
        collabPersonnes.setText(String.valueOf(nbPersonnePourType("Demande de Collaboration"))+" Personnes");
    }



    float pourcentageCalcul(String text) {
        // Calcul du pourcentage de réclamations d'un certain type par rapport au total des réclamations
        String sqlTotal = "SELECT COUNT(*) AS total_reclamations FROM reclamation";
        con = MyConnection.getInstance();
        int totalReclamations = 0;
        try {
            st = con.getCnx().prepareStatement(sqlTotal);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    totalReclamations = resultSet.getInt("total_reclamations");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sqlType = "SELECT COUNT(*) AS Totaldutype FROM reclamation WHERE type=?";
        int nombreReclamationsTypeSpecifique = 0;
        try {
            st = con.getCnx().prepareStatement(sqlType);
            st.setString(1, text);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    nombreReclamationsTypeSpecifique = resultSet.getInt("Totaldutype");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        float pourcentage = (float) nombreReclamationsTypeSpecifique / totalReclamations * 100;
        return pourcentage;
    }

    int nbPersonnePourType(String text){
        // Calcul du nombre de personnes ayant émis une réclamation d'un certain type
        String sqlType = "SELECT COUNT(*) AS Totaldutype FROM reclamation WHERE type=?";
        con = MyConnection.getInstance().getInstance();
        int nombreReclamationsTypeSpecifique = 0;
        try {
            st = con.getCnx().prepareStatement(sqlType);
            st.setString(1, text);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    nombreReclamationsTypeSpecifique = resultSet.getInt("Totaldutype");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return nombreReclamationsTypeSpecifique;
    }
    //Verifie si c'est entier
    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void clear(){
        tfId_reclamation.setText(null);
        tfNom.setText(null);
        tfMail.setText(null);
        tfTitre.setText(null);
        tfType.setText(null);
        tfDescription.setText(null);
        tfReponse.setText(null);
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
    void switchForm(ActionEvent event) {
        // Changement d'affichage entre les réclamations et les archives
        if (event.getSource() == btnArchives) {
            ensembleArchives.setVisible(true);
            ensembleReclamations.setVisible(false);
            btnRetourListe.setVisible(true);
            btnArchives.setVisible(false);
            Archivebtn.setVisible(false);
            showReclamationsArchivées();
            showReclamations();
            clear();
            tfRechercher.setText(null);
            nombreNotification();
            nbArchives.setVisible(false);
        } else if (event.getSource() == btnRetourListe) {
            ensembleArchives.setVisible(false);
            ensembleReclamations.setVisible(true);
            btnRetourListe.setVisible(false);
            btnArchives.setVisible(true);
            Archivebtn.setVisible(true);
            showReclamationsArchivées(); // Ajouter ceci pour afficher les réclamations archivées
            showReclamations();
            clear();
            tfRechercher.setText(null);
            nombreNotification();
            nbArchives.setVisible(true);
        }else if(event.getSource() == btnEnvoyerMail){
            nombreNotification();
            clear();
        } else if (event.getSource() == rechercheIcon) {
            nombreNotification();
            clear();
        }
    }

    public void exporterExcel(ActionEvent event) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reclamation");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id_Réclaamtion");
            headerRow.createCell(1).setCellValue("Id_client ");
            headerRow.createCell(2).setCellValue("description");
            headerRow.createCell(3).setCellValue("titre");
            headerRow.createCell(4).setCellValue("statut");
            headerRow.createCell(5).setCellValue("type");

            //Remplissage de la liste
            ObservableList<Reclamation> list = getReclamation(0);

            // Remplir les données
            int rowNum = 1;
            for (Reclamation reclamation : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reclamation.getId_reclamation());
                row.createCell(1).setCellValue(reclamation.getId_client());
                row.createCell(2).setCellValue(reclamation.getDescription());
                row.createCell(3).setCellValue(reclamation.getTitre());
                row.createCell(4).setCellValue(reclamation.getStatut());
                row.createCell(5).setCellValue(reclamation.getType());
            }

            String fileName = "export.xlsx"; // nom du fichier
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Export Excel réussi !");
                openFile(fileName); // passer le nom du fichier à la méthode openFile
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'export Excel : " + e.getMessage());
            } finally { try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private static void openFile(String fileName) {
        try {
            File file = new File(fileName);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Impossible d'ouvrir le fichier. Veuillez le faire manuellement.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du fichier Excel.");
        }


    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}



