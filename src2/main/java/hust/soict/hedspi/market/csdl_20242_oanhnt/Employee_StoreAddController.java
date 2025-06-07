package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class Employee_StoreAddController {
    @FXML private Button cancelBtn;
    @FXML private TextField priceField;
    @FXML private TextField productNameField;
    @FXML private TextField cateField;
    @FXML private TextField quantityField;
    @FXML private TextField unitField;
    @FXML private Button saveProductBtn;
    @FXML private ComboBox<String> supplierCombo;
    @FXML private ComboBox<String> creatorCombo;

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
        loadSuppliers();
        loadCreators();
        saveProductBtn.setOnAction(e -> saveProduct());
        cancelBtn.setOnAction(e -> closeWindow());
    }

    private void loadSuppliers() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT supplier_id, supplier_name FROM suppliers")) {
            while (rs.next()) {
                supplierCombo.getItems().add(rs.getInt(1) + ":" + rs.getString(2));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadCreators() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT employee_id, firstname, lastname FROM employee")) {
            while (rs.next()) {
                creatorCombo.getItems().add(rs.getInt(1) + ":" + rs.getString(2) + " " + rs.getString(3));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void saveProduct() {
        String supSel = supplierCombo.getValue();
        String[] supParts = supSel.split(":",2);
        int supplierId = Integer.parseInt(supParts[0]);

        String creSel = creatorCombo.getValue();
        int creatorId = Integer.parseInt(creSel.split(":",2)[0]);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            String name = productNameField.getText().trim();
            String unit = unitField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());
            int categoryId = getCategoryIdByName(cateField.getText().trim());

            // Insert product
            PreparedStatement ps1 = conn.prepareStatement(
                "INSERT INTO products(product_name, unit, price_with_tax, quantity_in_stock, category_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1,name); ps1.setString(2,unit);
            ps1.setDouble(3,price); ps1.setInt(4,qty);
            ps1.setInt(5,categoryId);
            ps1.executeUpdate();
            ResultSet rs1 = ps1.getGeneratedKeys(); rs1.next(); int pid = rs1.getInt(1);

            // Insert import_reports
            PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO import_reports(import_date, delivery_address, total_amount, employee_id, supplier_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps2.setDate(1, Date.valueOf(LocalDate.now()));
            ps2.setString(2, "");
            ps2.setDouble(3, price * qty);
            ps2.setInt(4, creatorId);
            ps2.setInt(5, supplierId);
            ps2.executeUpdate();
            ResultSet rs2 = ps2.getGeneratedKeys(); rs2.next(); int impId = rs2.getInt(1);

            // Insert batch
            PreparedStatement ps3 = conn.prepareStatement(
                "INSERT INTO batch(import_date, expiration_date, total_quantity, quantity_in_stock, product_id, import_id, value_batch) VALUES(?,?,?,?,?,?,?)");
            ps3.setDate(1, Date.valueOf(LocalDate.now())); ps3.setNull(2, Types.DATE);
            ps3.setInt(3, qty); ps3.setInt(4, qty);
            ps3.setInt(5, pid); ps3.setInt(6, impId); ps3.setDouble(7, price * qty);
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
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return -1;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveProductBtn.getScene().getWindow();
        stage.close();
    }
}