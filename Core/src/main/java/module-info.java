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


    opens com.example.guidemo_4semester;
    exports com.example.guidemo_4semester;
}




