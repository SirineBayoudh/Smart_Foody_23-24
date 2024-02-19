package com.example.demo.Controllers;

import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GestionUserController implements Initializable {

    @FXML
    private PieChart piechart;

    @FXML
    private ScrollBar scroll;

    @FXML
    private TableView tableUser;

    @FXML
    private TableColumn<Utilisateur, Integer> col_id;

    @FXML
    private TableColumn<Utilisateur, String> col_nom;

    @FXML
    private TableColumn<Utilisateur, String> col_prenom;

    @FXML
    private TableColumn<Utilisateur, String> col_genre;

    @FXML
    private TableColumn<Utilisateur, String> col_email;

    @FXML
    private TableColumn<Utilisateur, String> col_mdp;

    @FXML
    private TableColumn<Utilisateur, Integer> col_numtel;

    @FXML
    private TableColumn<Utilisateur, String> col_role;

    @FXML
    private TableColumn<Utilisateur, Integer> col_matricule;

    @FXML
    private TableColumn<Utilisateur, String> col_attestation;
    @FXML
    private TableColumn<Utilisateur, String> col_adresse;

    @FXML
    private TableColumn<Utilisateur, String> col_objectif;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherUtilisateurs();
    }

    public ObservableList<Utilisateur> getUtilisateurs(){
        ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();
        String req="select * from utilisateur";
        Connection cnx = MyConnection.instance.getCnx();
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                Utilisateur usr = new Utilisateur();
                usr.setId_utilisateur(rs.getInt(1));
                usr.setNom(rs.getString(2));
                usr.setPrenom(rs.getString(3));
                usr.setGenre(rs.getString(4));
                usr.setEmail(rs.getString(5));
                usr.setMot_de_passe(rs.getString(6));
                usr.setNum_tel(rs.getInt(7));
                usr.setRole(rs.getString(8));
                usr.setMatricule(rs.getInt(9));
                usr.setAttestation(rs.getString(10));
                usr.setAdresse(rs.getString(11));
                usr.setObjectif(rs.getString(12));
                utilisateurs.add(usr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return utilisateurs;
    }

    public void afficherUtilisateurs(){
        ObservableList<Utilisateur> listUsers = getUtilisateurs();

        col_id.setCellValueFactory(new PropertyValueFactory<Utilisateur,Integer>("id_utilisateur"));
        col_nom.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("nom"));
        col_prenom.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("prenom"));
        col_genre.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("genre"));
        col_email.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("email"));
        col_mdp.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("mot_de_passe"));
        col_numtel.setCellValueFactory(new PropertyValueFactory<Utilisateur,Integer>("num_tel"));
        col_role.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("role"));
        col_matricule.setCellValueFactory(new PropertyValueFactory<Utilisateur,Integer>("matricule"));
        col_attestation.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("attestation"));
        col_adresse.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("adresse"));
        col_objectif.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("objectif"));

        tableUser.setItems(listUsers);
    }

    @FXML
    void ajouterConseiller(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ajouterConseiller.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int id;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        GestionUserController.id = id;
    }

    @FXML
    private void modifierConseiller() {
        // Récupérer l'utilisateur sélectionné dans le TableView
        Utilisateur selectedUser = (Utilisateur) tableUser.getSelectionModel().getSelectedItem();
        id = selectedUser.getId_utilisateur();

        if (selectedUser != null) {
            // Charger le formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/modifierConseiller.fxml"));
            Parent root;
            try {
                root = loader.load();
                ModifierConseillerController controller = loader.getController();

                // Remplir les champs du formulaire avec les données de l'utilisateur sélectionné
                controller.initData(selectedUser);

                // Afficher le formulaire d'ajout
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                afficherUtilisateurs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Veuillez sélectionner un utilisateur à modifier.");
        }
    }

    @FXML
    void getData(MouseEvent event) {
        Utilisateur user =(Utilisateur) tableUser.getSelectionModel().getSelectedItem();
        id = user.getId_utilisateur();
    }

}

