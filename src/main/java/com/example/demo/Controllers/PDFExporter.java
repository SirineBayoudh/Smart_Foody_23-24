package com.example.demo.Controllers;

import com.example.demo.Models.Stock;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.ObservableList;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
public class PDFExporter {

    public static boolean exportStockToPDF(String fileName, Stock stock) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Stock Report"));
            document.add(new Paragraph("Référence Produit: " + stock.getProduitRef()));
            document.add(new Paragraph("Marque: " + stock.getProduitMarque()));
            document.add(new Paragraph("Quantité: " + stock.getQuantite()));

            document.close();

            System.out.println("Export PDF réussi.");

            // Ouvrir le fichier PDF avec l'application par défaut
            openPDFFile(fileName);

            return true; // Indicateur de succès
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export PDF.");
            return false; // Indicateur d'échec
        }
    }

    public static boolean exportAllStocksToPDF(String fileName, ObservableList<Stock> allStocks) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("All Stocks Report"));

            // Créer un tableau avec 4 colonnes (Référence Produit, Marque, Quantité, NbVendu)
            PdfPTable table = new PdfPTable(4);

            // Ajouter les en-têtes de colonne au tableau
            table.addCell("Référence Produit");
            table.addCell("Marque");
            table.addCell("Quantité");
            table.addCell("NbVendu");

            for (Stock stock : allStocks) {
                // Ajouter les données de chaque stock au tableau
                table.addCell(String.valueOf(stock.getProduitRef()));
                table.addCell(stock.getProduitMarque());
                table.addCell(String.valueOf(stock.getQuantite()));
                table.addCell(String.valueOf(stock.getNbVendu()));
            }

            // Ajouter le tableau au document PDF
            document.add(table);

            document.close();

            System.out.println("Export PDF réussi.");

            // Ouvrir le fichier PDF avec l'application par défaut
            openPDFFile(fileName);

            return true; // Indicateur de succès
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export PDF.");
            return false; // Indicateur d'échec
        }
    }

    private static void openPDFFile(String fileName) {
        try {
            File file = new File(fileName);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Impossible d'ouvrir le fichier. Veuillez le faire manuellement.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du fichier PDF.");
        }
    }

}
