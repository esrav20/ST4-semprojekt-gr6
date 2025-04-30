package dk.sdu.AssemblyStation;


import com.fasterxml.jackson.databind.util.JSONPObject;
import dk.sdu.Common.IMqttConnection;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Arrays;
import java.util.Iterator;

import org.json.JSONObject;

public class MqttMethods implements IMqttConnection {
    private  MqttClient client;


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
                String payload = new String(message.getPayload(), "UTF-8");

                JSONObject json = new JSONObject(payload);

                System.out.println("Received message on topic: " + topic);
                Iterator<String> keys = json.keys();  // Hent alle nøgler i JSON-objektet
                while (keys.hasNext()) {
                    String key = keys.next();
                    System.out.println(key + ": " + json.get(key));
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
            message.setQos(qos);
            client.publish(topic,message);
        } catch (MqttException e){
            e.printStackTrace();
        }

    }
}
