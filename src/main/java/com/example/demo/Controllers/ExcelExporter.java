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
            createCell(idRow, 0, "Id stock:",null);
            createCell(idRow, 1, String.valueOf(stock.getId_s()),null);

            Row nomRow = sheet.createRow(2);
            createCell(nomRow, 0, "Nom:",null);
            createCell(nomRow, 1, stock.getNom(),null);

            Row refProduitRow = sheet.createRow(3);
            createCell(refProduitRow, 0, "Référence Produit:",null);
            createCell(refProduitRow, 1, String.valueOf(stock.getProduitRef()),null);

            Row marqueRow = sheet.createRow(4);
            createCell(marqueRow, 0, "Marque:",null);
            createCell(marqueRow, 1, stock.getProduitMarque(),null);

            Row quantiteRow = sheet.createRow(5);
            createCell(quantiteRow, 0, "Quantité:",null);
            createCell(quantiteRow, 1, String.valueOf(stock.getQuantite()),null);

            Row nbVenduRow = sheet.createRow(6);
            createCell(nbVenduRow, 0, "Nombre vendus:",null);
            createCell(nbVenduRow, 1, String.valueOf(stock.getNbVendu()),null);

            Row coutTotalRow = sheet.createRow(7);
            createCell(coutTotalRow, 0, "Cout total:",null);
            createCell(coutTotalRow, 1, String.valueOf(stock.getCout()),null);

            // Enregistrer le fichier Excel
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

            System.out.println("Export Excel réussi.");
            openFile(fileName);
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


    public static boolean exportAllStocksToExcel(String fileName, ObservableList<Stock> allStocks) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock Report");

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 10);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            // Style pour les en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.cloneStyleFrom(boldStyle);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Ajouter le titre avec le style CSS
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(6);
            titleCell.setCellValue("Rapport de stock");
            titleCell.setCellStyle(boldStyle);

            // Créer un tableau avec 7 colonnes (Id stock, Nom stock, Référence Produit, Marque, Quantité, NbVendu, Cout total)
            Row headerRow = sheet.createRow(1);
            String[] headers = {"Id stock", "Nom stock","Référence", "Marque", "Quantité", "Nombre vendus ", "Cout total"};
            for (int i = 0; i < headers.length; i++) {
                Cell headerCell = headerRow.createCell(i * 2);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(headerStyle);

                // Fusionner les deux colonnes pour chaque mot de l'en-tête
                sheet.addMergedRegion(new CellRangeAddress(1, 1, i * 2, i * 2 + 1));
            }

            // Ajouter les données de chaque stock au tableau
            for (int rowIndex = 0; rowIndex < allStocks.size(); rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex + 2); // Commencer à partir de la troisième ligne
                Stock stock = allStocks.get(rowIndex);

                createCell(dataRow, 0, String.valueOf(stock.getId_s()), null);
                createCell(dataRow, 2, stock.getNom(), null);
                createCell(dataRow, 4, String.valueOf(stock.getProduitRef()), null);
                createCell(dataRow, 6, stock.getProduitMarque(), null);
                createCell(dataRow, 8, String.valueOf(stock.getQuantite()), null);
                createCell(dataRow, 10, String.valueOf(stock.getNbVendu()), null);
                createCell(dataRow, 12, String.valueOf(stock.getCout()), null);
            }

            // Enregistrer le fichier Excel
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

            System.out.println("Export Excel réussi.");
            openFile(fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export Excel.");
            return false;
        }
    }
    private static void openFile(String fileName) {
        try {
            File file = new File(fileName);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Impossible d'ouvrir le fichier. Veuillez le faire manuellement.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du fichier Excel.");
        }
    }
}
