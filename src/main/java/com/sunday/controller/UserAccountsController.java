package com.sunday.controller;

import com.sunday.model.User;
import com.sunday.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAccountsController implements Initializable {
    private final UserService userService;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button exit;
    @FXML
    private Button delete;
    @FXML
    private Button deleteAll;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exit.setOnAction(e -> ((Stage) exit.getScene().getWindow()).close());
        setUserList();
        delete.setOnAction(e -> {
            userService.deleteUser(userList.getSelectionModel().getSelectedItem());
            setUserList();
        });
        deleteAll.setOnAction(e -> {
            userService.deleteAll();
            setUserList();
        });
    }

    private void setUserList() {
        var list = userService.getAllUserData()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        ObservableList<String> ob = FXCollections.observableArrayList();
        ob.addAll(list);
        userList.setItems(ob);
    }
}
