package com.example.guidemo_4semester;

import dk.sdu.AGV.AGVMovement;
import dk.sdu.CommonAGV.AGVPI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/views/TabView.fxml"));
        Parent root = fxmlLoader.load();
        TabViewController controller = fxmlLoader.getController();
        AGVPI agv = new AGVMovement();
        controller.setAGV(agv);


        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
