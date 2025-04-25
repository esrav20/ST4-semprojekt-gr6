package dk.sdu.AGVConnection;
import java.io.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;



public class AGVConnection {
    public static volatile int currentState;
    public static volatile int battery = 100;
    public static boolean isConnected = true;

    public static void main(String[] args) throws IOException, InterruptedException {

       /* SendRequestPUT("http://localhost:8082/v1/status/", "{\"Program name\":\"MoveToAssemblyOperation\",\"State\":1}");
       SendRequestPUT("http://localhost:8082/v1/status/", "{\"State\":2}");
        GetRequest("http://localhost:8082/v1/status/");*/
        AGVConnectionManager.getInstance().setBaseUrl("http://localhost:8082/v1/status/");
        AGVLoop("http://localhost:8082/v1/status/");

    }


    public static void SendRequestPUT(String URLStr, String Operation) throws IOException, InterruptedException {
        AGVConnectionManager connectionManager = AGVConnectionManager.getInstance();
        HttpURLConnection con1 = null;
        try {
            con1 = connectionManager.createConnection();
            con1.setRequestMethod("PUT");
            con1.setConnectTimeout(5000);
            con1.setReadTimeout(5000);
            con1.setDoOutput(true);
            con1.setRequestProperty("Content-Type", "application/json");

            try(OutputStream OS = con1.getOutputStream()){
                byte[] input = Operation.getBytes("utf-8");
                OS.write(input, 0, input.length);
            }

            while (true) {
                if (currentState == 1 || currentState == 3) {
                    break; // it's idle, safe to send new command
                }
                System.out.println("AGV busy... waiting.");
                Thread.sleep(3000);
                GetRequest(URLStr); // Refresh actual currentState from server
            }


            int responseCode = con1.getResponseCode();
            InputStream responseStream;

            if (responseCode >= 400) {
                responseStream = con1.getErrorStream();
                System.out.println("❌ Error code: " + responseCode);
            } else {
                responseStream = con1.getInputStream();
                System.out.println("✅ Success code: " + responseCode);
            }
        } finally {
            if (con1 != null) {
                con1.disconnect();
            }
        }
    }

// Read the response
        /*if (responseStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                System.out.println("Response body: " + response.toString());
            }
        }*/


        /* con1.getInputStream();*/
        /*try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con1.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response: " + response.toString());

    }*/


    public static void GetRequest(String URLstr) throws IOException {
        AGVConnectionManager connectionManager = AGVConnectionManager.getInstance();
        HttpURLConnection con1 = null;

        try {
            // Create connection using singleton
            con1 = connectionManager.createConnection();
            con1.setRequestMethod("GET");
            con1.setConnectTimeout(5000);
            con1.setReadTimeout(5000);
            con1.setRequestProperty("Content-Type", "application/json");

            int status = con1.getResponseCode();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con1.getInputStream(), "utf-8"))) {
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
        } finally {
            if (con1 != null) {
                con1.disconnect();
            }
        }
    }
    public static void AGVLoop(String URLstr) throws IOException, InterruptedException {
        String[] operations = {
                "MoveToStorageOperation",
                "PickWarehouseOperation",
                "MoveToAssemblyOperation",
                "PutAssemblyOperation",
                "PutWarehouseOperation"
        };

        while(true) {
            for(String op : operations) {

                    // Get battery level
                    // Get battery level

//                    System.out.println("Battery: " + battery + "%");

                    // Handle low battery
                    if(battery < 20) {
                        System.out.println("Low battery, moving to charger");
                        GetRequest(URLstr);
                        SendRequestPUT(URLstr, "{\"Program name\":\"MoveToChargerOperation\",\"State\":1}");
                        Thread.sleep(1000); // Vigtig forsinkelse
                        GetRequest(URLstr);
                        SendRequestPUT(URLstr, "{\"State\":2}");
                        Thread.sleep(10000); // Vent på ladning

                        continue;
                    }

                    // Execute operation
                    System.out.println("Execute operation: " + op);
                GetRequest(URLstr);
                    SendRequestPUT(URLstr, "{\"Program name\":\"" + op + "\",\"State\":1}");
                    Thread.sleep(1000); // Vigtig forsinkelse mellem state changes
                GetRequest(URLstr);
                    SendRequestPUT(URLstr, "{\"State\":2}");

                    Thread.sleep(3000); // Vent på operation completion

                }
            }
        }
        public static void updateStatus() {
        try {
            GetRequest("http://localhost:8082/v1/status/");
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }
    }

    }


