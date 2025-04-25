package dk.sdu;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AGVConnectionManager {
    private static AGVConnectionManager instance;
    private String baseUrl;

    private AGVConnectionManager() {}

    public static synchronized AGVConnectionManager getInstance() {
        if (instance == null) {
            instance = new AGVConnectionManager();
        }
        return instance;
    }

    public void initialize(String baseUrl) {
        if (this.baseUrl == null) { // Only initialize once
            this.baseUrl = baseUrl;
        }
    }

    public HttpURLConnection createConnection(String endpoint) throws IOException {
        URL fullUrl = new URL(baseUrl + endpoint);
        return (HttpURLConnection) fullUrl.openConnection();
    }

    public HttpURLConnection createConnection() throws IOException {
        return createConnection(""); // Use base URL as-is
    }
}
