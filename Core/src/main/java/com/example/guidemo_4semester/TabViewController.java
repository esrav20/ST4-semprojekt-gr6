package com.example.guidemo_4semester;

import com.example.guidemo_4semester.Queue.Batch;
import dk.sdu.Common.IMqttService;
import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.guidemo_4semester.AddItemController;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

@Component
public class TabViewController {

    private final AGVPI agv;
    private final IMqttService iMqttService;
    private final WarehousePI warehouseClient;


    String[] productList = {"Toy Cars1", "Toy Cars2"};

    //Warehouse/Inventory:
    @FXML
    private TableView<InventoryView> inventoryTable;
    @FXML
    private TableColumn<InventoryView, Long> IDColumn;
    @FXML
    private TableColumn<InventoryView, String> itemColumn;
    @FXML
    private TableColumn<InventoryView, Integer> availableColumn;
    @FXML
    private TableColumn<InventoryView, Integer> inStockColumn;
    @FXML
    private ChoiceBox<String> warehouseDropdown;
    private ObservableList<InventoryView> inventoryData = FXCollections.observableArrayList();
    //---------------------------
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
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private SortedList<Batch> sortedList;
    private Integer queueValue;
    private boolean productionStarted = false;
    private Timeline updateTimer;
    private int status;
    private boolean emergencyActive = false;
    private int processId = 0;

    // vi skal ikke have en setDepencies metode - da Spring ikke kan starte programmet uden Constructor-based DI.
    @Autowired
    public TabViewController(WarehousePI warehouseClient, AGVPI agv, IMqttService iMqttService) throws MqttException {
        this.warehouseClient = warehouseClient;
        this.agv = agv;
        this.iMqttService = iMqttService;
    }


        private int wheelTrayId;
        private int chassisTrayId;





    private boolean checkStock(int quantity) {


        inventoryData.setAll(warehouseClient.getInventory());
        int wheelCount = 0;
        int chassisCount=0;

        for (InventoryView item : inventoryData) {
            String name = item.getItemName().toLowerCase();

            switch (name) {
                case "wheel" ->{
                    wheelCount = item.getQuantity();
                    wheelTrayId = item.getTrayId();
                }
                case "chassis" -> {
                    chassisCount = item.getQuantity();
                    chassisTrayId = item.getTrayId();
                }

            }
        }
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
        boolean isConnected = warehouseClient.isConnected(); // Assuming this method exists
        Platform.runLater(() -> {
            if (isConnected) {
                databaseConnectionCircle.setFill(Color.valueOf("#1fff25"));
            } else {
                databaseConnectionCircle.setFill(Color.RED);
            }
        });
    }



    private void updateWarehouseState() {
        int state = warehouseClient.getWarehouseState();

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
        try {
            inventoryData.clear();
            inventoryData.addAll(warehouseClient.getInventory());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Jeg kan love dig for load Inventory fejler du");
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
        setupTable();
        setupWarehouseDropdown();
        loadInventory();
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


    @FXML
    public void startProd() {
        if (!batchList.isEmpty()) {
            queueValue = queueView.getItems().get(0).getQuantity();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (queueValue > 0) {

                        if(!iMqttService.isConnected()) {
                            iMqttService.connect();
                        }

                        if (emergencyActive) {
                            System.out.println("Emergency stop activated");
                            break;
                        }

                        agv.needsCharging();
                        System.out.println(queueValue);
                        //Denne linje skal ændres!
                        if(wheelTrayId == 0 || chassisTrayId == 0){
                            System.out.println("TRAYID FANDT VI IKKE NEJ");
                        }
                        for(int i = 0; i < 4; i++){
                            warehouseClient.pickItem(wheelTrayId);
                        }
                        warehouseClient.pickItem(chassisTrayId);
                        if (emergencyActive) {
                            break;
                        }

                        if (warehouseClient.getWarehouseState() == 0) {

                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"MoveToStorageOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            if (emergencyActive) {
                                break;
                            }
                        }
                        if (warehouseClient.getWarehouseState() == 0 && agv.getCurrentstate() == 1) {
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
                            iMqttService.publish("emulator/operation", "{\"ProcessID\": "+ processId +"}");
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

                        if (agv.getCurrentstate() == 1 && warehouseClient.getWarehouseState() == 0) {
                            agv.needsCharging();
                            agv.sendRequest("{\"Program name\":\"PutWarehouseOperation\",\"State\":1}");
                            agv.sendRequest("{\"State\":2}");
                            agv.putItem("");

                            Optional<InventoryView> existingItemOpt = warehouseClient.getInventory().stream()
                                    .filter(item -> "Car".equals(item.getItemName()))
                                    .findFirst();

                            if (existingItemOpt.isPresent()) {
                                // Item exists - increase quantity
                                InventoryView existingItem = existingItemOpt.get();
                                int newQuantity = existingItem.getQuantity() + 1; // increase by 1 or your desired amount
                                warehouseClient.updateItem(existingItem.getId(),existingItem.getItemName(), newQuantity);
                            } else {
                                // Item does not exist - insert new item with new IDs
                                int nextTrayId = warehouseClient.getInventory().stream()
                                        .mapToInt(InventoryView::getTrayId)
                                        .max()
                                        .orElse(0) + 1;
                                long nextId = warehouseClient.getInventory().stream()
                                        .mapToLong(InventoryView::getId)
                                        .max()
                                        .orElse(0) + 1;
                                warehouseClient.insertItem(nextTrayId, nextId, "Car", 1);
                            }

                            if (emergencyActive) {
                                break;
                            }
                        }


                        Platform.runLater(() -> {
                            System.out.println(queueValue);
                        });

                        queueValue--;
                        Thread.sleep(100);
                    }


                    Platform.runLater(() -> {
                        batchList.remove(0);
                        if (!batchList.isEmpty()) {
                            startProd();
                            processId++;
                        }

                    });

                    return null;
                }
            };


            new Thread(task).start();
        }
    }

        private void startAGVUpdates () {
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

        private void updateAssemblyConnectionStatus () {
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

        private void updateAGVDisplay () {
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
        private void additem () {
            addButton.setOnMouseClicked(event -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/addItem.fxml"));

                    AddItemController controller = new AddItemController(warehouseClient);
                    controller.setOnSubmitSuccess(this::loadInventory);
                    fxmlLoader.setController(controller);

                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Add Item");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @FXML
        private void removeitem () {
            InventoryView selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/removeItem.fxml"));

                    RemoveItemController controller = new RemoveItemController(warehouseClient, selected);
                    controller.setOnRemoveSuccess(this::loadInventory);
                    fxmlLoader.setController(controller);

                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Remove Item");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
       /* InventoryView selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long id = selected.getId();
            String result = warehouseClient.deleteitems(id);
            System.out.println(result);
            loadInventory();
        }else{
            System.out.println("Item not found");
        }*/


        }


        @FXML
        private void editbutton () {
            InventoryView selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Editbutton.fxml"));

                    EditItemController controller = new EditItemController(warehouseClient, selected);
                    controller.setOnEditSuccess(this::loadInventory);
                    fxmlLoader.setController(controller);

                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Edit item");
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
