package com.example.demo.Controllers;

import com.example.demo.Models.Conseil;
import com.example.demo.Tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.scene.control.Pagination;

public class conseilController implements Initializable {
    private final String ACCOUNT_SID = "AC288eedcef6012dbb4dd8c8951813655b";
    private final String AUTH_TOKEN = "";
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnClear;
    @FXML
    private StackedBarChart<String, Number> stackedBarChart;
    @FXML
    private TableColumn<Conseil, Date> coldate;
    @FXML
    private TableColumn<Conseil, String> coldemande;
    @FXML
    private TableColumn<Conseil, Integer> colidclient;
    @FXML
    private TableColumn<Conseil, Integer> colidconseil;
    @FXML
    private TableColumn<Conseil, Integer> colmatricule;
    @FXML
    private TableColumn<Conseil, Integer> colnote;
    @FXML
    private TableColumn<Conseil, String> colreponse;
    @FXML
    private TableColumn<Conseil, String> colstatut;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TableView<Conseil> table;
    @FXML
    private PieChart chart;
    @FXML
    private TextArea tDemande;
    @FXML
    private VBox vboxUpdate;
    @FXML
    private Label countAll;
    @FXML
    private Label countEnAttente;
    @FXML
    private Label countTerminés;
    @FXML
    private Label countMoy;
    @FXML
    private TextArea tReponse;
    @FXML
    private Pagination pagination;
    Connection con = null;
    PreparedStatement st= null;
    ResultSet rs = null;
    int id_conseil=0;
    private final int ITEMS_PER_PAGE = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyConnection.getInstance();
        showConseils();
        setupPagination();
        // vboxUpdate form is visible only if there is a selection in the table
        vboxUpdate.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
        // delete, update, and clear buttons are only enabled when there is a selection in the table
        btnDelete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        btnUpdate.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        btnClear.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        // capturing id_conseil of selected item in the table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                id_conseil = newSelection.getId_conseil();
            }
        });
        // Populate the status ComboBox
        ObservableList<String> statusOptions = FXCollections.observableArrayList("en attente", "terminé");
        statusComboBox.setItems(statusOptions);
        // Add event handler to the ComboBox to filter data
        statusComboBox.setOnAction(event -> {
            String selectedStatus = (String) statusComboBox.getSelectionModel().getSelectedItem();
            if (selectedStatus != null) {
                filterData(selectedStatus);
                updatePieChart();
                populateStackedBarChart();
            }
        });
        btnClear.setOnAction(this::clearField);
        updatePieChart();
        populateStackedBarChart();
    }

    private void setupPagination() {
        int pageCount = (getConseils().size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        pagination.setPageCount(pageCount);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            int startIndex = newIndex.intValue() * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, getConseils().size());
            ObservableList<Conseil> pageData = FXCollections.observableArrayList(
                    getConseils().subList(startIndex, endIndex));
            table.setItems(pageData);
        });
    }

    private void populateStackedBarChart() {
        ObservableList<Conseil> dataList = getConseils();
        int[] noteDistribution = new int[5]; // Array to store the distribution of notes from 1 to 5

        for (Conseil conseil : dataList) {
            int note = conseil.getNote();
            if (note >= 1 && note <= 5) {
                noteDistribution[note - 1]++; // Decrement by 1 to fit in array index
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribution des notes");
        for (int i = 0; i < 5; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), noteDistribution[i]));
        }
        stackedBarChart.getData().clear();
        stackedBarChart.getData().add(series);
    }

    private void updatePieChart() {
        ObservableList<Conseil> dataList = getConseils();
        long nbEnAttente = dataList.stream().filter(conseil -> conseil.getStatut().equalsIgnoreCase("en attente")).count();
        long nbTerminé = dataList.stream().filter(conseil -> conseil.getStatut().equalsIgnoreCase("terminé")).count();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("En attente", nbEnAttente),
                new PieChart.Data("Terminé", nbTerminé)
        );
        chart.setData(pieChartData);
    }

    private void filterData(String selectedStatus) {
        // Get the current list of Conseil objects from the TableView
        ObservableList<Conseil> dataList = getConseils();
        // Create a filtered list to hold Conseil objects matching the selected status
        ObservableList<Conseil> filteredList = FXCollections.observableArrayList();
        // Iterate through the dataList and add Conseil objects that match the selected status
        for (Conseil conseil : dataList) {
            if (conseil.getStatut().equalsIgnoreCase(selectedStatus.trim())) {
                filteredList.add(conseil);
            }
        }
        // Set the filtered data to the TableView
        table.setItems(filteredList);
    }

    @FXML
    void clearField(ActionEvent event) {
        clear();
    }
    void clear() {
        tDemande.setText(null);
        tReponse.setText(null);
        table.getSelectionModel().clearSelection();
        showConseils();
    }

    public ObservableList<Conseil> getConseils(){
        ObservableList<Conseil> conseils = FXCollections.observableArrayList();
        String query="select * from conseil";
        con = MyConnection.instance.getCnx();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while(rs.next()){
                Conseil st = new Conseil();
                st.setId_conseil(rs.getInt("id_conseil"));
                st.setId_client(rs.getInt("id_client"));
                st.setMatricule(rs.getInt("matricule"));
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
        // Get the sublist containing the first 10 rows
        ObservableList<Conseil> firstPageData = FXCollections.observableArrayList(list.subList(0, Math.min(list.size(), ITEMS_PER_PAGE)));
        // Set the items to the TableView
        table.setItems(firstPageData);
        colidconseil.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("id_conseil"));
        colidclient.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("id_client"));
        colmatricule.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("matricule"));
        colstatut.setCellValueFactory(new PropertyValueFactory<Conseil,String>("statut"));
        coldemande.setCellValueFactory(new PropertyValueFactory<Conseil,String>("demande"));
        colreponse.setCellValueFactory(new PropertyValueFactory<Conseil,String>("reponse"));
        colnote.setCellValueFactory(new PropertyValueFactory<Conseil,Integer>("note"));
        coldate.setCellValueFactory(new PropertyValueFactory<Conseil, Date>("date_conseil"));
        statusComboBox.getSelectionModel().clearSelection();

        // Calculate the average note value
        int totalNote = 0;
        for (Conseil conseil : list) {
            totalNote += conseil.getNote();
        }
        double averageNote = (double) totalNote / list.size();
        // Format the average note value to have only two decimal places
        String formattedAverageNote = String.format("%.2f", averageNote);
        // Set the formatted average note value to the countMoy label
        countMoy.setText(formattedAverageNote + "/5");

        // Count the number of rows with status "en attente"
        long nb = list.stream().filter(conseil -> conseil.getStatut().equalsIgnoreCase("en attente")).count();
        // Set the count to the label
        countEnAttente.setText(String.valueOf(nb));
        // Count the number of rows with status "terminé"
        long nbt = list.stream().filter(conseil -> conseil.getStatut().equalsIgnoreCase("terminé")).count();

        // Set the count to the label
        countTerminés.setText(String.valueOf(nbt));
        // Update the count label
        countAll.setText(String.valueOf(list.size()));

        updatePieChart();
        populateStackedBarChart();
    }

    @FXML
    void deleteConseil(ActionEvent event) {
        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Etes-vous sûr de vouloir supprimer ce conseil?");
        // Show the alert and wait for the user's response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User clicked OK, proceed with delete
                String delete = "DELETE FROM conseil WHERE id_conseil = ?";
                con = MyConnection.instance.getCnx();
                try {
                    st = con.prepareStatement(delete);
                    st.setInt(1, id_conseil);
                    st.executeUpdate();
                    showConseils();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void getData(MouseEvent event) {
        if (event.getClickCount() == 2) { // Check for double-click
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
        if (event.getClickCount() == 1) { // Check for single-click
            Conseil selectedConseil = table.getSelectionModel().getSelectedItem();
            if (selectedConseil != null) {
                tDemande.setText(String.valueOf(selectedConseil.getDemande()));
                tReponse.setText(String.valueOf(selectedConseil.getReponse()));
            }
        }
    }

    public void sendSMS(String to, String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new PhoneNumber("+21651600246"),
                        new PhoneNumber("+13613103097"),
                        body)
                .create();
    }

    @FXML
    void updateConseil(ActionEvent event) {
        // Create an alert dialog for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir mettre à jour ce conseil?");
        // Show the alert and wait for the user's response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User clicked OK, proceed with update
                String update = "UPDATE conseil SET reponse = ?, statut = 'terminé' WHERE id_conseil = ?";
                con = MyConnection.instance.getCnx();
                try {
                    st = con.prepareStatement(update);
                    st.setString(1, tReponse.getText());
                    st.setInt(2, id_conseil);
                    st.executeUpdate();
                    sendSMS("+21651600246", "Un conseil a été mis à jour avec succès");
                    showConseils();
                    clear();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
