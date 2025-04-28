package dk.sdu.Service;

import com.example.generated.IEmulatorService;
import com.example.generated.IEmulatorService_Service;
import org.springframework.stereotype.Service;

@Service
public class soapWarehouseService {

    private final IEmulatorService servicePort;

    public soapWarehouseService() {
        IEmulatorService_Service service = new IEmulatorService_Service();
        this.servicePort = service.getBasicHttpBindingIEmulatorService();
    }

    public soapWarehouseService(IEmulatorService servicePort) {
        this.servicePort = servicePort;
    }

    public String getInventory() {
        return servicePort.getInventory();
    }

    public String insertItem(int trayId, String itemName) {
        return servicePort.insertItem(trayId, itemName);
    }

    public String pickItem(int trayId) {
        return servicePort.pickItem(trayId);
    }
}
