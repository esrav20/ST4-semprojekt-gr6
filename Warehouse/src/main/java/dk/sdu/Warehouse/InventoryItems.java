package dk.sdu.Warehouse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryItems implements InventoryView {

    private final SimpleStringProperty itemName;
    private final SimpleIntegerProperty quantity;
    private final SimpleIntegerProperty trayId;

    public InventoryItems(String itemName, int quantity){
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.trayId = new SimpleIntegerProperty(0);
    }

    @Override
    public int getTrayId() {
        return trayId.get();
    }

    public IntegerProperty trayIdProperty() {
        return trayId;
    }

    @Override
    public String getItemName() {
        return itemName.get();
    }

    public SimpleStringProperty itemNameProperty() { // Add property getter
        return itemName;
    }

    @Override
    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int newQuantity){
        this.quantity.set(newQuantity);
    }

    public SimpleIntegerProperty quantityProperty(){ // Add property getter
        return quantity;
    }
}