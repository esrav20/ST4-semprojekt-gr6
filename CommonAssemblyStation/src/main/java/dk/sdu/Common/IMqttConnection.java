package dk.sdu.Common;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface IMqttConnection {

    void connect(String Brokerurl) throws MqttException;
    void disconnect() throws MqttException;
    void subscribe(String topic, int qos) throws MqttException;
    void publish(String topic, String payload, int qos) throws MqttException;
}
