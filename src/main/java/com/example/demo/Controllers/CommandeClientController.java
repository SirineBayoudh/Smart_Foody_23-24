package com.example.demo.Controllers;

import com.example.demo.Models.*;
import com.example.demo.Tools.MyConnection;
import com.example.demo.Tools.ServiceCommande;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.WriterException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Controllers.PaiementStripeUI.creerSessionPaiement;
import static javafx.scene.paint.Color.BLACK;

public class CommandeClientController{
    static Stage stage;
    private static CommandeHolder holder = CommandeHolder.getInstance();
    private static Commande CurrentCommande = holder.getCommande();
    @FXML
    private CheckBox payOnDeliveryCheckBox;
    public ComboBox map;
    @FXML
    private VBox productsContainer;
    @FXML
    private BorderPane centerPane;
    @FXML
    private  VBox productsContainer1;
    static PanierController panierController = new PanierController(); // Création d'une instance de PanierController

    private static final String API_KEY = "2687d76eef6bf2a7b59beecb";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;
    public CommandeClientController(PanierController panierController) {
        this.panierController = panierController;
    }
    private static ServiceCommande commandeService;
    static NavbarreCotroller navbarreController = new NavbarreCotroller();

    static float latitude;
    static float longitude;
    public void savecoords(String latitude, String longitude) {
        CommandeClientController.latitude =Float.parseFloat(latitude);
        CommandeClientController.longitude =Float.parseFloat(longitude);
        System.out.println("Latitude from controller = "+latitude);
        System.out.println("Longitude from controller= "+longitude);
        saveOrder();
    }

    private void saveOrder() {
    }


    // Liste observable pour stocker les commandes
    private ObservableList<Commande> commandeList = FXCollections.observableArrayList();

    // Connexion à la base de données
    private static Connection cnx;
    @FXML
    private Text remiseInput;
    @FXML
    private TextArea clientInput;

    @FXML
    private Label titleLabel;

    @FXML
    private Text totaleInputEur;

    @FXML
    private Text totaleInputTnd;
    @FXML
    private Text adress;

    @FXML
    private Label infoLabel;
    @FXML
    private DatePicker dateCreationInput;
    @FXML
    private ComboBox<String> addressComboBox;

    double[] sousTotale = {0};
    double[] remise = {0};
    double[] totale = {0};
    // Constructeur
    public CommandeClientController() {
        cnx = MyConnection.getInstance().getCnx();
    }
    // Supposons que l'utilisateur connecté ait l'id 1
    int idUtilisateur = 14;
    Utilisateur utilisateur = new Utilisateur();
    public void initialize() {
        CurrentCommande = holder.getCommande();



        if (CurrentCommande == null) {
            // Initialisation avec des valeurs par défaut ou récupération d'une source

            System.out.println("Aucune commande sélectionnée.");
            // Retourner ou initialiser les éléments de l'UI avec des valeurs par défaut
            return;
        }

        this.afficherProduits();


        try {
            // Préparer la requête pour récupérer les données de l'utilisateur
            String selectQuery = "SELECT nom, prenom, email, adresse FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement pst = cnx.prepareStatement(selectQuery);
            pst.setInt(1, idUtilisateur);

            // Exécuter la requête
            ResultSet rs = pst.executeQuery();

            // Vérifier si des résultats sont retournés
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String adresse = rs.getString("adresse");

                utilisateur = new Utilisateur(nom,prenom,email,adresse);



            } else {
                System.out.println("Aucun utilisateur trouvé avec l'identifiant spécifié.");
            }

            // Fermer les ressources
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des informations de l'utilisateur : " + e.getMessage());
        }

        try {
            this.initData(CurrentCommande);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initData(Commande quest) throws IOException {
        // Initialisation de la commande courante avec les données passées en paramètre.
        CurrentCommande = quest;
        String toCurrency = "EUR"; // Définit la devise cible pour la conversion.

        // Met à jour l'interface utilisateur avec les détails de la commande courante.
        holder.setCommande(CurrentCommande);

        // Initialise l'objet de service pour interagir avec les données des commandes.
        commandeService = new ServiceCommande();

        // Convertit la date de la commande de String à LocalDate pour l'affichage.
        LocalDate createdConverted = LocalDate.parse(CurrentCommande.getDate_commande().toString());
        dateCreationInput.setValue(createdConverted);

        // Récupère et affiche le nom d'utilisateur du client à partir de son ID.
        clientInput.setText(String.valueOf(commandeService.usernameById(CurrentCommande.getId_client())));

        // Formatage et affichage de la remise en pourcentage.
        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        String remiseText = decimalFormat.format(CurrentCommande.getRemise() * 100) + " %";
        remiseInput.setText(remiseText);

        // Affichage de l'adresse de l'utilisateur.
        adress.setText(utilisateur.getAdresse());

        // Affichage du total de la commande en TND.
        totaleInputTnd.setText(CurrentCommande.getTotal_commande() + " TND");

        // Conversion de la devise de TND à EUR via une requête HTTP à un service de conversion de devises.
        /** Convertisseur Devise TND -> EUR */
        URL url = new URL(BASE_URL + "pair/TND/" + toCurrency + "/" + CurrentCommande.getTotal_commande());
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convertit la réponse en JSON.
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        // Récupère le résultat de la conversion et l'affiche.
        Float req_result = jsonobj.get("conversion_result").getAsFloat();
        CurrentCommande.setTotal_commande_devise(req_result);
        CurrentCommande.setClientUsername(commandeService.usernameById(CurrentCommande.getId_client()));
        totaleInputEur.setText(String.format("%.2f %s", req_result, toCurrency));
    }

    public void afficherProduits() {
        // Récupérer les lignes de commande pour afficher les produits
        List<LigneCommande> ligneCommandes = affichageProduitsDansCommande();

        // Nettoyer le conteneur des produits avant d'afficher de nouveaux produits
        productsContainer.getChildren().clear();

        // Boucle à travers chaque ligne de commande
        for (LigneCommande prod : ligneCommandes) {
            // Requête SQL pour récupérer les détails du produit à partir de la base de données
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    // Si la requête retourne un résultat, créer un objet Produit
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

                        // Créer un conteneur VBox pour le produit
                        VBox productBox = new VBox(10);
                        productBox.getStyleClass().add("product-box");
                        productsContainer.setStyle("-fx-padding:15px");

                        // Créer et styliser l'image du produit
                        ImageView productImage = new ImageView(new Image(p.getImage()));
                        productImage.setFitHeight(15);
                        productImage.setFitWidth(15);
                        Rectangle clip = new Rectangle(15, 15); // Set the dimensions as needed
                        clip.setArcWidth(30); // Adjust the corner radius
                        clip.setArcHeight(30);
                        productImage.setClip(clip);
                        productImage.setEffect(new DropShadow(10, BLACK)); // Adjust the shadow parameters

                        // Créer des étiquettes pour afficher les détails du produit
                        Label labelProduit = new Label(p.getMarque() + " - " + p.getRef());
                        labelProduit.setMinHeight(10);
                        labelProduit.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit.setAlignment(Pos.BOTTOM_CENTER);

                        DecimalFormat decimalFormat = new DecimalFormat("0.##");
                        decimalFormat.setDecimalSeparatorAlwaysShown(false);
                        Label labelProduit1 = new Label(decimalFormat.format(p.getPrix() * prod.getQuantite()) + " TND");
                        labelProduit1.setMinHeight(10);
                        labelProduit1.setStyle("-fx-position:absolute ;-fx-top:12px;");
                        labelProduit1.setAlignment(Pos.BOTTOM_CENTER);

                        // Champ de texte pour afficher la quantité du produit
                        TextField quantiteTextField = new TextField(String.valueOf(prod.getQuantite()));
                        quantiteTextField.setEditable(false);
                        quantiteTextField.setMaxWidth(100);
                        quantiteTextField.setMaxHeight(20);
                        quantiteTextField.setStyle("-fx-font-size: 10px;");
                        quantiteTextField.setAlignment(Pos.CENTER);

                        // Ajouter les éléments graphiques au conteneur productBox
                        productBox.setAlignment(Pos.CENTER);
                        productBox.setPrefWidth(166);
                        HBox.setHgrow(productBox, Priority.ALWAYS);
                        productBox.getChildren().addAll(productImage, labelProduit, labelProduit1, quantiteTextField);

                        // Ajouter le conteneur productBox au conteneur principal productsContainer
                        productsContainer.getChildren().add(productBox);
                    }
                }
            } catch (SQLException e) {
                // Gérer les erreurs liées à l'affichage du produit dans le panier
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }
        }

        // Créer un ScrollPane pour contenir les produits
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true); // Ajuster à la largeur du parent
    }



    //////////**************************** Affiche les produits dans le panier en rejoignant les
    // tables `produit` et `panier` par `ref_produit`.***********************************************
    private static List<LigneCommande> affichageProduitsDansCommande() {
        // Création d'une liste pour stocker les lignes de commande des produits
        List<LigneCommande> produits = new ArrayList<>();

        // Requête SQL pour récupérer les lignes de commande des produits dans une commande spécifique
        String requete = "SELECT lc.* FROM produit p " +
                "JOIN ligne_commande lc ON p.ref = lc.ref_produit and lc.id_commande=? ";

        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            // Remplacer le paramètre de la commande dans la requête SQL avec l'ID de la commande actuelle
            pst.setInt(1, CurrentCommande.getId_commande());

            try (ResultSet rs = pst.executeQuery()) {
                // Parcourir les résultats de la requête
                while (rs.next()) {
                    // Créer un objet LigneCommande pour chaque ligne de commande récupérée
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
            // Gérer les erreurs liées à la récupération des produits dans le panier
            System.out.println("Erreur lors de l'affichage des produits dans le panier : " + e.getMessage());
        }

        // Retourner la liste des lignes de commande des produits
        return produits;
    }


    // Méthode appelée lorsque l'utilisateur valide la livraison
    @FXML
    public void ajouterCommande() throws IOException, WriterException, MessagingException {
        if(!payOnDeliveryCheckBox.isSelected()){
            payer();
            validCommande();
          navbarre();

        }else{
            validCommande() ;
            navbarre();
        }
        panierController.updatecommande();




    }

    //* valid commande finale
    public static void validCommande() throws IOException, WriterException, MessagingException {
        // Récupérer les lignes de commande pour afficher les produits
        List<LigneCommande> ligneCommandes = affichageProduitsDansCommande();
        commandeService = new ServiceCommande();

        // URL de votre page Facebook Smart Foody
        String facebookPageURL = "https://www.facebook.com/smartfoody.tn";

        // Générer le code QR pour la commande avec l'URL de la page Facebook
        String qrCodePath = "C:\\Users\\INFOTEC\\Desktop\\3A\\uml\\Smart_Foody_23-24\\qr_code.png"; // Remplacer par le chemin où vous souhaitez enregistrer le code QR
        QRCodeGenerator.generateQRCode(facebookPageURL, 200, 200, qrCodePath);

        // Informations pour l'email
        // String emailClient = commandeService.emailById(CurrentCommande.getId_client());
        String emailClient= "sfadhila1234@gmail.com";
        String sujetEmail = "Confirmation de commande";

        // Modifier le contenuEmail pour inclure le code QR
        String contenuEmail = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html dir=\"ltr\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                "  <title></title>\n" +
                "  <!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "    <![endif]-->\n" +
                "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]-->\n" +
                "  <!--[if gte mso 9]>\n" +
                "<xml>\n" +
                "    <o:OfficeDocumentSettings>\n" +
                "    <o:AllowPNG></o:AllowPNG>\n" +
                "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "    </o:OfficeDocumentSettings>\n" +
                "</xml>\n" +
                "<![endif]-->\n" +
                "  <!--[if !mso]><!-- -->\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Raleway&display=swap\" rel=\"stylesheet\">\n" +
                "  <!--<![endif]-->\n" +
                " <!--[if mso]>\n" +
                " <style type=\"text/css\">\n" +
                "     ul {\n" +
                "  margin: 0 !important;\n" +
                "  }\n" +
                "  ol {\n" +
                "  margin: 0 !important;\n" +
                "  }\n" +
                "  li {\n" +
                "  margin-left: 47px !important;\n" +
                "  }\n" +
                "\n" +
                " </style><![endif]\n" +
                "--></head>\n" +
                " <body class=\"body\">\n" +
                "  <div dir=\"ltr\" class=\"es-wrapper-color\">\n" +
                "   <!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#eff7f6\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]-->\n" +
                "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "    <tbody>\n" +
                "     <tr>\n" +
                "      <td class=\"esd-email-paddings\" valign=\"top\">\n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"esd-header-popover es-header\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-header-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20 es-m-p0b\" align=\"left\">\n" +
                "               <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"281\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"261\" class=\"es-m-p0r es-m-p20b esd-container-frame\" valign=\"top\" align=\"center\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" class=\"esd-block-image es-p5t es-p5b es-m-txt-c\" style=\"font-size: 0px;\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/guids/CABINET_02d1bc47a643a3e7bfe02b0f41d6cb58a6c2703f13c0ecd11cddd42b47af504e/images/image.png\" alt=\"Logo\" style=\"display:block\" height=\"45\" title=\"Logo\" class=\"adapt-img\"></a></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                  <td class=\"es-hidden\" width=\"20\"></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"128\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"128\" align=\"left\" class=\"esd-container-frame es-m-p20b\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"131\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"131\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table>\n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table class=\"es-content-body\" style=\"background-color: #ffffff;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure\" align=\"left\">\n" +
                "               <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td class=\"es-m-p0r esd-container-frame\" width=\"600\" valign=\"top\" align=\"center\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-banner\" style=\"position: relative;\"><a target=\"_blank\"><img src=\"https://eetnmyy.stripocdn.email/content/guids/bannerImgGuid/images/image16788672966342121.png\" title=\"\" alt=\"\" class=\"adapt-img\" width=\"600\" height=\"200\"></a><esd-stored-config-block style=\"display: none;\">\n" +
                "                       </esd-stored-config-block></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p30t es-p30b es-p20r es-p20l\" align=\"left\" bgcolor=\"#6a994e\" style=\"background-color: #6a994e;\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text es-p10 es-m-txt-c\"><h3 style=\"color: #ffffff;\">Hello " + commandeService.usernameById(CurrentCommande.getId_client()) + ",</h3></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text es-m-txt-c es-p20t\"><p style=\"color: #ffffff;\">Merci pour votre commande récente. Nous sommes heureux de confirmer que nous avons reçu votre commande et qu’elle est en cours de traitement.</p></td>\n" +
                "                     </tr>\n" +
                "                     \n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table>\n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40t es-p30b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text\"><h1>Récapitulatif de la commande</h1></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text es-p40t es-p20r es-p20l es-m-p10t\"><h3 class=\"b_title\">COMMANDE NUM " + CurrentCommande.getId_commande() + "<br>" + CurrentCommande.getDate_commande() + "</h3></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40b es-p20r es-p20l\" align=\"left\">\n" +

                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"345\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                          <td width=\"560\" class=\"esd-container-frame\" align=\"left\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left:1px solid #386641;border-right:1px solid #386641;border-top:1px solid #386641;border-bottom:1px solid #386641;border-radius: 10px; border-collapse: separate;\">\n" +
                "                    <tbody>\n" +
                "                     ";

        for (LigneCommande prod : ligneCommandes) {
            // Requête SQL pour récupérer les détails du produit à partir de la base de données
            String requete = "SELECT p.* FROM produit p where  p.ref = ?";
            try (PreparedStatement pst = cnx.prepareStatement(requete)) {
                pst.setString(1, prod.getRefProduit());
                try (ResultSet rs = pst.executeQuery()) {
                    // Si la requête retourne un résultat, créer un objet Produit
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

                        contenuEmail += "<tr><td align=\"left\" class=\"esd-block-text es-p25t es-p25b es-p20r es-m-txt-c es-p20l\"><h3 class=\"p_name\" style=\"line-height: 150%;\">" + p.getMarque() + "</h3> <p class=\"p_description\" style=\"line-height: 150%;\">CATEGORY: " + p.getCategorie() + "</p>  <p style=\"line-height: 150%;\">QTY:&nbsp;" + prod.getQuantite() + "</p> <h3 style=\"line-height: 150%;\" class=\"p_price\">€ " + p.getPrix() + "</h3></td> </tr>\n";
                    }
                }
            } catch (SQLException e) {
                // Gérer les erreurs liées à l'affichage du produit dans le panier
                System.out.println("Erreur lors de l'affichage du produit dans le panier : " + e.getMessage());
            }
        }
        contenuEmail += " " +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40b es-p20r es-p20l\" align=\"left\">\n" +
                "               <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"195\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"195\" align=\"left\" class=\"esd-container-frame es-m-p20b\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"345\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"345\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-left:1px solid #386641;border-right:1px solid #386641;border-top:1px solid #386641;border-bottom:1px solid #386641;border-radius: 10px; border-collapse: separate;\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40t es-p30b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text\"><h1>Total de la commande</h1></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20 esdev-adapt-off\" align=\"left\">\n" +
                "               <table width=\"560\" cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td class=\"esdev-mso-td\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td width=\"270\" class=\"esd-container-frame\" align=\"left\">\n" +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td align=\"left\" class=\"esd-block-text\"><p>Sous-total<br>Remise</p></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                  <td width=\"20\"></td>\n" +
                "                  <td class=\"esdev-mso-td\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td width=\"270\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td align=\"right\" class=\"esd-block-text\"><p>€" + CurrentCommande.getTotal_commande_devise() / CurrentCommande.getRemise() + "<br>%";
        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        String remiseText = decimalFormat.format(CurrentCommande.getRemise() * 100) ;
        contenuEmail += remiseText;
        contenuEmail += "</p></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-spacer es-p5t es-p5b\" style=\"font-size:0\">\n" +
                "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td style=\"border-bottom: 5px dotted #a7c957; background: unset; height: 1px; width: 100%; margin: 0px;\"></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20 esdev-adapt-off\" align=\"left\">\n" +
                "               <table width=\"560\" cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td class=\"esdev-mso-td\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td width=\"270\" class=\"esd-container-frame\" align=\"left\">\n" +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td align=\"left\" class=\"esd-block-text es-m-txt-l\"><h3>Total</h3></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                  <td width=\"20\"></td>\n" +
                "                  <td class=\"esdev-mso-td\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td width=\"270\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td align=\"right\" class=\"esd-block-text es-m-txt-r\"><h3>€" + CurrentCommande.getTotal_commande_devise() + "</h3></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40t es-p30b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20\" align=\"left\">\n" +
                "               <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"270\" class=\"es-m-p20b esd-container-frame\" align=\"left\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"270\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20\" align=\"left\">\n" +
                "               <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"270\" class=\"es-m-p20b esd-container-frame\" align=\"left\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"270\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"270\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p30t es-p40b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"560\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table>\n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40t es-p20b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-text\"><h1>Votre information</h1></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p15t es-p15b es-p20r es-p20l\" align=\"left\">\n" +
                "               <!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"129\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"109\" class=\"es-m-p20b esd-container-frame\" align=\"left\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"right\" class=\"esd-block-image es-m-txt-c\" style=\"font-size: 0px;\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/guids/CABINET_128e4efa46af80b67022aaf8a3e25095/images/jakenackosif9tk5uykiunsplash_1_3_eWU.png\" alt=\"\" style=\"display: block;\" width=\"109\"></a></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                  <td class=\"es-hidden\" width=\"20\"></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"178\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td class=\"es-m-p0r esd-container-frame es-m-p20b\" width=\"178\" valign=\"top\" align=\"center\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" class=\"esd-block-text es-m-txt-c es-p5b es-m-p5t\"><h3>" + commandeService.usernameById(CurrentCommande.getId_client()) + "</h3></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" class=\"esd-block-image es-p10t es-m-txt-c\" style=\"font-size: 0px;\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/guids/CABINET_af08f412597e682bf2508636e5fc6513/images/vector_251_Wop.png\" alt=\"Signature\" style=\"display: block;\" width=\"60\" title=\"Signature\"></a></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"233\" valign=\"top\"><![endif]-->\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"233\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table>\n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure esdev-adapt-off es-p20\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" class=\"esd-block-text\"><p>Bonjour!\n" +
                " &nbsp;</p><p>\u200B</p><p>Merci pour votre commande récente. Nous sommes heureux de confirmer que nous avons reçu votre commande et qu’elle est en cours de traitement.</p></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure esdev-adapt-off es-p20\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"560\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p40b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-spacer es-p5t es-p5b\" style=\"font-size:0\">\n" +
                "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td style=\"border-bottom: 5px dotted #a7c957; background: unset; height: 1px; width: 100%; margin: 0px;\"></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table>\n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table bgcolor=\"#ffffff\" class=\"es-footer-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"600\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-spacer es-p5t es-p5b\" style=\"font-size:0\">\n" +
                "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td style=\"border-bottom: 2px solid #eff7f6; background: unset; height: 1px; width: 100%; margin: 0px;\"></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p30t es-p30b es-p20r es-p20l\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                  <td width=\"560\" align=\"left\" class=\"esd-container-frame\">\n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                    <tbody>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-image es-p20b es-m-txt-c\" style=\"font-size: 0px;\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/guids/CABINET_02d1bc47a643a3e7bfe02b0f41d6cb58a6c2703f13c0ecd11cddd42b47af504e/images/image.png\" alt=\"Logo\" style=\"display:block\" title=\"Logo\" height=\"50\" class=\"adapt-img\"></a></td>\n" +
                "                     </tr>\n" +
                "                     \n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" class=\"esd-block-social es-m-txt-c es-p20t es-p20b\" style=\"font-size:0\">\n" +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-table-not-adapt es-social\">\n" +
                "                        <tbody>\n" +
                "                         <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" class=\"es-p20r\"><a target=\"_blank\" href=\'" + facebookPageURL + "'\"><img src=\"https://eetnmyy.stripocdn.email/content/assets/img/social-icons/logo-black/facebook-logo-black.png\" alt=\"Fb\" title=\"Facebook\" height=\"24\"></a></td>\n" +
                "                          <td align=\"center\" valign=\"top\" class=\"es-p20r\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/assets/img/social-icons/logo-black/x-logo-black.png\" alt=\"X\" title=\"X.com\" height=\"24\"></a></td>\n" +
                "                          <td align=\"center\" valign=\"top\" class=\"es-p20r\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/assets/img/social-icons/logo-black/youtube-logo-black.png\" alt=\"Yt\" title=\"Youtube\" height=\"24\"></a></td>\n" +
                "                          <td align=\"center\" valign=\"top\"><a target=\"_blank\" href=\"https://viewstripo.email\"><img src=\"https://eetnmyy.stripocdn.email/content/assets/img/social-icons/logo-black/instagram-logo-black.png\" alt=\"Ig\" title=\"Instagram\" height=\"24\"></a></td>\n" +
                "                         </tr>\n" +
                "                        </tbody>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                     \n" +
                "                    </tbody>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table>\n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content esd-footer-popover\" align=\"center\">\n" +
                "        <tbody>\n" +
                "         <tr>\n" +
                "          <td class=\"esd-stripe\" align=\"center\">\n" +
                "           <table class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: transparent;\">\n" +
                "            <tbody>\n" +
                "             <tr>\n" +
                "              <td class=\"esd-structure es-p20\" align=\"left\">\n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                 <tr>\n" +
                "                      \n" +
                "                  <td width=\"560\" class=\"esd-container-frame\" align=\"center\" valign=\"top\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                          <tbody><tr><td align=\"center\" class=\"esd-empty-container\" style=\"display: none\"></td>\n" +
                "                      </tr></tbody></table>\n" +
                "                  </td>\n" +
                "              \n" +
                "                      \n" +
                "              </tr>\n" +
                "                </tbody>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "            </tbody>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "        </tbody>\n" +
                "       </table></td>\n" +
                "     </tr>\n" +
                "    </tbody>\n" +
                "   </table>\n" +
                "  </div>\n" +
                " \n" +
                "</body></html>";

        // Assurez-vous que la méthode envoyerEmailAvecImageInline est adaptée pour gérer plusieurs images (logo et code QR)
        EmailUtil.envoyerEmailAvecImageInline(emailClient, sujetEmail, contenuEmail, qrCodePath, "qrCode", "C:\\Users\\INFOTEC\\Desktop\\3A\\uml\\Smart_Foody_23-24\\qr_code.png", "logo");

        // Afficher une alerte de succès
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Commande passée");
        successAlert.setHeaderText(null);
        successAlert.setContentText("La commande a été ajoutée avec succès et un e-mail de confirmation a été envoyé.");
        successAlert.getDialogPane().getStylesheets().add(CommandeClientController.class.getResource("/com/example/demo/css/style_panier.css").toExternalForm());
        successAlert.getDialogPane().getStyleClass().add("custom-alert");
        successAlert.showAndWait();

        System.out.println("Commande ajoutée avec succès");
        panierController.viderPanier(true);


    }




    public void payer() {
        Stage stage = new Stage();
        WebView webView = new WebView();
        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Paiement Stripe");
        stage.show();
        creerSessionPaiement(webView,CurrentCommande,this);

    }


    public void map(ActionEvent actionEvent) {
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/demo/map.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    public void annulerCommande(ActionEvent actionEvent) throws SQLException {
        commandeService.deleteOne(CurrentCommande.getId_commande());
        try {
            Parent commandeParent = FXMLLoader.load(getClass().getResource("/com/example/demo/navbarre.fxml"));
            Scene commandeScene = new Scene(commandeParent);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(commandeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










    public void navbarre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/navbarre.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) productsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}