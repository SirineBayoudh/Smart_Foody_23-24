package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ajoutReclamationController {

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnEnvoi;

    @FXML
    private TextArea tfDesc;

    @FXML
    private TextField tfMail;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfTitre;

    @FXML
    private ComboBox<String> tfType;

    MyConnection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    // Map qui contient les mots à filtrer et les mots à ignorer en combinaison avec ces mots
    static Map<String, String[]> mots = new HashMap<>();

    // Longueur du plus long mot dans la liste
    static int longueurPlusGrandMot = 0;

    int id = 15;


    @FXML
    void initialize() {
        ObservableList<String> list = FXCollections.observableArrayList("Réclamation", "Demande d'information", "Remerciement", "Demande de Collaboration", "Autres");
        tfType.setItems(list);
        recuperValeur();
        loadConfigs();


        // Ajoutez un écouteur d'événements sur le champ tfDesc
        tfDesc.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Vérifiez si le texte a été saisi dans tfDesc
                if (!newValue.isEmpty()) {
                    // Activer le bouton Envoyer
                    btnEnvoi.setDisable(false);
                } else {
                    // Désactiver le bouton Envoyer si aucun texte n'est saisi
                    btnEnvoi.setDisable(true);
                }
            }
        });

    }

    void clear() {
        tfTitre.setText(null);
        tfDesc.setText(null);
        btnEnvoi.setDisable(true);
    }

    void recuperValeur() {
        String recup = "SELECT nom,prenom,email FROM utilisateur where id_utilisateur=? ";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(recup);
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");

                // Afficher les détails dans les champs de texte
                tfNom.setText(nom + " " + prenom);
                tfMail.setText(email);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void ajoutRec(ActionEvent event) {
        // Vérifier si les champs obligatoires sont remplis
        if (tfDesc.getText().isEmpty() || tfTitre.getText().isEmpty() || tfType.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires!", ButtonType.OK);
            alert.show();
            return; //sortir de la méthode
        }

        // Vérifier si l'utilisateur a déjà soumis 4 réclamations ce mois-ci
        if (reclamationsCeMois() >= 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Vous avez déjà soumis 4 réclamations ce mois-ci!", ButtonType.OK);
            alert.show();
            return;
        }

        // Vérification des gros mots
        String filteredText = filterText(tfDesc.getText(), tfNom.getText());
        if (!tfDesc.getText().equals(filteredText)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nous avons détecté des mots inappropriés dans votre message. \n Veuillez reformuler votre message sans utiliser de langage offensant.", ButtonType.OK);
            alert.show();
            return;
        }

        String insert = "INSERT INTO reclamation(id_client,description,titre,type) VALUES (?,?,?,?)";

        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(insert);
            st.setInt(1, id);
            st.setString(2, filteredText); // Utiliser le texte filtré
            st.setString(3, tfTitre.getText());
            st.setString(4, tfType.getValue());
            st.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Votre Réclamation a bien été envoyée!", ButtonType.OK);
            alert.show();
            clear();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void annulerAjout(ActionEvent event) {
        clear();
        btnEnvoi.setDisable(false);
    }
    // Méthode pour compter le nombre de réclamations soumises par l'utilisateur ce mois-ci
    int reclamationsCeMois() {
        int count = 0;
        String query = "SELECT COUNT(*) AS count FROM reclamation WHERE id_client = ? AND MONTH(date_reclamation) = ? AND YEAR(date_reclamation) = ?";
        con = MyConnection.getInstance();
        try {
            st = con.getCnx().prepareStatement(query);
            st.setInt(1, id);
            LocalDate date = LocalDate.now();
            st.setInt(2, date.getMonthValue());
            st.setInt(3, date.getYear());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }




    // Méthode pour charger les configurations de mots à filtrer
    public static void loadConfigs() {
        String filePath = "src/main/resources/com/example/demo/ImagesGestionReclamations/LISTE_DES_GROS_MOTS_A_IGNORER_AVEC_.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Le fichier spécifié n'existe pas : " + filePath);
            return;
        }
        try {
            BufferedReader lecteur = new BufferedReader(new FileReader(filePath));
            String ligne = "";
            int compteur = 0;
            while((ligne = lecteur.readLine()) != null) {
                compteur++;
                String[] contenu = null;
                try {
                    contenu = ligne.split(",");
                    if(contenu.length == 0) {
                        continue;
                    }
                    String mot = contenu[0];
                    String[] ignorerEnCombinaisonAvecMots = new String[]{};
                    if(contenu.length > 1) {
                        ignorerEnCombinaisonAvecMots = contenu[1].split("_");
                    }

                    if(mot.length() > longueurPlusGrandMot) {
                        longueurPlusGrandMot = mot.length();
                    }
                    mots.put(mot.replaceAll(" ", ""), ignorerEnCombinaisonAvecMots);

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Chargé " + compteur + " mots à filtrer");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parcourt une chaîne de caractères en entrée et vérifie si un mot grossier a été trouvé dans la liste, puis vérifie si le mot doit être ignoré (par exemple, "bass" contient le mot "*ss").
     * @param input
     * @return
     */
    public static ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        // N'oubliez pas de supprimer le leet speak, vous voudrez probablement déplacer cela dans sa propre fonction et utiliser des regex si vous voulez utiliser ceci

        input = input.replaceAll("1","i");
        input = input.replaceAll("!","i");
        input = input.replaceAll("3","e");
        input = input.replaceAll("4","a");
        input = input.replaceAll("@","a");
        input = input.replaceAll("5","s");
        input = input.replaceAll("7","t");
        input = input.replaceAll("0","o");
        input = input.replaceAll("9","g");

        ArrayList<String> motsGrossiers = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // parcourir chaque lettre dans le mot
        for(int debut = 0; debut < input.length(); debut++) {
            // à partir de chaque lettre, continuez à chercher des mots grossiers jusqu'à ce que la fin de la phrase soit atteinte ou que la longueur maximale du mot soit atteinte.
            for(int decalage = 1; decalage < (input.length()+1 - debut) && decalage < longueurPlusGrandMot; decalage++)  {
                String motÀVérifier = input.substring(debut, debut + decalage);
                if(mots.containsKey(motÀVérifier)) {
                    // par exemple, si vous voulez dire le mot "bass", cela devrait être possible.
                    String[] vérificationIgnorer = mots.get(motÀVérifier);
                    boolean ignorer = false;
                    for(int s = 0; s < vérificationIgnorer.length; s++ ) {
                        if(input.contains(vérificationIgnorer[s])) {
                            ignorer = true;
                            break;
                        }
                    }
                    if(!ignorer) {
                        motsGrossiers.add(motÀVérifier);
                    }
                }
            }
        }


        for(String s: motsGrossiers) {
            System.out.println(s + " qualifié comme un mot grossier dans un nom d'utilisateur");
        }
        return motsGrossiers;

    }

    public static String filterText(String input, String username) {
        ArrayList<String> motsGrossiers = badWordsFound(input);
        if(motsGrossiers.size() > 0) {
            return "Ce message a été bloqué car un mot grossier a été trouvé. Si vous pensez que ce mot ne devrait pas être bloqué, veuillez contacter le support.";
        }
        return input;
    }
}