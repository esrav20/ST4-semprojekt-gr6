module Core {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.paho.client.mqttv3;
    requires CommonAssemblyStation;
    requires AssemblyStation;


    opens com.example.guidemo_4semester to javafx.fxml;
    exports com.example.guidemo_4semester;
    exports dk.sdu;
}




