package dk.sdu.Warehouse.Service;

import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
import dk.sdu.Warehouse.InventoryItems;
import dk.sdu.Warehouse.InventoryRepos;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

//@ConfigurationProperties("service")
//connector vores repository til resten af applikationen
@Service
public class SoapWarehouseService implements WarehousePI {
    private final InventoryRepos inventoryRepos;
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
            return inventoryRepos.count() >= 0;
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
    public String insertItem(int trayId, long id, String itemName,int quantity) {
        Optional<InventoryItems> existingitem = inventoryRepos.findById(id);

        if (existingitem.isPresent()) {
            InventoryItems item = existingitem.get();
            item.setQuantity(item.getQuantity() + quantity); // <- den her er vigtig!
            inventoryRepos.save(item);
            return "Updated quantity on existing item (ID " + id + ")";

        } else {
            InventoryItems newItem = new InventoryItems();
            newItem.setId(id); // Bemærk: kræver du selv holder styr på unikke IDs!
            newItem.setTrayId(trayId);
            newItem.setItemName(itemName);
            newItem.setQuantity(quantity);
            inventoryRepos.save(newItem);
            return "Inserted new item (ID " + id + ")";
        }
    }

    //Kan finde/fjerne inventory i bestemt tray
    @Override
    public String pickItem(int trayId) {
        // fetch entity, not projection
        InventoryItems item = inventoryRepos.findByTrayId(trayId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() <= 0) {
            inventoryRepos.delete(item);
            return "Item picked and tray now empty";
        } else {
            inventoryRepos.save(item);
            return "Item picked, remaining quantity: " + item.getQuantity();

        }
    }


    @Override
    public String deleteitems(Long id) {
        InventoryItems item = inventoryRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        inventoryRepos.delete(item);
        return "Item deleted";
    }

    @Override
    public void updateItem(long id, String itemName, int quantity) {
        Optional<InventoryItems> itemOpt = inventoryRepos.findById(id);
        if (itemOpt.isPresent()) {
            InventoryItems item = itemOpt.get();
            item.setId(id);
            item.setItemName(itemName);
            item.setQuantity(quantity);
            inventoryRepos.save(item);
        }
    }

}