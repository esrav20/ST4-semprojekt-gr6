package dk.sdu.Warehouse;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

//Data model til at display inventory i view

public class InventoryItems {

    private final SimpleStringProperty itemName;

    private final SimpleIntegerProperty quantity;

    public InventoryItems(String itemName, int quantity){
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
    }


    public String getItemName() {
        return itemName.get();
    }

    public int getQuantity(){
        return quantity.get();
    }

}
