package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import com.microsoft.schemas.vml.CTGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NavbarreCotroller implements Initializable {

    public Button BtnConseil;
    public ImageView panier;
    public Button produit;
    @FXML
    public Pane badge;

    @FXML
    private BorderPane centerPane;

    @FXML
    public Text nbrCommande;

    Connection cnx;
    String idClient="14";

    public NavbarreCotroller() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        badge.setVisible(false);
    }

    @FXML
    private void handleClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Label) {
            Label label = (Label) source;
            switch (label.getId()) {
                case "labelAccueil":
                    // Chargez la vue d'accueil, si vous en avez une
                    break;
                case "labelProduits":
                    loadPage("/com/example/pitest/fxml/produit.fxml");
                    break;
                // Ajoutez d'autres cas pour les autres labels
            }
        } else if (source instanceof ImageView) {
            ImageView imageView = (ImageView) source;
            // Gérez les clics sur les icônes ici, similaire aux labels
        }
    }

    @FXML
    private void loadProduit() {
        produit.setStyle("-fx-background-color: #56AB2F");


        loadPage("/com/example/demo/produit.fxml");
        clear();
    }

    @FXML
    private void loadPanier() {
        panier.getStyleClass().add("highlighted-basket");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/panier.fxml"));
            try {
                Parent root = loader.load();
                PanierController panierController = loader.getController();
                centerPane.setCenter(root);

                int nbrCommandes = panierController.obtenirNombreProduitsDansLePanier();
                if (nbrCommandes > 0) {
                    nbrCommande.setText(String.valueOf(nbrCommandes));
                    badge.setVisible(true);
                } else {
                    badge.setVisible(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void clear(){
        panier.setStyle("-fx-background-color:#ffffff");
        produit.setStyle("-fx-background-color:#ffffff");

    }

    private void loadPage(String page) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource(page));
            if (centerPane != null) {
                centerPane.setCenter(root);

                String checkIfExistsQuery = "SELECT count(*) FROM ligne_commande lc JOIN panier p on lc.id_panier=p.id_panier WHERE id_client = ?";
                try (PreparedStatement pstCheck = cnx.prepareStatement(checkIfExistsQuery)) {
                    pstCheck.setInt(1, Integer.parseInt(idClient));
                    ResultSet rs = pstCheck.executeQuery();
                    if (rs.next()) {


                        int nbrCommandes = rs.getInt(1);
                        if(nbrCommandes>0){
                            nbrCommande.setText(String.valueOf(nbrCommandes));
                            badge.setVisible(true);
                        }else {
                            badge.setVisible(false);
                        }
                    }
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("centerPane is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}

