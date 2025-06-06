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
        Connection conn = null;
        PreparedStatement stmtProduct = null;
        PreparedStatement stmtImport = null;
        PreparedStatement stmtBatch = null;
        ResultSet rsProductKeys = null;
        ResultSet rsImportKeys = null;

        try {
            // 1. Đọc dữ liệu từ form
            String name     = productNameField.getText().trim();
            String unit     = unitField.getText().trim();
            double price    = Double.parseDouble(priceField.getText().trim());
            int quantity    = Integer.parseInt(quantityField.getText().trim());
            String category = cateField.getText().trim();
            String supplier = supField.getText().trim();

            // 2. Lấy category_id và supplier_id
            int categoryId = getCategoryIdByName(category);
            int supplierId = getSupplierIdByName(supplier);

            if (categoryId == -1 || supplierId == -1) {
                System.out.println("Không tìm thấy category hoặc supplier.");
                return;
            }

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // chạy trong một transaction

            // ──────── 3. Chèn vào products ────────
            String sqlProduct =
                "INSERT INTO products (product_name, unit, price_with_tax, quantity_in_stock, category_id) " +
                "VALUES (?, ?, ?, ?, ?)";
            stmtProduct = conn.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS);
            stmtProduct.setString(1, name);
            stmtProduct.setString(2, unit);
            stmtProduct.setDouble(3, price);
            stmtProduct.setInt(4, quantity);
            stmtProduct.setInt(5, categoryId);
            stmtProduct.executeUpdate();

            // Lấy product_id vừa tạo
            rsProductKeys = stmtProduct.getGeneratedKeys();
            int productId;
            if (rsProductKeys.next()) {
                productId = rsProductKeys.getInt(1);
            } else {
                throw new SQLException("Không lấy được product_id vừa tạo.");
            }

            // ──────── 4. Chèn vào import_reports ────────
            String sqlImport =
                "INSERT INTO import_reports (import_date, delivery_address, total_amount, employee_id, supplier_id) " +
                "VALUES (?, ?, ?, NULL, ?)";
            stmtImport = conn.prepareStatement(sqlImport, Statement.RETURN_GENERATED_KEYS);
            stmtImport.setDate(1, Date.valueOf(LocalDate.now()));
            stmtImport.setString(2, "");
            stmtImport.setDouble(3, price * quantity);
            stmtImport.setInt(4, supplierId);
            stmtImport.executeUpdate();

            // Lấy import_id vừa tạo
            rsImportKeys = stmtImport.getGeneratedKeys();
            int importId;
            if (rsImportKeys.next()) {
                importId = rsImportKeys.getInt(1);
            } else {
                throw new SQLException("Không lấy được import_id vừa tạo.");
            }

            // ──────── 5. Chèn vào batch ────────
            String sqlBatch =
                "INSERT INTO batch (import_date, expiration_date, total_quantity, quantity_in_stock, product_id, import_id, value_batch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtBatch = conn.prepareStatement(sqlBatch);
            stmtBatch.setDate(1, Date.valueOf(LocalDate.now()));
            stmtBatch.setDate(2, null);
            stmtBatch.setInt(3, quantity);
            stmtBatch.setInt(4, quantity);
            stmtBatch.setInt(5, productId);
            stmtBatch.setInt(6, importId);
            stmtBatch.setDouble(7, price * quantity);
            stmtBatch.executeUpdate();

            // ──────── 6. Commit transaction ────────
            conn.commit();

            // 7. Reload lại danh sách sản phẩm (có thể load thêm batch/phiếu nhập)
            storeManageController.loadProductDataFromDB();
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Đóng tài nguyên
            try { if (rsProductKeys != null) rsProductKeys.close(); } catch (Exception e) { }
            try { if (rsImportKeys   != null) rsImportKeys.close(); } catch (Exception e) { }
            try { if (stmtProduct    != null) stmtProduct.close(); } catch (Exception e) { }
            try { if (stmtImport     != null) stmtImport.close(); } catch (Exception e) { }
            try { if (stmtBatch      != null) stmtBatch.close(); } catch (Exception e) { }
            try { if (conn           != null) conn.setAutoCommit(true); } catch (Exception e) { }
        }
    }

    private int getCategoryIdByName(String name) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT category_id FROM categories WHERE category_name = ?"
             )) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("category_id");
            }
        }
        return -1;
    }

    private int getSupplierIdByName(String name) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT supplier_id FROM suppliers WHERE supplier_name = ?"
             )) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("supplier_id");
            }
        }
        return -1;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}