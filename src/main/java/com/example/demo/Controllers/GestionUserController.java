package com.example.demo.Controllers;

import com.example.demo.Models.Role;
import com.example.demo.Models.Utilisateur;
import com.example.demo.Tools.MyConnection;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Pagination;

public class GestionUserController implements Initializable {

    @FXML
    public TableView tableUser;

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
    private TableColumn<Utilisateur, String> col_matricule;

    @FXML
    private TableColumn<Utilisateur, String> col_attestation;
    @FXML
    private TableColumn<Utilisateur, String> col_adresse;

    @FXML
    private TableColumn<Utilisateur, String> col_objectif;

    @FXML
    private TableColumn<Utilisateur, Double> col_taille;

    @FXML
    private TableColumn<Utilisateur, Double> col_poids;

    @FXML
    private Button btn_ajout;

    @FXML
    private Button btn_modif;

    @FXML
    private Button btn_supprimer;

    @FXML
    private TextField tfrecherche;

    @FXML
    private ComboBox<String> choixRole;

    private String[] role = {"Tous","Client","Conseiller"};

    private String roleChoisi;

    @FXML
    private PieChart genderPieChart;

    private ObservableList<Utilisateur> allUsers = FXCollections.observableArrayList();

    @FXML
    private Label totalClients;

    @FXML
    private Label totalConseillers;

    @FXML
    private Pagination pagination;

    private static final int ITEMS_PER_PAGE= 8;

    static Connection cnx = MyConnection.instance.getCnx();

    private static int id;

    public String matriculeC;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        GestionUserController.id = id;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherUtilisateurs();

        tfrecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercher(newValue);
        });

        choixRole.getItems().addAll(role);
        choixRole.setOnAction(event -> {
            String roleChoisi = choixRole.getValue();
            filtrer(roleChoisi);
        });

        collectGenderData();
        totalClient();

        Pagination();
    }

    private void Pagination() {
        int pageCount = (getUtilisateurs().size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        pagination.setPageCount(pageCount);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            int startIndex = newIndex.intValue() * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, getUtilisateurs().size());
            ObservableList<Utilisateur> pageData = FXCollections.observableArrayList(
                    getUtilisateurs().subList(startIndex, endIndex));
            tableUser.setItems(pageData);
        });
        pagination.setCurrentPageIndex(0);
    }

    private void collectGenderData() {
        ObservableList<Utilisateur> listUsers = getUtilisateurs();
        int maleCount = 0;
        int femaleCount = 0;

        for (Utilisateur user : listUsers) {
            if (user.getGenre().equalsIgnoreCase("Homme")) {
                maleCount++;
            } else if (user.getGenre().equalsIgnoreCase("Femme")) {
                femaleCount++;
            }
        }

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Homme", maleCount),
                        new PieChart.Data("Femme", femaleCount)
                );

        genderPieChart.setData(pieChartData);


        PieChart.Data hommeData = pieChartData.get(0);
        PieChart.Data femmeData = pieChartData.get(1);

        Node hommeNode = hommeData.getNode();
        Node femmeNode = femmeData.getNode();

        RotateTransition rotateTransitionHomme = new RotateTransition(Duration.seconds(1), hommeNode);
        rotateTransitionHomme.setByAngle(360);
        rotateTransitionHomme.setCycleCount(1);

        RotateTransition rotateTransitionFemme = new RotateTransition(Duration.seconds(1), femmeNode);
        rotateTransitionFemme.setByAngle(360);
        rotateTransitionFemme.setCycleCount(1);

        rotateTransitionHomme.play();
        rotateTransitionFemme.play();

        for (final PieChart.Data data : pieChartData) {
            Node node = data.getNode();
            Tooltip tooltip = new Tooltip(String.format("%.1f%%", (data.getPieValue() / (maleCount + femaleCount)) * 100));
            PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
            pause.setOnFinished(e -> {
                tooltip.show(node, node.localToScreen(0, 0).getX() + node.getBoundsInLocal().getWidth() / 2, node.localToScreen(0, 0).getY() + node.getBoundsInLocal().getHeight() / 2);
            });

            node.setOnMouseEntered(event -> {
                pause.playFromStart();
            });

            node.setOnMouseExited(event -> {
                tooltip.hide();
                pause.stop();
            });
        }
    }


    public void totalClient() {
        ObservableList<Utilisateur> listUsers = getUtilisateurs();
        int nbClients = 0;
        int nbConseillers = 0;

        for (Utilisateur user : listUsers) {
            if (user.getRole().equalsIgnoreCase(Role.Client.toString())) {
                nbClients++;
            } else if (user.getRole().equals(Role.Conseiller.toString())) {
                nbConseillers++;
            }
        }

        totalClients.setText(String.valueOf(nbClients));
        totalConseillers.setText(String.valueOf(nbConseillers));

    }

    public ObservableList<Utilisateur> getUtilisateurs(){
        ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();
        String req="select * from utilisateur";
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
                usr.setMatricule(rs.getString(9));
                usr.setAttestation(rs.getString(10));
                usr.setAdresse(rs.getString(11));
                usr.setObjectif(rs.getString(12));
                usr.setTaille(rs.getDouble(14));
                usr.setPoids(rs.getDouble(15));
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
        col_matricule.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("matricule"));
        col_attestation.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("attestation"));
        col_adresse.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("adresse"));
        col_objectif.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("objectif"));
        col_taille.setCellValueFactory(new PropertyValueFactory<Utilisateur,Double>("taille"));
        col_poids.setCellValueFactory(new PropertyValueFactory<Utilisateur,Double>("poids"));

        tableUser.setItems(listUsers);

        collectGenderData();
        totalClient();
    }

    private void rechercher(String searchText) {
        ObservableList<Utilisateur> listUsers = getUtilisateurs();
        if (allUsers.isEmpty()) {
            allUsers.addAll(listUsers); // remplir tous les users if vide
        }

        ObservableList<Utilisateur> ListFiltre = FXCollections.observableArrayList();

        if (searchText.isEmpty()) {
            tableUser.setItems(allUsers);
        } else {
            for (Utilisateur user : allUsers) {
                String nom = user.getNom().toLowerCase();
                searchText = searchText.toLowerCase().trim();

                if (nom.contains(searchText)) {
                    ListFiltre.add(user);
                }
            }
            tableUser.setItems(ListFiltre);
        }
    }

    private void filtrer(String roleChoisi) {
        ObservableList<Utilisateur> listUsers = getUtilisateurs();
        if (allUsers.isEmpty()) {
            allUsers.addAll(listUsers);
        }

        ObservableList<Utilisateur> filteredUsers = FXCollections.observableArrayList();

        for (Utilisateur user : allUsers) {
            if (roleChoisi.equalsIgnoreCase("Tous") || user.getRole().equalsIgnoreCase(roleChoisi)) {
                filteredUsers.add(user);
            }
        }
        tableUser.setItems(filteredUsers);
    }

    @FXML
    void ajouterConseiller(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ajouterConseiller.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            AjouterConseillerController aj = loader.getController();
            aj.setGestionUserController(this);
            stage.setOnHidden((WindowEvent windowEvent) -> {
                afficherUtilisateurs();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void modifierConseiller() {
        // Récupérer l'utilisateur sélectionné dans le TableView
        Utilisateur selectedUser = (Utilisateur) tableUser.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            id = selectedUser.getId_utilisateur();
            if (selectedUser.getRole().equals(Role.Client.toString())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Vous ne pouvez pas modifier un client");
                alert.showAndWait();
            }
            else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/modifierConseiller.fxml"));
                Parent root;
                try {
                    root = loader.load();
                    ModifierConseillerController mc = loader.getController();

                    // Remplir les champs du formulaire avec les données de l'utilisateur sélectionné
                    mc.initData(selectedUser);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    mc.setGestionUserController(this);
                    stage.setOnHidden((WindowEvent windowEvent) -> {
                            afficherUtilisateurs();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un conseiller");
            alert.showAndWait();
        }
    }

    @FXML
    void getData(MouseEvent event) {
        Utilisateur user =(Utilisateur) tableUser.getSelectionModel().getSelectedItem();
        if (user != null) {
            id = user.getId_utilisateur();
        }
    }

    @FXML
    void supprimerConseiller(ActionEvent event) throws SQLException {
        Utilisateur selectedUser = (Utilisateur) tableUser.getSelectionModel().getSelectedItem();
        ObservableList<Utilisateur> listUsers = getUtilisateurs();

        if (selectedUser != null) {
            if (selectedUser.getRole().equals(Role.Client.toString())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Vous ne pouvez pas supprimer un client");
                alert.showAndWait();
            }
            else {
                String reqMatricule = "SELECT matricule FROM utilisateur WHERE id_utilisateur= ?";
                PreparedStatement pstMatricule = cnx.prepareStatement(reqMatricule);
                pstMatricule.setInt(1, id);
                ResultSet rsMatricule = pstMatricule.executeQuery();
                rsMatricule.next();
                matriculeC = rsMatricule.getString("matricule");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Êtes-vous sûr de vouloir modifier le conseiller ayant la matricule " + matriculeC + " ?");

                ButtonType buttonTypeOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.NO);

                alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeOui) {
                    String req = "DELETE FROM `utilisateur` WHERE `id_utilisateur`=?";
                    try {
                        PreparedStatement pst = cnx.prepareStatement(req);
                        pst.setInt(1, selectedUser.getId_utilisateur());
                        pst.executeUpdate();
                        afficherUtilisateurs();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un conseiller à supprimer.");
            alert.showAndWait();
        }
    }


}

