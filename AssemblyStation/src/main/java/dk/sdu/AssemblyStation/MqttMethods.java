package dk.sdu.AssemblyStation;


import com.fasterxml.jackson.databind.util.JSONPObject;
import dk.sdu.Common.IMqttConnection;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.json.JSONObject;
import com.google.gson.*;

public class MqttMethods implements IMqttConnection {
    private  MqttClient client;
    private boolean health;
    private int state;


    @Override
    public void connect(String Brokerurl) throws MqttException {
        client = new MqttClient(Brokerurl, MqttClient.generateClientId());
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
                cause.printStackTrace();
            }



            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String json = message.toString();
                if(topic.equals("emulator/status")){
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    state = obj.get("State").getAsInt();
                    System.out.println("State: " + state);
                } else if(topic.equals("emulator/checkhealth")){
                    json = json.substring(1, json.length()-1);
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                    health = obj.get("IsHealthy").getAsBoolean();
                    System.out.println("IsHealthy: " + health);
                }

            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete");
            }
        });
        try {
            client.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() throws MqttException {
        client.disconnect();

    }

    @Override
    public void subscribe(String topic, int qos) throws MqttException {
        try {
            client.subscribe(topic,qos);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }







    @Override
    public void publish(String topic, String payload, int qos) throws MqttException {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            System.out.println(message);
            message.setQos(qos);
            client.publish(topic,message);
        } catch (MqttException e){
            e.printStackTrace();
        }

    }
}
