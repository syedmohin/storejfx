package com.sunday.stage;

import animatefx.animation.BounceIn;
import com.sun.javafx.sg.prism.NGImageView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageListener implements ApplicationListener<StageReadyEvent> {

    private final ApplicationContext applicationContext;

    @Value("${store.title}")
    private String applicationTitle;
    @Value("classpath:/main.fxml")
    private Resource fxml;
    @Value("classpath:/image/icon.png")
    private Resource icon;
    public static Stage s;

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {

        try {
            var stage = stageReadyEvent.getStage();
            var fxmlLoader = new FXMLLoader(fxml.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle(applicationTitle);
            s = stage;
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.getIcons().add(new Image(icon.getInputStream()));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
            new BounceIn(root).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
