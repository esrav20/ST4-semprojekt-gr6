package com.example.guidemo_4semester;

import dk.sdu.CommonAGV.AGVPI;
import dk.sdu.CommonInventory.WarehousePI;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddItemController {
    private final WarehousePI warehouseClient;

    public AddItemController(WarehousePI warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

        @FXML private TextField Tray_ID;
        @FXML private TextField Amount;
        @FXML private TextField Item;

        @FXML
        private void handleSubmit() {
            String name = Item.getText();
            String amount = Amount.getText();
            String Trayid = Tray_ID.getText();



            System.out.println("Name: " + name);
            System.out.println("Amount: " + amount);
            System.out.println("Trayid: " + Trayid);

            warehouseClient.insertItem(Integer.parseInt(Trayid), name, Integer.parseInt(amount));

            System.out.println(warehouseClient.getInventory());

            Stage stage = (Stage) Item.getScene().getWindow();
            stage.close();

        }
    }

