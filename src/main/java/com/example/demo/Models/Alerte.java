package com.example.demo.Models;

import com.example.demo.Tools.MyConnection;

import java.time.LocalDateTime;
import java.util.Date;

public class Alerte {
    private int id_alerte;
    private int id_stock;
    private String description_alerte;
    private Date date;
  private Boolean Type ;

    private String typeString;
    public Alerte(int id_alerte, int id_stock, Date date, Boolean type) {
        this.id_alerte = id_alerte;
        this.id_stock = id_stock;
        this.date = date;
        this.Type = type;
    }
    public Alerte(int id_alerte, int id_stock, Date date) {
        this.id_alerte = id_alerte;
        this.id_stock = id_stock;
        this.date = date;
    }

    public Alerte(int id_alerte, int id_stock, String description_alerte, Date date,Boolean type) {
        this.id_alerte = id_alerte;
        this.id_stock = id_stock;
        this.description_alerte = description_alerte;
        this.date = date;
        this.Type = type;
        this.typeString = (type != null && type) ? "lue" : "non lue";
    }

    public Alerte(int id_stock, Date date) {
        this.id_stock = id_stock;
        this.date = date;
    }

    public int getId_alerte() {
        return id_alerte;
    }

    public void setId_alerte(int id_alerte) {
        this.id_alerte = id_alerte;
    }

    public int getId_stock() {
        return id_stock;
    }

    public void setId_stock(int id_stock) {
        this.id_stock = id_stock;
    }

    public String getDescription_alerte() {
        return description_alerte;
    }

    public void setDescription_alerte(String description_alerte) {
        this.description_alerte = description_alerte;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }
    public Boolean isType() {
        return Type;
    }

    public void setType(Boolean type) {
        Type = type;
    }

}
