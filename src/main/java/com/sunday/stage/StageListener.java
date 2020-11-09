package com.sunday.stage;

import animatefx.animation.BounceIn;
import animatefx.animation.FadeIn;
import com.sun.javafx.sg.prism.NGImageView;
import com.sunday.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
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
            var scene = new Scene(root);
            stage.setTitle(applicationTitle);
            s = stage;
            scene.setFill(Color.TRANSPARENT);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.getIcons().add(new Image(icon.getInputStream()));
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
            new BounceIn(root).setSpeed(0.4).play();
            new FadeIn(root).setSpeed(0.8).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
