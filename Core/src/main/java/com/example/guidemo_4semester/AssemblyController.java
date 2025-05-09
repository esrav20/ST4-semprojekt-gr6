package com.example.guidemo_4semester;

import dk.sdu.Common.IMqttService;
import dk.sdu.AssemblyStation.MqttService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AssemblyController {
    @FXML private Label connectionStatusLabel;
    @FXML private Button startButton;
    @FXML private Button checkHealthButton;
    @FXML private TextField processIdInput;

    private final IMqttService mqttService = MqttService.getMqttService();

    @FXML
    public void initialize() throws MqttException {
        mqttService.connect();

        startButton.setOnAction(event -> {
            int processId = Integer.parseInt(processIdInput.getText());
            try {
                mqttService.initPublish(processId);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
