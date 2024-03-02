package com.example.demo.Controllers;

import com.example.demo.Models.Avis;
import com.example.demo.Models.AvisData;
import com.example.demo.Models.Reclamation;
import com.example.demo.Tools.MyConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.File;

public class ajoutAvisController  {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label MoyenneText;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnSu;

    @FXML
    private Button btnAvis;

    @FXML
    private Pane intDonnerAvis;

    @FXML
    private Label nbAvis;

    @FXML
    private Label nbPersonne1;

    @FXML
    private Label nbPersonne2;

    @FXML
    private Label nbPersonne3;

    @FXML
    private Label nbPersonne4;

    @FXML
    private Label nbPersonne5;

    @FXML
    private ProgressBar progress1;

    @FXML
    private ProgressBar progress2;

    @FXML
    private ProgressBar progress3;

    @FXML
    private ProgressBar progress4;

    @FXML
    private ProgressBar progress5;

    @FXML
    private Spinner<?> quantité;

    @FXML
    private ImageView starRep1;

    @FXML
    private ImageView starRep11;

    @FXML
    private ImageView starRep2;

    @FXML
    private ImageView starRep21;

    @FXML
    private ImageView starRep3;

    @FXML
    private ImageView starRep31;

    @FXML
    private ImageView starRep4;

    @FXML
    private ImageView starRep41;

    @FXML
    private ImageView starRep5;

    @FXML
    private ImageView starRep51;

    @FXML
    private ImageView starVal1;

    @FXML
    private ImageView starVal11;

    @FXML
    private ImageView starVal2;

    @FXML
    private ImageView starVal21;

    @FXML
    private ImageView starVal3;

    @FXML
    private ImageView starVal31;

    @FXML
    private ImageView starVal4;

    @FXML
    private ImageView starVal41;

    @FXML
    private ImageView starVal5;

    @FXML
    private ImageView starVal51;

    @FXML
    private Label tfCategorie;

    @FXML
    private TextArea tfCom;

    @FXML
    private Label tfNom;

    @FXML
    private Label tfObjectif;

    @FXML
    private Label tfPrix;

    @FXML
    private ImageView tfImage;

    @FXML
    private ListView<AvisData> listAvis;


    @FXML
    private Pane paneNote;

    @FXML
    private Button btnApk;

    @FXML
    private TextField tfId;


    int id = 19;
    int ref = 120;

    // Connexion à la base de données
    private MyConnection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    int note;

    @FXML
    void AjouterPanier(ActionEvent event) {

    }

    @FXML
    void DonnerAvis(ActionEvent event) {
        intDonnerAvis.setVisible(true);
        btnApk.setVisible(false);
        btnSu.setVisible(true);


        // Appelez la fonction pour vérifier si un commentaire existe déjà
        boolean commentaireExiste = commentaireExisteDeja(ref, id);
        paneNote.setVisible(!commentaireExiste); // Si un commentaire existe déjà, paneNote ne doit pas être visible


        updateValues();

        // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
        List<AvisData> avisList = getAvis(ref);
        ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
        listAvis.setItems(observableList);

        // Ajoutez un écouteur d'événements sur le champ tfDesc
        tfCom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Vérifiez si le texte a été saisi dans tfCom
                if (!newValue.isEmpty()) {
                    // Activer le bouton Envoyer
                    btnSu.setDisable(false);
                    btnApk.setDisable(false);
                } else {
                    // Désactiver le bouton Envoyer si aucun texte n'est saisi
                    btnSu.setDisable(true);
                    btnApk.setDisable(true);
                }
            }
        });
    }
    @FXML
    void retourPage(MouseEvent event) {
        intDonnerAvis.setVisible(false);
        updateValues();

        // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
        List<AvisData> avisList = getAvis(ref);
        ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
        listAvis.setItems(observableList);
    }

    @FXML
    void ModifierDansAvis(ActionEvent event) {
        // Mettre à jour l'avis dans la base de données avec les nouvelles valeurs
        String updateQuery = "UPDATE avis SET nb_etoiles = ?, commentaire = ? WHERE id_avis = ?";
        try {
            PreparedStatement updateStatement = con.getCnx().prepareStatement(updateQuery);
            updateStatement.setInt(1, note);
            updateStatement.setString(2, tfCom.getText());
            updateStatement.setInt(3, Integer.parseInt(tfId.getText()));
            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected > 0) {
                afficherConfirmation("Succès", null, "L'avis a été modifié avec succès.");
                // Mettre à jour la liste des avis après la modification
                List<AvisData> avisList = getAvis(ref);
                ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
                listAvis.setItems(observableList);
            } else {
                afficherAlerte("Échec de la modification", "Impossible de modifier l'avis.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            afficherAlerte("Erreur de base de données", "Une erreur s'est produite lors de la modification de l'avis.");
        }

        // Réinitialiser les champs et les étoiles après la modification
        clear();
        intDonnerAvis.setVisible(false);
        btnApk.setVisible(false);
        updateValues();

        // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
        List<AvisData> avisList = getAvis(ref);
        ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
        listAvis.setItems(observableList);
    }

    public void switchStar(){
        // Initialiser les événements de clic sur les étoiles
        starVal11.setOnMouseClicked(event -> {
            note = 1;
            updateStars(note);
        });
        starVal21.setOnMouseClicked(event -> {
            note = 2;
            updateStars(note);
        });
        starVal31.setOnMouseClicked(event -> {
            note = 3;
            updateStars(note);
        });
        starVal41.setOnMouseClicked(event -> {
            note = 4;
            updateStars(note);
        });
        starVal51.setOnMouseClicked(event -> {
            note = 5;
            updateStars(note);
        });

    }



    @FXML
    private void initialize() {

        //Initialisation des textes
          MoyenneText.setText(String.valueOf(moyenneAvis(ref)));
          nbAvis.setText("( " + (NombreAvisPersonne(ref))+ " Personnes ont donné leurs avis )" );
          nbPersonne1.setText("( " + (nbAvisEtoile(ref,1))+ " )");
          nbPersonne2.setText("( " + (nbAvisEtoile(ref,2))+ " )");
          nbPersonne3.setText("( " + (nbAvisEtoile(ref,3))+ " )");
          nbPersonne4.setText("( " + (nbAvisEtoile(ref,4))+ " )");
          nbPersonne5.setText("( " + (nbAvisEtoile(ref,5))+ " )");
          recupeInfo(ref);

          //notes et mise à jour des stars
            switchStar();

          //Initialisation des progress

            progress1.setProgress(moyenneParEtoile(ref,1));
            progress2.setProgress(moyenneParEtoile(ref,2));
            progress3.setProgress(moyenneParEtoile(ref,3));
            progress4.setProgress(moyenneParEtoile(ref,4));
            progress5.setProgress(moyenneParEtoile(ref,5));



        // Affichage des avis dans la liste
        List<AvisData> avisList = getAvis(ref); // Récupération des avis depuis la base de données
        ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
        listAvis.setCellFactory(param -> new ListCell<AvisData>() {
            @Override
            protected void updateItem(AvisData avisData, boolean empty) {
                super.updateItem(avisData, empty);

                if (empty || avisData == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(avisData.getNom() + " & " + avisData.getPrenom() + "\t         " +
                            " Notes : " + avisData.getNbEtoiles() + " étoiles\n" +
                            " \n   " + avisData.getDateAvis() + " \t " +
                            "              Commentaire : " + avisData.getCommentaire());


                    // Vérifier si l'identifiant du client connecté est égal à celui de l'avis
                    if (avisData.getId_client() == id) {
                        // Créer un bouton de modification
                        Button modifierButton = new Button("Modifier");
                        modifierButton.getStyleClass().add("modifier-button"); // Ajouter une classe CSS pour la couleur
                        modifierButton.setOnAction(event -> {
                            // Charger les informations de l'avis sélectionné dans les champs correspondants
                            tfCom.setText(avisData.getCommentaire());
                            note = avisData.getNbEtoiles();
                            tfId.setText(String.valueOf(avisData.getId_avis()));
                            updateStars(note);

                            // Afficher le panneau pour donner un avis
                            intDonnerAvis.setVisible(true);
                            btnApk.setVisible(true);
                            btnSu.setVisible(false);
                            updateValues();

                            // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
                            List<AvisData> avisList = getAvis(ref);
                            ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
                            listAvis.setItems(observableList);
                        });

                        // Créer un bouton de suppression
                        Button supprimerButton = new Button("Supprimer");
                        supprimerButton.getStyleClass().add("supprimer-button"); // Ajouter une classe CSS pour la couleur
                        supprimerButton.setOnAction(event -> supprimerAvis(avisData));

                        // Créer une grille pour placer les boutons à droite
                        GridPane gridPane = new GridPane();
                        gridPane.setHgap(10); // Espacement horizontal entre les boutons

                        // Ajouter les boutons à la grille
                        gridPane.add(modifierButton, 0, 0);
                        gridPane.add(supprimerButton, 1, 0);

                        // Aligner la grille à droite
                        gridPane.setAlignment(Pos.CENTER_RIGHT);

                        // Définir la grille comme élément graphique de la cellule
                        setGraphic(gridPane);
                    } else {
                        // Si l'identifiant du client connecté n'est pas égal à celui de l'avis, afficher null pour effacer les boutons
                        // Créer un bouton de suppression
                        Button signalerButton = new Button("Signaler");
                        signalerButton.getStyleClass().add("supprimer-button"); // Ajouter une classe CSS pour la couleur
                        signalerButton.setOnAction(event -> signalerFonction(avisData));
                        setGraphic(signalerButton);
                    }
                }
            }
        });
        listAvis.setItems(observableList); // Affichage des avis dans la liste

    }

    private void signalerFonction(AvisData avisData) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous signaler ce commentaire ?");
        alert.setContentText("Veuillez confirmer votre choix.");
        ButtonType boutonArch = new ButtonType("Oui");
        ButtonType boutonAnn = new ButtonType("Non");
        alert.getButtonTypes().setAll(boutonArch, boutonAnn);
        alert.showAndWait().ifPresent(reponse -> {
            if(reponse == boutonArch){
                String req = "UPDATE avis SET signaler = signaler + 1 WHERE id_avis=?";
                con = MyConnection.getInstance();
                try {
                    st = con.getCnx().prepareStatement(req);
                    st.setInt(1,avisData.getId_avis());
                    st.executeUpdate();
                    afficherConfirmation("Succès", null, "Message reussi avec succès");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{

            }
        });
    }

    private List<AvisData> getAvis(int ref) {
        String sql = "SELECT utilisateur.nom, utilisateur.prenom, avis.id_avis, avis.id_client, avis.date_avis, avis.nb_etoiles, avis.commentaire " +
                "FROM avis " +
                "JOIN utilisateur ON avis.id_client = utilisateur.id_utilisateur " +
                "WHERE ref_produit = ? " +
                "ORDER BY avis.date_avis";
        List<AvisData> avisList = new ArrayList<>();

        try {
            st = con.getCnx().prepareStatement(sql);
            st.setInt(1, ref);
            ResultSet resultSet = st.executeQuery();

            while (resultSet.next()) {
                AvisData avisData = new AvisData();
                avisData.setNom(resultSet.getString("nom"));
                avisData.setPrenom(resultSet.getString("prenom"));
                avisData.setId_avis(resultSet.getInt("id_avis"));
                avisData.setId_client(resultSet.getInt("id_client"));
                avisData.setDateAvis(resultSet.getDate("date_avis"));
                avisData.setNbEtoiles(resultSet.getInt("nb_etoiles"));
                avisData.setCommentaire(resultSet.getString("commentaire"));
                avisList.add(avisData);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return avisList;
    }


    private void supprimerAvis(AvisData avisData) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet avis ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM avis WHERE id_avis = ?";
            try {
                PreparedStatement preparedStatement = con.getCnx().prepareStatement(sql);
                preparedStatement.setInt(1, avisData.getId_avis());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    afficherConfirmation("Succès", null, "L'avis a été supprimé avec succès.");
                } else {
                    afficherAlerte("Échec de la suppression", "Impossible de supprimer l'avis.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                afficherAlerte("Erreur de base de données", "Une erreur s'est produite lors de la suppression de l'avis.");
            }
            updateValues();

            // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
            List<AvisData> avisList = getAvis(ref);
            ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
            listAvis.setItems(observableList);
        }
    }




    private void updateStars(int note) {
        // Mettre à jour l'affichage des étoiles en fonction de la note passée en paramètre
        switch (note) {
            case 0:
                // Réinitialiser toutes les étoiles
                starRep11.setVisible(false);
                starRep21.setVisible(false);
                starRep31.setVisible(false);
                starRep41.setVisible(false);
                starRep51.setVisible(false);
                break;
            case 1:
                starRep11.setVisible(true);
                starRep21.setVisible(false);
                starRep31.setVisible(false);
                starRep41.setVisible(false);
                starRep51.setVisible(false);
                break;
            case 2:
                starRep11.setVisible(true);
                starRep21.setVisible(true);
                starRep31.setVisible(false);
                starRep41.setVisible(false);
                starRep51.setVisible(false);
                break;
            case 3:
                starRep11.setVisible(true);
                starRep21.setVisible(true);
                starRep31.setVisible(true);
                starRep41.setVisible(false);
                starRep51.setVisible(false);
                break;
            case 4:
                starRep11.setVisible(true);
                starRep21.setVisible(true);
                starRep31.setVisible(true);
                starRep41.setVisible(true);
                starRep51.setVisible(false);
                break;
            case 5:
                starRep11.setVisible(true);
                starRep21.setVisible(true);
                starRep31.setVisible(true);
                starRep41.setVisible(true);
                starRep51.setVisible(true);
                break;
        }
    }
    @FXML
    private void EnvoyerAvis() {
        if (tfCom.getText().isEmpty()) {
            // Afficher un message d'alerte si l'un des champs est vide
            afficherAlerte("Champs manquants", "Veuillez remplir tous les champs avant d'envoyer le courriel.");
        } else {
            String selectQuery = "SELECT COUNT(*) FROM avis WHERE ref_produit = ? AND id_client = ?";
            String insererAvecNote = "INSERT INTO avis (ref_produit, id_client, nb_etoiles, commentaire) VALUES (?, ?, ?, ?)";

            try {
                // Vérifier si le commentaire existe déjà pour cette référence et cet ID client
                PreparedStatement checkStatement = con.getCnx().prepareStatement(selectQuery);
                checkStatement.setInt(1, ref);
                checkStatement.setInt(2, id);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int commentaireExiste = resultSet.getInt(1);
                checkStatement.close();

                if (commentaireExiste > 0) {
                        int nb = 0;
                    // Insérer le nouveau commentaire sans la note dans la base de données
                    PreparedStatement insertStatement = con.getCnx().prepareStatement(insererAvecNote);
                    insertStatement.setInt(1, ref);
                    insertStatement.setInt(2, id);
                    insertStatement.setInt(3, nb);
                    insertStatement.setString(4, tfCom.getText());
                    insertStatement.executeUpdate();
                    insertStatement.close();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Avis ajouté avec succès!", ButtonType.OK);
                    alert.show();
                    clear();
                    updateValues();
                } else {
                    // Sinon, insérer le nouveau commentaire avec la note dans la base de données
                    PreparedStatement insertStatement = con.getCnx().prepareStatement(insererAvecNote);
                    insertStatement.setInt(1, ref);
                    insertStatement.setInt(2, id);
                    insertStatement.setInt(3, note); // Assurez-vous que note a une valeur appropriée avant cette étape
                    insertStatement.setString(4, tfCom.getText());
                    insertStatement.executeUpdate();
                    insertStatement.close();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Avis ajouté avec succès!", ButtonType.OK);
                    alert.show();
                    clear();
                    updateValues();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            // Mettre à jour la liste des avis après l'ajout d'un nouvel avis
            List<AvisData> avisList = getAvis(ref);
            ObservableList<AvisData> observableList = FXCollections.observableArrayList(avisList);
            listAvis.setItems(observableList);
            btnSu.setDisable(true);
        }
        intDonnerAvis.setVisible(false);
    }

    @FXML
    private void Annuler() {
        clear();
        updateValues();
    }

    public int NombreAvisPersonne(int ref){
        // Calcul le nombre total de personne ayant donner leurs avis pour un produit
        String sqlTotal = "SELECT COUNT(*) AS total_avis FROM avis WHERE ref_produit=? AND nb_etoiles != 0 ";
        con = MyConnection.getInstance();
        int totalAvis = 0;
        int sommeNotes = 0;
        try {
            st = con.getCnx().prepareStatement(sqlTotal);
            st.setInt(1, ref);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    totalAvis = resultSet.getInt("total_avis");
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  totalAvis;
    }

    public float moyenneParEtoile(int ref, int nb){
        float moyenneT = NombreAvisPersonne(ref);
        int   ParEtoile= nbAvisEtoile(ref,nb);

        return ParEtoile/moyenneT;
    }

    public float moyenneAvis(int ref) {
        // Calcul de la moyenne des avis pour un produit
        String sqlTotal = "SELECT COUNT(*) AS total_avis FROM avis WHERE ref_produit=? AND nb_etoiles != 0 ";
        con = MyConnection.getInstance();
        int totalAvis = 0;
        int sommeNotes = 0;
        try {
            st = con.getCnx().prepareStatement(sqlTotal);
            st.setInt(1, ref);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    totalAvis = resultSet.getInt("total_avis");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (totalAvis == 0) {
            return 0; // Éviter la division par zéro
        }

        for (int i = 1; i <= 5; i++) {
            sommeNotes += i * nbAvisEtoile(ref, i);
        }

        float moyenne = (float) sommeNotes / totalAvis;
           //Affichage des étoiles par rapport à la valeur de la moyenne
         if(moyenne == 0){

         }else if(moyenne >=1 && moyenne <2 ){
             starRep1.setVisible(true);
         }else if(moyenne >=2 && moyenne <3 ){
             starRep1.setVisible(true);
             starRep2.setVisible(true);
         }else if(moyenne >=3 && moyenne <4 ){
             starRep1.setVisible(true);
             starRep2.setVisible(true);
             starRep3.setVisible(true);
         }else if(moyenne >=4 && moyenne <5 ){
             starRep1.setVisible(true);
             starRep2.setVisible(true);
             starRep3.setVisible(true);
             starRep4.setVisible(true);
         }else if (moyenne > 4){
             starRep1.setVisible(true);
             starRep2.setVisible(true);
             starRep3.setVisible(true);
             starRep4.setVisible(true);
             starRep5.setVisible(true);
         }

        return moyenne;
    }

    private int nbAvisEtoile(int ref, int nbE) {
        // Calcul le nombre de personnes ayant donné cette note
        String sqlNb = "SELECT COUNT(*) AS Totalavis FROM avis WHERE ref_produit=? AND nb_etoiles=?";
        con = MyConnection.getInstance();
        int nombre = 0;
        try {
            st = con.getCnx().prepareStatement(sqlNb);
            st.setInt(1, ref);
            st.setInt(2, nbE);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    nombre = resultSet.getInt("Totalavis");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return nombre;
    }

    // Récupérer les informations sur un produit
    public void recupeInfo(int ref){
        String req = "SELECT marque, categorie, prix, image, objectif FROM produit WHERE ref=?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(req);
            st.setInt(1, ref);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String marque = rs.getString("marque");
                String categorie = rs.getString("categorie");
                double prix = rs.getDouble("prix");
                String image = rs.getString("image");
                String objectif = rs.getString("objectif");

                // Insérer les résultats dans les champs de texte
                tfNom.setText(marque);
                tfCategorie.setText(categorie);
                tfPrix.setText( prix + " DT");
                tfObjectif.setText(objectif);
                tfImage.setImage(new Image(new File(image).toURI().toString()));
                tfImage.setVisible(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    private void updateValues() {
        // Mettre à jour les valeurs après chaque clic sur une étoile
        MoyenneText.setText(String.valueOf(moyenneAvis(ref)));
        nbAvis.setText("( " + (NombreAvisPersonne(ref))+ " )" );
        nbPersonne1.setText("( " + (nbAvisEtoile(ref,1))+ " )");
        nbPersonne2.setText("( " + (nbAvisEtoile(ref,2))+ " )");
        nbPersonne3.setText("( " + (nbAvisEtoile(ref,3))+ " )");
        nbPersonne4.setText("( " + (nbAvisEtoile(ref,4))+ " )");
        nbPersonne5.setText("( " + (nbAvisEtoile(ref,5))+ " )");

        // Mettre à jour les progress bars
        progress1.setProgress(moyenneParEtoile(ref,1));
        progress2.setProgress(moyenneParEtoile(ref,2));
        progress3.setProgress(moyenneParEtoile(ref,3));
        progress4.setProgress(moyenneParEtoile(ref,4));
        progress5.setProgress(moyenneParEtoile(ref,5));
    }

    //Mettre à null le contenu des label et textArea
    public void clear(){
        // Réinitialiser les étoiles et le commentaire
        note = 0;
        tfCom.clear();
        starRep11.setVisible(false);
        starRep21.setVisible(false);
        starRep31.setVisible(false);
        starRep41.setVisible(false);
        starRep51.setVisible(false);
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

    // Créez une fonction distincte pour vérifier si un commentaire existe déjà
    private boolean commentaireExisteDeja(int ref, int idClient) {
        String selectQuery = "SELECT COUNT(*) FROM avis WHERE ref_produit = ? AND id_client = ?";
        con = MyConnection.getInstance();
        int commentaireExiste = 0;
        try {
            PreparedStatement checkStatement = con.getCnx().prepareStatement(selectQuery);
            checkStatement.setInt(1, ref);
            checkStatement.setInt(2, idClient);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            commentaireExiste = resultSet.getInt(1);
            checkStatement.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return commentaireExiste > 0;
    }

}





/*
public class ajoutAvisController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnSu;

    @FXML
    private TextArea tfCom;

    @FXML
    private TextField tfNote;
    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    int ref = 120;


    @FXML
    void initialize() {

    }

    @FXML
    void donnerAvis(ActionEvent event) {
        con = MyConnection.getInstance();
        String inserer = "INSERT INTO avis (ref_produit, nb_etoiles, commentaire) VALUES (?, ?, ?)";
        try {
            st = con.getCnx().prepareStatement(inserer);
            st.setInt(1, ref);
            st.setInt(2, Integer.parseInt(tfNote.getText()));
            st.setString(3, tfCom.getText()); // Utiliser setString pour un champ de texte
            st.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Avis ajouté avec succès!", ButtonType.OK);
            alert.show();
            clear();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    void clear(){
        tfNote.setText(null);
        tfCom.setText(null);
    }

}


 */