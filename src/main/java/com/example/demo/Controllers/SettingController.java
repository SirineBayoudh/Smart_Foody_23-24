package com.example.demo.Controllers;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.lang.reflect.Field;
import java.util.Locale;

public class SettingController {

    @FXML
    private RadioButton englishRadioButton;
    @FXML
    private AnchorPane clickpane;
    @FXML
    private RadioButton frenchRadioButton;

    private VBox languageVBox;
    private LanguageManager languageManager = LanguageManager.getInstance();
    @FXML
    private void initialize() {
        languageVBox = new VBox();

        // Par défaut, chargez les propriétés en anglais
        loadProperties(Locale.FRENCH);
    }

    @FXML
    private void handlePaneClick(MouseEvent event) {
        // Mettez à jour la langue en fonction du bouton radio actuellement sélectionné
        if (englishRadioButton.isSelected()) {
            loadProperties(Locale.ENGLISH);
        } else if (frenchRadioButton.isSelected()) {
            loadProperties(Locale.FRENCH);
        }
    }

    @FXML
    private void onEnglishSelected() {
        // Mettez à jour la langue lorsque le bouton Anglais est sélectionné
        loadProperties(Locale.ENGLISH);
    }

    @FXML
    private void onFrenchSelected() {
        // Mettez à jour la langue lorsque le bouton Français est sélectionné
        loadProperties(Locale.FRENCH);
    }

    private void loadProperties(Locale locale) {
        LanguageManager languageManager = LanguageManager.getInstance();
        languageManager.initialize(locale);

        // Iteration over all fields annotated with @FXML
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class)) {
                try {
                    field.setAccessible(true);
                    Object node = field.get(this);

                    // Update the text if the field is a RadioButton
                    if (node instanceof RadioButton) {
                        ((RadioButton) node).setText(languageManager.getText(field.getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
