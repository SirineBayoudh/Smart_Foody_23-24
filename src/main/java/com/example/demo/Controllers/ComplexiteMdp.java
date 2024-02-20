package com.example.demo.Controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexiteMdp {

    private int nb = 0 ;

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public void Calcul(String password) {
        // Vérifier si la longueur du mot de passe est > 8
        if (password.length() >= 8) {
            this.nb += 2;
        }

        // Vérifier si le mot de passe contient au moins une lettre majuscule
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Matcher upperCaseMatcher = upperCasePattern.matcher(password);
        if (upperCaseMatcher.find()) {
            this.nb += 3;
        }

        // Vérifier si le mot de passe contient au moins une lettre miniscule
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Matcher lowerCaseMatcher = lowerCasePattern.matcher(password);
        if (lowerCaseMatcher.find()) {
            this.nb += 3;
        }

        // Vérifier si le mot de passe contient au moins un caractère spécial
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (specialCharMatcher.find()) {
            this.nb += 4;
        }
    }
}
