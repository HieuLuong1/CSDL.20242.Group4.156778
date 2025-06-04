import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class User_PurchaseHistoryController {

    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, String> colInvoiceId;
    @FXML
    private TableColumn<Invoice, String> colDate;
    @FXML
    private TableColumn<Invoice, String> colTotal;
    @FXML
    private TableColumn<Invoice, String> colPaymentMethod;

    @FXML
    private TableView<InvoiceItem> invoiceDetailTable;
    @FXML
    private TableColumn<InvoiceItem, String> colProductName;
    @FXML
    private TableColumn<InvoiceItem, Integer> colQuantity;
    @FXML
    private TableColumn<InvoiceItem, Double> colUnitPrice;

    public void initialize() {
        // Thiết lập cột bảng hóa đơn
        colInvoiceId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f", data.getValue().getTotal())));
        colPaymentMethod.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentMethod()));

        // Thiết lập cột bảng chi tiết hóa đơn
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct()));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colUnitPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject());

        // Sự kiện chọn hóa đơn để hiển thị chi tiết
        invoiceTable.setOnMouseClicked(event -> {
            Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                invoiceDetailTable.setItems(FXCollections.observableArrayList(selected.getItems()));
                invoiceDetailTable.setVisible(true);
            }
        });
    }

    public void loadInvoices() {
        List<Invoice> list = new ArrayList<>();

        List<InvoiceItem> items1 = new ArrayList<>();
        items1.add(new InvoiceItem("Mì tôm", 5, 3500));
        items1.add(new InvoiceItem("Nước ngọt", 2, 10000));
        double subtotal1 = items1.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        double vatAmount1 = subtotal1 * 0.1; // 10%
        items1.add(new InvoiceItem("VAT 10%", 1, vatAmount1));
        list.add(new Invoice("HD001", "2024-05-20", "Chuyển khoản ngân hàng", items1));

        List<InvoiceItem> items2 = new ArrayList<>();
        items2.add(new InvoiceItem("Bánh mì", 3, 4000));
        items2.add(new InvoiceItem("Sữa tươi", 2, 12000));
        double subtotal2 = items2.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        double vatAmount2 = subtotal2 * 0.1; // 10%
        items2.add(new InvoiceItem("VAT 10%", 1, vatAmount2));
        list.add(new Invoice("HD002", "2024-05-21", "Thẻ tín dụng", items2));

        ObservableList<Invoice> obsList = FXCollections.observableArrayList(list);
        invoiceTable.setItems(obsList);
    }
}
