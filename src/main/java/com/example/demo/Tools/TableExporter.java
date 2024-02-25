package com.example.demo.Tools;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class TableExporter {
    public static void exportToExcel(TableView table, String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();

        // Add header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < table.getColumns().size(); i++) {
            TableColumn column = (TableColumn) table.getColumns().get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(column.getText());
        }

        // Add data rows
        for (int rowIndex = 0; rowIndex < table.getItems().size(); rowIndex++) {
            Row dataRow = sheet.createRow(rowIndex + 1);
            for (int columnIndex = 0; columnIndex < table.getColumns().size(); columnIndex++) {
                TableColumn column = (TableColumn) table.getColumns().get(columnIndex);
                Cell cell = dataRow.createCell(columnIndex);
                cell.setCellValue(column.getCellData(rowIndex).toString());
            }
        }

        // Save the workbook to a file
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}
