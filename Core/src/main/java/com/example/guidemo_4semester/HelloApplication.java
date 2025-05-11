package com.example.guidemo_4semester;

import dk.sdu.AGV.AGVMovement;
import dk.sdu.Common.IMqttService;
import dk.sdu.CommonAGV.AGVPI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;


@SpringBootApplication(scanBasePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example"})
public class HelloApplication extends Application {

    @Autowired
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext =
                new SpringApplicationBuilder(HelloApplication.class)
                        .web(WebApplicationType.NONE)
                        .headless(false)
                        .run();
    }

    @Override
    public void start(Stage stage) throws IOException, MqttException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/views/TabView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
