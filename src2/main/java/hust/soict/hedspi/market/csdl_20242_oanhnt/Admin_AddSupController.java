package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Admin_AddSupController {

    @FXML private TextField tfID;
    @FXML private TextField tfName;
    @FXML private TextField tfAddress;
    @FXML private TextField tfPerson;
    @FXML private TextField tfPhone;
    @FXML private Button btnConfirm;

    private Admin_SupplierController mainController; // tham chiếu controller chính

    public void setMainController(Admin_SupplierController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        btnConfirm.setOnAction(e -> {
            String id = tfID.getText();
            String name = tfName.getText();
            String address = tfAddress.getText();
            String person = tfPerson.getText();
            String phone = tfPhone.getText();

            if (!id.isEmpty() && !name.isEmpty()) {
                Supplier newSupplier = new Supplier(id, name, address, person, phone);
                mainController.addSupplier(newSupplier); // gọi hàm bên controller chính
                // Đóng cửa sổ
                btnConfirm.getScene().getWindow().hide();
            }
        });
    }
}
