package dk.sdu;

import dk.sdu.AssemblyStation.MqttService;
import dk.sdu.Common.IMqttService;

public class MqttServiceProvider {
    private static final IMqttService mqttService = new MqttService();

    private static IMqttService getMqttService() {
        return mqttService;
    }
}
