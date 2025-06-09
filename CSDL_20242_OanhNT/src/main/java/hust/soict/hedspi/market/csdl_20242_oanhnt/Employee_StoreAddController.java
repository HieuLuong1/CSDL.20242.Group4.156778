package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class Employee_StoreAddController {
    @FXML private TextField productNameField;
    @FXML private TextField unitField;
    @FXML private TextField cateField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private Button saveProductBtn;
    @FXML private Button cancelBtn;

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
        cancelBtn.setOnAction(e -> closeWindow());
    }

    @FXML
    private void handleSave() {
        String name = productNameField.getText().trim();
        String unit = unitField.getText().trim();
        String cateName = cateField.getText().trim();
        String priceText = priceField.getText().trim();
        String qtyText = quantityField.getText().trim();

        if (name.isEmpty() || unit.isEmpty() || cateName.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceText);
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            System.out.println("Đơn giá hoặc số lượng không hợp lệ");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int categoryId = getCategoryIdByName(cateName, conn);
            if (categoryId == -1) {
                System.out.println("Loại sản phẩm không tồn tại");
                conn.rollback();
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO products(product_name, unit, price_with_tax, quantity_in_stock, category_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, unit);
                ps.setDouble(3, price);
                ps.setInt(4, qty);
                ps.setInt(5, categoryId);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.out.println("Lỗi khi lấy product_id");
                        return;
                    }
                    int productId = rs.getInt(1);
                    System.out.println("Thêm sản phẩm thành công với ID: " + productId);
                }
            }

            conn.commit();

            if (storeManageController != null) {
                storeManageController.loadProductDataFromDB();
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryIdByName(String name, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT category_id FROM categories WHERE category_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("category_id");
            }
        }
        return -1;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}
