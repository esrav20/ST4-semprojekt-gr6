package dk.sdu.Warehouse.Service;

import com.example.generated.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SoapWarehouseService implements WarehousePI {

    private final IEmulatorService port;
    private final ObjectMapper mapper = new ObjectMapper();

    public SoapWarehouseService() {
        IEmulatorService_Service svc = new IEmulatorService_Service();
        // choose the BasicHttpBinding endpoint
        this.port = svc.getBasicHttpBindingIEmulatorService();
    }

    @Override
    public List<InventoryView> getInventory() {
        // SOAP call returns a JSON string
        String json = port.getInventory();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode inv = root.get("Inventory");
            List<InventoryView> list = new ArrayList<>();
            if (inv.isArray()) {
                for (JsonNode trayMap : inv) {
                    // each trayMap is an object: keys are trayIds, values are names
                    trayMap.fieldNames().forEachRemaining(k -> {
                        JsonNode nameNode = trayMap.get(k);
                        int trayId = Integer.parseInt(k);
                        String itemName = nameNode.isNull() ? "" : nameNode.asText();
                        // no quantity field in JSON, default to 1
                        list.add(new SimpleInventoryView(null, trayId, itemName, 1));
                    });
                }
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse inventory JSON", e);
        }
    }

    @Override
    public String insertItem(int trayId, long id, String itemName, int quantity) {
        // SOAP only takes trayId + name
        return port.insertItem(trayId, itemName);
    }

    @Override
    public String pickItem(int trayId) {
        return port.pickItem(trayId);
    }

    @Override
    public String deleteitems(Long id) {
        // not supported by SOAP emulator
        throw new UnsupportedOperationException("deleteitems not implemented in SOAP warehouse");
    }

    @Override
    public void updateItem(long id, String itemName, int quantity) {
        // not supported by SOAP emulator
        throw new UnsupportedOperationException("updateItem not implemented in SOAP warehouse");
    }

    @Override
    public boolean isConnected() {
        try {
            port.getInventory();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getWarehouseState() {
        // parse the "State" field out of the same JSON
        String json = port.getInventory();
        try {
            JsonNode root = mapper.readTree(json);
            return root.path("State").asInt(0);
        } catch (IOException e) {
            return 0;
        }
    }

    // Simple InventoryView implementation
    private static class SimpleInventoryView implements InventoryView {
        private final Long id;
        private final int trayId;
        private final String itemName;
        private final int quantity;

        SimpleInventoryView(Long id, int trayId, String itemName, int quantity) {
            this.id = id;
            this.trayId = trayId;
            this.itemName = itemName;
            this.quantity = quantity;
        }

        @Override public Long getId()           { return id; }
        @Override public int getTrayId()        { return trayId; }
        @Override public String getItemName()   { return itemName; }
        @Override public int getQuantity()      { return quantity; }
    }
}
