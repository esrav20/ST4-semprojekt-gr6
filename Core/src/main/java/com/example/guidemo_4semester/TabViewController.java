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

    protected void loadInventory() {
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
    @FXML private TableColumn<Batch, Integer> quantityQueue;
    @FXML private TableColumn<Batch, Integer> priorityQueue;
    @FXML private TableColumn<Batch, String> statusQueue;
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private ImageView editInventoryButton;


    private int batchCounter = 1;
    String[] productList = {"Toy Cars1", "Toy Cars2"};
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private SortedList<Batch> sortedList;
    private Integer queueValue;

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
                agv.sendRequest("MoveToStorageOperation");
                updateAGVDisplay();
                iMqttService.publish("emulator/operation",  "{\"ProcessID\": 12345}");
            } catch (IOException | InterruptedException | MqttException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void startProd() throws IOException, InterruptedException, MqttException {
        if (!batchList.isEmpty()) {
            productionStarted = true;
        queueValue = Integer.valueOf(queueView.getItems().get(0).getQuantity());
        while(queueValue > 0) {
            warehouseClient.pickItem();
            if (warehouseClient.value()) {
                agv.sendRequest("{\"Program name\":\"MoveToStorageOperation\",\"State\":1}");
                agv.sendRequest("{\"State\":2}");
            }
            if (agv.getCurrentstate() == 1) {

                agv.pickItem();
                agv.sendRequest("{\"Program name\":\"MoveToStorageAssembly\",\"State\":1}");
                agv.sendRequest("{\"State\":2}");
            }
            if (agv.getCurrentstate() == 1 || iMqttService.getAssemblyCurrentstate() == 0) {
                iMqttService.publish("emulator/operation", "{\"ProcessID\": 12345}");
                while(iMqttService.getAssemblyCurrentstate() == 1) {
                    iMqttService.wait();
                }
            }
            if (iMqttService.getAssemblyCurrentstate() == 0) {
                agv.pickItem();
                agv.sendRequest("{\"Program name\":\"MoveToStorageOperation\",\"State\":1}");
                agv.sendRequest("{\"State\":2}");
            }
            if (agv.getCurrentstate() == 1) {
                warehouseClient.putItem();
            }

            System.out.println(queueValue);
            queueValue--;
        }
        batchList.remove(0);
        startProd();
        }
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
        private void additem(){
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
        private void removeitem(){
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
        private void editbutton() {
            InventoryView selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Editbutton.fxml"));

                    EditItemController controller = new EditItemController(warehouseClient,selected);
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
