package com.example.demo.Controllers;

import com.example.demo.Models.Conseil;
import com.example.demo.Tools.MyConnection;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.dynamsoft.dbr.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;

import com.google.gson.Gson;
import javafx.util.Duration;

public class AjoutConseilController implements Initializable {
    @FXML
    private ImageView productImage;

    @FXML
    private Text marqueLabel;
    @FXML
    private Text productLabel;
    @FXML
    private Text barcodeLabel;
    @FXML
    private TextArea calLabel;

    @FXML
    private TextArea demandeLabel;
    @FXML
    private TextArea tDemande;
    @FXML
    private TextArea tNote;
    @FXML
    private TextArea foodLabel;
    @FXML
    private Button btnUpdate;
    @FXML
    private StackPane pane;
    int id_conseil=0;
    @FXML
    private TableColumn<Conseil, Integer> colidconseil;
    @FXML
    private TableColumn<Conseil, java.util.Date> coldate;
    @FXML
    private TableColumn<Conseil, String> coldemande;
    @FXML
    private TableColumn<Conseil, Integer> colnote;
    @FXML
    private TableColumn<Conseil, String> colreponse;
    @FXML
    private TableColumn<Conseil, String> colstatut;
    @FXML
    private TableView<Conseil> table;
    @FXML
    private Text quoteText;
    @FXML
    private Text foodInfoText;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    @FXML
    private TextArea foodName;
    public class Store {
        public String name;
        public String price;
        public String link;
        public String currency;
        public String currency_symbol;
    }

    public class Review {
        public String name;
        public String rating;
        public String title;
        public String review;
        public String date;
    }

    public class Product {
        public String barcode_number;
        public String barcode_formats;
        public String mpn;
        public String model;
        public String asin;
        public String title;
        public String category;
        public String manufacturer;
        public String brand;
        public String[] contributors;
        public String age_group;
        public String ingredients;
        public String nutrition_facts;
        public String color;
        public String format;
        public String multipack;
        public String size;
        public String length;
        public String width;
        public String height;
        public String weight;
        public String release_date;
        public String description;
        public Object[] features;
        public String[] images;
        public AjoutConseilController.Store[] stores;
        public AjoutConseilController.Review[] reviews;
    }

    public class RootObject {
        public AjoutConseilController.Product[] products;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyConnection.getInstance();
        showConseils();
        setborder(pane);
        btnUpdate.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        // capturing id_conseil of selected item in the table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                id_conseil = newSelection.getId_conseil();
            }
        });
        Quote quoteService = new Quote();
        try {
            String randomQuote = quoteService.getRandomQuote();
            quoteText.setText(randomQuote);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void ajoutConseil(ActionEvent event) {
        try {
            if (demandeLabel.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tout les champs.");
                return; // Stop execution
            }
            String insert = "INSERT INTO conseil(id_client, matricule, statut, demande, reponse, date_conseil) VALUES (?, ?, ?, ?, ?, ?)";
            con = MyConnection.instance.getCnx();
            st = con.prepareStatement(insert);
            st.setString(1, String.valueOf(1));
            st.setInt(2, 1);
            st.setString(3, "en attente");
            st.setString(4, demandeLabel.getText());
            st.setString(5, "");
            LocalDate currentDate = LocalDate.now();
            Date date = Date.valueOf(currentDate);
            st.setObject(6, date);
            int rowsAffected = st.executeUpdate();
            showConseils();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Conseil ajouté avec succès.");
                sendEmail("yassiine.studies@gmail.com", "Smart Foody : Nouvelle demande reçue", demandeLabel.getText(), currentDate.toString());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout du conseil.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Entrée invalide.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date invalide.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : échec de l'ajout de conseil.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void sendEmail(String to, String subject, String demande, String dateConseil) {
        // SMTP server configuration
        String host = "smtp.gmail.com"; // SMTP server hostname
        String username = "smartfoody.2024@gmail.com"; // SMTP username
        String password = ""; // SMTP password
        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        // Session object to authenticate the sender
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            // Construct email body with HTML formatting
            String body = "<html>"
                    + "<body>"
                    + "<div style='width: 500px;background-color: #aad597; padding: 20px;'>"
                    + "<p style='color:darkgreen;font-size:14px;'>Bonjour,</p>"
                    + "<p style='color:darkgreen;font-size:18px;'>Une nouvelle demande a été reçue :</p>"
                    + "<p style='color:black;font-weight:bold;font-size:16px;'>Objet: " + demande + "</p>"
                    + "<p style='color:darkgreen;'font-weight:bold;font-size:16px;'>Date de réception: " + dateConseil + "</p>"
                    + "<p style='font-weight:bold;'>Veuillez consulter l'application pour la traiter.</p>"
                    + "<p style='color:gray;'>Ceci est un message automatique. Merci de ne pas répondre.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Conseil> getConseils(){
        ObservableList<Conseil> conseils = FXCollections.observableArrayList();
        String query="select * from conseil WHERE id_client='1'";
        con = MyConnection.instance.getCnx();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while(rs.next()){
                Conseil st = new Conseil();
                st.setId_conseil(rs.getInt("id_conseil"));
                st.setStatut(rs.getString("statut"));
                st.setDemande(rs.getString("demande"));
                st.setReponse(rs.getString("reponse"));
                st.setNote(rs.getInt("note"));
                st.setDate_conseil(rs.getDate("date_conseil"));
                conseils.add(st)
                ;            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conseils;
    }

    public void showConseils(){
        ObservableList<Conseil> list = getConseils();
        table.setItems(list);
        colidconseil.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("id_conseil"));
        colstatut.setCellValueFactory(new PropertyValueFactory<Conseil,String>("statut"));
        coldemande.setCellValueFactory(new PropertyValueFactory<Conseil,String>("demande"));
        colreponse.setCellValueFactory(new PropertyValueFactory<Conseil,String>("reponse"));
        colnote.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("note"));
        coldate.setCellValueFactory(new PropertyValueFactory<Conseil, java.util.Date>("date_conseil"));
    }

    @FXML
    void getData(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Conseil selectedConseil = table.getSelectionModel().getSelectedItem();
            if (selectedConseil != null) {
                id_conseil = selectedConseil.getId_conseil();
                tDemande.setText(String.valueOf(selectedConseil.getDemande()));
                tNote.setText(String.valueOf(selectedConseil.getNote()));
            }
        }
        if (event.getClickCount() == 2) {
            Conseil selectedConseil = table.getSelectionModel().getSelectedItem();
            if (selectedConseil != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/DetailConseilWindow.fxml"));
                    Parent root = loader.load();
                    DetailWindowController controller = loader.getController();
                    controller.initData(selectedConseil);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void openChatBotWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ChatBot.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ChatBot");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateConseil(ActionEvent event) {
        try {
            if (tNote.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le champ.");
                return;
            }
            try{
            int noteValue = Integer.parseInt(tNote.getText());
            if (noteValue < 1 || noteValue > 5) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir une note entre 1 et 5.");
                return;
            }
            }catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir une valeur numérique pour la note.");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir mettre à jour ce conseil?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String update = "UPDATE conseil SET note = ? WHERE id_conseil = ?";
                    con = MyConnection.instance.getCnx();
                    try {
                        st = con.prepareStatement(update);
                        st.setString(1, tNote.getText());
                        st.setInt(2, id_conseil);
                        int rowsAffected = st.executeUpdate();
                        showConseils();
                        if (rowsAffected > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Conseil mis à jour avec succès.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du conseil.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("SQLException occurred: " + e.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : Échec de la mise à jour du conseil.");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est apparue.");
        }
    }

    @FXML
    private void searchFood(ActionEvent event) {
        String foodName = foodLabel.getText();
        if (foodName == null || foodName.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir un nom d'aliment.");
        }
        else {
            FoodCalorie foodCalorieService = new FoodCalorie();
            try {
                String calorieInfo = foodCalorieService.getFoodCalories(foodName);
                foodInfoText.setText(calorieInfo);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'obtention des calories.");
            }
        }
    }

    @FXML
    void clear(MouseEvent event) {
        foodLabel.setText(null);
        foodInfoText.setText(null);
    }

    @FXML
    public void selectImage() {
        barcodeLabel.setText(null);
        productLabel.setText(null);
        marqueLabel.setText(null);
        calLabel.setText(null);
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));
                ImageView imageView = new ImageView(image);
                String barcode = readBarcode(file);
                barcodeLabel.setText(barcode);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String readBarcode(File file) {
        try {
            // Initialize Dynamsoft Barcode Reader
            BarcodeReader.initLicense("");
            BarcodeReader dbr = new BarcodeReader();

            // Decode barcode from the selected image file
            TextResult[] results = dbr.decodeFile(file.getAbsolutePath(), "");

            // Extract and return the barcode text
            if (results != null && results.length > 0) {
                System.out.println(results[0].barcodeText);
                try {
                    URL url = new URL("https://api.barcodelookup.com/v3/products?barcode="+results[0].barcodeText+"&formatted=y&key=");
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str = "";
                    String data = "";
                    while (null != (str= br.readLine())) {
                        data+=str;
                    }

                    Gson g = new Gson();

                    AjoutConseilController.RootObject value = g.fromJson(data, AjoutConseilController.RootObject.class);

                    String barcode = value.products[0].barcode_number;

                    String name = value.products[0].title;
                    productLabel.setText(name);

                    String marque = value.products[0].brand;
                    marqueLabel.setText(marque);

                    String cal = value.products[0].nutrition_facts;
                    calLabel.setText(cal);

                    //System.out.println("Entire Response:");
                    //System.out.println(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return results[0].barcodeText;
            } else {
                return "No barcode found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private void setborder(StackPane pane) {

        Color[] colors = Stream.of("#3c7720", "#bbddab", "#88c46d", "#66b343", "#56ab2f", "#448825", "#33661c", "56ab2f", "#19330e")
                .map(Color::web)
                .toArray(Color[]::new);

        List<Border> list = new ArrayList<>();

        int mills[] = {-50};
        KeyFrame keyFrames[]  = Stream.iterate(0, i -> i+1)
                .limit(100)
                .map(i -> new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop[]{new Stop(0, colors[i%colors.length]), new Stop(1, colors[(i+1)%colors.length])}))
                .map(lg -> new Border(new BorderStroke(lg, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))))
                .map(b -> new KeyFrame(Duration.millis(mills[0]+=200), new KeyValue(pane.borderProperty(), b, Interpolator.EASE_IN)))
                .toArray(KeyFrame[]::new);

        Timeline timeline = new Timeline(keyFrames);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
