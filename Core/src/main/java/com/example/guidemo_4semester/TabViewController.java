package com.example.guidemo_4semester;
import dk.sdu.AGV.AGVMovement;
import dk.sdu.CommonAGV.AGVPI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import dk.sdu.AGV.AGVConnection;

import java.io.IOException;

public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;
    private Timeline updateTimer;
    int status;
    String error;
    AGVPI agv = new AGVMovement();
    @FXML
    public void initialize() {
        startAGVUpdates();
    }
    private void sendRequest(String Operation) throws IOException, InterruptedException {
        agv.sendRequest("{\"Program name\":\"" + Operation + "\",\"State\":1}");
        agv.sendRequest("{\"State\":2}");
        status = agv.getStatus();
        error = agv.getErrorcode();
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

        switch(AGVConnection.currentState) {
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

        // Update connection circle
        String connectionStatus = AGVConnection.isConnected ? "#1fff25" : "RED";

        // Apply updates on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(javafx.scene.paint.Color.valueOf(circleColor));
            agvConnectionCircle.setFill(javafx.scene.paint.Color.valueOf(connectionStatus));
            agvParameterLabel.setText("Battery: " + AGVConnection.battery + "%");
            startProdButton.setOnMouseClicked(event -> {
                try {
                    setStartProdButton();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

    }
}