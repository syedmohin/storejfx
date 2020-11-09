package com.sunday;

import com.sunday.stage.StageReadyEvent;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class JavafxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        try {
            ApplicationContextInitializer<GenericApplicationContext> initializer = ac -> {
                ac.registerBean(Application.class, () -> JavafxApplication.this);
                ac.registerBean(Parameters.class, this::getParameters);
                ac.registerBean(HostServices.class, this::getHostServices);
            };
            System.setProperty("java.awt.headless","true");
            this.context = new SpringApplicationBuilder()
                    .sources(StoreJavafxApplication.class)
                    .initializers(initializer)
                    .run(getParameters().getRaw().toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}
