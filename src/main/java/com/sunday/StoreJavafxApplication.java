package com.sunday;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StoreJavafxApplication {

    public static void main(String[] args) {
        Application.launch(JavafxApplication.class, args);
    }
}
