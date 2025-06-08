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
                invoiceDetailTable.setItems(FXCollections.observableArrayList(selected.getItems()));
                invoiceDetailTable.setVisible(true);
            }
        });
    }

    public void loadInvoices() {
        Map<String, List<InvoiceItem>> invoiceMap = new LinkedHashMap<>();
        Map<String, String> dateMap = new HashMap<>();
        Map<String, String> methodMap = new HashMap<>();

        String sql = """
				    SELECT o.order_id, o.order_date, o.payment_method,
				           p.product_name, od.quantity, p.price_with_tax
				    FROM customer c
				    JOIN orders o ON c.customer_id = o.customer_id
				    JOIN order_details od ON o.order_id = od.order_id
				    JOIN batch b ON od.batch_id = b.batch_id
				    JOIN products p ON b.product_id = p.product_id
				    WHERE c.customer_id = ?
				    ORDER BY o.order_date DESC, o.order_id, p.product_name
				""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, User_MainController.customerID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String orderId = String.valueOf(rs.getInt("order_id"));
                String orderDate = rs.getDate("order_date").toString();
                String method = rs.getString("payment_method");

                InvoiceItem item = new InvoiceItem(
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_with_tax")
                );

                invoiceMap.putIfAbsent(orderId, new ArrayList<>());
                invoiceMap.get(orderId).add(item);
                dateMap.put(orderId, orderDate);
                methodMap.put(orderId, method);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Invoice> list = new ArrayList<>();
        for (String id : invoiceMap.keySet()) {
            list.add(new Invoice(
                    id,
                    dateMap.get(id),
                    methodMap.get(id),
                    invoiceMap.get(id)
            ));
        }
        list.sort(Comparator.comparing(Invoice::getDate).reversed());
        invoiceTable.setItems(FXCollections.observableArrayList(list));
    }
}