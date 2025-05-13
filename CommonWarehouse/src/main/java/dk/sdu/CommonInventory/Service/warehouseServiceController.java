package dk.sdu.CommonInventory.Service;

import dk.sdu.CommonInventory.InventoryItems;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@SpringBootApplication(scanBasePackages = "dk.sdu.CommonInventory.Service")
@RestController
@RequestMapping("/warehouse")
public class warehouseServiceController {

    private SoapWarehouseService soapWarehouseService;

   /* public void WarehouseController(SoapWarehouseService soapWarehouseService) {
        this.soapWarehouseService = soapWarehouseService;
    }*/

    public warehouseServiceController(SoapWarehouseService soapWarehouseService) {
        this.soapWarehouseService = soapWarehouseService;
    }

    //GET alt i inventory
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
