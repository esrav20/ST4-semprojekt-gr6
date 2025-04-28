package dk.sdu;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface IMqttConnection {

    void connect();
    void disconnect();
    void subscribe(String topic, int qos, MessageHandler handler) throws MqttException;
    void publish(String topic, String payload, int qos) throws MqttException;
}
