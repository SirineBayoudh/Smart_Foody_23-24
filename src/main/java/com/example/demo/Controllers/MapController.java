package com.example.demo.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

public class MapController {

    @FXML
    private StackPane mapPane;

    private JXMapViewer mapViewer;

    public void initialize() {
        // Créer une instance de JXMapViewer
        mapViewer = new JXMapViewer();

        // Définir la position et le zoom initiaux
        GeoPosition initialPosition = new GeoPosition(48.8583, 2.2944); // Latitude et longitude de Paris
        mapViewer.setZoom(10);
        mapViewer.setAddressLocation(initialPosition);

        // Ajouter la carte au conteneur
       // mapPane.getChildren().add(mapViewer);

        // Ajuster la taille de la carte pour remplir le conteneur
       // mapPane.widthProperty().addListener((observable, oldValue, newValue) -> mapViewer.setPreferredSize(new Dimension(newValue.intValue(), mapPane.getHeight())));
        //mapPane.heightProperty().addListener((observable, oldValue, newValue) -> mapViewer.setPreferredSize(new Dimension(mapPane.getWidth(), newValue.intValue())));
    }
}
