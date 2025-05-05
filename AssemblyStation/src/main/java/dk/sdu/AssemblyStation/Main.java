//// mqtt-client/src/main/java/com/example/Main.java
//package dk.sdu;
//
//import dk.sdu.Common.IMqttService;
//i
//import org.eclipse.paho.client.mqttv3.MqttException;
//
//public class Main {
//    public static void main(String[] args) throws MqttException, InterruptedException {
//        IMqttConnection mqtt = new MqttMethods();
//
//        mqtt.connect("tcp://localhost:9001");
//        mqtt.subscribe("emulator/status",1);
//        mqtt.subscribe("emulator/checkhealth",2);
//        mqtt.publish("emulator/operation", "{\"ProcessID\": 9999}", 1);
//
//
//
//    }
//}
