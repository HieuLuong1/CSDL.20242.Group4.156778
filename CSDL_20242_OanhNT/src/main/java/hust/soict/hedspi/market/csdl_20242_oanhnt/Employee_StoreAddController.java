package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Employee_StoreAddController {
    @FXML
    private Button cancelBtn;

    @FXML
    private TextField priceField;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField cateField;

    @FXML
    private TextField quantityField;
    @FXML
    private TextField unitField;
    @FXML
    private Button saveProductBtn;

    @FXML
    private TextField supField;
    private Employee_StoreManageController storeManageController;

    public void setStoreManageController(Employee_StoreManageController controller) {
        this.storeManageController = controller;
    }

    @FXML
    private void initialize() {
        saveProductBtn.setOnAction(e -> saveProduct());
        cancelBtn.setOnAction(e -> closeWindow());
    }
    private void saveProduct() {
        try{
            //int id = Integer.parseInt(productNameField.getText());
            String name = productNameField.getText();
            String category = cateField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            String unit = unitField.getText();
            String supplier = supField.getText();
            Item newItem = new Item(name, unit,  price,quantity,category, supplier);
            storeManageController.addItem(newItem);
            closeWindow();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}
