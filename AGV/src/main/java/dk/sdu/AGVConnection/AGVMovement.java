package dk.sdu.AGVConnection;

import dk.sdu.AGVPI;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class AGVMovement implements AGVPI {
    private static int status;
    private  AGVConnectionManager connectionManager = AGVConnectionManager.getInstance();
    private HttpURLConnection connection;
    private int currentState = 0;
    public static volatile int battery = 100;


    @Override
    public void connectionAGV(String URL) throws IOException {
        connectionManager.setBaseUrl(URL);
        connection = connectionManager.createConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
    }
    public AGVMovement() throws IOException {
    }
    public void sendRequest(String Operation) throws IOException, InterruptedException {
        connection.setRequestMethod("PUT");

        try(OutputStream OS = connection.getOutputStream()){
            byte[] input = Operation.getBytes("utf-8");
            OS.write(input, 0, input.length);
        }

        while (true) {
            if (currentState == 2) {
                Thread.sleep(5000);
                getRequest();
            }else {
                break;
            }
        }
    }
    public void getRequest() throws IOException {
        connection.setRequestMethod("GET");

        status = connection.getResponseCode();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }

            String jsonResponse = response.toString();
            int batteryStart = jsonResponse.indexOf("\"battery\":") + 10;
            int batteryEnd = jsonResponse.indexOf(",", batteryStart);
            battery = Integer.parseInt(jsonResponse.substring(batteryStart, batteryEnd));

            JSONObject json = new JSONObject(jsonResponse.toString());
            currentState = json.getInt("state");

            System.out.println("Response (" + status + "): " + response.toString());
        }
    }


    public int getStatus(){
        return status;
    }

}
