package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.*;

public class User_PurchaseHistoryController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> colInvoiceId;
    @FXML private TableColumn<Invoice, String> colDate;
    @FXML private TableColumn<Invoice, String> colTotal;
    @FXML private TableColumn<Invoice, String> colPaymentMethod;

    @FXML private TableView<InvoiceItem> invoiceDetailTable;
    @FXML private TableColumn<InvoiceItem, String> colProductName;
    @FXML private TableColumn<InvoiceItem, Integer> colQuantity;
    @FXML private TableColumn<InvoiceItem, Double> colUnitPrice;

    public void initialize() {
        // Cột bảng hóa đơn
        colInvoiceId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f", data.getValue().getTotal())));
        colPaymentMethod.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentMethod()));

        // Cột bảng sản phẩm
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct()));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colUnitPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject());

        // Sự kiện chọn hóa đơn
        invoiceTable.setOnMouseClicked(event -> {
            Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                List<InvoiceItem> items = fetchInvoiceItems(selected.getId());
                invoiceDetailTable.setItems(FXCollections.observableArrayList(items));
                invoiceDetailTable.setVisible(true);
            }
        });
    }

    public void loadInvoices() {
        ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM order_bought(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, User_MainController.customerPhone); // truyền số điện thoại

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String orderId = String.valueOf(rs.getInt("orderid"));
                String orderDate = rs.getDate("orderdate").toString();
                String method = rs.getString("method");
                double totalAmount = rs.getDouble("totalamount");

                Invoice invoice = new Invoice(orderId, orderDate, method, totalAmount);
                invoiceList.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        invoiceTable.setItems(invoiceList);
    }
    private List<InvoiceItem> fetchInvoiceItems(String orderId) {
        List<InvoiceItem> list = new ArrayList<>();
        String sql = """
        SELECT p.product_name, od.quantity, p.price_with_tax
        FROM order_details od
        JOIN batch b ON od.batch_id = b.batch_id
        JOIN products p ON b.product_id = p.product_id
        WHERE od.order_id = ?
        ORDER BY p.product_name
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(orderId));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new InvoiceItem(
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_with_tax")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}