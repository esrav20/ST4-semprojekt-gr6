package dk.sdu.Warehouse.Service;

import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.Warehouse.InventoryItems;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@SpringBootApplication(scanBasePackages = "dk.sdu.CommonInventory.dk.sdu.Warehouse.Service")
@RestController
@RequestMapping("/warehouse")
public class warehouseServiceController {

    private SoapWarehouseService soapWarehouseService;

    public warehouseServiceController(SoapWarehouseService soapWarehouseService) {
        this.soapWarehouseService = soapWarehouseService;
    }

    //GET alt i inventory
    @GetMapping("/inventory")
    public List<InventoryView> getInventory() {
        return soapWarehouseService.getInventory();
    }

    //(POST)sæt et item ind i inventory tray
    @PostMapping("/insert")
    public String insertItem(@RequestParam int trayId, @RequestParam long id, @RequestParam String itemName, @RequestParam int quantity) {
        return soapWarehouseService.insertItem(trayId, id, itemName, quantity);
    }

    //(POST) vælg et tray og fjern item
    @PostMapping("/pick")
    public String pickItem(@RequestParam int trayId) {
        return soapWarehouseService.pickItem(trayId);
    }
}
