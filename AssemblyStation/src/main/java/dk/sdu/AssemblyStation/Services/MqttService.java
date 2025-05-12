package dk.sdu.AssemblyStation.Services;


import dk.sdu.Common.IMqttService;
import org.eclipse.paho.client.mqttv3.*;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class MqttService implements IMqttService {
    private MqttClient client;
    private MqttCallback callback;
    private BiConsumer<Integer, Boolean> messageHandler;
    private int state;
    private boolean health;


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
                if (callback == null) {
                    setCallback(null);
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
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("MQTT: Connection lost - " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String json = message.toString();
                if(topic.equals("emulator/status")){
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    state = obj.get("State").getAsInt();


                    if(messageHandler !=null){
                        messageHandler.accept(state,null);
                    }
                } else if(topic.equals("emulator/checkhealth")){
                    json = json.substring(1, json.length()-1);
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    health = obj.get("IsHealthy").getAsBoolean();


                    if(messageHandler !=null){
                        messageHandler.accept(null,health);
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    @Override
    public void setMessagehandler(BiConsumer<Integer, Boolean> handler) {
        this.messageHandler = handler;
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