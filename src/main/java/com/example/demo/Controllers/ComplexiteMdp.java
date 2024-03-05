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
        //longueur  > 8
        if (password.length() >= 8) {
            this.nb += 2;
        }

        // 1 lettre majus
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Matcher upperCaseMatcher = upperCasePattern.matcher(password);
        if (upperCaseMatcher.find()) {
            this.nb += 3;
        }

        // 1 lettre min
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Matcher lowerCaseMatcher = lowerCasePattern.matcher(password);
        if (lowerCaseMatcher.find()) {
            this.nb += 3;
        }

        // 1 caractère spécial
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (specialCharMatcher.find()) {
            this.nb += 4;
        }
    }
}
