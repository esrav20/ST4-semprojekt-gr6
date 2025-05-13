package dk.sdu.CommonInventory.Service;

import dk.sdu.CommonInventory.InventoryItems;
import dk.sdu.CommonInventory.InventoryRepos;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

//@ConfigurationProperties("service")
//connector vores repository til resten af applikationen
@Service
public class SoapWarehouseService {
    private final InventoryRepos inventoryRepos;

    //private final IEmulatorService servicePort;
    @Autowired
    public SoapWarehouseService(InventoryRepos inventoryRepos) {
        this.inventoryRepos = inventoryRepos;
    }


    //returnere inventory
    public List<InventoryItems> getInventory() {
        return inventoryRepos.findAll();
    }

    //Handler at kunne indsætte items på trays
    public InventoryItems insertItem(int trayId, String itemName) {
        InventoryItems item = new InventoryItems();
        item.setTrayId(trayId);
        item.setItemName(itemName);
        item.setQuantity(1);
        return inventoryRepos.save(item);
    }


    //Kan finde/fjerne inventory i bestemt tray
    public String pickItem(int trayId) {
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
}

