package com.sunday.service;

import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.model.StockModifiedAmount;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelFileService {
    private final CustomerService customerService;
    private final StockService stockService;

    public void generateExcelOfTodayCustomer(String dir) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(LocalDate.now().toString());
        var customerList = customerService.getAllData();

        var rowCount = 0;
        var headerRow = sheet.createRow(0);
        sheet.getPrintSetup().setLandscape(true);
        headerRow.createCell(0).setCellValue("Transcation ID");
        headerRow.createCell(1).setCellValue("Customer Name");
        headerRow.createCell(2).setCellValue("Total Amount");
        headerRow.createCell(3).setCellValue("Balance");
        headerRow.createCell(4).setCellValue("Paid Amount");
        headerRow.createCell(5).setCellValue("Weight");
        headerRow.createCell(6).setCellValue("Rate");
        headerRow.createCell(7).setCellValue("Crate");
        headerRow.createCell(8).setCellValue("Pending Crate");

        var date = headerRow.createCell(9);
        date.setCellValue("Date");

        var pt = new PropertyTemplate();
        pt.drawBorders(new CellRangeAddress(0, customerList.size(), 0, 9),
                BorderStyle.MEDIUM, BorderExtent.ALL);

        for (Customer c : customerList) {
            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(c.getCustomerId());
            row.createCell(1).setCellValue(c.getCustomerName());
            row.createCell(2).setCellValue(c.getTotalAmount());
            row.createCell(3).setCellValue(c.getBalance());
            var p = c.getCustomerModifiedAmount().stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            row.createCell(4).setCellValue(p);
            row.createCell(5).setCellValue(c.getWeight());
            row.createCell(6).setCellValue(c.getRate());
            row.createCell(7).setCellValue(c.getCrate());
            row.createCell(8).setCellValue(c.getCrate() - c.getReturnedCrate());
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            row.createCell(9).setCellValue(formatter.format(c.getDate()));
        }
        for (var i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }
        pt.applyBorders(sheet);
        var file = new File(dir + "\\" + LocalDate.now().toString() + "-customer.xlsx");
        try (var outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateExcelOfTodayStock(String dir) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(LocalDate.now().toString());
        var customerList = stockService.getAllData();

        var rowCount = 0;
        var headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Transcation ID");
        headerRow.createCell(1).setCellValue("Vehicle Number");
        headerRow.createCell(2).setCellValue("Total Amount");
        headerRow.createCell(3).setCellValue("Balance");
        headerRow.createCell(4).setCellValue("Paid Amount");
        headerRow.createCell(5).setCellValue("Weight");
        headerRow.createCell(6).setCellValue("Rate");
        headerRow.createCell(7).setCellValue("Date");

        var pt = new PropertyTemplate();
        pt.drawBorders(new CellRangeAddress(0, customerList.size(), 0, 7),
                BorderStyle.MEDIUM, BorderExtent.ALL);

        for (var c : customerList) {
            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(c.getStockId());
            row.createCell(1).setCellValue(c.getVehicleNo());
            row.createCell(2).setCellValue(c.getTotalAmount());
            row.createCell(3).setCellValue(c.getBalance());
            var p = c.getStockModifiedAmount().stream()
                    .mapToInt(StockModifiedAmount::getPaidAmount)
                    .sum();
            row.createCell(4).setCellValue(p);
            row.createCell(5).setCellValue(c.getWeight());
            row.createCell(6).setCellValue(c.getRate());
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            row.createCell(7).setCellValue(formatter.format(c.getDate()));
        }
        for (var i = 0; i <= 7; i++) {
            sheet.autoSizeColumn(i);
        }
        var file = new File(dir + "\\" + LocalDate.now().toString() + "-stock.xlsx");
        pt.applyBorders(sheet);
        try (var outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateExcelOfUnpaidCustomer(String dir) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(LocalDate.now().toString());
        var customer = customerService.getAllData();
        var customerList = customer.stream()
                .filter(s -> s.getBalance() > 0)
                .collect(Collectors.toList());
        System.out.println(customerList);

        var rowCount = 0;
        var headerRow = sheet.createRow(0);
        sheet.getPrintSetup().setLandscape(true);
        headerRow.createCell(0).setCellValue("Transcation ID");
        headerRow.createCell(1).setCellValue("Customer Name");
        headerRow.createCell(2).setCellValue("Total Amount");
        headerRow.createCell(3).setCellValue("Balance");
        headerRow.createCell(4).setCellValue("Paid Amount");
        headerRow.createCell(5).setCellValue("Weight");
        headerRow.createCell(6).setCellValue("Rate");
        headerRow.createCell(7).setCellValue("Crate");
        headerRow.createCell(8).setCellValue("Pending Crate");
        headerRow.createCell(9).setCellValue("Date");

        var pt = new PropertyTemplate();
        pt.drawBorders(new CellRangeAddress(0, customerList.size(), 0, 9),
                BorderStyle.MEDIUM, BorderExtent.ALL);

        for (Customer c : customerList) {
            Row row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(c.getCustomerId());
            row.createCell(1).setCellValue(c.getCustomerName());
            row.createCell(2).setCellValue(c.getTotalAmount());
            row.createCell(3).setCellValue(c.getBalance());
            var p = c.getCustomerModifiedAmount().stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            row.createCell(4).setCellValue(p);
            row.createCell(5).setCellValue(c.getWeight());
            row.createCell(6).setCellValue(c.getRate());
            row.createCell(7).setCellValue(c.getCrate());
            row.createCell(8).setCellValue(c.getCrate() - c.getReturnedCrate());
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            row.createCell(9).setCellValue(formatter.format(c.getDate()));
        }
        for (var i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }
        pt.applyBorders(sheet);
        File file = new File(dir + "\\" + LocalDate.now().toString() + "-unpaid customer.xlsx");
        try (var outputStream = new FileOutputStream(dir + "\\" + LocalDate.now().toString() + "-unpaid customer.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
