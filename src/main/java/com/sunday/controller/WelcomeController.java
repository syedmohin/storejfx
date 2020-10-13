package com.sunday.controller;


import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeInDown;
import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.model.Stock;
import com.sunday.model.StockModifiedAmount;
import com.sunday.repository.PrinterRepository;
import com.sunday.service.CustomerService;
import com.sunday.service.ExcelFileService;
import com.sunday.service.PrinterService;
import com.sunday.service.StockService;
import com.sunday.stage.StageListener;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.RequiredArgsConstructor;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class WelcomeController implements Initializable {

    @Value("classpath:/001.mp3")
    private Resource surah;
    private final ApplicationContext applicationContext;
    private final CustomerService customerService;
    private final StockService stockService;
    private final ExcelFileService excelFileService;
    private final PrinterRepository printerRepository;

    final DirectoryChooser directoryChooser = new DirectoryChooser();

    @Value("classpath:/showcustomerdetails.fxml")
    private Resource showcustomdetails;

    @Value("classpath:/showstockdetails.fxml")
    private Resource showstockdetails;

    @Value("classpath:/printerselection.fxml")
    private Resource printerList;
    @FXML
    private Button submitCustomer;
    @FXML
    private Button submitStock;
    @FXML
    private Button stockBtn;

    @FXML
    private AnchorPane customer;
    @FXML
    private AnchorPane stock;

    /*
     * All TextField
     */
    @FXML
    private TextField weightCustomer;
    @FXML
    private TextField vehicleNoStock;
    @FXML
    private TextField rateCustomer;
    @FXML
    private TextField rateStock;
    @FXML
    private TextField customerName;
    @FXML
    private TextField paidAmountCustomer;
    @FXML
    private TextField crateCustomer;
    private final PrinterService printerService;

    @FXML
    private TextField paidStock;
    @FXML
    private Label totalAmountCustomer;
    @FXML
    private TableView<CustomerObservable> customerTable;
    @FXML
    private TableView<StockObservable> stockTable;
    @FXML
    private TextField weightTonsStock;
    @FXML
    private TextField customerFilter;

    @FXML
    private Button exit;
    @FXML
    private Button minus;
    @FXML
    private AnchorPane main;

    @FXML
    private AnchorPane stockText;
    @FXML
    private Label totalAmountStock;
    @FXML
    private Button excelToday;
    @FXML
    private AnchorPane customerFields;
    @FXML
    private Button printer;

    @FXML
    private Button stockExport;
    @FXML
    private Label time;
    @FXML
    private Button unpaid;


    ObservableList<StockObservable> obStock = FXCollections.observableArrayList();
    ObservableList<CustomerObservable> ob = FXCollections.observableArrayList();

    private double xOffSet = 0;
    private double yOffSet = 0;
    private boolean play = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textBinding();
        moveable();
        setTime();
        unpaid.setOnAction(e -> {
            directoryChooser.setTitle("Select the Folder");
            var f = new File(System.getProperty("user.home") + "/Documents/Store/Customer");
            String dir = f.getAbsolutePath();
            if (!f.mkdirs()) {
                dir = f.getAbsolutePath();
            }
            directoryChooser.setInitialDirectory(new File(dir));
            var d = directoryChooser.showDialog(excelToday.getScene().getWindow());
            if (d != null)
                excelFileService.generateExcelOfUnpaidCustomer(d.getAbsolutePath());
        });
        createCustomerTable();
        createStockTable();
        bindingFields();
        time.setOnMouseClicked(e -> {
            try {
                AudioClip rn = new AudioClip(surah.getURI().toString());
                rn.stop();
                rn.play();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        exit.setOnAction(e -> {
            new FadeInDown(main).play();
            Platform.exit();
        });
        minus.setOnAction(e -> {
            new FadeInDown(main).play();
            ((Stage) minus.getScene().getWindow()).setIconified(true);
        });
        submitCustomer.setOnAction(ae -> {
            try {
                checkingFieldsOfCustomers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        submitStock.setOnAction(ae -> {
            try {
                Platform.runLater(() -> {
                    checkingFieldsOfStock();
                    clearStockField();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        excelToday.setOnAction(e -> {
            directoryChooser.setTitle("Select the Folder");
            var f = new File(System.getProperty("user.home") + "/Documents/Store/Customer");
            String dir = f.getAbsolutePath();
            if (!f.mkdirs()) {
                dir = f.getAbsolutePath();
            }
            directoryChooser.setInitialDirectory(new File(dir));
            var d = directoryChooser.showDialog(excelToday.getScene().getWindow());
            if (d != null)
                excelFileService.generateExcelOfTodayCustomer(d.getAbsolutePath());
        });
        var printToolTip = new Tooltip();
        printToolTip.setText("Select the Printer ");
        printer.setTooltip(printToolTip);
        printer.setOnAction(e -> {
            try {
                var printServices = PrintServiceLookup.lookupPrintServices(null, null);
                var list = Stream.of(printServices)
                        .map(PrintService::getName)
                        .collect(Collectors.toList());
                var stage = new Stage();
                final FXMLLoader fxml = new FXMLLoader(printerList.getURL());
                fxml.setControllerFactory(applicationContext::getBean);
                Parent load = fxml.load();
                SetPrinterController printerController = fxml.getController();
                printerController.setPrinterList(list);
                var scene = new Scene(load);
                stage.setResizable(false);
                stage.setTitle("Select the Printer");
                stage.getIcons().add(new Image("/image/icon.png"));
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) stage.close();
                });
                stage.setScene(scene);
                stage.show();
                new BounceIn(load).play();
            } catch (Exception x) {
                x.printStackTrace();
            }
        });
        stockExport.setOnAction(e -> {
            directoryChooser.setTitle("Select the Folder");
            var f = new File(System.getProperty("user.home") + "/Documents/Store/Stock");
            String dir = f.getAbsolutePath();
            if (!f.mkdirs()) {
                dir = f.getAbsolutePath();
            }
            directoryChooser.setInitialDirectory(new File(dir));
            var d = directoryChooser.showDialog(excelToday.getScene().getWindow());
            if (d != null)
                excelFileService.generateExcelOfTodayStock(d.getAbsolutePath());
        });
    }

    private void textBinding() {
        TextFields.bindAutoCompletion(customerFilter, List.of("CUST"));
        TextFields.bindAutoCompletion(customerName, customerService.getAllCustomerNames());
        TextFields.bindAutoCompletion(vehicleNoStock, stockService.getAllvehicle());
    }


    private void setTime() {
        time.setText("بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِیْمَِلْحَمْدُ لِلّٰهِ رَبِّ الْعٰلَمِیْنَۙالرَّحْمٰنِ الرَّحِیْمِۙمٰلِكِ یَوْمِ الدِّیْنِؕاِیَّاكَ نَعْبُدُ وَ اِیَّاكَ نَسْتَعِیْنُؕاِهْدِنَا الصِّرَاطَ الْمُسْتَقِیْمَۙصِرَاطَ الَّذِیْنَ اَنْعَمْتَ عَلَیْهِمْ ﴰ غَیْرِ الْمَغْضُوْبِ عَلَیْهِمْ وَ لَا الضَّآلِّیْنَ۠");
    }

    private void bindingFields() {
        var weightBind = new SimpleIntegerProperty();
        var rateBind = new SimpleIntegerProperty();
        var totalBind = new SimpleIntegerProperty();
        totalBind.bind(weightBind.multiply(rateBind));
        StringConverter<? extends Number> converter = new IntegerStringConverter();
        Bindings.bindBidirectional(weightCustomer.textProperty(), weightBind, (StringConverter<Number>) converter);
        Bindings.bindBidirectional(rateCustomer.textProperty(), rateBind, (StringConverter<Number>) converter);
        totalAmountCustomer.textProperty().bind(totalBind.asString());

        var weightStockBind = new SimpleIntegerProperty();
        var rateStockBind = new SimpleIntegerProperty();
        var totalStockBind = new SimpleIntegerProperty();
        totalStockBind.bind(weightStockBind.multiply(rateStockBind));
        StringConverter<? extends Number> converterStock = new IntegerStringConverter();
        Bindings.bindBidirectional(weightTonsStock.textProperty(), weightStockBind, (StringConverter<Number>) converterStock);
        Bindings.bindBidirectional(rateStock.textProperty(), rateStockBind, (StringConverter<Number>) converterStock);
        totalAmountStock.textProperty().bind(totalStockBind.asString());
    }

    private void alertMe(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Form Validation");
        alert.setContentText(msg);
        alert.setResizable(false);
        alert.showAndWait();
    }

    private void alertSuccess(String msg, Alert.AlertType type, String header) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.setResizable(false);
        alert.showAndWait();
    }

    private void checkingFieldsOfStock() {
        var total = 0;
        var valid = false;
        var stock = new Stock();
        var sma = new StockModifiedAmount();
        if (vehicleNoStock.getText().trim().isEmpty()) {
            alertMe("You need to fill the Vehicle Number ");
            valid = false;
        } else {
            stock.setVehicleNo(vehicleNoStock.getText().toUpperCase().trim());
            valid = true;
        }
        if (weightTonsStock.getText().trim().isEmpty()) {
            alertMe("You need to fill the weight field");
        } else {
            try {
                var w1 = Integer.parseInt(weightTonsStock.getText().trim());
                stock.setWeight(w1);
                total = w1;
            } catch (Exception e) {
                alertMe("Weight must be Number Only");
            }
        }
        if (rateStock.getText().trim().isEmpty()) {
            alertMe("You need to fill the Rate field");
        } else {
            try {
                var r1 = Integer.parseInt(rateStock.getText().trim());
                stock.setRate(r1);
                total = total * r1;
                stock.setTotalAmount(total);
            } catch (Exception e) {
                alertMe("Rate must be Number only");
            }
        }
        if (paidStock.getText().trim().isEmpty() || Integer.parseInt(paidStock.getText()) == 0) {
            stock.setBalance(total);
        } else {
            try {
                var p1 = Integer.parseInt(paidStock.getText().trim());
                if (p1 == 0) {
                    stock.setBalance(total);
                    sma.setPaidAmount(p1);
                    sma.setModifieddate(LocalDate.now());
                }

                if (p1 == total) {
                    stock.setBalance(0);
                    sma.setPaidAmount(p1);
                    sma.setModifieddate(LocalDate.now());
                }
                if (total > p1) {
                    stock.setBalance(total - p1);
                    sma.setPaidAmount(p1);
                    sma.setModifieddate(LocalDate.now());
                }
            } catch (Exception e) {
                alertMe("Amount must be Number Only");
            }
        }
        if (valid) {
            stock.getStockModifiedAmount().add(sma);
            var st = stockService.insertData(stock);
            stockTable.getItems().add(new StockObservable(st.getStockId(), st.getVehicleNo(), st.getWeight(), st.getRate(), st.getTotalAmount(), st.getBalance(), st.getDate()));
            filterTable();
            clearStockField();
        } else {
            System.out.println("Fill the fields");
        }
    }

    private void checkingFieldsOfCustomers() {
        var total = 0;
        var paid = 0;
        var customer = new Customer();
        var cma = new CustomerModifiedAmount();
        var valid = false;
        if (customerName.getText().trim().isEmpty()) {
            alertMe("You need to fill the Customer Name");
        } else {
            customer.setCustomerName(customerName.getText());
            valid = true;
        }
        if (weightCustomer.getText().trim().isEmpty()) {
            alertMe("You need to fill the Weight");
            valid = false;
        } else {
            try {
                var w = Integer.parseInt(weightCustomer.getText());
                total = w;
                customer.setWeight(w);
                valid = true;
            } catch (Exception e) {
                alertMe("Weight must be Number");
                valid = false;
            }
        }
        if (rateCustomer.getText().trim().isEmpty()) {
            alertMe("You need to fill the Rate");
            valid = false;
        } else {
            try {
                var r = Integer.parseInt(rateCustomer.getText());
                customer.setRate(r);
                total = total * r;
                customer.setTotalAmount(total);
                valid = true;
            } catch (Exception e) {
                alertMe("Rate must be Number");
                valid = false;
            }
        }
        if (crateCustomer.getText().trim().isEmpty()) {
            alertMe("You need to fill Crate");
        } else {
            try {
                var c = Integer.parseInt(crateCustomer.getText());
                customer.setCrate(c);
                valid = true;
            } catch (Exception e) {
                alertMe("Crate must be Number");
                valid = false;
            }
        }
        if (paidAmountCustomer.getText().trim().isEmpty()) {
            customer.setBalance(total);
        } else {
            try {
                var p = Integer.parseInt(paidAmountCustomer.getText().trim());
                paid = p;
                if (p == 0) {
                    customer.setBalance(total);
                }
                if (total == p) {
                    customer.setBalance(0);
                    cma.setPaidAmount(p);
                    cma.setModifiedDate(LocalDate.now());
                    customer.setTotalAmount(p);
                    customer.setComplete(true);
                    customer.getCustomerModifiedAmount().add(cma);
                } else if (total > p) {
                    customer.setBalance(total - p);
                    cma.setPaidAmount(p);
                    cma.setModifiedDate(LocalDate.now());
                }
            } catch (Exception e) {
                alertMe("Amount must be Number");
                valid = false;
            }
        }
        if (valid) {
            if (paid > total) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Infomation");
                alert.setContentText("You have entered Paid Amount more than Total Amount");
                alert.showAndWait();
                paidAmountCustomer.setFocusTraversable(true);
            } else {
                var c = customerService.insert(customer, cma);
                ob.add(new CustomerObservable(c.getCustomerId(), c.getCustomerName(), c.getWeight(), c.getRate(), c.getCrate(), c.getReturnedCrate(), c.getTotalAmount(), c.getBalance(), c.getDate()));
                customerTable.setItems(ob);
                printCustomer(c);
                filterTable();
                clearCustomerField();
            }
        } else {
            alertMe("Fill in the details");
        }
    }

    private void printCustomer(Customer c) {
        String customerData = null;
        if (c != null) {
            customerData = printerService.printCustomer(c);
        }
        var job = PrinterJob.createPrinterJob();
        var p = Printer.getAllPrinters();
        Printer selectedPrinter = null;
        var a4 = Paper.A4;
        for (Printer pt : p) {
            if (pt.getName().equals(getPrinterFromDB())) {
                selectedPrinter = pt;
                selectedPrinter.createPageLayout(a4, PageOrientation.PORTRAIT, 0.039f, 0.039f, 0.039f, 0.039f);
            }
        }
        job.setPrinter(selectedPrinter);
        var wv = new WebView();
        wv.setPrefSize(300, 300);
        wv.setMaxSize(300, 300);
        var we = wv.getEngine();
        we.loadContent(customerData);
        we.print(job);
        job.endJob();
    }

    private String getPrinterFromDB() {
        var list = new ArrayList<com.sunday.model.Printer>();
        var it = printerRepository.findAll();
        it.forEach(list::add);
        return list.get(0).getPrinterName();
    }


    private void createStockTable() {
        var stock = stockService.getAllData();
        stockTable.setEditable(true);
        var stockNo = new TableColumn<StockObservable, String>("#");
        stockNo.setCellValueFactory(p -> p.getValue().stockId);

        var vehicleNo = new TableColumn<StockObservable, String>("Vehicle Number");
        vehicleNo.setCellValueFactory(p -> p.getValue().vehicleNo);
        vehicleNo.setCellFactory(TextFieldTableCell.forTableColumn());
        vehicleNo.setOnEditCommit(e -> {
            var stockId = e.getTableView().getItems().get(e.getTablePosition().getRow()).stockId.get();
            var st = stockService.updateVehicleNo(stockId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).vehicleNo.set(st.getVehicleNo());
        });
        var weight = new TableColumn<StockObservable, Integer>("Weight in [Kgs]");
        weight.setCellValueFactory(p -> p.getValue().weight.asObject());
        weight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        weight.setOnEditCommit(e -> {
            var stockId = e.getTableView().getItems().get(e.getTablePosition().getRow()).stockId.get();
            var st = stockService.updateWeight(stockId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).weight.set(st.getWeight());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).totalAmount.set(st.getTotalAmount());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).Balance.set(st.getBalance());
        });

        var rate = new TableColumn<StockObservable, Integer>("Rate");
        rate.setCellValueFactory(p -> p.getValue().rate.asObject());
        rate.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        rate.setOnEditCommit(e -> {
            var stockId = e.getTableView().getItems().get(e.getTablePosition().getRow()).stockId.get();
            var st = stockService.updateRate(stockId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).rate.set(st.getRate());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).totalAmount.set(st.getTotalAmount());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).Balance.set(st.getBalance());
        });

        var totalAmount = new TableColumn<StockObservable, Integer>("Total Amount");
        totalAmount.setCellValueFactory(p -> p.getValue().totalAmount.asObject());

        var Balance = new TableColumn<StockObservable, Integer>("Balance");
        Balance.setCellValueFactory(p -> p.getValue().Balance.asObject());
        Balance.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        Balance.setOnEditCommit(e -> {
            var stockId = e.getTableView().getItems().get(e.getTablePosition().getRow()).stockId.get();
            var st = stockService.updateBalance(stockId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).Balance.set(st.getBalance());
        });

        var date = new TableColumn<StockObservable, String>("Date");
        date.setCellValueFactory(p -> p.getValue().date);

        stock.forEach(s -> obStock.add(new StockObservable(s.getStockId(), s.getVehicleNo(), s.getWeight(), s.getRate(), s.getTotalAmount(), s.getBalance(), s.getDate())));
        stockTable.getColumns().addAll(stockNo, vehicleNo, weight, rate, totalAmount, Balance, date);
        stockTable.setItems(obStock);
        addButtonToStockTable();
    }

    private void addButtonToStockTable() {
        var show = new TableColumn<StockObservable, Void>("");
        var showCellFactory = new Callback<TableColumn<StockObservable, Void>, TableCell<StockObservable, Void>>() {
            @Override
            public TableCell<StockObservable, Void> call(TableColumn<StockObservable, Void> param) {
                return new TableCell<>() {
                    final Button btn = new Button("Show Details");

                    {
                        btn.setStyle("-fx-background-color: #ffffff;" +
                                "-fx-text-fill: #4934eb;" +
                                "-fx-font-size: 15;" +
                                "-fx-background-radius: 0;");
                        btn.getStyleClass().add(".btn");
                        btn.setOnAction(a -> {
                            try {
                                var stId = getTableView().getItems().get(getIndex()).stockId.getValue();
                                var stock = stockService.findByStockId(stId);
                                var stage = new Stage();
                                var fxml = new FXMLLoader(showstockdetails.getURL());
                                fxml.setControllerFactory(applicationContext::getBean);
                                Parent load = fxml.load();
                                ShowStockDetails details = fxml.getController();
                                details.setStock(stock);
                                var scene = new Scene(load);
                                scene.setOnKeyPressed(event -> {
                                    if (event.getCode() == KeyCode.ESCAPE) stage.close();
                                });
                                stage.initStyle(StageStyle.TRANSPARENT);
                                stage.setResizable(false);
                                stage.setScene(scene);
                                stage.show();
                                new BounceIn(load).play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);

                    }
                };
            }
        };
        show.setCellFactory(showCellFactory);
        var delete = new TableColumn<StockObservable, Void>("");
        var deleteCellFactory = new Callback<TableColumn<StockObservable, Void>, TableCell<StockObservable, Void>>() {
            @Override
            public TableCell<StockObservable, Void> call(TableColumn<StockObservable, Void> param) {
                return new TableCell<>() {
                    final Button btn = new Button("Delete");

                    {
                        btn.setStyle("-fx-background-color: #ffffff;" +
                                "-fx-text-fill: #eb4034;" +
                                "-fx-font-size: 15;" +
                                "-fx-background-radius: 0;");
                        btn.getStyleClass().add(".btn");
                        btn.setOnAction(a -> {
                            try {
                                var st = getTableView().getItems().get(getIndex()).stockId.getValue();
                                if (stockService.deleteRecord(st)) {
                                    getTableView().getItems().remove(getIndex());
                                    alertSuccess("Record Have been Deleted with ID : " + st, Alert.AlertType.INFORMATION, "Successful");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);

                    }
                };
            }
        };
        delete.setCellFactory(deleteCellFactory);
        stockTable.getColumns().addAll(delete, show);
    }

    private static class StockObservable {
        final StringProperty vehicleNo;
        final StringProperty stockId;
        final IntegerProperty weight;
        final IntegerProperty rate;
        final IntegerProperty totalAmount;
        final IntegerProperty Balance;
        final StringProperty date;

        StockObservable(String stockId, String vehicleNo, int weight, int rate, int totalAmount, int balance, LocalDate date) {
            this.vehicleNo = new SimpleStringProperty(vehicleNo);
            this.stockId = new SimpleStringProperty(stockId);
            this.weight = new SimpleIntegerProperty(weight);
            this.rate = new SimpleIntegerProperty(rate);
            this.Balance = new SimpleIntegerProperty(balance);
            this.totalAmount = new SimpleIntegerProperty(totalAmount);
            this.date = new SimpleStringProperty(date.toString());
        }
    }

    @FXML
    public void hideAndShow(ActionEvent actionEvent) {
        if (actionEvent.getSource() == stockBtn) {
            stock.setVisible(true);
            customer.setVisible(false);
            new BounceIn(stockText).play();
            new FadeIn(stock).play();
        } else {
            stock.setVisible(false);
            customer.setVisible(true);
            new BounceIn(customerFields).play();
            new FadeIn(customer).play();
        }
    }

    private void moveable() {
        main.setOnMousePressed(e -> {
            xOffSet = e.getSceneX();
            yOffSet = e.getSceneY();
        });
        main.setOnMouseDragged(e -> {
            StageListener.s.setX(e.getScreenX() - xOffSet);
            StageListener.s.setY(e.getScreenY() - yOffSet);
            StageListener.s.setOpacity(0.8);
        });
        main.setOnMouseDragOver(e -> StageListener.s.setOpacity(1));
        main.setOnMouseReleased(e -> StageListener.s.setOpacity(1));
    }

    private void createCustomerTable() {
        var cust = customerService.getAllData();
        customerTable.setEditable(true);
        var custNo = new TableColumn<CustomerObservable, String>("#");
        custNo.setCellValueFactory(p -> p.getValue().custNo);

        var customerName = new TableColumn<CustomerObservable, String>("Customer Name");
        customerName.setCellValueFactory(param -> param.getValue().name);
        customerName.setCellFactory(TextFieldTableCell.forTableColumn());
        customerName.setOnEditCommit(e -> {
            var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
            customerService.updateCustomerName(custId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).name.set(e.getNewValue());
        });

        var weight = new TableColumn<CustomerObservable, Integer>("Weight in Kgs");
        weight.setCellValueFactory(param -> param.getValue().weight.asObject());
        weight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        weight.setOnEditCommit(e -> {
            var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
            var cu = customerService.updateWeight(custId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).weight.set(cu.getWeight());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).totalAmount.set(cu.getTotalAmount());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).balance.set(cu.getBalance());
        });

        var rate = new TableColumn<CustomerObservable, Integer>("Rate");
        rate.setCellValueFactory(param -> param.getValue().rate.asObject());
        rate.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        rate.setOnEditCommit(e -> {
            var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
            var cu = customerService.updateRate(custId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).rate.set(cu.getRate());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).totalAmount.set(cu.getTotalAmount());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).balance.set(cu.getBalance());
        });

        var crate = new TableColumn<CustomerObservable, Integer>("Crate");
        crate.setCellValueFactory(p -> p.getValue().crate.asObject());
        crate.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        crate.setOnEditCommit(e -> {
            var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
            var cu = customerService.updateCrate(custId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).crate.set(cu.getCrate());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).returedCrate.set(cu.getReturnedCrate());
        });

        var returnedCrate = new TableColumn<CustomerObservable, Integer>("Pending Crate");
        returnedCrate.setCellValueFactory(p -> p.getValue().returedCrate.asObject());
        returnedCrate.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        returnedCrate.setOnEditCommit(e -> {
            try {
                var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
                var c = customerService.updateReturnCrate(custId, e.getNewValue());
                var show = c.getCrate() - c.getReturnedCrate();
                e.getTableView().getItems().get(e.getTablePosition().getRow()).returedCrate.set(show);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        var totalAmount = new TableColumn<CustomerObservable, Integer>("Total Amount");
        totalAmount.setCellValueFactory(p -> p.getValue().totalAmount.asObject());

        var balance = new TableColumn<CustomerObservable, Integer>("Balance");
        balance.setCellValueFactory(p -> p.getValue().balance.asObject());
        balance.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        balance.setOnEditCommit(e -> {
            var custId = e.getTableView().getItems().get(e.getTablePosition().getRow()).custNo.get();
            var cu = customerService.updateBalance(custId, e.getNewValue());
            e.getTableView().getItems().get(e.getTablePosition().getRow()).balance.set(cu.getBalance());
        });

        var date = new TableColumn<CustomerObservable, String>("Date ");
        date.setCellValueFactory(p -> p.getValue().date);

        cust.forEach(c -> ob.add(new CustomerObservable(c.getCustomerId(), c.getCustomerName(), c.getWeight(), c.getRate(), c.getCrate(), c.getReturnedCrate(), c.getTotalAmount(), c.getBalance(), c.getDate())));
        customerTable.setItems(ob);
        customerTable.getColumns().addAll(custNo, customerName, weight, rate, crate, returnedCrate, totalAmount, balance, date);
        customerTable.setItems(ob);
        addShowButton();
        filterTable();
    }

    private void filterTable() {
        var filteredList = new FilteredList<>(ob, b -> true);

        customerFilter.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(cust1 -> {
            if (newValue == null || newValue.isEmpty()) return true;
            var lcf = newValue.toLowerCase();
            if (cust1.name.get().toLowerCase().contains(lcf)) return true;
            else return cust1.custNo.get().toLowerCase().contains(lcf);
        }));
        SortedList<CustomerObservable> sort = new SortedList<>(filteredList);
        sort.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sort);
        new FadeIn(customerTable).play();
    }

    private static class CustomerObservable {
        final SimpleStringProperty custNo;
        final SimpleStringProperty name;
        final SimpleIntegerProperty weight;
        final SimpleIntegerProperty rate;
        final SimpleIntegerProperty crate;
        final SimpleIntegerProperty returedCrate;
        final SimpleIntegerProperty totalAmount;
        final SimpleIntegerProperty balance;
        final SimpleStringProperty date;

        public CustomerObservable(String custNo, String name, int weight, int rate, int crate, int returedCrate, int totalAmount, int balance, LocalDate date) {
            this.custNo = new SimpleStringProperty(custNo);
            this.name = new SimpleStringProperty(name);
            this.weight = new SimpleIntegerProperty(weight);
            this.rate = new SimpleIntegerProperty(rate);
            this.crate = new SimpleIntegerProperty(crate);
            this.returedCrate = new SimpleIntegerProperty(crate - returedCrate);
            this.totalAmount = new SimpleIntegerProperty(totalAmount);
            this.balance = new SimpleIntegerProperty(balance);
            this.date = new SimpleStringProperty(date.toString());
        }

        @Override
        public String toString() {
            return "CustomerObservable{" +
                    "custNo=" + custNo +
                    ", name=" + name +
                    ", weight=" + weight +
                    ", rate=" + rate +
                    ", crate=" + crate +
                    ", returedCrate=" + returedCrate +
                    ", totalAmount=" + totalAmount +
                    ", balance=" + balance +
                    ", date=" + date +
                    '}';
        }
    }

    private void addShowButton() {
        var show = new TableColumn<CustomerObservable, Void>("Show Details");
        var delete = new TableColumn<CustomerObservable, Void>("");
        var showCellFactory = new Callback<TableColumn<CustomerObservable, Void>, TableCell<CustomerObservable, Void>>() {
            @Override
            public TableCell<CustomerObservable, Void> call(TableColumn<CustomerObservable, Void> param) {
                return new TableCell<>() {
                    final Button btn = new Button("Show Details");

                    {
                        btn.setStyle("-fx-background-color: #ffffff;" +
                                "-fx-text-fill: #4934eb;" +
                                "-fx-font-size: 15;" +
                                "-fx-background-radius: 0;");
                        btn.setOnAction(a -> {
                            try {
                                var customerId = getTableView().getItems().get(getIndex()).custNo.getValue();
                                var customer = customerService.findByCustomerId(customerId);
                                var stage = new Stage();
                                var fxml = new FXMLLoader(showcustomdetails.getURL());
                                fxml.setControllerFactory(applicationContext::getBean);
                                Parent load = fxml.load();
                                ShowCustomerDetails details = fxml.getController();
                                details.setCustomer(customer);
                                var scene = new Scene(load);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.TRANSPARENT);
                                scene.setOnKeyPressed(event -> {
                                    if (event.getCode() == KeyCode.ESCAPE) {
                                        stage.close();
                                    }
                                });
                                stage.setScene(scene);
                                stage.show();
                                new BounceIn(load).play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);
                    }
                };
            }
        };
        show.setCellFactory(showCellFactory);
        var deleteCellFactory = new Callback<TableColumn<CustomerObservable, Void>, TableCell<CustomerObservable, Void>>() {

            @Override
            public TableCell<CustomerObservable, Void> call(TableColumn<CustomerObservable, Void> param) {
                return new TableCell<>() {
                    final Button btn = new Button("Delete");

                    {
                        btn.getStyleClass().add(".btn");
                        btn.setStyle("-fx-background-color: #ffffff;" +
                                "-fx-text-fill: #eb4034;" +
                                "-fx-font-size: 15;" +
                                "-fx-background-radius: 0;");
                        btn.setOnAction(a -> {
                            var customerId = getTableView().getItems().get(getIndex()).custNo.getValue();
                            if (customerService.deleteRecord(customerId)) {
                                customerTable.setItems(ob);
                                ob.remove(getTableView().getItems().get(getIndex()));
                                customerTable.setItems(ob);
                                filterTable();
                                alertSuccess("Record Have been Deleted with ID : " + customerId, Alert.AlertType.INFORMATION, "Successful");
                            } else {
                                alertSuccess("Unable to Delete ID with : " + customerId, Alert.AlertType.ERROR, "Failed");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);

                    }
                };
            }
        };
        delete.setCellFactory(deleteCellFactory);
        customerTable.getColumns().addAll(delete, show);
    }

    private void clearCustomerField() {
        customerName.clear();
        weightCustomer.clear();
        rateCustomer.clear();
        crateCustomer.clear();
        paidAmountCustomer.clear();
    }

    private void clearStockField() {
        vehicleNoStock.clear();
        weightTonsStock.clear();
        rateStock.clear();
        paidStock.clear();
    }
}