package com.example.demo.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private List<LanguageObserver> observers = new ArrayList<>();
    private Locale currentLocale;  // Added field to store the current locale

    private LanguageManager() {
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void initialize(Locale locale) {
        currentLocale = locale;  // Update the current locale
        resourceBundle = ResourceBundle.getBundle("labels", currentLocale);
        notifyObservers();
    }

    public String getText(String key) {
        if (resourceBundle == null) {
            throw new IllegalStateException("LanguageManager not initialized");
        }
        return resourceBundle.getString(key);
    }

    public void addObserver(LanguageObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LanguageObserver observer) {
        observers.remove(observer);
    }
    private Locale selectedLocale;
    public void saveSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }


    private void notifyObservers() {
        for (LanguageObserver observer : observers) {
            observer.onLanguageChanged();
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
