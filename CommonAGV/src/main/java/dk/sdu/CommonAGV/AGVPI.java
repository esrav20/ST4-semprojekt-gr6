package dk.sdu.CommonAGV;

import java.io.IOException;

public interface AGVPI {
    void connectionAGV(String URL) throws IOException;
    int getStatus();
    void sendRequest(String operationJson) throws IOException, InterruptedException;
}
