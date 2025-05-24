package com.example.guidemo_4semester;

//import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.InventoryItems;
import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.ServiceSoap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.BiConsumer;

public class EditItemController {
    ServiceSoap serviceSoap;
    private InventoryView selectedItem;
    private BiConsumer<String, Integer> onEditSuccess;


    //@FXML private TextField Amount;
    @FXML private TextField Item;
    @FXML private TextField Amount;

    public EditItemController() {
    }

    public void setSelectedItem(InventoryView selectedItem) {
        this.selectedItem = selectedItem;

        if (selectedItem != null) {
            Item.setText(selectedItem.getItemName());
            Amount.setText(String.valueOf(selectedItem.getQuantity()));
        }
    }


    public void setOnEditSuccess(BiConsumer<String, Integer> onEditSuccess) {
        this.onEditSuccess = onEditSuccess;
    }

    @FXML
    public void initialize() {
        if (selectedItem != null) {
            Item.setText(selectedItem.getItemName());
            //Amount.setText(String.valueOf(selectedItem.getQuantity()));
            Amount.setText(String.valueOf(selectedItem.getQuantity()));
        }
    }
    @FXML
    private void editItem() {
        String itemName = Item.getText().trim();
        String quantityText = Amount.getText().trim();
        int newQuantity;

        if (itemName.isBlank()) {
            System.out.println("Error: Item name cannot be blank.");
            return;
        }
        if (quantityText.isBlank()) {
            System.out.println("Error: Quantity cannot be blank.");
            return;
        }

        try {
            newQuantity = Integer.parseInt(quantityText);
            if (newQuantity < 0) {
                System.out.println("Error: Quantity must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid quantity.");
            return;
        }

        // Call the callback to update UI in TabViewController
        if(onEditSuccess != null) {
            Platform.runLater(() -> onEditSuccess.accept(itemName, newQuantity));
        }

        Stage stage = (Stage) Item.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Platform.runLater(() -> {
            Stage stage = (Stage) Item.getScene().getWindow();
            stage.close();
        });
    }
}
