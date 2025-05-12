package com.example.guidemo_4semester;

import com.example.guidemo_4semester.Queue.Batch;
import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.Common.IMqttService;
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
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;

@Component
public class TabViewController {
    @FXML private Label agvStatusLabel;
    @FXML private Circle agvStatusCircle;
    @FXML private Circle agvConnectionCircle;
    @FXML private Label agvParameterLabel;
    @FXML private Button startProdButton;
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
    private Timeline updateTimer;
    private int status;
    private AGVPI agv;
    private IMqttService iMqttService;
    private int QueuePriority;
    private String QueueQuantity;
    private String QueueProduct;
    String[] productList = {"Toy Cars1", "Toy Cars2"};
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private SortedList<Batch> sortedList;
    private int batchCounter = 1;

    public TabViewController(AGVPI agv, IMqttService iMqttService) {
        this.agv = agv;
        this.iMqttService = iMqttService;

    }

    @FXML
    public void initialize() {
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
        startAGVUpdates();
        normalPriorityButton.setSelected(true);
        // Button event setup should happen once
        startProdButton.setOnMouseClicked(event -> {
            try {
                setStartProdButton();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
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

        Platform.runLater(() -> {
            agvStatusLabel.setText(statusText);
            agvStatusCircle.setFill(Color.valueOf(circleColor));
            agvConnectionCircle.setFill(javafx.scene.paint.Color.valueOf(connectionStatus));
            agvParameterLabel.setText("Battery: " + agv.getBatteryLevel() + "%");
        });
    }
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

}
