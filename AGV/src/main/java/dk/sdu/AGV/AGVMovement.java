package dk.sdu.AGV;

import dk.sdu.CommonAGV.AGVPI;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AGVMovement implements AGVPI {
    private AGVConnectionManager connectionManager = AGVConnectionManager.getInstance();
    private int currentState;
    private static volatile int battery = 100;
    private static int lastStatusCode;
    private static String status;
    private List<String> carriedItems = new ArrayList<>();
    private final int MAX_ITEMS_CAPACITY = 10;
    private final int battery_treshold = 20;
    private boolean connected;

    @Override
    public int getStatus() {
        return lastStatusCode;
    }
    @Override
    public String getErrorcode(){
        return status;
    }
    @Override
    public int getCurrentstate(){
        return currentState;
    }
    @Override
    public int getBatteryLevel() {
        return battery;
    }

    @Override
    public void needsCharging() throws IOException, InterruptedException {
        if(battery < battery_treshold){
            charge();
        };
    }

    @Override
    public void charge() throws IOException, InterruptedException {
        System.out.println("Low battery (" + battery + "%), moving to charger");

        // Move to charger
        sendRequest("{\"Program name\":\"MoveToChargerOperation\",\"State\":1}");
        Thread.sleep(1000);

        // Start charging
        sendRequest("{\"State\":2}");
        Thread.sleep(10000);

        System.out.println("Charging complete. Battery level: " + battery + "%");
    }
    @Override
    public void connectionAGV(String url) throws IOException {
        connectionManager.setBaseUrl(url);
        connected = true;
    }
    @Override
    public boolean isConnected(){
        return connected;
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
        status = connection.getResponseMessage();
        getRequest();
        connection.disconnect();
        // This is needed if the request isn't able to be recieved
        while(true) {
            if (currentState == 2) {
                System.out.println("Busy");
                Thread.sleep(5000);
                getRequest();
                sendRequest(operationJson);
            }
            else {
                break;
            }
        }
        connected = true;

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
    public void pickItem(String itemId) throws IOException, InterruptedException {
        if (carriedItems.size() >= MAX_ITEMS_CAPACITY) {
            throw new IllegalStateException("AGV cannot carry more items");
        }

        JSONObject pickOperation = new JSONObject();
        pickOperation.put("operation", "pick");
        pickOperation.put("itemId", itemId);

        sendRequest(pickOperation.toString());
        carriedItems.add(itemId);
    }

    @Override
    public void putItem(String itemId) throws IOException, InterruptedException {
        if (!carriedItems.contains(itemId)) {
            throw new IllegalArgumentException("Item not carried by AGV");
        }

        JSONObject putOperation = new JSONObject();
        putOperation.put("operation", "drop");
        putOperation.put("itemId", itemId);

        sendRequest(putOperation.toString());
        carriedItems.remove(itemId);
    }

    @Override
    public List<String> getCarriedItems() {
        return Collections.unmodifiableList(carriedItems);
    }

    @Override
    public boolean canCarryMoreItems() {
        return carriedItems.size() < MAX_ITEMS_CAPACITY;
    }
}
