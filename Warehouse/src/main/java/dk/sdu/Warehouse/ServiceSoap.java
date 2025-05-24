package dk.sdu.Warehouse;

import warehouseclient.IEmulatorService;
import warehouseclient.IEmulatorService_Service;
import org.springframework.stereotype.Service;


import jakarta.annotation.PostConstruct;

@Service
public class ServiceSoap {

    private IEmulatorService port;

    //lav service og hent port
   @PostConstruct
    public void init() {
        IEmulatorService_Service service = new IEmulatorService_Service();
        this.port = service.getBasicHttpBindingIEmulatorService();
    }

    public boolean isConnected(){
        try{
            port.getInventory();
            return true;
        } catch (Exception e){
            return false;
        }
}
    public String insertItem(int trayId, String name) {
        return port.insertItem(trayId, name);
    }

    public String pickItem(int trayId) {
        return port.pickItem(trayId);
    }

    public String getInventory() {
        return port.getInventory(); // JSON string
    }
}
