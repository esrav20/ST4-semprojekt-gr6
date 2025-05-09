module AssemblyStation {
    exports dk.sdu.AssemblyStation.Services;
    requires org.eclipse.paho.client.mqttv3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires CommonAssemblyStation;
    requires com.google.gson;
    requires spring.context;

}