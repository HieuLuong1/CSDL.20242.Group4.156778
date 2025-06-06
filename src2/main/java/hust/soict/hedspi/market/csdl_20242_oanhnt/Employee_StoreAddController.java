package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee_StoreAddController {
    @FXML private Button cancelBtn;
    @FXML private TextField priceField;
    @FXML private TextField productNameField;
    @FXML private TextField cateField;
    @FXML private TextField quantityField;
    @FXML private TextField unitField;
    @FXML private Button saveProductBtn;
    @FXML private TextField supField;

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
        try {
            String name = productNameField.getText().trim();
            String unit = unitField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String category = cateField.getText().trim();
            String supplier = supField.getText().trim();

            int categoryId = getCategoryIdByName(category);
            int supplierId = getSupplierIdByName(supplier);

            if (categoryId == -1 || supplierId == -1) {
                System.out.println("Không tìm thấy category hoặc supplier.");
                return;
            }

            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO products (product_name, unit, price_with_tax, quantity_in_stock, category_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, unit);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, categoryId);
            stmt.executeUpdate();
            stmt.close();

            storeManageController.loadProductDataFromDB();
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryIdByName(String name) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT category_id FROM categories WHERE category_name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("category_id");
        return -1;
    }

    private int getSupplierIdByName(String name) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT supplier_id FROM suppliers WHERE supplier_name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("supplier_id");
        return -1;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}
