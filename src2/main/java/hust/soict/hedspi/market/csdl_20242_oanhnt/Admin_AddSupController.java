package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller của form “Thêm Nhà Cung Cấp”.
 * - User chỉ cần nhập tên, địa chỉ, đại diện, điện thoại. ID sẽ do DB sinh tự động.
 * - Khi Confirm, gọi mainController.addSupplierToDB(...)
 *   để INSERT vào DB và thêm vào TableView.
 */
public class Admin_AddSupController {

    @FXML private TextField tfName;       // tên nhà cung cấp
    @FXML private TextField tfAddress;    // địa chỉ
    @FXML private TextField tfPerson;     // đại diện
    @FXML private TextField tfPhone;      // điện thoại
    @FXML private Button btnConfirm;      // nút Save

    private Admin_SupplierController mainController;

    public void setMainController(Admin_SupplierController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        // Khi nhấn Confirm (Save), gọi phương thức addSupplierToDB(...) ở controller chính
        btnConfirm.setOnAction(event -> {
            String name = tfName.getText().trim();
            String address = tfAddress.getText().trim();
            String person = tfPerson.getText().trim();
            String phone = tfPhone.getText().trim();

            // Tối thiểu phải có tên, phần còn lại có thể để trống
            if (!name.isEmpty()) {
                mainController.addSupplierToDB(name, address, person, phone);
                // Đóng cửa sổ hiện tại sau khi thêm thành công
                btnConfirm.getScene().getWindow().hide();
            } else {
                // Nếu muốn, có thể thêm cảnh báo cho user rằng "Tên không được để trống"
            }
        });
    }
}
