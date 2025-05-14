package com.example.guidemo_4semester;

import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.Common.IMqttService;
import dk.sdu.CommonInventory.InventoryItems;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class TabViewController {

    // vi skal ikke have en setDepencies metode - da Spring ikke kan starte programmet uden Constructor-based DI.
    @Autowired
    public TabViewController(WarehouseClient warehouseClient, AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.warehouseClient = warehouseClient;
        this.agv = agv;
        this.iMqttService = iMqttService;
    }

    //Warehouse/Inventory:

    private final WarehouseClient warehouseClient;

    @FXML private TableView<InventoryItems> inventoryTable;
    @FXML private TableColumn<InventoryItems, Long> IDColumn;
    @FXML private TableColumn<InventoryItems, String> itemColumn;
    @FXML private TableColumn<InventoryItems, Integer> availableColumn;
    @FXML private TableColumn<InventoryItems, Integer> inStockColumn;
    @FXML private ChoiceBox<String> warehouseDropdown;
    @FXML private Circle databaseConnectionCircle;

    private ObservableList<InventoryItems> inventoryData = FXCollections.observableArrayList();

    private void setupTable() {
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        inventoryTable.setItems(inventoryData);
    }

    private void setupWarehouseDropdown() {
        warehouseDropdown.setItems(FXCollections.observableArrayList("Warehouse1", "Warehouse2"));
        warehouseDropdown.setOnAction(event -> loadInventory());
    }

    private void loadInventory() {
        try{
            inventoryData.clear();
            inventoryData.addAll(warehouseClient.getInventory());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Jeg kan love dig for load Inventory fejler du");
        }
    }

    @FXML
    private void handleAddButton() {
        // Handle add
    }

    @FXML
    private void handleRemoveButton() {
        /*InventoryItems selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            inventoryRepos.delete(selected);
            loadInventory();
        }*/
    }
    private void updateDatabaseConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            databaseConnectionCircle.setFill(Color.valueOf(connected ? "#1fff25" : "RED"));
        });
    }

    private void checkDatabaseConnection() {
        boolean isConnected = false;
        try {
            // To check connection
            warehouseClient.getInventory(); // If this succeeds, we're connected
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
        }
        updateDatabaseConnectionStatus(isConnected);
    }

    private void startDatabaseConnectionChecks() {
        Timeline dbCheckTimer = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> checkDatabaseConnection())
        );
        dbCheckTimer.setCycleCount(Animation.INDEFINITE);
        dbCheckTimer.play();
    }

    //---------------------------
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Circle assemblyConnectionCircle;
    @FXML private Circle assemblyStatusCircle;
    @FXML private Label assemblyStatusLabel;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;
    @FXML private TextFlow messageBoard;
    @FXML private TextField processIdInput;
    @FXML private Button checkHealthButton;

    private Timeline updateTimer;
    private int status;
    private final AGVPI agv;
    private final IMqttService iMqttService;




    private void appendMessageBoard(String text) {
        Platform.runLater(() -> {
            Text msg = new Text(text + "\n");
            messageBoard.getChildren().add(msg);
        });
    }

    @FXML
    public void initialize() throws MqttException {
        setupMqtt();
        startAGVUpdates();
        checkDatabaseConnection();
        startDatabaseConnectionChecks();
        startProdButton.setOnAction(event -> {
            int processId = Integer.parseInt(processIdInput.getText());
            try {
                iMqttService.initPublish(processId);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            setupTable();
            setupWarehouseDropdown();
            loadInventory();
        });

        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupMqtt() throws MqttException {
        MqttCallback mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                appendMessageBoard("MQTT Connection lost: " + cause.getMessage());
                updateConnectionStatus(false);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String msg = new String(message.getPayload());
                appendMessageBoard("MQTT message on [" + topic + "]: " + msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                appendMessageBoard("MQTT Delivery complete");
            }
        };

        iMqttService.setCallback(mqttCallback); // Set the callback
        iMqttService.connect();
        updateConnectionStatus(true);
    }

    private void updateConnectionStatus(boolean connected) {
        Platform.runLater(() ->
                assemblyConnectionCircle.setFill(Color.valueOf(connected ? "#1fff25" : "RED"))
        );
    }



    private void setStartProdButton() throws IOException, InterruptedException {

        agv.sendRequest("MoveToStorageOperation");
        updateAGVDisplay();
    }
    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> updateAGVDisplay())
        );
        updateTimer.setCycleCount(Animation.INDEFINITE);
        updateTimer.play();
    }

    private void updateAGVDisplay() {
        String statusText;
        String circleColor;
        switch (agv.getCurrentstate()) {
            case 1:
                statusText = "Idle";
                circleColor = "DODGERBLUE";
                break;
            case 2:
                statusText = "Working";
                circleColor = "#1fff25";
                break;
            case 3:
                statusText = "Charging";
                circleColor = "ORANGE";
                break;
            default:
                statusText = "Error";
                circleColor = "RED";
        }

        String connectionStatus = agv.isConnected() ? "#1fff25" : "RED";
        System.out.println(agv.isConnected());

        Platform.runLater(() -> {
            agvStatusLabel.textProperty().unbind(); // Vi får en runtimeException, når vi kører appen og denne er bound i forvejen.
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(Color.valueOf(circleColor));
            agvConnectionCircle.setFill(Color.valueOf(connectionStatus));
            agvParameterLabel.textProperty().unbind(); // samme som l.159.
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });


    }

}
