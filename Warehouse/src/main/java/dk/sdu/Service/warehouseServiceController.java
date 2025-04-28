package dk.sdu.Service;

import dk.sdu.Service.soapWarehouseService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/inventory")
    public String getInventory() {
        return soapWarehouseService.getInventory();
    }

    @PostMapping("/insert")
    public String insertItem(@RequestParam int trayId, @RequestParam String itemName) {
        return soapWarehouseService.insertItem(trayId, itemName);
    }

    @PostMapping("/pick")
    public String pickItem(@RequestParam int trayId) {
        return soapWarehouseService.pickItem(trayId);
    }
}
