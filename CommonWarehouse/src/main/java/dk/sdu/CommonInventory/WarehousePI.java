package dk.sdu.CommonInventory;

import java.util.List;

public interface WarehousePI {
    String insertItem(int trayId, String itemName);
    String pickItem(int trayId);
    List<InventoryView> getInventory();
    boolean isConnected();
    int getWarehouseState();
    String deleteitems(Long id);
    void updateItem(long id, String itemName, int quantity);


}
