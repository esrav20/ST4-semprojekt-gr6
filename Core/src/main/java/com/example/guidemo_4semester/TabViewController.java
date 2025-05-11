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
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;

public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Circle assemblyConnectionCircle;
    @FXML private Circle assemblyStatusCircle;
    @FXML private Label assemblyStatusLabel;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;
    @FXML private TextFlow messageBoard;
    @FXML private TextField processIdInput;
    @FXML private Button checkHealthButton;

    private Timeline updateTimer;
    private int status;
    private AGVPI agv;
    private IMqttService iMqttService;


    public TabViewController() {
    }


    public void setDependencies(AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.agv = agv;
        this.iMqttService = iMqttService;
        setupMqtt();
    }

    private void appendMessageBoard(String text) {
        Platform.runLater(() -> {
            Text msg = new Text(text + "\n");
            messageBoard.getChildren().add(msg);
        });
    }

    @FXML
    public void initialize() {
        startAGVUpdates();
        startProdButton.setOnAction(event -> {
            int processId = Integer.parseInt(processIdInput.getText());
            try {
                iMqttService.initPublish(processId);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });

        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupMqtt() throws MqttException {
        MqttCallback mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                appendMessageBoard("MQTT Connection lost: " + cause.getMessage());
                updateConnectionStatus(false);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String msg = new String(message.getPayload());
                appendMessageBoard("MQTT message on [" + topic + "]: " + msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                appendMessageBoard("MQTT Delivery complete");
            }
        };

        iMqttService.setCallback(mqttCallback); // Set the callback
        iMqttService.connect();
        updateConnectionStatus(true);
    }

    private void updateConnectionStatus(boolean connected) {
        Platform.runLater(() ->
                assemblyConnectionCircle.setFill(Color.valueOf(connected ? "#1fff25" : "RED"))
        );
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
            agvConnectionCircle.setFill(Color.valueOf(connectionStatus));
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });


    }

}
