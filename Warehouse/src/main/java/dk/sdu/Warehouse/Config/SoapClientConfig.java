package dk.sdu.Warehouse.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import warehouseclient.IEmulatorService;
import warehouseclient.IEmulatorService_Service;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration (proxyBeanMethods = false)
public class SoapClientConfig {

    public SoapClientConfig(){

    }

    @Bean
    public IEmulatorService iEmulatorService() throws MalformedURLException {
        URL wsdlURL = new URL("http://localhost:8081/Service?wsdl");
        IEmulatorService_Service service = new IEmulatorService_Service(wsdlURL);
        return service.getBasicHttpBindingIEmulatorService();
    }
}
