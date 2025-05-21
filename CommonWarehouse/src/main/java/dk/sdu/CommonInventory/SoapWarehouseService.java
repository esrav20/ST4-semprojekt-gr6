package dk.sdu.CommonInventory;

import java.util.List;

public interface SoapWarehouseService {

    String insertItem(int trayId, long id, String itemName, int quantity);
    String pickItem(int trayId);
    List<InventoryView> getInventory();
    String deleteitems(Long id);
    void updateItem(long id, String itemName, int quantity);
    boolean isConnected();
    int getWarehouseState();
}