package dk.sdu.Warehouse.Service;

import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
import dk.sdu.Warehouse.InventoryItems;
import dk.sdu.Warehouse.InventoryRepos;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@ConfigurationProperties("service")
//connector vores repository til resten af applikationen
@Service
public class SoapWarehouseService implements WarehousePI {
    private final InventoryRepos inventoryRepos;

    // 0 = Idle, 1 = Executing, 2 = Error
    private volatile int currentState = 0;


    //private final IEmulatorService servicePort;
    @Autowired
    public SoapWarehouseService(@Lazy InventoryRepos inventoryRepos) {
        this.inventoryRepos = inventoryRepos;
    }


    //returnere inventory
    @Override
    public List<InventoryView> getInventory() {
        return inventoryRepos.findAllBy();
    }


    @Override
    public boolean isConnected() {
        try {
            // Attempt a simple operation to check if the database is accessible
            return inventoryRepos.count() >= 0;  // Check if repository is accessible
        } catch (Exception e) {
            // If any exception occurs, return false (disconnected)
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getWarehouseState() {
        return currentState;
    }

    //Handler at kunne indsætte items på trays
    @Override
    public String insertItem(int trayId, String itemName) {
        try {
        currentState = 1;
        InventoryItems item = new InventoryItems();
        item.setTrayId(trayId);
        item.setItemName(itemName);
        item.setQuantity(1);
        inventoryRepos.save(item);
        return "Done";
} catch (Exception e){
        currentState = 2;
        return "Insert Failed:" + e.getMessage();
        }
        finally {
            currentState = 0;
        }
    }



    //Kan finde/fjerne inventory i bestemt tray
    @Override
    public String pickItem(int trayId) {
        try {
            currentState = 1;

            // fetch entity, not projection
            InventoryItems item = (InventoryItems) inventoryRepos.findByTrayId(trayId)
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            item.setQuantity(item.getQuantity() - 1);

            if (item.getQuantity() <= 0) {
                inventoryRepos.delete(item);
                return "Item picked and tray now empty";
            } else {
                inventoryRepos.save(item);
                return "Item picked, remaining quantity: " + item.getQuantity();
            }
        } catch (Exception e) {
            currentState = 2;
            return "Pick Failed:" + e.getMessage();
        } finally {
            currentState = 0;
        }
    }

}

