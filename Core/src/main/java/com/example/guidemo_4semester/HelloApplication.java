package com.example.guidemo_4semester;

import javafx.application.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.stage.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.io.IOException;


@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"dk.sdu.AGV", "dk.sdu.AssemblyStation", "dk.sdu", "com.example", "dk.sdu.CommonInventory.*"})
@EnableJpaRepositories(basePackages = "dk.sdu.CommonInventory")
@EntityScan(basePackages = "dk.sdu.CommonInventory")
public class HelloApplication extends Application {


    private ConfigurableApplicationContext springContext;

    private Scene scene;
    private Stage stage;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void init() {
        springContext =
                new SpringApplicationBuilder(MySpringBoot.class)
                        .web(WebApplicationType.NONE)
                        .headless(false)
                        .run();
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TabView.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Hello!");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
