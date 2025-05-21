package com.example.guidemo_4semester;

import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.SoapWarehouseService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddItemController {
    private final SoapWarehouseService warehouseClient;
    private Runnable onSubmitSuccess;

    public void setOnSubmitSuccess(Runnable onSubmitSuccess) {
        this.onSubmitSuccess = onSubmitSuccess;
    }

    public AddItemController(SoapWarehouseService warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

        @FXML private TextField Id;
        @FXML private TextField Amount;
        @FXML private TextField Item;

        @FXML
        protected void handleSubmit() {
            String name = Item.getText();
            String amount = Amount.getText();
            String id = Id.getText();

            System.out.println("Name: " + name);
            System.out.println("Amount: " + amount);
            System.out.println("ID: " + id);


            int nextTrayId = warehouseClient.getInventory().stream()
                    .mapToInt(InventoryView::getTrayId)
                    .max()
                    .orElse(0) + 1;


            warehouseClient.insertItem(nextTrayId, Long.parseLong(id), name, Integer.parseInt(amount));

            System.out.println(warehouseClient.getInventory());
            if(onSubmitSuccess != null) {
                onSubmitSuccess.run();
            }

            Stage stage = (Stage) Item.getScene().getWindow();
            stage.close();


        }

    }

