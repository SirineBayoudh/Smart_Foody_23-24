package com.example.demo.Controllers;

import java.util.List;

public interface ICrud<T>{
    public void ajouterEntite(T t);

    public void modifierEntite(T t);
    public void supprimerEntite(T t);
    public List<T> afficherEntite();
}
