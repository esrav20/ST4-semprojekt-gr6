package com.example.guidemo_4semester;

import com.example.guidemo_4semester.Queue.Batch;
import dk.sdu.Common.IMqttService;
import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.InventoryItems;
import dk.sdu.Warehouse.ServiceSoap;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Component
public class TabViewController {

    private final AGVPI agv;
    private final IMqttService iMqttService;
    private final ServiceSoap serviceSoap;

    private final ObjectMapper objectMapper = new ObjectMapper();


    String[] productList = {"Toy Cars1", "Toy Cars2"};

    //Warehouse/Inventory:
    @FXML
    private TableView<InventoryItems> inventoryTable;
    @FXML
    private TableColumn<InventoryItems, Long> IDColumn;
    @FXML
    private TableColumn<InventoryItems, String> itemColumn;
    @FXML
    private TableColumn<InventoryItems, Integer> availableColumn;

    @FXML
    private ChoiceBox<String> warehouseDropdown;
    private final ObservableList<InventoryItems> inventoryData = FXCollections.observableArrayList();

    @FXML
    private Label agvStatusLabel;
    @FXML
    private Circle agvStatusCircle;
    @FXML
    private Circle agvConnectionCircle;
    @FXML
    private Circle assemblyConnectionCircle;
    @FXML
    private Circle assemblyStatusCircle;
    @FXML
    private Label assemblyStatusLabel;
    @FXML
    private Label agvParameterLabel;
    @FXML
    private Circle databaseConnectionCircle;
    @FXML
    private Circle warehouseStateCircle;
    @FXML
    private Label warehouseStateLabel;
    @FXML
    private Button startProdButton;
    @FXML
    private TextFlow messageBoard;
    @FXML
    private TextField processIdInput;
    @FXML
    private Button checkHealthButton;
    @FXML
    private Label HealthyLabel;
    @FXML
    private RadioButton normalPriorityButton;
    @FXML
    private RadioButton highPriorityButton;
    @FXML
    private TextField quantityInput;
    @FXML
    private ChoiceBox productChoice;
    @FXML
    private TableView<Batch> queueView;
    @FXML
    private TableColumn<Batch, Integer> batchID;
    @FXML
    private TableColumn<Batch, String> productQueue;
    @FXML
    private TableColumn<Batch, Integer> quantityQueue;
    @FXML
    private TableColumn<Batch, String> priorityQueue;
    @FXML
    private TableColumn<Batch, String> statusQueue;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private ImageView editInventoryButton;
    @FXML
    private Button emergencyStopButton;

    private int batchCounter = 1;
    private final ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private SortedList<Batch> sortedList;
    private Integer queueValue;
    private final boolean productionStarted = false;
    private Timeline updateTimer;
    private int status;
    private boolean emergencyActive = false;
    private int processId = 0;


    public TabViewController(ServiceSoap serviceSoap, AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.serviceSoap = serviceSoap;
        this.agv = agv;
        this.iMqttService = iMqttService;
    }


    private int wheelTrayId = -1;
    private int chassisTrayId = -1;

    private boolean checkStock(int quantity) {
        // These counts will now come directly from the tableView's displayed data (inventoryData)
        int wheelCount = 0;
        int chassisCount = 0;

        // Iterate through the currently displayed inventory data to get counts
        for (InventoryItems item : inventoryData) {
            if (item.getItemName().equalsIgnoreCase("wheel")) {
                wheelCount = item.getQuantity(); // Get the quantity directly from the displayed data
            } else if (item.getItemName().equalsIgnoreCase("chassis")) {
                chassisCount = item.getQuantity(); // Get the quantity directly from the displayed data
            }
        }

        int tempWheelTrayId = -1; // This won't be dynamically determined from UI data
        int tempChassisTrayId = -1; // This won't be dynamically determined from UI data
        wheelTrayId = tempWheelTrayId; // Retaining variable names as requested
        chassisTrayId = tempChassisTrayId; // Retaining variable names as requested


        int requiredWheels = quantity * 4;
        int requiredChassis = quantity;

        if (wheelCount < requiredWheels || chassisCount < requiredChassis) {
            System.out.println("Not enough materials");
            System.out.println("Required: " + requiredWheels + " wheels, " + requiredChassis + " chassis");
            System.out.println("Available: " + wheelCount + " wheels, " + chassisCount + " chassis");
            return false;
        }
        return true;
    }

    private void updateDatabaseConnectionStatus() {
        boolean isConnected = serviceSoap.isConnected();
        Platform.runLater(() -> {
            if (isConnected) {
                databaseConnectionCircle.setFill(Color.valueOf("#1fff25"));
            } else {
                databaseConnectionCircle.setFill(Color.RED);
            }
        });
    }

    private int parseStateFromInventoryResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            return json.getInt("State");
        } catch (JSONException e) {
            System.err.println("Error parsing state from inventory JSON: " + e.getMessage());
            return -1; // Indicate error
        }
    }

    private void updateWarehouseState() {
        String response = serviceSoap.getInventory();
        int state = parseStateFromInventoryResponse(response);

        Platform.runLater(() -> {
            switch (state) {
                case 0 -> {
                    warehouseStateLabel.setText("Idle");
                    warehouseStateCircle.setFill(Color.DODGERBLUE);
                }
                case 1 -> {
                    warehouseStateLabel.setText("Executing");
                    warehouseStateCircle.setFill(Color.valueOf("#1fff25"));
                }
                case 2 -> {
                    warehouseStateLabel.setText("Error");
                    warehouseStateCircle.setFill(Color.RED);
                }
                default -> {
                    warehouseStateLabel.setText("Unknown");
                    warehouseStateCircle.setFill(Color.GRAY);
                }
            }
        });
    }

    private ObservableList<InventoryItems> parseInventoryResponse(String response) {
        ObservableList<InventoryItems> aggregatedInventory = FXCollections.observableArrayList();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray inventoryArray = json.getJSONArray("Inventory");

            Map<String, Integer> itemQuantities = new HashMap<>();

            for (int i = 0; i < inventoryArray.length(); i++) {
                JSONObject itemObj = inventoryArray.getJSONObject(i);
                String content = itemObj.getString("Content");

                if (content != null && !content.isBlank() && !content.equalsIgnoreCase("null")) {
                    itemQuantities.put(content, itemQuantities.getOrDefault(content, 0) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : itemQuantities.entrySet()) {
                aggregatedInventory.add(new InventoryItems(entry.getKey(), entry.getValue()));
            }

        } catch (JSONException e) {
            System.err.println("Error parsing inventory JSON: " + e.getMessage());
            e.printStackTrace();

            showAlert(Alert.AlertType.ERROR, "Parsing Error", "Failed to parse inventory data from backend.");
        }
        return aggregatedInventory;
    }


    private void setupTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupWarehouseDropdown() {
        warehouseDropdown.setItems(FXCollections.observableArrayList("Warehouse1"));
        warehouseDropdown.setOnAction(event -> loadInventory());
    }

    private void loadInventory() {
        System.out.println("TabViewController: loadInventory() called. Requesting current inventory from backend.");
        try {
            String response = serviceSoap.getInventory();
            System.out.println("TabViewController: Raw response from serviceSoap.getInventory(): " + response);
            ObservableList<InventoryItems> parsedData = parseInventoryResponse(response);
            System.out.println("TabViewController: Parsed data size for table: " + parsedData.size());
            Platform.runLater(() -> {
                inventoryData.setAll(parsedData); // Update the ObservableList
                System.out.println("TabViewController: inventoryTable updated with new data.");
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("TabViewController: Failed to load inventory due to an exception.");
        }
    }

    @FXML
    private void addQueue(ActionEvent event) {
        String queuePriority = normalPriorityButton.isSelected() ? "Normal" :
                (highPriorityButton.isSelected() ? "High" : null);
        Integer queueQuantity = Integer.valueOf(quantityInput.getText());
        String queueProduct = productChoice.getValue().toString();
        if (!checkStock(queueQuantity)) {
            System.out.println("Production cannot start due to insufficient materials");
            return;
        }
        if (queueQuantity == 0 || queueProduct == null || queuePriority == null) {
            System.out.println("Please fill all inputs correctly.");
            return;
        }

        Batch newBatch = new Batch(batchCounter++, queueProduct, queueQuantity, queuePriority, "Pending");
        batchList.add(newBatch);
        quantityInput.clear();
    }


    @FXML
    private void deleteSelectedRow() {
        Batch selectedBatch = queueView.getSelectionModel().getSelectedItem();
        if (selectedBatch != null) {
            batchList.remove(selectedBatch);
        }
    }

    @FXML
    public void initialize() throws MqttException {
        setupTable();
        queueView.setEditable(true);
        batchID.setCellValueFactory(new PropertyValueFactory<>("batchID"));
        productQueue.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityQueue.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priorityQueue.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
        statusQueue.setCellValueFactory(new PropertyValueFactory<>("status"));

        sortedList = new SortedList<>(batchList);
        sortedList.setComparator(
                Comparator.comparing((Batch b) -> b.getPriority().equalsIgnoreCase("High") ? 1 : 0)
                        .reversed()
                        .thenComparingInt(Batch::getBatchID)
        );
        queueView.setItems(sortedList);

        deleteButton.disableProperty().bind(queueView.getSelectionModel().selectedItemProperty().isNull());

        quantityQueue.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityQueue.setOnEditCommit(event -> {
            int editedRow = event.getTablePosition().getRow();
            if (productionStarted && editedRow == 0) {
                return;
            }
            Batch batch = event.getRowValue();
            batch.setQuantity(event.getNewValue());
            queueView.refresh();
        });

        priorityQueue.setCellFactory(ComboBoxTableCell.forTableColumn("High", "Normal"));
        priorityQueue.setOnEditCommit(event -> {
            int editedRow = event.getTablePosition().getRow();
            if (productionStarted && editedRow == 0) {
                return;
            }
            Batch batch = event.getRowValue();
            batch.setPriority(event.getNewValue());
            sortedList.setComparator(null);
            sortedList.setComparator(
                    Comparator.comparing((Batch b) -> b.getPriority().equalsIgnoreCase("High") ? 1 : 0)
                            .reversed()
                            .thenComparingInt(Batch::getBatchID)
            );
        });
        productChoice.setItems(FXCollections.observableArrayList(productList));
        iMqttService.setMessagehandler((state, health) -> {
            Platform.runLater(() -> {
                if (state != null) {
                    String text = switch (state) {
                        case 1 -> "Running";
                        case 0 -> "Idle";
                        default -> "Unknown";
                    };
                    assemblyStatusLabel.textProperty().unbind();
                    assemblyStatusLabel.setText(text);
                    assemblyStatusCircle.setFill(text.equals("Running") ? Color.valueOf("#1fff25") : Color.DODGERBLUE);
                }

                if (health != null) {
                    HealthyLabel.textProperty().unbind();
                    HealthyLabel.setText(String.valueOf(health));
                }
            });
        });

        startAGVUpdates();
        updateAGVDisplay();
        iMqttService.connect();
        loadInventory();
        inventoryTable.setItems(inventoryData);
        setupWarehouseDropdown();
        emergencyStopButton.setOnMouseClicked(event -> {
            if (!emergencyActive) {
                handleEmergencyStop();
            } else {
                handleEmergencyReset();
            }
        });
    }

    private void handleEmergencyStop() {
        emergencyActive = true;

        Platform.runLater(() -> {
            agvStatusLabel.setText("Emergency Stop");
            agvStatusCircle.setFill(Color.RED);

            emergencyStopButton.setText("Reset Emergency button");
            startProdButton.setDisable(true);
        });


        new Thread(() -> {
            try {
                if (iMqttService != null && agv.isConnected()) {
                    try {
                        iMqttService.disconnect();
                    } catch (Exception e) {
                        System.err.println("MQTT disconnected");
                    }
                }
            } catch (Exception e) {
                System.err.println("AGV Error: Failed to stop AGV " + e.getMessage());
            }
        }).start();
    }

    private void handleEmergencyReset() {
        emergencyActive = false;


        Platform.runLater(() -> {
            emergencyStopButton.setText("Emergency stop");
            emergencyStopButton.setStyle("");
            startProdButton.setDisable(false);
        });


        new Thread(() -> {
            try {

                emergencyActive = false;
            } catch (Exception e) {
                System.err.println("Error: Failed to reset emergency state " + e.getMessage());
            }
        }).start();
    }

    //    int warehouseState = getWarehouseState();
    private int getWarehouseState() {
        try {
            String json = serviceSoap.getInventory();
            JSONObject obj = new JSONObject(json);
            return obj.getInt("State");
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // or any value indicating error/unknown
        }
    }

    @FXML
    public void startProd() {
        if (!batchList.isEmpty()) {
            queueValue = queueView.getItems().get(0).getQuantity();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (queueValue > 0) {

                        if (!iMqttService.isConnected()) {
                            iMqttService.connect();
                        }

                        if (emergencyActive) {
                            System.out.println("Emergency stop activated");
                            break;
                        }

                        agv.needsCharging();
                        System.out.println(queueValue);
                        //Denne linje skal ændres!
                        for (int i = 0; i < 4; i++) {
                            serviceSoap.pickItem(wheelTrayId);
                        }
                        serviceSoap.pickItem(chassisTrayId);
                        if (emergencyActive) {
                            break;
                        }

                        if (getWarehouseState() == 0) {

                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"MoveToStorageOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            if (emergencyActive) {
                                break;
                            }
                        }
                        if (getWarehouseState() == 0 && agv.getCurrentstate() == 1) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"PickWarehouseOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            agv.pickItem("");
                            if (emergencyActive) {
                                break;
                            }
                        }

                        if (agv.getCurrentstate() == 1) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"MoveToStorageAssembly\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            if (emergencyActive) {
                                break;
                            }
                        }

                        if (iMqttService.getAssemblyCurrentstate() == 0 && agv.getCurrentstate() == 1) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"PutAssemblyOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            agv.putItem("");
                            if (emergencyActive) {
                                break;
                            }
                        }

                        if (agv.getCurrentstate() == 1 || iMqttService.getAssemblyCurrentstate() == 1) {
                            agv.needsCharging();
                            iMqttService.publish("emulator/operation", "{\"ProcessID\": " + processId + "}");
                            while (iMqttService.getAssemblyCurrentstate() == 1) {
                                iMqttService.wait();
                                if (emergencyActive) {
                                    break;
                                }
                            }
                        }

                        if (iMqttService.getAssemblyCurrentstate() == 0 && agv.getCurrentstate() == 1) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"PickAssemblyOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            agv.pickItem("");
                            if (emergencyActive) {
                                break;
                            }
                        }

                        if (agv.getCurrentstate() == 1) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"MoveToStorageOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            if (emergencyActive) {
                                break;
                            }
                        }

                        if (agv.getCurrentstate() == 1 && getWarehouseState() == 0) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"PutWarehouseOperation\",\"State\":1}");
                            serviceSoap.pickItem(10);
                            loadInventory();
                            Thread.sleep(100);
                            serviceSoap.insertItem(10, "Toy Car");
                            loadInventory();
                            agv.sendRequest("{\"State\":2}");
                            agv.putItem("");

                            if (emergencyActive) {
                                break;
                            }
                        }


                        Platform.runLater(() -> {
                            System.out.println(queueValue);
                        });

                        queueValue--;
                        Thread.sleep(100);
                        processId++;
                    }


                    Platform.runLater(() -> {
                        batchList.remove(0);
                        if (!batchList.isEmpty()) {
                            startProd();
                        }
                    });

                    return null;
                }
            };


            new Thread(task).start();
        }
    }

    private void startAGVUpdates() {
        updateTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    updateAGVDisplay();
                    updateAssemblyConnectionStatus();
                    updateWarehouseState();
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
            case 1 -> {
                statusText = "Idle";
                circleColor = "DODGERBLUE";
            }
            case 2 -> {
                statusText = "Working";
                circleColor = "#1fff25";
            }
            case 3 -> {
                statusText = "Charging";
                circleColor = "ORANGE";
            }
            default -> {
                statusText = "Error";
                circleColor = "RED";
            }
        }

        String connectionStatus = agv.isConnected() ? "#1fff25" : "RED";

        Platform.runLater(() -> {
            agvStatusLabel.textProperty().unbind(); // Vi får en runtimeException, når vi kører appen og denne er bound i forvejen.
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(Color.valueOf(circleColor));
            agvConnectionCircle.setFill(Color.valueOf(connectionStatus));
            agvParameterLabel.textProperty().unbind(); // samme som l.159.
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });
    }

    @FXML
    private void additem() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/addItem.fxml"));
            Parent parent = fxmlLoader.load();

            AddItemController controller = fxmlLoader.getController();
            controller.setServiceSoap(serviceSoap);
            controller.setOnSubmitSuccess(this::loadInventory);


            Stage stage = new Stage();
            stage.setTitle("Add Item");
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void removeitem() {
        InventoryItems selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getQuantity() <= 0) {
                // This alert shows if the aggregated quantity is 0, preventing the dialog
                showAlert(Alert.AlertType.INFORMATION, "No Item to Remove", "There are no '" + selected.getItemName() + "' items listed as 'Available' to remove.");
                return;
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/removeItem.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                RemoveItemController controller = fxmlLoader.getController();

                controller.setServiceSoap(serviceSoap);
                controller.setSelectedItem(selected); // Passes the selected item to the controller
                controller.setOnRemoveSuccess(this::loadInventory);

                Stage stage = new Stage();
                stage.setTitle("Confirm Removal");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                System.err.println("Error opening Remove Item dialog: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open remove dialog. Details: " + e.getMessage());
            }
        } else {
            // This alert shows if nothing is selected in the main table
            showAlert(Alert.AlertType.INFORMATION, "No Selection", "Please select an item in the table to remove.");
        }
    }

    @FXML
    private void editbutton() {

        InventoryItems selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Editbutton.fxml"));

                Scene scene = new Scene(fxmlLoader.load());

                // Pass the selected InventoryItems object to the EditItemController
                EditItemController controller = fxmlLoader.getController();
                controller.setSelectedItem(selected);

                // Set the callback to update the UI directly
                controller.setOnEditSuccess(this::updateAvailableQuantity);


                Stage stage = new Stage();
                stage.setTitle("Edit Item Quantity");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error opening Edit Item dialog.");
            }
        } else {
            System.out.println("No item selected for editing.");
        }
    }

    private void updateAvailableQuantity(String itemName, Integer newQuantity) {
        Platform.runLater(() -> {
            boolean found = false;
            for (InventoryItems item : inventoryData) {
                if (item.getItemName().equalsIgnoreCase(itemName)) {
                    item.setQuantity(newQuantity); // Update the quantity
                    found = true;
                    System.out.println("UI quantity for '" + itemName + "' set to " + newQuantity);
                    break;
                }
            }
            if (!found) {
                // If item not found (e.g., quantity was 0, now > 0), add it
                if (newQuantity > 0) {
                    inventoryData.add(new InventoryItems(itemName, newQuantity));
                    System.out.println("Added '" + itemName + "' with quantity " + newQuantity + " to UI.");
                }
            }
        });
    }
}
