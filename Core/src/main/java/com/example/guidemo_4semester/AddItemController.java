package com.example.guidemo_4semester;

import dk.sdu.Warehouse.ServiceSoap;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

public class AddItemController {
    private final ServiceSoap serviceSoap;
    private Runnable onSubmitSuccess;

    public void setOnSubmitSuccess(Runnable onSubmitSuccess) {
        this.onSubmitSuccess = onSubmitSuccess;
    }

    public AddItemController(ServiceSoap serviceSoap) {
        this.serviceSoap = serviceSoap;
    }

    //        @FXML private TextField Id;
//        @FXML private TextField Amount;
    @FXML
    private TextField Item;

    @FXML
    protected void handleSubmit() {
        String name = Item.getText().trim();
//            String amount = Amount.getText();
//            String id = Id.getText();

        System.out.println("Name: " + name);
//            System.out.println("Amount: " + amount);
//            System.out.println("ID: " + id);


        String response = serviceSoap.getInventory();
        JSONObject obj = new JSONObject(response);
        JSONObject inventoryObj = obj.getJSONArray("Inventory").getJSONObject(0);

        String emptyTrayId = null;
        for (String key : inventoryObj.keySet()) {
            if (inventoryObj.getString(key).isEmpty()) {
                emptyTrayId = key;
                break;
            }
        }
        if (emptyTrayId != null) {
            serviceSoap.insertItem(Integer.parseInt(emptyTrayId), name);
            if (onSubmitSuccess != null) {
                onSubmitSuccess.run();
            }


            //serviceSoap.insertItem(nextTrayId, Long.parseLong(id), name, Integer.parseInt(amount));

            System.out.println(serviceSoap.getInventory());
            if (onSubmitSuccess != null) {
                onSubmitSuccess.run();
            }

            Stage stage = (Stage) Item.getScene().getWindow();
            stage.close();


        }

    }
}

