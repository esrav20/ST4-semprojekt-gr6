package dk.sdu.AGVConnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AGVConnectionManager {
    private static AGVConnectionManager instance;
    private String baseUrl;


    private AGVConnectionManager() {
        this.baseUrl = "http://localhost:8082/v1/status/";
    }

    public static synchronized AGVConnectionManager getInstance() {
        if (instance == null) {
            instance = new AGVConnectionManager();
        }
        return instance;
    }


    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpURLConnection createConnection(String endpoint) throws IOException {
        URL fullUrl = new URL(baseUrl + endpoint);
        return (HttpURLConnection) fullUrl.openConnection();
    }

    // General connection without additional endpoint (uses base URL)
    public HttpURLConnection createConnection() throws IOException {
        return createConnection("");
    }
}