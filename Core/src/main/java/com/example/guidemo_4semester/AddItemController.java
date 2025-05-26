package com.example.guidemo_4semester;

import dk.sdu.Warehouse.ServiceSoap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // Needed for Alert class
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // Needed for ButtonType.YES/NO
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class AddItemController {

    public Button AddItemButton;
    public Button itemCancelButton;
    private ServiceSoap serviceSoap;
    private Runnable onSubmitSuccess;

    public AddItemController() {
    }

    public void setServiceSoap(ServiceSoap serviceSoap) {
        this.serviceSoap = serviceSoap;
    }

    public void setOnSubmitSuccess(Runnable onSubmitSuccess) {
        this.onSubmitSuccess = onSubmitSuccess;
    }

    @FXML
    private TextField Id;

    @FXML
    private TextField Item;

    @FXML
    public void initialize() {
        System.out.println("Add Item Controller initialized. Item field is " + (Item == null ? "null" : "OK"));
    }

    @FXML
    protected void handleSubmit() {
        String itemNameToAdd = Item.getText().trim();
        String trayIdText = Id.getText().trim();
        int targetTrayId = -1; // Default to -1 indicating no specific tray entered

        // --- Input Validation ---
        if (itemNameToAdd.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Item name cannot be blank.");
            return;
        }

        // --- Tray ID Validation ---
        if (!trayIdText.isBlank()) {
            try {
                targetTrayId = Integer.parseInt(trayIdText);
                if (targetTrayId <= 0 || targetTrayId > 10) { // Assuming 1-10 trays
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Tray ID must be between 1 and 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Tray ID. Please enter a valid number.");
                return;
            }
        }

        // --- Get current inventory ---
        String currentInventoryResponse = serviceSoap.getInventory();
        System.out.println("Current Inventory Response: " + currentInventoryResponse);

        try {
            JSONObject obj = new JSONObject(currentInventoryResponse);
            JSONArray inventoryArray = obj.getJSONArray("Inventory");

            // Map to store current content of each tray
            String[] trayContents = new String[11]; // Index 0 unused, for tray IDs 1-10
            for (int i = 0; i < inventoryArray.length(); i++) {
                JSONObject itemObj = inventoryArray.getJSONObject(i);
                int id = itemObj.getInt("Id");
                String content = itemObj.getString("Content").trim();
                if (id >= 1 && id <= 10) { // Ensure ID is within expected range
                    trayContents[id] = content;
                }
            }

            int trayToUse = -1; // The tray we will attempt to insert into

            // --- Determine which tray to use and handle existing content ---

            // Priority 1: Use user-specified targetTrayId
            if (targetTrayId != -1) {
                String contentAtTarget = trayContents[targetTrayId];

                if (contentAtTarget == null || contentAtTarget.isBlank() || contentAtTarget.equalsIgnoreCase("null")) {
                    // Tray is empty - safe to use
                    trayToUse = targetTrayId;
                } else if (contentAtTarget.equalsIgnoreCase(itemNameToAdd)) {
                    // Tray already contains the same item - no action needed for this physical add
                    showAlert(Alert.AlertType.INFORMATION, "Item Already Present", "Tray " + targetTrayId + " already contains '" + itemNameToAdd + "'. No action needed for this physical item.");
                    // Since it's already there, we can treat this as "added" and close the dialog.
                    Platform.runLater(() -> {
                        if (onSubmitSuccess != null) {
                            onSubmitSuccess.run();
                        }
                        Stage stage = (Stage) Item.getScene().getWindow();
                        stage.close();
                    });
                    return; // Exit method as no further physical add is needed
                } else {
                    // Tray contains a DIFFERENT item - ASK FOR REPLACEMENT
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Replace Item?");
                    alert.setHeaderText("Tray " + targetTrayId + " is occupied by '" + contentAtTarget + "'.");
                    alert.setContentText("Do you want to replace it with '" + itemNameToAdd + "'?");
                    Optional<ButtonType> result = alert.showAndWait(); // This blocks execution

                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        // User wants to replace - first pick the existing item
                        System.out.println("AddItemController: Picking (clearing) tray: " + targetTrayId + " which contained '" + contentAtTarget + "'.");
                        String pickResult = serviceSoap.pickItem(targetTrayId);
                        System.out.println("AddItemController: PickItem Result for tray " + targetTrayId + ": " + pickResult);
                        if (pickResult.contains("Error") || pickResult.contains("could not be handled")) {
                            showAlert(Alert.AlertType.ERROR, "Backend Error", "Failed to clear tray " + targetTrayId + ". Cannot insert new item.");
                            return; // Cannot proceed with this tray
                        } else {
                            trayToUse = targetTrayId; // Tray cleared, now use it
                        }
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "Action Cancelled", "Replacement for Tray " + targetTrayId + " cancelled. Item not added.");
                        return; // User cancelled replacement
                    }
                }
            } else {
                // Priority 2: Find the first available empty or replaceable tray if no specific ID was given
                for (int i = 1; i <= 10; i++) {
                    String content = trayContents[i];
                    if (content == null || content.isBlank() || content.equalsIgnoreCase("null")) {
                        trayToUse = i; // Found an empty tray
                        break;
                    } else if (content.equalsIgnoreCase(itemNameToAdd)) {
                        showAlert(Alert.AlertType.INFORMATION, "Item Already Present", "Tray " + i + " already contains '" + itemNameToAdd + "'. No action needed for this physical item.");
                        Platform.runLater(() -> {
                            if (onSubmitSuccess != null) {
                                onSubmitSuccess.run();
                            }
                            Stage stage = (Stage) Item.getScene().getWindow();
                            stage.close();
                        });
                        return; // Item already "added" to backend, close dialog
                    } else {
                        // Tray contains a DIFFERENT item - ASK FOR REPLACEMENT
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Replace Item?");
                        alert.setHeaderText("Tray " + i + " is occupied by '" + content + "'.");
                        alert.setContentText("Do you want to replace it with '" + itemNameToAdd + "'?");
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.YES) {
                            System.out.println("AddItemController: Picking (clearing) tray: " + i + " which contained '" + content + "'.");
                            String pickResult = serviceSoap.pickItem(i);
                            System.out.println("AddItemController: PickItem Result for tray " + i + ": " + pickResult);
                            if (pickResult.contains("Error") || pickResult.contains("could not be handled")) {
                                showAlert(Alert.AlertType.ERROR, "Backend Error", "Failed to clear tray " + i + ". Cannot insert new item.");
                                // Continue to next tray if this one failed to clear
                            } else {
                                trayToUse = i; // Tray cleared, now use it
                                break; // Found a tray, stop searching
                            }
                        } else {
                            showAlert(Alert.AlertType.INFORMATION, "Action Cancelled", "Replacement for Tray " + i + " cancelled. Skipping this tray.");
                        }
                    }
                }
            }

            // --- Perform the actual backend insert if a tray was found/designated ---
            if (trayToUse != -1) {
                System.out.println("AddItemController: Inserting '" + itemNameToAdd + "' into tray: " + trayToUse + ".");
                String insertResult = serviceSoap.insertItem(trayToUse, itemNameToAdd);
                System.out.println("AddItemController: InsertItem Result for tray " + trayToUse + ": " + insertResult);
                if (insertResult.contains("Error") || insertResult.contains("could not be handled")) {
                    showAlert(Alert.AlertType.ERROR, "Backend Error", "Error: Failed to insert '" + itemNameToAdd + "' into tray " + trayToUse + ". Item was NOT added.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Item Added", "Successfully added one '" + itemNameToAdd + "' to tray " + trayToUse + ".");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Tray Found", "Could not find a suitable empty or replaceable tray. Item was not added to backend.");
            }

            // --- Trigger UI refresh in TabViewController and close dialog ---
            if (onSubmitSuccess != null) {
                Platform.runLater(onSubmitSuccess); // Calls TabViewController.loadInventory()
            }

            Platform.runLater(() -> {
                Stage stage = (Stage) Item.getScene().getWindow();
                stage.close();
            });

        } catch (JSONException e) {
            System.err.println("Error parsing inventory JSON response: " + e.getMessage());
            System.err.println("Problematic JSON response: " + currentInventoryResponse);
            showAlert(Alert.AlertType.ERROR, "JSON Parsing Error", "Failed to parse inventory data from backend: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    // Helper method for showing alerts (private to this controller)
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> { // Ensure alert is shown on JavaFX Application Thread
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}