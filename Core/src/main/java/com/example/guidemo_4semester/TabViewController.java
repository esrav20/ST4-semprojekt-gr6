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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


import java.io.IOException;

@Component
public class TabViewController {

    //Warehouse/Inventory:




    //---------------------------
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
    private final AGVPI agv;
    private final IMqttService iMqttService;


    // vi skal ikke have en setDepencies metode - da Spring ikke kan starte programmet uden Constructor-based DI.
    @Autowired
    public TabViewController(AGVPI agv, IMqttService iMqttService, ) throws MqttException {
        this.agv = agv;
        this.iMqttService = iMqttService;
    }


    private void appendMessageBoard(String text) {
        Platform.runLater(() -> {
            Text msg = new Text(text + "\n");
            messageBoard.getChildren().add(msg);
        });
    }

    @FXML
    public void initialize() throws MqttException {
        setupMqtt();
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
            agvStatusLabel.textProperty().unbind(); // Vi får en runtimeException, når vi kører appen og denne er bound i forvejen.
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(Color.valueOf(circleColor));
            agvConnectionCircle.setFill(Color.valueOf(connectionStatus));
            agvParameterLabel.textProperty().unbind(); // samme som l.159.
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });


    }

}
