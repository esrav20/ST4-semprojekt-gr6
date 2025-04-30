module Core {
    requires javafx.controls;
    requires javafx.fxml;
    requires AGV;
    requires CommonAGV;

    opens com.example.guidemo_4semester to javafx.fxml;
    exports com.example.guidemo_4semester;
}




