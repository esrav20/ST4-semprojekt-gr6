package dk.sdu.AssemblyStation;


import dk.sdu.Common.IMqttService;
import org.eclipse.paho.client.mqttv3.*;

import com.google.gson.*;

public class MqttService implements IMqttService {
    private  MqttClient client;

    public MqttService() {
        try {
            client = new MqttClient("tcp://localhost:9001", MqttClient.generateClientId());
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost" + cause.getMessage());
                }

                public void messageArrived(String topic, MqttMessage message) {
                    String json = new String(message.getPayload());
                    JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                    System.out.println("Received Message: " + obj);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery Complete");
                }
            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


                @Override
                public void connect() {
                    try {
                        client.connect();
                        client.subscribe("emulator/status");
                        client.subscribe("emulator/checkhealth");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void disconnect() {
                }

                @Override
                public void publish(String topic, String payload, int qos) {
                    try {
                        MqttMessage message = new MqttMessage(payload.getBytes());
                        message.setQos(1);
                        client.publish(topic, message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}