package dk.sdu.AssemblyStation.Services;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import dk.sdu.Common.IMqttService;
import org.eclipse.paho.client.mqttv3.*;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttService implements IMqttService {
    private MqttClient client;
    private MqttCallback callback;


    public MqttService() {
        try {
            client = new MqttClient("tcp://localhost:9001", MqttClient.generateClientId());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void connect() {
        try {
            if (client != null && !client.isConnected()) {
                if (callback != null) {
                    client.setCallback(callback);
                }

            client.connect();
            client.subscribe("emulator/status", 1);
            client.subscribe("emulator/checkhealth", 1);

            }
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("MQTT: Connection failed");
        }
    }

    @Override
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
            System.out.println("Disconnected from MQTT");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCallback(MqttCallback callback) throws MqttException {
        this.callback = callback;
        if (client != null && client.isConnected()) {
            client.setCallback(callback);
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    @Override
    public void initPublish(int processId) throws MqttException {
        String payload = String.format("{\"processId\":%d}", processId);
        publish("emulator/operation", payload);
    }


    @Override
    public void publish(String topic, String payload) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                System.out.println("MQTT: Published to topic '" + topic + "' with payload: " + payload);

            } else {

                System.err.println("Not connected to MQTT. Cannot publish.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}