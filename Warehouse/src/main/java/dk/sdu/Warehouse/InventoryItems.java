package dk.sdu.Warehouse;

import jakarta.persistence.*;

//Model af layout af database tables
@Entity
@Table(name = "inventory_items")
public class InventoryItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int trayId;
    private String itemName;
    private int quantity;


    //getter og setters
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public int getTrayId(){
        return trayId;
    }

    public void setTrayId(int trayId){
        this.trayId = trayId;
    }

    public String getItemName(){
        return itemName;
    }

    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}
