package dk.sdu;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class MqttClientTest {

    public static void main(String[] args) throws MqttException {


        MqttClient client = new MqttClient(
                "tcp://0.0.0.0:9001", //URI
                MqttClient.generateClientId(), //ClientId
                new MemoryPersistence());//Persistence

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection lost");

                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    System.out.println(topic + ": " + new String(mqttMessage.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
                client.connect();
                client.subscribe("emulator/status", 1);
                client.subscribe("emulator/checkhealth", 2);

    }
}