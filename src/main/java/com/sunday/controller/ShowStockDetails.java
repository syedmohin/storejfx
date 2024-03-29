package com.sunday.controller;

import com.sun.javafx.print.Units;
import com.sunday.model.Stock;
import com.sunday.service.PrinterService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.print.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.print.PageOrientation.PORTRAIT;
import static javafx.print.Paper.A4;
import static javafx.print.Paper.MONARCH_ENVELOPE;
import static javafx.print.Printer.MarginType.DEFAULT;

@Component
@RequiredArgsConstructor
public class ShowStockDetails implements Initializable {

    private final PrinterService printerService;


    @FXML
    private Label vehicleNo;
    @FXML
    private Label totalAmount;
    @FXML
    private Label balance;
    @FXML
    private Label rate;
    @FXML
    private Label weight;
    @FXML
    private Label paidAmount;
    @FXML
    private TableView<StockModifiedAmountObservable> stockTable;
    @FXML
    private Button exit;
    @FXML
    private Button print;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exit.setOnAction(e -> ((Stage) exit.getScene().getWindow()).close());
    }

    public void setStock(Stock stock) {
        exit.setCancelButton(true);
        vehicleNo.setText(stock.getVehicleNo());
        totalAmount.setText(stock.getTotalAmount() + "");
        balance.setText(stock.getBalance() + "");
        paidAmount.setText(stock.getTotalAmount() - stock.getBalance() + "");
        rate.setText(stock.getRate() + "");
        weight.setText(stock.getWeight() + "");

        var sma = stock.getStockModifiedAmount();

        var date = new TableColumn<StockModifiedAmountObservable, String>("Date");
        date.setCellValueFactory(p -> p.getValue().date);

        var paid = new TableColumn<StockModifiedAmountObservable, Integer>("Amount Paid");
        paid.setCellValueFactory(p -> p.getValue().paid.asObject());
        ObservableList<StockModifiedAmountObservable> ob = FXCollections.observableArrayList();
        sma.forEach(s -> ob.add(new StockModifiedAmountObservable(s.getPaidAmount(), s.getModifieddate())));

        stockTable.setEditable(true);
        stockTable.getColumns().addAll(paid, date);
        stockTable.setItems(ob);
        print.setOnAction(e -> {
            var stockData = printerService.printStock(stock);
            var job = PrinterJob.createPrinterJob();
            var printer = Printer.defaultPrinterProperty().get();
            printer.createPageLayout(Paper.A6, PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            job.setPrinter(printer);
            var we = new WebView().getEngine();
            we.loadContent(stockData);
            we.print(job);
            job.endJob();
        });

    }

    private static class StockModifiedAmountObservable {
        final IntegerProperty paid;
        final StringProperty date;

        public StockModifiedAmountObservable(Integer paid, LocalDate date) {
            this.paid = new SimpleIntegerProperty(paid);
            this.date = new SimpleStringProperty(date.format(formatter));
        }
    }
}
