package com.example.guidemo_4semester;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    @FXML private Label HealthyLabel;


    private Timeline updateTimer;
    private int status;
    private final AGVPI agv;
    private final IMqttService iMqttService;
    private boolean health;
    private int state;


    // vi skal ikke have en setDepencies metode - da Spring ikke kan starte programmet uden Constructor-based DI.
    @Autowired
    public TabViewController(AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.agv = agv;
        this.iMqttService = iMqttService;
    }



    @FXML
    public void initialize() throws MqttException {


        iMqttService.setCallback(createMqttCallback());

        startAGVUpdates();
        iMqttService.connect();
        System.out.println(iMqttService.isConnected());


        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
                iMqttService.publish("emulator/operation",  "{\"ProcessID\": 12345}");
            } catch (IOException | InterruptedException | MqttException e) {
                e.printStackTrace();
            }
        });
    }


    private void setStartProdButton() throws IOException, InterruptedException {

        agv.sendRequest("MoveToStorageOperation");
        updateAGVDisplay();
    }
    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e ->{
                    updateAGVDisplay();
                    updateAssemblyConnectionStatus();
                })
        );
        updateTimer.setCycleCount(Animation.INDEFINITE);
        updateTimer.play();
    }
    private MqttCallback createMqttCallback() {

        return new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("MQTT: Connection lost - " + cause.getMessage());
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String json = message.toString();
                if(topic.equals("emulator/status")){
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    state = obj.get("State").getAsInt();

                    String Assemblystate;
                    switch (state) {
                        case 1:
                            Assemblystate = "Running";
                            break;
                        case 0:
                            Assemblystate = "Idle";
                            break;
                        default:
                            Assemblystate = "Unknown";
                    }
                    Platform.runLater(() -> {
                        assemblyStatusLabel.textProperty().unbind();
                        assemblyStatusLabel.setText(Assemblystate);
                    });
                } else if(topic.equals("emulator/checkhealth")){
                    json = json.substring(1, json.length()-1);
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    health = obj.get("IsHealthy").getAsBoolean();

                    Platform.runLater(() -> {
                        HealthyLabel.textProperty().unbind();
                        HealthyLabel.setText(String.valueOf(health));

                        String AssemblyStatus = iMqttService.isConnected() ? "#1fff25" : "RED";
                        assemblyConnectionCircle.setFill(Color.valueOf(AssemblyStatus));
                        });
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    System.out.println("MQTT: Delivery complete for message: " + token.getMessage());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void updateAssemblyConnectionStatus() {
        boolean connected = iMqttService.isConnected();
        Platform.runLater(() -> {
            if (connected) {
                assemblyConnectionCircle.setFill(Color.valueOf("#1fff25"));
            } else {
                assemblyConnectionCircle.setFill(Color.RED);
                assemblyStatusCircle.setFill(Color.RED);
                assemblyStatusLabel.textProperty().unbind();
                assemblyStatusLabel.setText("Error");
            }
        });
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
