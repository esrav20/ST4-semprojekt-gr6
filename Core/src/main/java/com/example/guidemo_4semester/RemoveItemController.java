package com.example.guidemo_4semester;

import dk.sdu.Warehouse.InventoryItems;
import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.ServiceSoap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoveItemController {
    private ServiceSoap serviceSoap;
    private Runnable onRemoveSuccess;
    private InventoryView selectedItem; // The aggregated item selected from the TableView

    @FXML private Button yesButton;
    @FXML private Button removeCancelButton;
    @FXML private Text confirmationText;


    public RemoveItemController() {
    }

    // Setters for dependencies (TabViewController will call these)
    public void setServiceSoap(ServiceSoap serviceSoap) {
        this.serviceSoap = serviceSoap;
    }

    public void setSelectedItem(InventoryView selectedItem) {
        this.selectedItem = selectedItem;

        updateUIWithSelectedItem();
    }

    public void setOnRemoveSuccess(Runnable onRemoveSuccess) {
        this.onRemoveSuccess = onRemoveSuccess;
    }

    @FXML
    public void initialize() {

    }

    // NEW: Helper method to update UI once selectedItem is set
    private void updateUIWithSelectedItem() {
        if (selectedItem != null) {
            confirmationText.setText("Are you sure you want to remove one '" + selectedItem.getItemName() + "' from the inventory?");
            if (yesButton != null) yesButton.setDisable(false);
        } else {
            confirmationText.setText("No item was selected for removal.");
            if (yesButton != null) yesButton.setDisable(true);
            if (removeCancelButton != null) removeCancelButton.setText("Close");
        }
    }

    @FXML
    private void HandleRemove() {
        if (selectedItem == null || selectedItem.getQuantity() <= 0) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.INFORMATION, "No Item to Remove", "There are no '" + (selectedItem != null ? selectedItem.getItemName() : "selected") + "' items listed as 'Available' to remove.");
                Stage stage = (Stage) yesButton.getScene().getWindow();
                stage.close();
            });
            return;
        }

        String itemName = selectedItem.getItemName();
        int trayId = -1;

        try {
            String jsonResponse = serviceSoap.getInventory();
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray inventoryArray = json.getJSONArray("Inventory");

            for (int i = 0; i < inventoryArray.length(); i++) {
                JSONObject itemObj = inventoryArray.getJSONObject(i);
                int id = itemObj.getInt("Id");
                String content = itemObj.getString("Content").trim();

                if (content != null && !content.isBlank() && content.equalsIgnoreCase(itemName)) {
                    trayId = id;
                    break;
                }
            }

            if (trayId != -1) {
                System.out.println("Attempting to pick item '" + itemName + "' from tray " + trayId + ".");
                String result = serviceSoap.pickItem(trayId);
                System.out.println("Backend pickItem response for tray " + trayId + ": " + result);

                if (result != null && result.toLowerCase().contains("error")) {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Removal Failed", "Backend reported an error: " + result);
                    });
                } else {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully removed one '" + itemName + "' from inventory.");
                        if (onRemoveSuccess != null) {
                            onRemoveSuccess.run();
                        }
                    });
                }
            } else {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.WARNING, "Item Not Found", "Could not find a physical instance of '" + itemName + "' in any tray to remove. The UI might be out of sync, or the item was already removed.");
                    if (onRemoveSuccess != null) {
                        onRemoveSuccess.run();
                    }
                });
            }

        } catch (JSONException e) {
            System.err.println("Error parsing inventory JSON in RemoveItemController: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to read inventory data. Details: " + e.getMessage());
            });
        } catch (Exception e) {
            System.err.println("Error during item removal: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Removal Failed", "Failed to remove item from backend. Details: " + e.getMessage());
            });
        } finally {
            Platform.runLater(() -> {
                Stage stage = (Stage) yesButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Item removal cancelled.");
        Platform.runLater(() -> {
            Stage stage = (Stage) removeCancelButton.getScene().getWindow();
            stage.close();
        });
    }

    // Removed @FXML annotation as it's a private helper, not an FXML event handler
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}