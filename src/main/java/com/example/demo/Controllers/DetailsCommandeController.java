/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.demo.Controllers;


import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.demo.Models.Commande;
import com.example.demo.Models.CommandeHolder;
import com.example.demo.Tools.ServiceCommande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author omarb
 */
public class  DetailsCommandeController implements Initializable {

    @FXML
    private Pane clickpane;
    @FXML
    private TextArea clientInput ;

    @FXML
    private DatePicker    dateCreationInput;

    @FXML
    private Text etatInput;

    @FXML
    private Text remiseInput;

    @FXML
    private Text totaleInout;
    @FXML
    private Button okbutton;

    private ServiceCommande commandeService;

    private CommandeHolder holder = CommandeHolder.getInstance();
    private Commande CurrentCommande = holder.getCommande();

    /**
     * Initializes the controller class.
     */
    public void initData(Commande quest) {
        CurrentCommande = quest;
        holder.setCommande(CurrentCommande);
        commandeService = new ServiceCommande();
        LocalDate createdConverted = LocalDate.parse(CurrentCommande.getDate_commande().toString());
        dateCreationInput.setValue(createdConverted);
        clientInput.setText(String.valueOf(commandeService.usernameById(CurrentCommande.getId_client())));

        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        String remiseText = decimalFormat.format(CurrentCommande.getRemise() * 100) + " %";
        remiseInput.setText(remiseText);
        etatInput.setText(String.valueOf(CurrentCommande.getEtat()));
        totaleInout.setText(CurrentCommande.getTotal_commande()+" $");
        System.out.println(CurrentCommande.getEtat());
        if (CurrentCommande.getEtat().toLowerCase().equals("livre")) {
            okbutton.setCancelButton(true);
        } else if (CurrentCommande.getEtat().toLowerCase().equals("en cours")) {

            okbutton.setDisable(false);
            okbutton.setStyle("-fx-background-color: green;");
            okbutton.setText("Livré");
        } else {
            okbutton.setDisable(false);
            okbutton.setText("En Cours");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.initData(CurrentCommande);

    }

    @FXML
    public void modifierCommande(ActionEvent event) {

   /*     try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String createdAt = dateCreationInput.getValue().format(formatter);
            String contenus = clientInput.getText();

            if (createdAt.isEmpty() || contenus.isEmpty()) {
                Alert al = new Alert(Alert.AlertType.WARNING);
                al.setTitle("Erreur");
                al.setContentText("Veuillez remplir tous les champs !");
                al.show();
                return;
            }
            if (contenus.length() < 3 || contenus.length() > 200) {
                Alert al = new Alert(Alert.AlertType.WARNING);
                al.setTitle("Erreur de données");
                al.setContentText("Le champ 'Contenus' doit contenir entre 3 et 200 caractères !");
                al.show();
                return;
            }

            CurrentCommande.setDate_commande(createdAt);
            CurrentCommande.setId_client(contenus);

            ServiceQuestion service = new ServiceQuestion();
            service.updateQuestion(CurrentQuestion, CurrentQuestion.getId());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification de Question");
            alert.setHeaderText(null);
            alert.setContentText("La Question a été modifiée avec succès !");
            alert.showAndWait();
            Parent root = FXMLLoader.load(getClass().getResource("../../../../../../../../../../OneDrive/Bureau/Formatage/Projects/Java/WorkshopJDBC3A37/src/com/dynamics/pidev/gui/AfficherQuestion.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la modification de la Question !");
            alert.showAndWait();
        }*/
    }


    @FXML
    void switchButton(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Commande_dash.fxml"));
            Parent root = loader.load();
            // Assuming you have a Pane named detailsPane in your Commande_dash.fxml
            clickpane.getChildren().clear();
            clickpane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }


}
