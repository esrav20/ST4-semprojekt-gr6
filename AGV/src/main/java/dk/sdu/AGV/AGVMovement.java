package dk.sdu.AGV;

import dk.sdu.CommonAGV.AGVPI;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;

public class AGVMovement implements AGVPI {

    private AGVConnectionManager connectionManager = AGVConnectionManager.getInstance();
    private int currentState = 0;
    private static volatile int battery = 100;
    private static int lastStatusCode;

    public AGVMovement() {}

    @Override
    public void connectionAGV(String url) throws IOException {
        connectionManager.setBaseUrl(url);
    }
    @Override
    public void sendRequest(String operationJson) throws IOException, InterruptedException {
        HttpURLConnection connection = connectionManager.createConnection();
        connection.setRequestMethod("PUT");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = operationJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        lastStatusCode = connection.getResponseCode();
        connection.disconnect();
        // This is needed if the request isn't able to be recieved
        if (currentState == 2) {
            Thread.sleep(5000);
            getRequest();
            sendRequest(operationJson);
        }

    }

    public void getRequest() throws IOException {
        HttpURLConnection connection = connectionManager.createConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        lastStatusCode = connection.getResponseCode();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }

            String jsonResponse = response.toString();
            JSONObject json = new JSONObject(jsonResponse);
            currentState = json.getInt("state");
            battery = json.getInt("battery");

            System.out.println("Response (" + lastStatusCode + "): " + jsonResponse);
        }

        connection.disconnect();
    }

    @Override
    public int getStatus() {
        return lastStatusCode;
    }
}
