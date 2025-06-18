package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeOrderController implements Initializable {
    @FXML private Button btnNewOrder;
    @FXML private Button btnPrint;
    @FXML private TableView<Invoice> tableView;
    @FXML private TableColumn<Invoice, String> colEmp;
    @FXML private TableColumn<Invoice, String> colID;
    @FXML private TableColumn<Invoice, String> colMethod;
    @FXML private TableColumn<Invoice, String> colName;
    @FXML private TableColumn<Invoice, String> colOrderDate;
    @FXML private TableColumn<Invoice, String> colPhone;
    @FXML private TableColumn<Invoice, Double> colTotal;
    @FXML private Label lbSumCustomer;
    @FXML private TextField tfSearchField;

    private List<Invoice> allInvoices = new ArrayList<>();
    private int employeeId;

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        showTodayOrderCount(); // Gọi hàm này ngay sau khi nhận được ID
    }
    private void showTodayOrderCount() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT total_order(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                lbSumCustomer.setText(String.valueOf(count));
            } else {
                lbSumCustomer.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lbSumCustomer.setText("Lỗi");
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập column
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneCustomer"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameCustomer"));
        colEmp.setCellValueFactory(new PropertyValueFactory<>("nameEmployee"));

        loadOrdersFromDB();

        tfSearchField.textProperty().addListener((observable, oldValue, newValue) ->
                searchInvoiceByPhone(newValue)
        );

        btnPrint.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnPrint.setDisable(newSel == null);
        });
    }

    void loadOrdersFromDB() {
        allInvoices.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Cần SELECT: order_id, order_date, total_amount, payment_method, customer_name, phone, employee_name
            String sql =
                    "SELECT " +
                            "  o.order_id, " +
                            "  o.order_date, " +
                            "  o.total_amount, " +
                            "  o.payment_method, " +
                            "  c.fullname AS customer_name, " +
                            "  c.phone, " +
                            "  e.firstname || ' ' || e.lastname AS emp_name " +
                            "FROM orders o " +
                            "  JOIN customer c ON o.customer_id = c.customer_id " +
                            "  JOIN employee e ON o.employee_id = e.employee_id order by o.order_id desc";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = String.format("HD%03d", rs.getInt("order_id"));
                String date = rs.getDate("order_date").toString();
                double total = rs.getDouble("total_amount");
                String method = rs.getString("payment_method");
                String phone = rs.getString("phone");
                String custName = rs.getString("customer_name");
                String empName = rs.getString("emp_name");

                // Lấy chi tiết cho mỗi order
                List<InvoiceItem> items = getOrderDetailsFromDB(rs.getInt("order_id"), conn);
                Invoice invoice = new Invoice(id, date, method, phone, custName, empName, items);
                allInvoices.add(invoice);
            }

            tableView.getItems().setAll(allInvoices);
            lbSumCustomer.setText(String.valueOf(allInvoices.size()));

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<InvoiceItem> getOrderDetailsFromDB(int orderId, Connection conn) throws SQLException {
        List<InvoiceItem> items = new ArrayList<>();
        // Cần SELECT đúng: od.batch_id, od.quantity, p.product_name, p.price_with_tax
        String sql =
                "SELECT " +
                        "  od.batch_id, " +
                        "  od.quantity, " +
                        "  p.product_name, " +
                        "  p.price_with_tax " +
                        "FROM order_details od " +
                        "  JOIN batch b ON od.batch_id = b.batch_id " +
                        "  JOIN products p ON b.product_id = p.product_id " +
                        "WHERE od.order_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String product = rs.getString("product_name");
            int quantity = rs.getInt("quantity");
            double price = rs.getDouble("price_with_tax");
            items.add(new InvoiceItem(
                    rs.getInt("batch_id"),
                    null,
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price_with_tax"),
                    rs.getInt("quantity") * rs.getDouble("price_with_tax")
            ));
        }
        rs.close();
        stmt.close();
        return items;
    }

    private void searchInvoiceByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            tableView.getItems().setAll(allInvoices);
            return;
        }

        List<Invoice> filtered = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM order_bought(?)"
             )) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("orderid");
                    String id = String.format("HD%03d", orderId);
                    String date = rs.getDate("orderdate").toString();
                    String method = rs.getString("method");
                    double total = rs.getDouble("totalamount");

                    // Lấy thêm thông tin tên khách hàng, nhân viên, số điện thoại (dùng truy vấn phụ)
                    try (PreparedStatement detailStmt = conn.prepareStatement("""
                    SELECT c.fullname AS cust_name, c.phone, e.firstname || ' ' || e.lastname AS emp_name
                    FROM orders o
                    JOIN customer c ON o.customer_id = c.customer_id
                    JOIN employee e ON o.employee_id = e.employee_id
                    WHERE o.order_id = ?
                """)) {
                        detailStmt.setInt(1, orderId);
                        ResultSet drs = detailStmt.executeQuery();
                        if (drs.next()) {
                            String phoneCust = drs.getString("phone");
                            String custName = drs.getString("cust_name");
                            String empName = drs.getString("emp_name");

                            List<InvoiceItem> items = getOrderDetailsFromDB(orderId, conn);
                            Invoice invoice = new Invoice(id, date, method, phoneCust, custName, empName, items);
                            filtered.add(invoice);
                        }
                        drs.close();
                    }
                }
            }

            tableView.getItems().setAll(filtered);
            lbSumCustomer.setText(String.valueOf(filtered.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void handleNewOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_NewOrder.fxml"));
            Parent root = loader.load();
            EmployeeNewOrderController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tạo hóa đơn");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePrint() {
        Invoice selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một hóa đơn để in.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("=========== HÓA ĐƠN ===========\n");
        builder.append("Mã hóa đơn: ").append(selected.getId()).append("\n");
        builder.append("Ngày mua: ").append(selected.getDate()).append("\n");
        builder.append("Khách hàng: ").append(selected.getNameCustomer()).append("\n");
        builder.append("SĐT: ").append(selected.getPhoneCustomer()).append("\n");
        builder.append("Nhân viên tạo: ").append(selected.getNameEmployee()).append("\n");
        builder.append("Phương thức: ").append(selected.getPaymentMethod()).append("\n");
        builder.append("-------------------------------\n");
        builder.append("Sản phẩm\tSL\tĐơn giá\tThành tiền\n");

        double total = 0;
        for (InvoiceItem item : selected.getItems()) {
            double lineTotal = item.getQuantity() * item.getUnitPrice();
            total += lineTotal;
            builder.append(String.format("%-20s%5d%12.0f%12.0f\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    lineTotal));
        }

        builder.append("-------------------------------\n");
        builder.append(String.format("TỔNG TIỀN: %.0f VND\n", total));
        builder.append("================================");

        // Hiển thị hóa đơn bằng Alert hoặc có thể mở cửa sổ riêng nếu bạn muốn
        TextArea textArea = new TextArea(builder.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết Hóa đơn");
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void reloadAfterNewOrder() {
        loadOrdersFromDB();
        showTodayOrderCount();
    }
}