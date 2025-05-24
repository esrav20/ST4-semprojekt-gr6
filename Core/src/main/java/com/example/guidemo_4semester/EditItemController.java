package com.example.guidemo_4semester;

//import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.InventoryView;
import dk.sdu.Warehouse.ServiceSoap;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditItemController {
    ServiceSoap serviceSoap;
    private InventoryView selectedItem;
    private Runnable onEditSuccess;

    //@FXML private TextField Amount;
    @FXML private TextField Item;


    public EditItemController(ServiceSoap serviceSoap, InventoryView selectedItem) {
        this.serviceSoap = serviceSoap;
        this.selectedItem = selectedItem;
    }

    public void setOnEditSuccess(Runnable onEditSuccess) {
        this.onEditSuccess = onEditSuccess;
    }

    @FXML
    public void initialize() {
        if (selectedItem != null) {
            Item.setText(selectedItem.getItemName());
            //Amount.setText(String.valueOf(selectedItem.getQuantity()));
        }
    }
    @FXML
    private void editItem() {
        String itemName = Item.getText();
        //int quantity = Integer.parseInt(Amount.getText());

        serviceSoap.insertItem(selectedItem.getTrayId(), itemName);

        if(onEditSuccess != null) {
            onEditSuccess.run();
        }

        Stage stage = (Stage) Item.getScene().getWindow();
        stage.close();

    }
}
