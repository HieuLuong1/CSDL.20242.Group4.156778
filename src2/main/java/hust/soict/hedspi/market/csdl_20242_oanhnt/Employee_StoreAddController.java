package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class Employee_StoreAddController {
    @FXML private Button cancelBtn;
    @FXML private TextField priceField;
    @FXML private TextField productNameField;
    @FXML private TextField cateField;
    @FXML private TextField quantityField;
    @FXML private TextField unitField;
    @FXML private TextField supField; // thay cho supplierCombo
    @FXML private Button saveProductBtn;

    private Employee_StoreManageController storeManageController;
    private int employeeId;

    public void setStoreManageController(Employee_StoreManageController controller) {
        this.storeManageController = controller;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    private void initialize() {
        saveProductBtn.setOnAction(e -> saveProduct());
        cancelBtn.setOnAction(e -> closeWindow());
    }

    private void saveProduct() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String name = productNameField.getText().trim();
            String unit = unitField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());
            int categoryId = getCategoryIdByName(cateField.getText().trim());

            if (categoryId == -1) {
                System.out.println("Loại sản phẩm không tồn tại.");
                return;
            }

            int supplierId = getSupplierIdByName(supField.getText().trim());
            if (supplierId == -1) {
                System.out.println("Nhà cung cấp không tồn tại.");
                return;
            }

            // Thêm sản phẩm
            PreparedStatement ps1 = conn.prepareStatement(
                "INSERT INTO products(product_name, unit, price_with_tax, quantity_in_stock, category_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, name);
            ps1.setString(2, unit);
            ps1.setDouble(3, price);
            ps1.setInt(4, qty);
            ps1.setInt(5, categoryId);
            ps1.executeUpdate();
            ResultSet rs1 = ps1.getGeneratedKeys();
            rs1.next();
            int pid = rs1.getInt(1);

            // Thêm phiếu nhập
            PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO import_reports(import_date, delivery_address, total_amount, employee_id, supplier_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps2.setDate(1, Date.valueOf(LocalDate.now()));
            ps2.setString(2, "");
            ps2.setDouble(3, price * qty);
            ps2.setInt(4, employeeId); // chính là người tạo phiếu
            ps2.setInt(5, supplierId);
            ps2.executeUpdate();
            ResultSet rs2 = ps2.getGeneratedKeys();
            rs2.next();
            int impId = rs2.getInt(1);

            // Thêm lô hàng
            PreparedStatement ps3 = conn.prepareStatement(
                "INSERT INTO batch(import_date, expiration_date, total_quantity, quantity_in_stock, product_id, import_id, value_batch) VALUES(?,?,?,?,?,?,?)");
            ps3.setDate(1, Date.valueOf(LocalDate.now()));
            ps3.setNull(2, Types.DATE); // chưa nhập hạn dùng
            ps3.setInt(3, qty);
            ps3.setInt(4, qty);
            ps3.setInt(5, pid);
            ps3.setInt(6, impId);
            ps3.setDouble(7, price * qty);
            ps3.executeUpdate();

            conn.commit();
            storeManageController.loadProductDataFromDB();
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryIdByName(String name) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT category_id FROM categories WHERE category_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private int getSupplierIdByName(String name) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT supplier_id FROM suppliers WHERE supplier_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}
