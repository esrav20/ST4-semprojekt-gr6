package dk.sdu.CommonInventory;

import dk.sdu.CommonInventory.Service.SoapWarehouseService;
import org.junit.jupiter.api.Test;            // For writing test methods (JUnit 5)
import org.springframework.beans.factory.annotation.Autowired;  // For autowiring the service
import org.springframework.boot.test.context.SpringBootTest;  // For loading the Spring application context
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class SoapWarehouseServiceTest {
    @Autowired
    private SoapWarehouseService service;

    @Test
    void testServiceNotNull(){
        assertNotNull(service);
    }
}
