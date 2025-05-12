package com.example.guidemo_4semester;

import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.Common.IMqttService;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;
    @FXML private Button emergencyStopButton;
    private Timeline updateTimer;
    private int status;
    private AGVPI agv;
    private IMqttService iMqttService;
    private boolean emergencyActive = false;


    public TabViewController(AGVPI agv, IMqttService iMqttService) {
        this.agv = agv;
        this.iMqttService = iMqttService;
    }

    @FXML
    public void initialize() {
        startAGVUpdates();

        // Button event setup should happen once
        startProdButton.setOnMouseClicked(event -> {
            if(!emergencyActive) {
                try {
                    setStartProdButton();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        emergencyStopButton.setOnMouseClicked(event -> {
            if(!emergencyActive){
                handleEmergencyStop();
            }else {
                handleEmergencyReset();
            }
        } );
    }

    private void handleEmergencyStop(){
        emergencyActive = true;

        Platform.runLater(() -> {
            agvStatusLabel.setText("Emergency Stop");
            agvStatusCircle.setFill(Color.RED);

            emergencyStopButton.setText("Reset Emergency button");
            startProdButton.setDisable(true);

        });

        new Thread(() -> {
        try{
            agv.sendRequest("EmergencyStop");

            if (iMqttService != null && agv.isConnected()) {
                try{
                    iMqttService.publish("system/emergency", "Emergency stop activated");
                }catch (Exception e){
                    System.err.println("MQTT Error: Failed to publish emergency message" + e.getMessage());
                }
            }
        }catch (Exception e){
            System.err.println("AGV Error: Failed to send emergencystop to AGV " + e.getMessage());
        }
        }).start();
    }

    private void handleEmergencyReset(){
        Platform.runLater(()-> {
            emergencyStopButton.setText("Emergency stop");
            emergencyStopButton.setStyle(""); // sets to default css style
            startProdButton.setDisable(false);
        });

        new Thread(()-> {
            try{
                agv.sendRequest("ResetEmergency");
                emergencyActive = false;
            }catch (Exception e){
                System.err.println("Error: Failed to reset emergency state " + e.getMessage());
            }
        }).start();
    }


    private void setStartProdButton() throws IOException, InterruptedException {

        agv.sendRequest("MoveToStorageOperation");
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

        if(emergencyActive){
            Platform.runLater(()-> {
                agvStatusLabel.setText("Emergency stop");
                agvStatusCircle.setFill(Color.RED);
            });
            return;
        }

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
