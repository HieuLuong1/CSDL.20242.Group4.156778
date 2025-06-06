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
import java.util.ArrayList;
import java.util.List;

public class EmployeeNewOrderController {
    @FXML private TextField tfOrderID, tfNameProduct, tfQuantity, tfPhone, tfEmp, tfNameCustomer;
    @FXML private TableView<Batch> tbBatch;
    @FXML private TableColumn<Batch, Integer> colBatchID, colTotalQuan, colQuanInStock;
    @FXML private TableView<InvoiceItem> tableOrder;
    @FXML private TableColumn<InvoiceItem, Integer> colBatchOrder;
    @FXML private TableColumn<InvoiceItem, String> colProductOrder;
    @FXML private TableColumn<InvoiceItem, Integer> colQuanOrder;
    @FXML private TableColumn<InvoiceItem, Double> colPrice, colSum;
    @FXML private DatePicker dpOrderDate;
    @FXML private Label lbTotalMoney;
    @FXML private Button btnConfirm;
    @FXML private RadioButton radioBtnCash, radioBtnCard, radioBtnWallet;

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
        colQuanInStock.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(() ->
                        cellData.getValue().getTotalQuantity() - cellData.getValue().getSoldQuantity()).asObject());

        tbBatch.setItems(FXCollections.observableArrayList());

        colBatchOrder.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colProductOrder.setCellValueFactory(new PropertyValueFactory<>("product"));
        colQuanOrder.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colSum.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableOrder.setItems(orderItems);

        loadBatchesFromDB();
        tfNameProduct.textProperty().addListener((obs, oldVal, newVal) -> searchBatchByProductName());
    }

    private void updateTotalMoneyLabel() {
        lbTotalMoney.setText(String.format("%.0f VND", totalMoney));
    }

    private void loadBatchesFromDB() {
        allBatches.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, b.quantity_in_stock, " +
                         "p.product_name, s.supplier_name, b.value_batch, p.price_with_tax " +
                         "FROM batch b " +
                         "JOIN products p ON b.product_id = p.product_id " +
                         "JOIN suppliers s ON b.supplier_id = s.supplier_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int batchId = rs.getInt("batch_id");
                LocalDate importDate = rs.getDate("import_date").toLocalDate();
                LocalDate expDate = rs.getDate("expiration_date").toLocalDate();
                int totalQty = rs.getInt("total_quantity");
                int inStock = rs.getInt("quantity_in_stock");
                int soldQty = totalQty - inStock;
                String productName = rs.getString("product_name");
                String supplier = rs.getString("supplier_name");
                int value = rs.getInt("value_batch");
                double price = rs.getDouble("price_with_tax");

                allBatches.add(new Batch(batchId, importDate, expDate, totalQty, soldQty, productName, supplier, value));
                allItems.add(new Item(productName, "", price, 0, "", supplier));
            }
            tbBatch.setItems(allBatches);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchBatchByProductName() {
        String keyword = tfNameProduct.getText().trim().toLowerCase();
        if (keyword.isEmpty()) return;
        ObservableList<Batch> filtered = FXCollections.observableArrayList();
        for (Batch b : allBatches) {
            if (b.getProductName().toLowerCase().contains(keyword)) {
                filtered.add(b);
            }
        }
        tbBatch.setItems(filtered);
    }

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
        int inStock = selected.getTotalQuantity() - selected.getSoldQuantity();
        if (quantity <= 0 || quantity > inStock) {
            showAlert("Số lượng vượt quá số lượng còn lại.");
            return;
        }
        Item matchedItem = null;
        for (Item item : allItems) {
            if (item.getName().equals(selected.getProductName()) &&
                    item.getSupplier().equals(selected.getSupplier())) {
                matchedItem = item; break;
            }
        }
        if (matchedItem == null) {
            showAlert("Không tìm thấy sản phẩm tương ứng.");
            return;
        }
        double price = matchedItem.getPrice();
        double total = price * quantity;

        orderItems.add(new InvoiceItem(selected.getBatchId(), selected.getProductName(), quantity, price, total));
        selected.setSoldQuantity(selected.getSoldQuantity() + quantity);
        totalMoney += total;
        updateTotalMoneyLabel();
        tbBatch.refresh();
        tfQuantity.clear();
    }

    @FXML
    private void handleCancel() {
        InvoiceItem selectedItem = tableOrder.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            totalMoney -= selectedItem.getTotal();
            updateTotalMoneyLabel();
            for (Batch batch : allBatches) {
                if (batch.getBatchId() == selectedItem.getBatchId()) {
                    batch.setSoldQuantity(batch.getSoldQuantity() - selectedItem.getQuantity());
                    break;
                }
            }
            tableOrder.getItems().remove(selectedItem);
            tbBatch.refresh();
        } else {
            showAlert("Vui lòng chọn dòng muốn hủy trong bảng hóa đơn.");
        }
    }

    @FXML
    private ObservableList<InvoiceItem> handleConfirm() {
        String nameCustomer = tfNameCustomer.getText().trim();
        String phoneCustomer = tfPhone.getText().trim();
        String nameEmployee = tfEmp.getText().trim();
        LocalDate dateValue = dpOrderDate.getValue();

        String paymentMethod = radioBtnCash.isSelected() ? "Tien mat" :
                               radioBtnCard.isSelected() ? "Chuyen khoan" :
                               radioBtnWallet.isSelected() ? "Vi dien tu" : "";

        if (nameCustomer.isEmpty() || phoneCustomer.isEmpty() || nameEmployee.isEmpty() ||
                dateValue == null || paymentMethod.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return null;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int customerId = findCustomerId(phoneCustomer, conn);
            if (customerId == -1) {
                PreparedStatement insertCustomer = conn.prepareStatement(
                    "INSERT INTO customer(fullname, phone, email) VALUES (?, ?, ?) RETURNING customer_id");
                insertCustomer.setString(1, nameCustomer);
                insertCustomer.setString(2, phoneCustomer);
                insertCustomer.setString(3, "");
                ResultSet rs = insertCustomer.executeQuery();
                if (rs.next()) customerId = rs.getInt(1);
                rs.close(); insertCustomer.close();
            }

            int employeeId = findEmployeeIdByName(nameEmployee, conn);
            if (employeeId == -1) {
                showAlert("Không tìm thấy nhân viên.");
                return null;
            }

            double totalAmount = totalMoney;
            PreparedStatement insertOrder = conn.prepareStatement(
                "INSERT INTO orders(order_date, total_amount, payment_method, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING order_id");
            insertOrder.setDate(1, Date.valueOf(dateValue));
            insertOrder.setDouble(2, totalAmount);
            insertOrder.setString(3, paymentMethod);
            insertOrder.setInt(4, customerId);
            insertOrder.setInt(5, employeeId);

            ResultSet rsOrder = insertOrder.executeQuery();
            int newOrderId = -1;
            if (rsOrder.next()) newOrderId = rsOrder.getInt(1);
            rsOrder.close(); insertOrder.close();

            for (InvoiceItem item : orderItems) {
                PreparedStatement detailStmt = conn.prepareStatement(
                    "INSERT INTO order_details(order_id, batch_id, quantity) VALUES (?, ?, ?)");
                detailStmt.setInt(1, newOrderId);
                detailStmt.setInt(2, item.getBatchId());
                detailStmt.setInt(3, item.getQuantity());
                detailStmt.executeUpdate();
                detailStmt.close();

                PreparedStatement updateStockStmt = conn.prepareStatement(
                    "UPDATE batch SET quantity_in_stock = quantity_in_stock - ? WHERE batch_id = ?");
                updateStockStmt.setInt(1, item.getQuantity());
                updateStockStmt.setInt(2, item.getBatchId());
                updateStockStmt.executeUpdate();
                updateStockStmt.close();
            }

            Invoice newInvoice = new Invoice("HD" + String.format("%03d", newOrderId),
                    dateValue.toString(), paymentMethod, phoneCustomer,
                    nameCustomer, nameEmployee, orderItems);
            parentController.addNewInvoice(newInvoice);

            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
            return orderItems;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi ghi vào cơ sở dữ liệu.");
            return null;
        }
    }

    private int findCustomerId(String phone, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT customer_id FROM customer WHERE phone = ?");
        stmt.setString(1, phone);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("customer_id");
        return -1;
    }

    private int findEmployeeIdByName(String name, Connection conn) throws SQLException {
        String[] parts = name.trim().split(" ", 2);
        if (parts.length < 2) return -1;
        String first = parts[0], last = parts[1];
        PreparedStatement stmt = conn.prepareStatement("SELECT employee_id FROM employee WHERE firstname = ? AND lastname = ?");
        stmt.setString(1, first);
        stmt.setString(2, last);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("employee_id");
        return -1;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
