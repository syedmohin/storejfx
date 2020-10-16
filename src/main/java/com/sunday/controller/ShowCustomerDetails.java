package com.sunday.controller;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.repository.PrinterRepository;
import com.sunday.service.PrinterService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageOrientation;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class ShowCustomerDetails implements Initializable {
    private final PrinterService printerService;
    private final PrinterRepository printerRepository;

    @FXML
    private Label customerName;
    @FXML
    private Label pendingCrate;
    @FXML
    private Label balance;
    @FXML
    private Label rate;
    @FXML
    private Label totalAMount;
    @FXML
    private Label customerId;
    @FXML
    private Label weight;
    @FXML
    private Label crate;
    @FXML
    private Label paidAmount;
    @FXML
    private TableView<CustomerModifiedObservable> customerModifiedTable;
    @FXML
    private Button printBill;
    @FXML
    private Button exit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exit.setCancelButton(true);
        exit.setOnAction(e -> ((Stage) exit.getScene().getWindow()).close());
    }

    public void setCustomer(Customer c) {
        customerModifiedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        var paidCol = new TableColumn<CustomerModifiedObservable, Integer>("Amount Paid");

        var dateCol = new TableColumn<CustomerModifiedObservable, String>("Date");

        paidCol.setCellValueFactory(p -> p.getValue().paid.asObject());
        dateCol.setCellValueFactory(p -> p.getValue().date);
        if (c != null) {
            customerName.setText(c.getCustomerName());
            weight.setText(c.getWeight() + "");
            crate.setText(c.getCrate() + "");
            var paid = c.getCustomerModifiedAmount()
                    .stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            paidAmount.setText(paid + "");
            customerId.setText(c.getCustomerId());
            pendingCrate.setText(c.getCrate() - c.getReturnedCrate() + "");
            totalAMount.setText(c.getTotalAmount() + "");
            rate.setText(c.getRate() + "");
            balance.setText(c.getBalance() + "");


            ObservableList<CustomerModifiedObservable> ob = FXCollections.observableArrayList();
            var cma = c.getCustomerModifiedAmount();
            if (!cma.isEmpty()) {
                cma.forEach(a -> ob.add(new CustomerModifiedObservable(a.getPaidAmount(), a.getModifiedDate())));
            }
            customerModifiedTable.setItems(ob);
            customerModifiedTable.getColumns().add(paidCol);
            customerModifiedTable.getColumns().add(dateCol);
        } else {
            System.out.println("null");
        }
        printBill.setOnAction(ae -> {
            String customerData = null;
            if (c != null) {
                customerData = printerService.printCustomer(c);
            }
            var job = PrinterJob.createPrinterJob();
            var p = Printer.getAllPrinters();
            Printer selectedPrinter = null;
            Paper paper = PrintHelper.createPaper("AFC", 80, 1000, Units.MM);
            for (Printer pt : p) {
                if (pt.getName().equals(getPrinterFromDB())) {
                    selectedPrinter = pt;
                    selectedPrinter.createPageLayout(paper, PageOrientation.PORTRAIT, 0.08f, 0.08f, 0.08f, 0.08f);
                }
            }
            job.setPrinter(selectedPrinter);
            var wv = new WebView();
            var we = wv.getEngine();
            we.loadContent(customerData);
            we.print(job);
            job.endJob();
        });
    }

    private String getPrinterFromDB() {
        var list = new ArrayList<com.sunday.model.Printer>();
        var it = printerRepository.findAll();
        it.forEach(list::add);
        return list.get(0).getPrinterName();
    }

    private static class CustomerModifiedObservable {
        final IntegerProperty paid;
        final StringProperty date;

        public CustomerModifiedObservable(Integer paid, LocalDate date) {
            this.paid = new SimpleIntegerProperty(paid);
            this.date = new SimpleStringProperty(date.toString());
        }
    }
}
