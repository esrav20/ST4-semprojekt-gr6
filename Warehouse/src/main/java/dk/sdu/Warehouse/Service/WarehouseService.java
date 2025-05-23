package dk.sdu.Warehouse.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouseclient.*;

import java.net.URL;

@Service
public class WarehouseService {

    private final IEmulatorService iEmulatorService;

    @Autowired
    public WarehouseService(IEmulatorService iEmulatorService) {
        this.iEmulatorService = iEmulatorService;
    }

    public String pickItem(int trayId) {
        return iEmulatorService.pickItem(trayId);
    }
    public String insertItem(int trayId, String itemName) {
        return iEmulatorService.insertItem(trayId, itemName);
    }
    public String getInventory() {
        return iEmulatorService.getInventory();
    }
}
