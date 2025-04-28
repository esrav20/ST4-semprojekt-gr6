package dk.sdu.Common;

import dk.sdu.IMqttConnection;
import dk.sdu.MessageHandler;
import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConnection implements IMqttConnection {

    private final MqttClient mqttClient;

    public MqttConnection(String brokerUrl) throws MqttException {
        this.mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
    }
    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void subscribe(String topic, int qos, MessageHandler handler) throws MqttException {
        mqttClient.subscribe(topic, qos, (receivedTopic, MqttMessage) -> {
            String payload = new String(MqttMessage.getPayload(), StandardCharsets.UTF_8);
            handler.handle(receivedTopic, payload);
        });

    }

    @Override
    public void publish(String topic, String payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(qos);
        mqttClient.publish(topic, message);
    }

    public void setCallback(MqttCallback callback) {
        mqttClient.setCallback(callback);
    }
}
