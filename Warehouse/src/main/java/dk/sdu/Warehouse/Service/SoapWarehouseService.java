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

    //Handler at kunne indsætte items på trays
    @Override
    public String insertItem(int trayId, String itemName) {
        InventoryItems item = new InventoryItems();
        item.setTrayId(trayId);
        item.setItemName(itemName);
        item.setQuantity(1);
        inventoryRepos.save(item);
        return "Done";
    }

    //Kan finde/fjerne inventory i bestemt tray
    @Override
    public String pickItem(int trayId) {
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
    }

}

