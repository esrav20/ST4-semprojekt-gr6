package dk.sdu.Warehouse.Service;

import org.springframework.web.bind.annotation.*;

//@SpringBootApplication(scanBasePackages = "dk.sdu.CommonInventory.dk.sdu.Warehouse.Service")
@RestController
@RequestMapping("/warehouse")

public class warehouseServiceController {

    private WarehouseService warehouseService;

    public warehouseServiceController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    //GET alt i inventory
    @GetMapping("/inventory")
    public String getInventory() {
        return warehouseService.getInventory();
    }

    //(POST)sæt et item ind i inventory tray
    @PostMapping("/insert")
    public String insertItem(@RequestParam int trayId, @RequestParam String itemName) {
//        return WarehouseService.insertItem(trayId, itemName);
        warehouseService.insertItem(trayId, itemName);
        return "item inserted into tray" + trayId;
   }

    //(POST) vælg et tray og fjern item
    @PostMapping("/pick")
    public String pickItem(@RequestParam int trayId) {
        return warehouseService.pickItem(trayId);
    }
}
