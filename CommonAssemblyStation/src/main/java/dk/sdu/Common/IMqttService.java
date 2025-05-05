package dk.sdu.Common;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface  IMqttService {

    void connect() throws MqttException;
    void disconnect() throws MqttException;
    void publish(String topic, String payload, int qos) throws MqttException;
}
