package com.example.guidemo_4semester;

import com.example.guidemo_4semester.Queue.Batch;
import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.Common.IMqttService;
import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Comparator;

@Component
public class TabViewController {

    private final AGVPI agv;
    private final IMqttService iMqttService;
    private final WarehousePI warehouseClient;


    // vi skal ikke have en setDepencies metode - da Spring ikke kan starte programmet uden Constructor-based DI.
    @Autowired
    public TabViewController(WarehousePI warehouseClient, AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.warehouseClient = warehouseClient;
        this.agv = agv;
        this.iMqttService = iMqttService;
    }

    //Warehouse/Inventory:



    @FXML private TableView<InventoryView> inventoryTable;
    @FXML private TableColumn<InventoryView, Long> IDColumn;
    @FXML private TableColumn<InventoryView, String> itemColumn;
    @FXML private TableColumn<InventoryView, Integer> availableColumn;
    @FXML private TableColumn<InventoryView, Integer> inStockColumn;
    @FXML private ChoiceBox<String> warehouseDropdown;

    private ObservableList<InventoryView> inventoryData = FXCollections.observableArrayList();

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
    @FXML private Label HealthyLabel;
    @FXML private RadioButton normalPriorityButton;
    @FXML private RadioButton highPriorityButton;
    @FXML private TextField quantityInput;
    @FXML private ChoiceBox productChoice;
    @FXML private TableView<Batch> queueView;
    @FXML private TableColumn<Batch, Integer> batchID;
    @FXML private TableColumn<Batch, String> productQueue;
    @FXML private TableColumn<Batch, String> quantityQueue;
    @FXML private TableColumn<Batch, Integer> priorityQueue;
    @FXML private TableColumn<Batch, String> statusQueue;
    private int batchCounter = 1;
    String[] productList = {"Toy Cars1", "Toy Cars2"};
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private SortedList<Batch> sortedList;

    private Timeline updateTimer;
    private int status;

    @FXML
    private void addQueue(ActionEvent event) {
        int queuePriority = normalPriorityButton.isSelected() ? 5 : (highPriorityButton.isSelected() ? 10 : 0);
        String queueQuantity = quantityInput.getText();
        String queueProduct = productChoice.getValue().toString();

        if (queueQuantity.isEmpty() || queueProduct == null || queuePriority == 0) {
            System.out.println("Please fill all inputs correctly.");
            return;
        }

        Batch newBatch = new Batch(batchCounter++, queueProduct, queueQuantity, queuePriority, "Pending");
        batchList.add(newBatch);

        System.out.println("QueueQuantity: " + queueQuantity + ", QueueProduct: " + queueProduct + ", QueuePriority: " + queuePriority);
        quantityInput.clear();
    }


    @FXML
    public void initialize() throws MqttException {
        batchID.setCellValueFactory(new PropertyValueFactory<>("batchID"));
        productQueue.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityQueue.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priorityQueue.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusQueue.setCellValueFactory(new PropertyValueFactory<>("status"));
        sortedList = new SortedList<>(batchList);
        // Comparator: High priority (10) come before Normal (5)
        sortedList.setComparator(
                Comparator.comparingInt(Batch::getPriority).reversed()
                        .thenComparingInt(Batch::getBatchID)
        );
        queueView.setItems(sortedList);

        productChoice.setItems(FXCollections.observableArrayList(productList));
        iMqttService.setMessagehandler((state,health) -> {
            Platform.runLater(() -> {
                if (state != null) {
                    String text = switch (state) {
                        case 1 -> "Running";
                        case 0 -> "Idle";
                        default -> "Unknown";
                    };
                    assemblyStatusLabel.textProperty().unbind();
                    assemblyStatusLabel.setText(text);
                    assemblyStatusCircle.setFill(text.equals("Running") ? Color.valueOf("#1fff25"): Color.DODGERBLUE);
                }

                if (health != null) {
                    HealthyLabel.textProperty().unbind();
                    HealthyLabel.setText(String.valueOf(health));
                }
            });
        });

        startAGVUpdates();
        iMqttService.connect();
        setupTable();
        setupWarehouseDropdown();
        loadInventory();
        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
                iMqttService.publish("emulator/operation",  "{\"ProcessID\": 12345}");
            } catch (IOException | InterruptedException | MqttException e) {
                e.printStackTrace();
            }
        });


    }



    private void setStartProdButton() throws IOException, InterruptedException {

        agv.sendRequest("MoveToStorageOperation");
        updateAGVDisplay();

        String t = warehouseClient.insertItem(2,"hej");
        System.out.println(t);
    }
    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e ->{
                    updateAGVDisplay();
                    updateAssemblyConnectionStatus();
                })
        );
        updateTimer.setCycleCount(Animation.INDEFINITE);
        updateTimer.play();
    }

    private void updateAssemblyConnectionStatus() {
        boolean connected = iMqttService.isConnected();
        Platform.runLater(() -> {
            if (connected) {
                assemblyConnectionCircle.setFill(Color.valueOf("#1fff25"));
            } else {
                assemblyConnectionCircle.setFill(Color.RED);
                assemblyStatusCircle.setFill(Color.RED);
                assemblyStatusLabel.textProperty().unbind();
                assemblyStatusLabel.setText("Error");
            }
        });
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
