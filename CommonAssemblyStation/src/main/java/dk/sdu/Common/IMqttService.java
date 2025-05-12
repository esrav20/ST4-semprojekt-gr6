package dk.sdu.Common;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.function.BiConsumer;

public interface  IMqttService {

    void connect() throws MqttException;
    void disconnect() throws MqttException;
    void initPublish(int processId) throws MqttException;
    void publish(String topic, String payload) throws MqttException;
    void setCallback(MqttCallback mqttCallback) throws MqttException;
    boolean isConnected();
    void setMessagehandler(BiConsumer<Integer, Boolean> handler);
}
