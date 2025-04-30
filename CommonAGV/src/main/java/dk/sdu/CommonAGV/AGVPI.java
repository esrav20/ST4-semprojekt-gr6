package dk.sdu.CommonAGV;

import java.io.IOException;
import java.util.List;

public interface AGVPI {
    void connectionAGV(String URL) throws IOException;
    int getStatus();
    void sendRequest(String operationJson) throws IOException, InterruptedException;
    String getErrorcode();
    void pickItem(String itemId) throws IOException, InterruptedException;
    void putItem(String itemId) throws IOException, InterruptedException;
    List<String> getCarriedItems();
    boolean canCarryMoreItems();
}