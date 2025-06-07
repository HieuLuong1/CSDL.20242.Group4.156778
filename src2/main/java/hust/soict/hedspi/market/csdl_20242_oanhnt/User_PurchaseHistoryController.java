package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.ResourceBundle;

public class User_PurchaseHistoryController implements Initializable {
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> colInvoiceId;
    @FXML private TableColumn<Invoice, String> colDate;
    @FXML private TableColumn<Invoice, String> colTotal;
    @FXML private TableColumn<Invoice, String> colPaymentMethod;

    @FXML private TableView<InvoiceItem> invoiceDetailTable;
    @FXML private TableColumn<InvoiceItem, String> colProductName;
    @FXML private TableColumn<InvoiceItem, Integer> colQuantity;
    @FXML private TableColumn<InvoiceItem, Double> colUnitPrice;

    private int customerId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colInvoiceId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f", data.getValue().getTotal())));
        colPaymentMethod.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentMethod()));

        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct()));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colUnitPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject());

        invoiceTable.setOnMouseClicked(event -> {
            Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                invoiceDetailTable.setItems(FXCollections.observableArrayList(sel.getItems()));
                invoiceDetailTable.setVisible(true);
            }
        });
    }

    public void setCustomerId(int id) {
        this.customerId = id;
        loadInvoices();
    }

    public void loadInvoices() {
        Map<String, List<InvoiceItem>> invoiceMap = new LinkedHashMap<>();
        Map<String, String> dateMap = new HashMap<>();
        Map<String, String> methodMap = new HashMap<>();

        String sql = "SELECT o.order_id, o.order_date, o.payment_method, " +
                     "p.product_name, od.quantity, p.price_with_tax " +
                     "FROM orders o " +
                     "JOIN order_details od ON o.order_id = od.order_id " +
                     "JOIN batch b ON od.batch_id = b.batch_id " +
                     "JOIN products p ON b.product_id = p.product_id " +
                     "WHERE o.customer_id = ? " +
                     "ORDER BY o.order_date DESC, o.order_id, p.product_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String orderId = String.valueOf(rs.getInt("order_id"));
                    String orderDate = rs.getDate("order_date").toString();
                    String method = rs.getString("payment_method");

                    InvoiceItem item = new InvoiceItem(
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price_with_tax")
                    );

                    invoiceMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(item);
                    dateMap.put(orderId, orderDate);
                    methodMap.put(orderId, method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Invoice> list = new ArrayList<>();
        for (String id : invoiceMap.keySet()) {
            List<InvoiceItem> items = invoiceMap.get(id);
            double total = items.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
            list.add(new Invoice(id, dateMap.get(id), total, methodMap.get(id), items));
        }

        invoiceTable.setItems(FXCollections.observableArrayList(list));
    }

    public static class Invoice {
        private final String id, date, paymentMethod;
        private final double total;
        private final List<InvoiceItem> items;

        public Invoice(String id, String date, double total, String paymentMethod, List<InvoiceItem> items) {
            this.id = id;
            this.date = date;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.items = items;
        }

        public String getId() { return id; }
        public String getDate() { return date; }
        public double getTotal() { return total; }
        public String getPaymentMethod() { return paymentMethod; }
        public List<InvoiceItem> getItems() { return items; }
    }

    public static class InvoiceItem {
        private final String product;
        private final int quantity;
        private final double unitPrice;

        public InvoiceItem(String product, int quantity, double unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
    }
}