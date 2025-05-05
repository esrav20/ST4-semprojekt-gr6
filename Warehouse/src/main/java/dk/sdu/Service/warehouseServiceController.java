package dk.sdu.Service;

import dk.sdu.InventoryItems;
import dk.sdu.Service.soapWarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class warehouseServiceController {

    private soapWarehouseService soapWarehouseService;

    public void WarehouseController(soapWarehouseService soapWarehouseService) {
        this.soapWarehouseService = soapWarehouseService;
    }

    public warehouseServiceController(dk.sdu.Service.soapWarehouseService soapWarehouseService) {
        this.soapWarehouseService = soapWarehouseService;
    }

    //GET all inventory
    @GetMapping("/inventory")
    public List<InventoryItems> getInventory() {
        return soapWarehouseService.getInventory();
    }

    //(POST)sæt et item ind i inventory tray
    @PostMapping("/insert")
    public InventoryItems insertItem(@RequestParam int trayId, @RequestParam String itemName) {
        return soapWarehouseService.insertItem(trayId, itemName);
    }

    //(POST) vælg et tray og fjern item
    @PostMapping("/pick")
    public String pickItem(@RequestParam int trayId) {
        return soapWarehouseService.pickItem(trayId);
    }
}
