package com.example.guidemo_4semester;

import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddItemController {
    private final WarehousePI warehouseClient;
    private Runnable onSubmitSuccess;

    public void setOnSubmitSuccess(Runnable onSubmitSuccess) {
        this.onSubmitSuccess = onSubmitSuccess;
    }

    public AddItemController(WarehousePI warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

        @FXML private TextField Amount;
        @FXML private TextField Item;

        @FXML
        private void handleSubmit() {
            String name = Item.getText();
            String amount = Amount.getText();

            System.out.println("Name: " + name);
            System.out.println("Amount: " + amount);

            int nextTrayId = warehouseClient.getInventory().stream()
                    .mapToInt(InventoryView::getTrayId)
                    .max()
                    .orElse(0) + 1;


            warehouseClient.insertItem(nextTrayId, name, Integer.parseInt(amount));

            System.out.println(warehouseClient.getInventory());
            if(onSubmitSuccess != null) {
                onSubmitSuccess.run();
            }

            Stage stage = (Stage) Item.getScene().getWindow();
            stage.close();


        }

    }

