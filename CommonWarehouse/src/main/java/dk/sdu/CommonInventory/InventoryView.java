package dk.sdu.CommonInventory;

public interface InventoryView {
    Long getId();
    int getTrayId();
    String getItemName();
    int getQuantity();
}
