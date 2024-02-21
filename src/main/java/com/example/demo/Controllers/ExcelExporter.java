package com.example.demo.Controllers;

import com.example.demo.Models.Stock;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static boolean exportStockToExcel(String fileName, Stock stock) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock Report");

            // Créer la première ligne pour le titre
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Rapport sur le stock " + stock.getNom());

            // Créer les lignes pour les données
            Row idRow = sheet.createRow(1);
            createCell(idRow, 0, "Id stock:", null);
            createCell(idRow, 1, String.valueOf(stock.getId_s()), null);

            Row nomRow = sheet.createRow(2);
            createCell(nomRow, 0, "Nom:", null);
            createCell(nomRow, 1, stock.getNom(), null);

            Row refProduitRow = sheet.createRow(3);
            createCell(refProduitRow, 0, "Référence Produit:", null);
            createCell(refProduitRow, 1, String.valueOf(stock.getProduitRef()), null);

            Row marqueRow = sheet.createRow(4);
            createCell(marqueRow, 0, "Marque:", null);
            createCell(marqueRow, 1, stock.getProduitMarque(), null);

            Row quantiteRow = sheet.createRow(5);
            createCell(quantiteRow, 0, "Quantité:", null);
            createCell(quantiteRow, 1, String.valueOf(stock.getQuantite()), null);

            Row nbVenduRow = sheet.createRow(6);
            createCell(nbVenduRow, 0, "Nombre vendus:", null);
            createCell(nbVenduRow, 1, String.valueOf(stock.getNbVendu()), null);

            Row coutTotalRow = sheet.createRow(7);
            createCell(coutTotalRow, 0, "Cout total:", null);
            createCell(coutTotalRow, 1, String.valueOf(stock.getCout()), null);

            // Enregistrer le fichier Excel
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

            System.out.println("Export Excel réussi.");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export Excel.");
            return false;
        }
    }

    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);

        if (style != null) {
            cell.setCellStyle(style);
        }
    }

}