package dk.sdu.Warehouse.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import warehouseclient.IEmulatorService;
import warehouseclient.IEmulatorService_Service;

@Configuration
public class SoapClientConfig {

    @Bean
    public IEmulatorService iEmulatorService() {
        IEmulatorService_Service service = new IEmulatorService_Service();
        return service.getBasicHttpBindingIEmulatorService();
    }
}
