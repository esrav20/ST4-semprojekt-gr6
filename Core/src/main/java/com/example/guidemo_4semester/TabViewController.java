package com.example.guidemo_4semester;

import dk.sdu.CommonAGV.AGVPI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;

public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;

    private Timeline updateTimer;
    private AGVPI agv;
    private int status;

    public void setAGV(AGVPI agv) {
        this.agv = agv;
    }

    @FXML
    public void initialize() {
        startAGVUpdates();

        // Button event setup should happen once
        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendRequest(String operation) throws IOException, InterruptedException {
        agv.needsCharging();
        agv.sendRequest("{\"Program name\":\"" + operation + "\",\"State\":1}");
        agv.sendRequest("{\"State\":2}");
        status = agv.getStatus();
        String error = agv.getErrorcode();
        System.out.println(status);
        System.out.println(error);
    }

    private void agvPickItem() throws IOException, InterruptedException {
        agv.pickItem("Hej");
    }

    private void agvPutItem() throws IOException, InterruptedException {
        agv.putItem("Hej");
    }

    private void setStartProdButton() throws IOException, InterruptedException {
        sendRequest("MoveToStorageOperation");
        updateAGVDisplay();
    }
    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> updateAGVDisplay())
        );
        updateTimer.setCycleCount(Animation.INDEFINITE);
        updateTimer.play();
    }

    private void updateAGVDisplay() {
        String statusText;
        String circleColor;
        switch (agv.getCurrentstate()) {
            case 1:
                statusText = "Idle";
                circleColor = "DODGERBLUE";
                break;
            case 2:
                statusText = "Working";
                circleColor = "#1fff25";
                break;
            case 3:
                statusText = "Charging";
                circleColor = "ORANGE";
                break;
            default:
                statusText = "Error";
                circleColor = "RED";
        }

        String connectionStatus = agv.isConnected() ? "#1fff25" : "RED";
        System.out.println(agv.isConnected());

        Platform.runLater(() -> {
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(Color.valueOf(circleColor));
            agvConnectionCircle.setFill(javafx.scene.paint.Color.valueOf(connectionStatus));
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });
    }
}
