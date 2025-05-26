package dk.sdu.CommonAGV;

import java.io.IOException;
import java.util.List;

public interface AGVPI {
    void connectionAGV(String URL) throws IOException;
    int getCurrentstate();
    void sendRequest(String operationJson) throws IOException, InterruptedException;
    void pickItem(String itemId) throws IOException, InterruptedException;
    void putItem(String itemId) throws IOException, InterruptedException;
    int getBatteryLevel();
    void needsCharging() throws IOException, InterruptedException;
    void charge() throws IOException, InterruptedException;
    boolean isConnected();
}