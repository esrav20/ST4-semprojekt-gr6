package dk.sdu.CommonInventory;

import java.util.List;

public interface WarehousePI {
    String insertItem(int trayId, String itemName, int quantity);
    String pickItem(int trayId);
    List<InventoryView> getInventory();
}
