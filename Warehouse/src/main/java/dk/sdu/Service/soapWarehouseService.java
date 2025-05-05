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

    //returnere en string som er inventory
    public String getInventory() {
        return servicePort.getInventory();
    }

    //Handler at kunne indsætte items på trays
    public String insertItem(int trayId, String itemName) {
        return servicePort.insertItem(trayId, itemName);
    }

    //Kan finde/fjerne inventory i bestemt tray
    public String pickItem(int trayId) {
        return servicePort.pickItem(trayId);
    }
}
