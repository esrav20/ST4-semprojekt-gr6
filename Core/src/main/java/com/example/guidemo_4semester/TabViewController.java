package com.example.guidemo_4semester;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.text.TextFlow;

public class TabViewController {

    @FXML
    private TableView<?> batchTableView; // Define the TableView for batches
    @FXML
    private TableColumn<?, ?> batchID;
    @FXML
    private TableColumn<?, ?> productQueue;
    @FXML
    private TableColumn<?, ?> quantityQueue;
    @FXML
    private TableColumn<?, ?> priorityQueue;
    @FXML
    private TableColumn<?, ?> statusQueue;

    @FXML
    private Button addQueueButton;
    @FXML
    private Button editQueueButton;
    @FXML
    private Button startProdButton;

    @FXML
    private ChoiceBox<String> productChoice;
    @FXML
    private RadioButton highPriorityButton;
    @FXML
    private RadioButton normalPriorityButton;

    @FXML
    private TextField quantityInput;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label messageBoard;

    @FXML
    private void initialize() {
        // Initialize the UI components
        System.out.println("TabViewController initialized!");

        // Set up event listeners for buttons, choice boxes, etc.
        addQueueButton.setOnAction(event -> handleAddQueue());
        editQueueButton.setOnAction(event -> handleEditQueue());
        startProdButton.setOnAction(event -> handleStartProduction());
    }

    private void handleAddQueue() {
        // Handle adding a new queue item
        String quantity = quantityInput.getText();
        String product = productChoice.getValue();
        String priority = highPriorityButton.isSelected() ? "High" : "Normal";

        // Add logic to handle the new item (like adding it to the table)
        System.out.println("Added to queue: " + product + ", Quantity: " + quantity + ", Priority: " + priority);
    }

    private void handleEditQueue() {
        // Handle editing the queue, e.g., opening a dialog to modify an existing queue item
        System.out.println("Edit Queue Button clicked!");
    }

    private void handleStartProduction() {
        // Handle starting production
        System.out.println("Start Production Button clicked!");
        progressBar.setProgress(0.5); // Simulating production progress
    }
}
