package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Admin_AddSupController {

    @FXML private TextField tfName;
    @FXML private TextField tfAddress;
    @FXML private TextField tfPerson;
    @FXML private TextField tfPhone;
    @FXML private Button btnConfirm;

    private Admin_SupplierController mainController;

    public void setMainController(Admin_SupplierController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        btnConfirm.setOnAction(e -> {
            String name = tfName.getText().trim();
            String address = tfAddress.getText().trim();
            String person = tfPerson.getText().trim();
            String phone = tfPhone.getText().trim();

            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Tên nhà cung cấp không được để trống!");
                alert.showAndWait();
                return;
            }

            mainController.addSupplierToDB(name, address, person, phone);

            btnConfirm.getScene().getWindow().hide();
        });
    }
}
