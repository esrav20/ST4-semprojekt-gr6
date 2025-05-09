module Core {
    requires javafx.controls;
    requires javafx.fxml;
    requires AGV;
    requires CommonAGV;
    requires CommonAssemblyStation;
    requires org.eclipse.paho.client.mqttv3;
    requires AssemblyStation;

    opens com.example.guidemo_4semester to javafx.fxml;
    exports com.example.guidemo_4semester;
}




