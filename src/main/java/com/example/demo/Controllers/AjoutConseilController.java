package com.example.demo.Controllers;

import com.example.demo.Tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjoutConseilController implements Initializable {

    @FXML
    private TextArea demandeLabel;
    Connection con = null;
    PreparedStatement st= null;

    @FXML
    void ajoutConseil(ActionEvent event) {
        try {
            // Check if any of the input fields are empty
            if (demandeLabel.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Please fill in all the fields.");
                return; // Stop execution
            }

            String insert = "INSERT INTO conseil(id_conseil, id_client, matricule, statut, demande, reponse, date_conseil) VALUES (?, ?, ?, ?, ?, ?, ?)";
            con = MyConnection.instance.getCnx();
            st = con.prepareStatement(insert);
            st.setString(1, String.valueOf(9998));
            st.setString(2, String.valueOf(123123123));
            st.setInt(3, 321312312);
            st.setString(4, "en attente");
            st.setString(5, demandeLabel.getText());
            st.setString(6, "");
            LocalDate currentDate = LocalDate.now();
            Date date = Date.valueOf(currentDate);
            st.setObject(7, date);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Conseil added successfully.");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to add conseil.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Invalid input");
        } catch (IllegalArgumentException e) {
            showAlert(AlertType.ERROR, "Error", "Invalid date format.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred: Failed to add conseil.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyConnection.getInstance();
        //noteChoiceBox.getSelectionModel().selectFirst();
    }
}
