package com.example.demo.Controllers;

import com.example.demo.Models.Stock;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import javafx.collections.ObservableList;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import java.awt.Desktop;
import java.io.*;
import java.util.List;

import com.itextpdf.tool.xml.XMLWorkerHelper;
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
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));

        document.open();
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        // Ajouter le titre avec le style CSS
        Paragraph title = new Paragraph("All Stocks Report",boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);  // Ajouter un espace après le titre

        document.add(title);

        // Créer un tableau avec 7 colonnes (Id stock, Nom stock, Référence Produit, Marque, Quantité, NbVendu, cout total)
        PdfPTable table = new PdfPTable(7);

        // Ajouter les entêtes de colonne au tableau avec le style CSS
        String[] headers = {"Id stock", "Nom stock", "Référence Produit", "Marque", "Quantité", "NbVendu", "Cout total"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(new BaseColor(136, 196, 109));  // Couleur de fond

            headerCell.setMinimumHeight(40);
            table.addCell(headerCell);
        }

        // Ajouter les données de chaque stock au tableau
        for (Stock stock : allStocks) {
            table.addCell(createCell(String.valueOf(stock.getId_s())));
            table.addCell(createCell(stock.getNom()));
            table.addCell(createCell(String.valueOf(stock.getProduitRef())));
            table.addCell(createCell(stock.getProduitMarque()));
            table.addCell(createCell(String.valueOf(stock.getQuantite())));
            table.addCell(createCell(String.valueOf(stock.getNbVendu())));
            table.addCell(createCell(String.valueOf(stock.getCout())));
        }

        // Ajouter le tableau au document PDF
        document.add(table);

        document.close();

        // Ouvrir le fichier PDF avec l'application par défaut
        openPDFFile(fileName);

        return true; // Indicateur de succès
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
        System.err.println("Erreur lors de l'export PDF.");
        return false; // Indicateur d'échec
    }
}

    // Fonction utilitaire pour créer une cellule avec style CSS
    private static PdfPCell createCell(String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        return cell;
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
