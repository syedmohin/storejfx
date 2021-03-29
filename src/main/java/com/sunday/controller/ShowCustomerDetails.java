package com.sunday.controller;

import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.service.PrinterService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

import static javafx.print.PageOrientation.PORTRAIT;
import static javafx.print.Paper.A4;
import static javafx.print.Printer.MarginType.DEFAULT;

@Component
@RequiredArgsConstructor
public class ShowCustomerDetails implements Initializable {
    private final PrinterService printerService;

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
            var printer = Printer.defaultPrinterProperty().get();
            printer.createPageLayout(A4, PORTRAIT, DEFAULT);
            job.setPrinter(printer);
            var we = new WebView().getEngine();
            we.loadContent(customerData);
            we.print(job);
            job.endJob();
        });
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
