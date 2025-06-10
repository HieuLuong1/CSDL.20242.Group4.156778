package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class EmployeeNewOrderController {

    @FXML
    private TextField tfOrderID;
    @FXML
    private TextField tfNameProduct;
    @FXML
    private TextField tfQuantity;
    @FXML
    private TextField tfPhone;
    @FXML
    private TextField tfEmp;

    @FXML
    private TableView<Batch> tbBatch;
    @FXML
    private TableColumn<Batch, Integer> colBatchID;
    @FXML
    private TableColumn<Batch, Integer> colTotalQuan;
    @FXML
    private TableColumn<Batch, Integer> colQuanInStock;

    @FXML
    private TableView<InvoiceItem> tableOrder;
    @FXML
    private TableColumn<InvoiceItem, Integer> colBatchOrder;
    @FXML
    private TableColumn<InvoiceItem, String> colProductOrder;
    @FXML
    private TableColumn<InvoiceItem, Integer> colQuanOrder;
    @FXML
    private TableColumn<InvoiceItem, Double> colPrice;
    @FXML
    private TableColumn<InvoiceItem, Double> colSum;

    @FXML
    private DatePicker dpOrderDate;
    @FXML
    private Label lbTotalMoney;

    @FXML
    private Button btnConfirm;

    @FXML
    private RadioButton radioBtnCash;
    @FXML
    private RadioButton radioBtnCard;
    @FXML
    private RadioButton radioBtnWallet;

    private ObservableList<Batch> allBatches = FXCollections.observableArrayList();
    private ObservableList<Item> allItems = FXCollections.observableArrayList();
    private ObservableList<InvoiceItem> orderItems = FXCollections.observableArrayList();

    private double totalMoney = 0.0;

    private EmployeeOrderController parentController;

    public void setParentController(EmployeeOrderController parentController) {
        this.parentController = parentController;
    }

    public void initialize() {
        totalMoney = 0.0;
        updateTotalMoneyLabel();

        colBatchID.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colTotalQuan.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colQuanInStock.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        tbBatch.setItems(allBatches);

        // Cấu hình bảng Order Items
        colBatchOrder.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colProductOrder.setCellValueFactory(new PropertyValueFactory<>("product"));
        colQuanOrder.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colSum.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableOrder.setItems(orderItems);

        // Tải dữ liệu Batch từ DB
        loadBatchesFromDB();

        tfNameProduct.textProperty().addListener((obs, oldVal, newVal) -> searchBatchByProductName());
    }

    private void updateTotalMoneyLabel() {
        lbTotalMoney.setText(String.format("%.0f VND", totalMoney));
    }

    private void loadBatchesFromDB() {
        allBatches.clear();
        allItems.clear();

        String sql = "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, b.quantity_in_stock, "
                + "p.product_id, p.product_name, s.supplier_name, b.value_batch, p.price_with_tax "
                + "FROM batch b "
                + "JOIN products p ON b.product_id = p.product_id "
                + "JOIN import_reports ir ON b.import_id = ir.import_id "
                + "JOIN suppliers s ON ir.supplier_id = s.supplier_id where b.quantity_in_stock > 0";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int batchId = rs.getInt("batch_id");
                LocalDate importDate = rs.getDate("import_date").toLocalDate();
                Date expRaw = rs.getDate("expiration_date");
                LocalDate expDate = expRaw != null ? expRaw.toLocalDate() : null;
                int totalQty = rs.getInt("total_quantity");
                int quantityInStock = rs.getInt("quantity_in_stock");
                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                String supplierName = rs.getString("supplier_name");
                int valueBatch = rs.getInt("value_batch");
                double price = rs.getDouble("price_with_tax");

                allBatches.add(new Batch(batchId, importDate, expDate, totalQty, quantityInStock, productName, supplierName, valueBatch));
                allItems.add(new Item(productId, productName, "", price, 0, ""));
            }

            tbBatch.setItems(allBatches);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải dữ liệu lô hàng từ cơ sở dữ liệu.");
        }
    }

    private int getProductIdByName(String productName) {
        for (Item item : allItems) {
            if (item.getName().equalsIgnoreCase(productName)) {
                return item.getProductId();
            }
        }
        return -1;
    }

    private void searchBatchByProductName() {
        String keyword = tfNameProduct.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tbBatch.setItems(allBatches);
            return;
        }

        ObservableList<Batch> filtered = FXCollections.observableArrayList();
        for (Batch b : allBatches) {
            String productName = b.getProductName().toLowerCase();
            int productId = getProductIdByName(b.getProductName());

            if (productName.contains(keyword) || String.valueOf(productId).contains(keyword)) {
                filtered.add(b);
            }
        }
        tbBatch.setItems(filtered);
    }

    // Xử lý thêm sản phẩm vào hóa đơn (Invoice)
    @FXML
    private void handleSend() {
        Batch selected = tbBatch.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn lô hàng.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(tfQuantity.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Số lượng không hợp lệ.");
            return;
        }

        int inStock = selected.getQuantityInStock();
        if (quantity <= 0 || quantity > inStock) {
            showAlert("Số lượng vượt quá số lượng tồn kho.");
            return;
        }

        // Tìm Item tương ứng để lấy giá
        Item matchedItem = allItems.stream()
                .filter(item -> item.getName().equals(selected.getProductName()))
                .findFirst()
                .orElse(null);

        if (matchedItem == null) {
            showAlert("Không tìm thấy sản phẩm tương ứng.");
            return;
        }

        double price = matchedItem.getPrice();
        double total = price * quantity;

        // Thêm vào danh sách orderItems
        orderItems.add(new InvoiceItem(selected.getBatchId(), selected.getProductName(), quantity, price, total));

        // Cập nhật số lượng đã bán của batch và tổng tiền
        selected.setQuantityInStock(inStock - quantity);
        totalMoney += total;
        updateTotalMoneyLabel();

        tbBatch.refresh();
        tfQuantity.clear();
    }

    // Xử lý hủy 1 sản phẩm trong hóa đơn
    @FXML
    private void handleCancel() {
        InvoiceItem selectedItem = tableOrder.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Vui lòng chọn dòng muốn hủy trong bảng hóa đơn.");
            return;
        }

        totalMoney -= selectedItem.getTotal();
        updateTotalMoneyLabel();

        for (Batch batch : allBatches) {
            if (batch.getBatchId() == selectedItem.getBatchId()) {
                batch.setQuantityInStock(batch.getQuantityInStock() + selectedItem.getQuantity());
                break;
            }
        }

        orderItems.remove(selectedItem);
        tbBatch.refresh();
    }

    // Xử lý xác nhận đặt hàng
    @FXML
    private ObservableList<InvoiceItem> handleConfirm() {
        String phoneCustomer = tfPhone.getText().trim();
        int nameId = Integer.parseInt(tfEmp.getText().trim());
        LocalDate dateValue = dpOrderDate.getValue();

        String paymentMethod = radioBtnCash.isSelected() ? "Tien mat"
                : radioBtnCard.isSelected() ? "Chuyen khoan"
                : radioBtnWallet.isSelected() ? "Vi dien tu" : "";

        if (phoneCustomer.isEmpty() || dateValue == null || paymentMethod.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin khách hàng, nhân viên, ngày đặt và phương thức thanh toán.");
            return null;
        }

        if (orderItems.isEmpty()) {
            showAlert("Hóa đơn không có sản phẩm.");
            return null;
        }

        String orderId = tfOrderID.getText().trim();
        if (orderId.isEmpty()) {
            showAlert("Mã đơn hàng không được để trống.");
            return null;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int customerId = getCustomerIdByPhone(conn, phoneCustomer);
            int employeeId = getEmployeeIdById(conn, nameId);

            if (customerId == -1) {
                showAlert("Không tìm thấy khách hàng.");
                conn.rollback();
                return null;
            }
            if (employeeId == -1) {
                showAlert("Không tìm thấy nhân viên.");
                conn.rollback();
                return null;
            }

            // Insert invoice
            String insertInvoiceSQL = "INSERT INTO orders(order_id, order_date, total_amount,  payment_method, customer_id, employee_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInvoice = conn.prepareStatement(insertInvoiceSQL)) {
                psInvoice.setInt(1, Integer.parseInt(orderId));
                psInvoice.setDate(2, Date.valueOf(dateValue));
                psInvoice.setDouble(3, totalMoney);
                psInvoice.setString(4, paymentMethod);
                psInvoice.setInt(5, customerId);
                psInvoice.setInt(6, employeeId);

                psInvoice.executeUpdate();
            }

            // Insert invoice details and update batch quantities
            String insertDetailSQL = "INSERT INTO order_details(order_id, batch_id, quantity) VALUES (?, ?, ?)";
            //String updateBatchSQL = "UPDATE batch SET quantity_in_stock = quantity_in_stock - ? WHERE batch_id = ?";

            try (PreparedStatement psDetail = conn.prepareStatement(insertDetailSQL)) {
                //PreparedStatement psBatchUpdate = conn.prepareStatement(updateBatchSQL)) {

                for (InvoiceItem item : orderItems) {
                    psDetail.setInt(1, Integer.parseInt(orderId));
                    psDetail.setInt(2, item.getBatchId());
                    psDetail.setInt(3, item.getQuantity());

                    psDetail.executeUpdate();

                    //psBatchUpdate.setInt(1, item.getQuantity());
                    //psBatchUpdate.setInt(2, item.getBatchId());
                    //psBatchUpdate.executeUpdate();
                }

            }

            conn.commit();
            showAlert("Đơn hàng đã được lưu thành công.");

            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
            if (parentController != null) {
                parentController.loadOrdersFromDB();  // reload dữ liệu mới từ DB
            }

            return orderItems;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu đơn hàng vào cơ sở dữ liệu.");
            return null;
        }
    }

    private int getCustomerIdByPhone(Connection conn, String phone) throws SQLException {
        String sql = "SELECT customer_id FROM customer where phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        }
        return -1;
    }

    private int getEmployeeIdById(Connection conn, int id) throws SQLException {
        String sql = "SELECT employee_id FROM employee WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                }
            }
        }
        return -1;
    }

    // Hiện hộp thoại cảnh báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}