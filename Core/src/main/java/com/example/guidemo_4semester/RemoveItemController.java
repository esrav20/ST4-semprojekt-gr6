package com.example.guidemo_4semester;
import com.example.guidemo_4semester.TabViewController;
import dk.sdu.CommonInventory.InventoryView;
import dk.sdu.CommonInventory.WarehousePI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class RemoveItemController {
    WarehousePI warehouseClient;
    private Runnable onRemoveSuccess;
    private InventoryView selectedItem;
    @FXML
    private TableView<InventoryView> inventoryTable;
    @FXML
    Button yesButton;
    @FXML
    Button removeCancelButton;
    public RemoveItemController(WarehousePI warehouseClient, InventoryView selectedItem) {
        this.warehouseClient = warehouseClient;
        this.selectedItem = selectedItem;
    }
    public void setOnRemoveSuccess(Runnable onRemoveSuccess) {
        this.onRemoveSuccess = onRemoveSuccess;
    }

@FXML
    private void HandleRemove() {
        //InventoryView selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Long id = selectedItem.getId();
            String result = warehouseClient.deleteitems(id);

            if (onRemoveSuccess != null) {
                onRemoveSuccess.run(); // <-- triggers loadInventory()
            }

           Stage stage = (Stage) yesButton.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Item not found");
        }
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) removeCancelButton.getScene().getWindow();
        stage.close();
    }

}
