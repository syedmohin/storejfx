package com.sunday.controller;

import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import com.sunday.service.UserService;
import com.sunday.stage.StageListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.exit;
import static javafx.application.Platform.runLater;

@Component
@RequiredArgsConstructor
public class MainController implements Initializable {

    private final ApplicationContext applicationContext;
    private final UserService userService;

    @Value("${store.title}")
    private String applicationTitle;
    @Value("classpath:/welcome.fxml")
    private Resource fxml;
    @Value("classpath:/image/icon.png")
    private Resource icon;
    public static Stage s;

    @FXML
    private TextField sUser;
    @FXML
    private PasswordField spassword;
    @FXML
    private AnchorPane main;
    @FXML
    private TextField lusername;
    @FXML
    private Button login;
    @FXML
    private PasswordField sapassword, lpassword;
    @FXML
    private Button signUp;
    @FXML
    private AnchorPane signUpPane;
    @FXML
    private AnchorPane loginPane;
    private double xOffSet = 0;
    private double yOffSet = 0;
    @FXML
    private Button loginShow, signShow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        movable();
        main.setOpacity(0);
        main.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) exit();
        });
        lpassword.setOnKeyPressed(e -> runLater(() -> {
            if (e.getCode() == KeyCode.ENTER) loginAction();
        }));
        loginPane.setVisible(true);
        signUpPane.setVisible(false);
        loginShow.setOnAction(e -> {
            loginPane.setVisible(true);
            new FadeOut(signUpPane).setSpeed(.8).setDelay(Duration.seconds(25)).play();
            signUpPane.setVisible(false);
            new FadeIn(loginPane).setSpeed(.8).setDelay(Duration.seconds(25)).play();
        });
        signShow.setOnAction(e -> {
            loginPane.setVisible(false);
            new FadeIn(signUpPane).setSpeed(.8).setDelay(Duration.seconds(25)).play();
            signUpPane.setVisible(true);
            new FadeOut(loginPane).setSpeed(.8).setDelay(Duration.seconds(25)).play();
        });
        signUp.setOnAction(e -> runLater(this::signUpAction));
        login.setOnAction(e -> runLater(this::loginAction));
    }

    private void signUpAction() {
        if (!(sUser.getText().isEmpty() | spassword.getText().isEmpty() | sapassword.getText().isEmpty())) {
            if (spassword.getText().equals(sapassword.getText())) {
                var user = userService.insertUser(sUser.getText().trim(), spassword.getText().trim());
                if (user.getUsername().isEmpty() | user.getUsername() == null | user.getPassword().isEmpty() | user.getPassword() == null) {
                    runLater(() -> {
                        alertBox("Registration Form", "Registration Failed!!");
                    });
                } else {
                    signUpPane.setVisible(false);
                    new FadeOut(signUpPane).setSpeed(.5).play();
                    loginPane.setVisible(true);
                    new FadeIn(loginPane).setSpeed(.5).play();
                }
            } else {
                alertBox("Registration Form", "Password must be Same in both fields");
            }
        } else {
            alertBox("Registration Form", "fields should not be empty");
        }
    }

    private void loginAction() {
        if (!(lusername.getText().isEmpty() | lpassword.getText().isEmpty())) {
            if (userService.checkUserAndPassword(lusername.getText().trim(), lpassword.getText().trim())) {
                try {
                    StageListener.s.close();
                    var stage = new Stage();
                    var fxmlLoader = new FXMLLoader(fxml.getURL());
                    fxmlLoader.setControllerFactory(applicationContext::getBean);
                    Parent root = fxmlLoader.load();
                    var scene = new Scene(root);
                    stage.setTitle(applicationTitle);
                    stage.centerOnScreen();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setResizable(false);
                    stage.getIcons().add(new Image(icon.getInputStream()));
                    stage.setScene(scene);
                    s = stage;
                    stage.show();
                    new BounceIn(root).play();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                alertBox("Login Form", "Username and Password mismatch");
            }
        } else {
            alertBox("Login Form", "Must be filled ");
        }
    }

    private void alertBox(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        var boxBlur = new BoxBlur(4, 4, 3);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.initOwner(MainController.s);
        alert.setOnShowing(e -> main.setEffect(boxBlur));
        alert.setOnCloseRequest(e -> main.setEffect(null));
        alert.showAndWait();
    }

    private void movable() {
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
}
