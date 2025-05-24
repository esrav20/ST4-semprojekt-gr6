package dk.sdu.Warehouse;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

//Data model til at display inventory i view

public class InventoryItems {

    private final SimpleStringProperty itemName;

    private final SimpleIntegerProperty quantity;

    //private final SimpleIntegerProperty trayId;
    public InventoryItems(String itemName, int quantity){
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
        //this.trayId = new SimpleIntegerProperty(trayId);
    }


    public String getItemName() {
        return itemName.get();
    }

    public int getQuantity(){
        return quantity.get();
    }

//    public int getTrayId() {
//        return trayId.get();
//    }
}
