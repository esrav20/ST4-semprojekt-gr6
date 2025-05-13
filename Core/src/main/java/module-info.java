module Core {
    requires javafx.controls;
    requires javafx.fxml;
    requires CommonWarehouse;
    requires CommonAGV;
    requires CommonAssemblyStation;
    requires org.eclipse.paho.client.mqttv3;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires jdk.compiler;
    requires spring.context;
    requires spring.beans;
    requires AssemblyStation;
    requires AGV;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires spring.web;
    requires spring.data.jpa;
    requires spring.tx;


    opens com.example.guidemo_4semester to javafx.fxml, spring.core, spring.beans, spring.context;

    exports com.example.guidemo_4semester;

}




