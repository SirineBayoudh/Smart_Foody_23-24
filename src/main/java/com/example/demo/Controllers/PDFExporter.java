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


            return true; // Indicateur de succès
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export PDF.");
            return false; // Indicateur d'échec
        }
    }

}
