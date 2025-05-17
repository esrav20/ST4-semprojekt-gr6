package com.example.guidemo_4semester.Queue;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

public class Batch {
    private final SimpleIntegerProperty batchID;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty priority; // Changed from Integer to String
    private final SimpleStringProperty status;

    public Batch(int batchID, String productName, int quantity, String priority, String status) {
        this.batchID = new SimpleIntegerProperty(batchID);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.priority = new SimpleStringProperty(priority);
        this.status = new SimpleStringProperty(status);
    }

    public int getBatchID() { return batchID.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getPriority() { return priority.get(); }
    public String getStatus() { return status.get(); }

    public void setBatchID(int id) { batchID.set(id); }
    public void setProductName(String name) { productName.set(name); }
    public void setQuantity(int qty) { quantity.set(qty); }
    public void setPriority(String prio) { priority.set(prio); }
    public void setStatus(String stat) { status.set(stat); }

    public SimpleIntegerProperty batchIDProperty() { return batchID; }
    public SimpleStringProperty productNameProperty() { return productName; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public StringProperty priorityProperty() { return priority; }
    public SimpleStringProperty statusProperty() { return status; }
}