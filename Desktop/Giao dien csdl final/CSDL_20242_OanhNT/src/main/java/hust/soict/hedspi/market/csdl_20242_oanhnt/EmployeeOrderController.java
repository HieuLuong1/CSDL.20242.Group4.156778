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
                            "  JOIN employee e ON o.employee_id = e.employee_id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("order_id"));
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
            items.add(new InvoiceItem(product, quantity, price));
        }
        rs.close();
        stmt.close();
        return items;
    }

    private void searchInvoiceByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            tableView.getItems().setAll(allInvoices);
        } else {
            List<Invoice> filtered = new ArrayList<>();
            for (Invoice invoice : allInvoices) {
                if (invoice.getPhoneCustomer().contains(phone)) {
                    filtered.add(invoice);
                }
            }
            tableView.getItems().setAll(filtered);
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
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Hóa đơn đã được in thành công.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewInvoice(Invoice newInvoice) {
        allInvoices.add(newInvoice);
        tableView.getItems().add(newInvoice);
        lbSumCustomer.setText(String.valueOf(allInvoices.size()));
    }
}