package dk.sdu.Warehouse.Service;


import org.springframework.stereotype.Service;
import warehouseclient.*;

import java.net.URL;

@Service
public class WarehouseService {

    private final IEmulatorService iEmulatorService;
    public WarehouseService() throws Exception {
        URL wsdlURL = new URL("http://localhost:8081/Service?wsdl");
        IEmulatorService_Service service = new IEmulatorService_Service(wsdlURL);
        this.iEmulatorService = service.getBasicHttpBindingIEmulatorService();
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
