package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class Admin_AddImportController {

    @FXML private Button btnAddBatch;
    @FXML private VBox batchContainer;
    @FXML private Button btnCreate;
    @FXML private VBox batchList;

    @FXML private TextField txtImportId;
    @FXML private DatePicker dateImportDate;
    @FXML private TextField txtLocation;
    @FXML private TextField txtSupplier;
    @FXML private TextField txtCreator;

    private ObservableList<ImportOrder> importOrderList;

    public void setImportOrderList(ObservableList<ImportOrder> list) {
        this.importOrderList = list;
    }

    @FXML
    public void initialize() {
        btnAddBatch.setOnAction(event -> addBatchFields());
        btnCreate.setOnAction(event -> handleCreateImport());
    }

    private void addBatchFields() {
        HBox batchBox = new HBox(10);

        TextField txtBatchId = new TextField();
        txtBatchId.setPromptText("Mã lô");

        TextField txtExpiryDate = new TextField();
        txtExpiryDate.setPromptText("Hạn sử dụng (yyyy-MM-dd)");

        TextField txtQuantity = new TextField();
        txtQuantity.setPromptText("Số lượng");

        TextField txtProductName = new TextField();
        txtProductName.setPromptText("Tên sản phẩm");

        TextField txtCost = new TextField();
        txtCost.setPromptText("Điền giá lô");

        batchBox.getChildren().addAll(txtBatchId, txtExpiryDate, txtQuantity, txtProductName, txtCost);
        batchContainer.getChildren().add(batchBox);
    }

    @FXML
    private void handleCreateImport() {
        LocalDate date = dateImportDate.getValue();
        String address = txtLocation.getText().trim();
        String supplierName = txtSupplier.getText().trim();
        String creatorIdStr = txtCreator.getText().trim();
        int employeeId;
        try {
            employeeId = Integer.parseInt(creatorIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Mã nhân viên không hợp lệ.");
            return;
        }
        int supplierId = getSupplierIdByName(supplierName);


        if (supplierId == -1) {
            System.out.println("Không tìm thấy nhà cung cấp.");
            return;
        }
        if (employeeId == -1) {
            System.out.println("Không tìm thấy nhân viên: " + employeeId);
            return;
        }

        ObservableList<String> batchNames = FXCollections.observableArrayList();
        double totalValue = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction

            // 1. Insert into import_reports
            String sqlImport = "INSERT INTO import_reports (import_date, delivery_address, total_amount, employee_id, supplier_id) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING import_id";
            PreparedStatement psImport = conn.prepareStatement(sqlImport);
            psImport.setDate(1, Date.valueOf(date));
            psImport.setString(2, address);
            psImport.setDouble(3, 0); // temporary
            psImport.setInt(4, employeeId);  // ✅ fix đúng
            psImport.setInt(5, supplierId);

            ResultSet rsImport = psImport.executeQuery();
            int importId = -1;
            if (rsImport.next()) {
                importId = rsImport.getInt("import_id");
            } else {
                throw new SQLException("Không thể tạo phiếu nhập.");
            }
            rsImport.close();
            psImport.close();

            // 2. Insert batches
            String sqlBatch = "INSERT INTO batch (import_date, expiration_date, total_quantity, quantity_in_stock, product_id, import_id, value_batch) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING batch_id";
            PreparedStatement psBatch = conn.prepareStatement(sqlBatch);

            for (Node node : batchContainer.getChildren()) {
                if (node instanceof HBox hBox) {
                    String batchName = null;
                    String expiryDateStr = null;
                    String quantityStr = null;
                    String productName = null;
                    String costStr = null;

                    for (Node child : hBox.getChildren()) {
                        if (child instanceof TextField tf) {
                            String prompt = tf.getPromptText();
                            String text = tf.getText().trim();

                            switch (prompt) {
                                case "Mã lô" -> batchName = text;
                                case "Hạn sử dụng (yyyy-MM-dd)" -> expiryDateStr = text;
                                case "Số lượng" -> quantityStr = text;
                                case "Tên sản phẩm" -> productName = text;
                                case "Điền giá lô" -> costStr = text;
                            }
                        }
                    }

                    if (productName == null || productName.isEmpty()) continue;

                    int productId = getProductIdByName(productName);
                    if (productId == -1) {
                        System.out.println("Không tìm thấy sản phẩm: " + productName);
                        continue;
                    }

                    int quantity = Integer.parseInt(quantityStr);
                    double cost = Double.parseDouble(costStr);
                    LocalDate expiryDate = expiryDateStr.isEmpty() ? null : LocalDate.parse(expiryDateStr);

                    psBatch.setDate(1, Date.valueOf(date));
                    if (expiryDate != null) {
                        psBatch.setDate(2, Date.valueOf(expiryDate));
                    } else {
                        psBatch.setNull(2, Types.DATE);
                    }
                    psBatch.setInt(3, quantity);
                    psBatch.setInt(4, quantity);
                    psBatch.setInt(5, productId);
                    psBatch.setInt(6, importId);
                    psBatch.setDouble(7, cost);

                    psBatch.executeQuery();

                    batchNames.add("Lô" + batchName);
                    totalValue += cost;
                }
            }

            psBatch.close();

            // 3. Update total value
            PreparedStatement psUpdate = conn.prepareStatement("UPDATE import_reports SET total_amount = ? WHERE import_id = ?");
            psUpdate.setDouble(1, totalValue);
            psUpdate.setInt(2, importId);
            psUpdate.executeUpdate();
            psUpdate.close();

            conn.commit();

            // 4. Add to observable list
            String importCode = String.valueOf(importId);
            ImportOrder order = new ImportOrder(importCode, date, address, totalValue, batchNames, supplierName);
            importOrderList.add(order);

            ((Stage) btnCreate.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSupplierIdByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT supplier_id FROM suppliers WHERE supplier_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("supplier_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getProductIdByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT product_id FROM products WHERE product_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("product_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}