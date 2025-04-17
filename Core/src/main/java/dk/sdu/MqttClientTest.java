package dk.sdu;

import com.fasterxml.jackson.core.ObjectCodec;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttClientTest {

    public static void main(String[] args) throws MqttException {

        MqttClient client = new MqttClient(
                "tcp://0.0.0.0:9001", //URI
                MqttClient.generateClientId(), //ClientId
                new MemoryPersistence()); //Persistence


            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection lost");

                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);

                    ObjectMapper objectMapper = new ObjectMapper();
                    String lastStateSignature = "";

                    if (topic.equals("emulator/status")) {
                        try {
                            JsonNode root = objectMapper.readTree(payload);
                            String currentSignature = root.path("LastOperation").asText() + "-" +
                                    root.path("CurrentOperation").asText() + "-" +
                                    root.path("State").asText();

                            if (!currentSignature.equals(lastStateSignature)) {
                                System.out.println("Status changed: " + payload);
                                lastStateSignature = currentSignature;
                            }

                        } catch (Exception e) {
                            System.out.println("Failed to parse JSON: " + payload);
                        }
                    } else {
                        System.out.println(topic + ": " + payload);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
                client.connect();
                client.subscribe("emulator/status", 1);
                client.subscribe("emulator/checkhealth", 2);

                Scanner scanner = new Scanner(System.in);
                System.out.println("Skriv processID for at publishe til emulator/operation, skriv 'exit' for at stoppe");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            // Create JSON payload
            String jsonPayload = "{\"ProcessID\":\"" + input + "\"}";
            MqttMessage message = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));
            message.setQos(1);

            try {
                client.publish("emulator/operation", message);
                System.out.println("Published: " + jsonPayload);
            } catch (MqttException e) {
                System.err.println("Failed to publish: " + e.getMessage());
            }
        }
        scanner.close();
        client.disconnect();
        System.out.println("Disconnected");

    }
}
