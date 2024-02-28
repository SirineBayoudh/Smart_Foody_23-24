/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.demo.Controllers;


import java.awt.*;
import java.io.*;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.demo.Models.LigneCommande;
import com.example.demo.Models.Produit;
import com.example.demo.Tools.MyConnection;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

//import com.google.gson.Gson;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static com.itextpdf.kernel.pdf.PdfName.Colors;
import static javafx.scene.paint.Color.*;

/**
 * FXML Controller class
 *
 * @author omarb
 */
public class DetailsCommandeController implements Initializable {

    private static final String API_KEY = "2687d76eef6bf2a7b59beecb";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;
    @FXML
    private Pane clickpane;
    @FXML
    private TextArea clientInput;

    @FXML
    private VBox productsContainer;
    @FXML
    private DatePicker dateCreationInput;

    @FXML
    private Text etatInput;

    @FXML
    private Text remiseInput;

    @FXML
    private Text totaleInputEur;

    @FXML
    private Text totaleInputTnd;
    @FXML
    private Button okbutton;

    private ServiceCommande commandeService;

    private CommandeHolder holder = CommandeHolder.getInstance();
    private Commande CurrentCommande = holder.getCommande();
    Connection cnx;

    // Constructeur
    public DetailsCommandeController() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Initializes the controller class.
     */
    public void initData(Commande quest) throws IOException {
        CurrentCommande = quest;
        String toCurrency = "EUR";

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
        totaleInputTnd.setText(CurrentCommande.getTotal_commande() + " TND");
        /** Convertiseur Devise TND -> EUR*/
        URL url = new URL(BASE_URL + "pair/TND/" + toCurrency + "/" + CurrentCommande.getTotal_commande());
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

// Convert to JSON
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

// Accessing object
        Float req_result = jsonobj.get("conversion_result").getAsFloat();

        totaleInputEur.setText(String.format("%.2f %s", req_result, toCurrency));

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

        try {
            this.afficherProduits();
            this.initData(CurrentCommande);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void afficherProduits() {
        List<LigneCommande> ligneCommandes = affichageProduitsDansCommande();
        productsContainer.getChildren().clear();

        for (LigneCommande prod : ligneCommandes) {
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Produit p = new Produit(
                                rs.getString("ref"),
                                rs.getString("marque"),
                                rs.getString("categorie"),
                                rs.getFloat("prix"),
                                rs.getString("image"),
                                rs.getString("objectif"),
                                rs.getString("critere")
                        );

                        VBox productBox = new VBox(10);
                        productBox.getStyleClass().add("product-box");
                        productsContainer.setStyle("-fx-padding:15px");

                        ImageView productImage = new ImageView(new Image(p.getImage()));
                        productImage.setFitHeight(15);
                        productImage.setFitWidth(15);

                        Rectangle clip = new Rectangle(15, 15); // Set the dimensions as needed
                        clip.setArcWidth(30); // Adjust the corner radius
                        clip.setArcHeight(30);
                        productImage.setClip(clip);
                        productImage.setEffect(new DropShadow(10, BLACK)); // Adjust the shadow parameters

                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef());
                        labelProduit.setMinHeight(10);
                        labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label
                        DecimalFormat decimalFormat = new DecimalFormat("0.##");
                        decimalFormat.setDecimalSeparatorAlwaysShown(false);

                        Label labelProduit1 = new Label(decimalFormat.format(p.getPrix() * prod.getQuantite()) + " TND");

                        labelProduit1.setMinHeight(10);
                        labelProduit1.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit1.setAlignment(Pos.BOTTOM_CENTER); // Center-align the label

                        TextField quantiteTextField = new TextField(String.valueOf(prod.getQuantite()));
                        quantiteTextField.setEditable(false);
                        quantiteTextField.setMaxWidth(100);
                        quantiteTextField.setMaxHeight(20);
                        quantiteTextField.setStyle("-fx-font-size: 10px;");
                        quantiteTextField.setAlignment(Pos.CENTER);

                        productBox.setAlignment(Pos.CENTER);
                        productBox.setPrefWidth(166);
                        HBox.setHgrow(productBox, Priority.ALWAYS);

                        productBox.getChildren().addAll(productImage, labelProduit, labelProduit1, quantiteTextField);
                        productsContainer.getChildren().add(productBox);


                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }

        }
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true); // Adjust to your needs

    }


    @FXML
    public void modifierCommande(ActionEvent event) {
        try {
            // Si vous souhaitez changer l'état de la commande dans les deux cas :
            if (CurrentCommande.getEtat().toLowerCase().equals("en cours")) {
                CurrentCommande.setEtat("livré"); // Modifier l'état de la commande à "livré"
            } else {
                CurrentCommande.setEtat("en cours"); // Modifier l'état de la commande à "en cours"
            }

            // Mise à jour de l'état dans la base de données
            String updateQuery = "UPDATE commande SET etat = ? WHERE id_commande = ?";
            try (PreparedStatement pst = cnx.prepareStatement(updateQuery)) {
                pst.setString(1, CurrentCommande.getEtat());
                pst.setInt(2, CurrentCommande.getId_commande());
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    // Mise à jour réussie
                    // Mise à jour de l'état dans l'interface graphique
                    etatInput.setText(CurrentCommande.getEtat());

                    // Afficher un message de succès
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Modification de l'état de la commande");
                    alert.setHeaderText(null);
                    alert.setContentText("L'état de la commande a été modifié avec succès !");
                    alert.showAndWait();
                } else {
                    // Aucune ligne mise à jour
                    throw new SQLException("Aucune ligne mise à jour dans la base de données.");
                }
            }
        } catch (SQLException e) {
            // Afficher un message d'erreur en cas d'échec
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la modification de l'état de la commande : " + e.getMessage());
            alert.showAndWait();
        }
    }


    private List<LigneCommande> affichageProduitsDansCommande() {
        List<LigneCommande> produits = new ArrayList<>();
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc  ON p.ref = lc.ref_produit and lc.id_commande=? ";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setInt(1, CurrentCommande.getId_commande());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    produits.add(new LigneCommande(
                            rs.getInt("id_lc"),
                            rs.getInt("id_panier"),
                            rs.getInt("quantite"),
                            rs.getString("ref_produit"),
                            rs.getInt("id_commande")

                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }
        return produits;
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

    public void exportPDF() {
        Commande commande = CurrentCommande; // Utilisez l'instance CurrentCommande chargée dans l'interface
        if (commande != null) {
            String path = "DetailCommande_" + commande.getId_commande() + ".pdf";
            try {
                PdfWriter pdfWriter = new PdfWriter(path);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                pdfDocument.setDefaultPageSize(PageSize.A4);
                Document document = new Document(pdfDocument);

                // Ajouter le logo et le titre au même niveau
                float[] columnWidths = {2, 8};
                Table headerTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

                String imagePath = "C:/Users/INFOTEC/Desktop/Smart_Foody_23-24/src/main/resources/com/example/demo/Images/trans_logo.png";
                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(ImageDataFactory.create(imagePath)).setAutoScale(true);
                headerTable.addCell(new com.itextpdf.layout.element.Cell().add(logo).setBorder(com.itextpdf.layout.border.Border.NO_BORDER));

                // Titre avec l'ID de la commande
                Paragraph title = new Paragraph("Détails de la Commande (ID: " + commande.getId_commande() + ")")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(20)
                        .setBold()
                        .setFontColor(new DeviceRgb(0, 128, 0)); // Vert
                headerTable.addCell(new com.itextpdf.layout.element.Cell().add(title).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

                document.add(headerTable);

                // Ajouter une séparation visuelle après le titre
                document.add(new Paragraph("\n"));

                // Ajouter la liste des produits dans cette commande
                List<LigneCommande> ligneCommandes = affichageProduitsDansCommande();
                if (!ligneCommandes.isEmpty()) {
                    // Titre de la liste des produits
                    Paragraph productListTitle = new Paragraph("Liste des produits dans cette commande :")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(14)
                            .setBold()
                            .setFontColor(new DeviceRgb(0, 128, 0)); // Vert
                    document.add(productListTitle);

                    // Création du tableau pour les produits
                    Table productTable = new Table(new float[]{4, 3, 3}).useAllAvailableWidth();
                    productTable.setBackgroundColor(new DeviceRgb(204, 255, 204)); // Vert clair

                    // Ajouter les titres des colonnes
                    productTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Produit")).setBackgroundColor(new DeviceRgb(0, 128, 0))); // Vert foncé
                    productTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Quantité")).setBackgroundColor(new DeviceRgb(0, 128, 0))); // Vert foncé
                    productTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Prix")).setBackgroundColor(new DeviceRgb(0, 128, 0))); // Vert foncé

                    // Ajouter les données des produits
                    for (LigneCommande ligneCommande : ligneCommandes) {
                        String productName = getProductNameByRef(ligneCommande.getRefProduit());
                        float productPrice = getProductPriceByRef(ligneCommande.getRefProduit());
                        productTable.addCell(new Cell().add(new Paragraph(productName)));
                        productTable.addCell(new Cell().add(new Paragraph(String.valueOf(ligneCommande.getQuantite()))));
                        productTable.addCell(new Cell().add(new Paragraph(String.valueOf(productPrice))));
                    }

                    document.add(productTable);

                    // Ajouter une séparation visuelle après la liste des produits
                    document.add(new Paragraph("\n"));
                }

                // Création du tableau pour les autres détails de la commande
                Table table = new Table(new float[]{4, 6});
                table.useAllAvailableWidth();

                DeviceRgb greenColor = new DeviceRgb(0, 128, 0);
                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                // Ajout des titres des colonnes
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Attribut")).setFontColor(greenColor).setBold());
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Valeur")).setFontColor(greenColor).setBold());

                // Ajout des données de la commande
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("ID Commande")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(commande.getId_commande()))));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Date de création")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(commande.getDate_commande()))));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Client")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(commandeService.usernameById(commande.getId_client()))));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Etat")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(commande.getEtat())));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Remise")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(decimalFormat.format(commande.getRemise() * 100) + "%")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Total")));
                table.addCell(new Cell().add(new Paragraph(decimalFormat.format(commande.getTotal_commande()) + "$")));

                document.add(table);

                document.close();
                Desktop.getDesktop().open(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'exportation");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de l'exportation du PDF.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune commande sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une commande pour exporter.");
            alert.showAndWait();
        }
    }



    private String getProductNameByRef(String refProduit) {
        String productName = null;
        String requete = "SELECT marque FROM produit WHERE ref = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, refProduit);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    productName = rs.getString("marque");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom du produit : " + e.getMessage());
        }
        return productName;
    }

    // Méthode pour obtenir le prix du produit à partir de sa référence
    private float getProductPriceByRef(String refProduit) {
        float productPrice = 0;
        String requete = "SELECT prix FROM produit WHERE ref = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, refProduit);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    productPrice = rs.getFloat("prix");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du prix du produit : " + e.getMessage());
        }
        return productPrice;
    }


}
