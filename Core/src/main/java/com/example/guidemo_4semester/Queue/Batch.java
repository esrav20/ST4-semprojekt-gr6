package com.example.guidemo_4semester.Queue;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Batch {
    private final SimpleIntegerProperty batchID;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty quantity;
    private final SimpleIntegerProperty priority;
    private final SimpleStringProperty status;

    public Batch(int batchID, String productName, String quantity, int priority, String status) {
        this.batchID = new SimpleIntegerProperty(batchID);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleStringProperty(quantity);
        this.priority = new SimpleIntegerProperty(priority);
        this.status = new SimpleStringProperty(status);
    }

    public int getBatchID() { return batchID.get(); }
    public String getProductName() { return productName.get(); }
    public String getQuantity() { return quantity.get(); }
    public int getPriority() { return priority.get(); }
    public String getStatus() { return status.get(); }

    public void setBatchID(int id) { batchID.set(id); }
    public void setProductName(String name) { productName.set(name); }
    public void setQuantity(String qty) { quantity.set(qty); }
    public void setPriority(int prio) { priority.set(prio); }
    public void setStatus(String stat) { status.set(stat); }
}

