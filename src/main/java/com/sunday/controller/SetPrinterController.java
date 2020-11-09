package com.sunday.controller;

import com.sunday.model.Printer;
import com.sunday.repository.PrinterRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class SetPrinterController implements Initializable {

    private final PrinterRepository printerRepository;

    @FXML
    private Button setPrinter;
    @FXML
    private ListView<String> listPrinter;
    @FXML
    private Button exit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exit.setCancelButton(true);
    }

    public void setPrinterList(List<String> p) {
        var po = FXCollections.observableArrayList(p);
        listPrinter.setItems(po);
        setPrinter.setOnAction(e -> {
            printerRepository.deleteAll();
            var printer = new Printer();
            printer.setPrinterName(listPrinter.getSelectionModel().getSelectedItem());
            printerRepository.save(printer);
            exit();
        });
        exit.setOnAction(e -> exit());
    }

    private void exit() {
        ((Stage) exit.getScene().getWindow()).close();
    }
}
