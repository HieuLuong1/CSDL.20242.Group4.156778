package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EmployeeNewOrderController {
    @FXML private TextField tfOrderID, tfNameProduct, tfQuantity, tfPhone, tfEmp, tfNameCustomer;
    @FXML private TableView<Batch> tbBatch;
    @FXML private TableColumn<Batch, Integer> colBatchID;
    @FXML private TableColumn<Batch, Integer> colTotalQuan;
    @FXML private TableColumn<Batch, Integer> colQuanInStock;

    @FXML private TableView<InvoiceItem> tableOrder; // Bảng dưới cùng
    @FXML private TableColumn<InvoiceItem, Integer> colBatchOrder;
    @FXML private TableColumn<InvoiceItem, String> colProductOrder;
    @FXML private TableColumn<InvoiceItem, Integer> colQuanOrder;
    @FXML private TableColumn<InvoiceItem, Double> colPrice;
    @FXML private TableColumn<InvoiceItem, Double> colSum;
    @FXML
    private DatePicker dpOrderDate;
    @FXML private Label lbTotalMoney;
    private ObservableList<Batch> allBatches = FXCollections.observableArrayList();
    private ObservableList<Item> allItems = FXCollections.observableArrayList();
    private ObservableList<InvoiceItem> orderItems = FXCollections.observableArrayList();

    private double totalMoney = 0.0;
    private void updateTotalMoneyLabel() {
        lbTotalMoney.setText(String.format("%.0f VND", totalMoney));
    }

    private EmployeeOrderController parentController;

    public void setParentController(EmployeeOrderController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private Button btnConfirm;
    @FXML
    private RadioButton radioBtnCash, radioBtnCard, radioBtnWallet;

    public void initialize(){
        totalMoney = 0.0;
        updateTotalMoneyLabel();
        colBatchID.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colTotalQuan.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colQuanInStock.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(() ->
                        cellData.getValue().getTotalQuantity() - cellData.getValue().getSoldQuantity()).asObject());

        tbBatch.setItems(FXCollections.observableArrayList());
        // Cột bảng đơn hàng
        colBatchOrder.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colProductOrder.setCellValueFactory(new PropertyValueFactory<>("product"));
        colQuanOrder.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colSum.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableOrder.setItems(orderItems);

        generateSampleData();
        tfNameProduct.textProperty().addListener((obs, oldVal, newVal) -> searchBatchByProductName());
    }
    private void generateSampleData() {
        allItems.addAll(
                new Item("Sữa tươi", "hộp", 12000, 100, "Đồ uống", "Vinamilk"),
                new Item("Sữa tươi", "hộp", 13000, 50, "Đồ uống", "TH True Milk"),
                new Item("Nước khoáng", "chai", 8000, 200, "Đồ uống", "LaVie")
        );

        allBatches.addAll(
                new Batch(1, LocalDate.now().minusDays(5), LocalDate.now().plusDays(30), 100, 10, "Sữa tươi", "Vinamilk", 100000),
                new Batch(2, LocalDate.now().minusDays(10), LocalDate.now().plusDays(60), 50, 5, "Sữa tươi", "TH True Milk", 200000),
                new Batch(3, LocalDate.now().minusDays(3), LocalDate.now().plusDays(40), 200, 100, "Nước khoáng", "LaVie", 30000)
        );
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

        // Tìm item tương ứng trong allItems theo tên và nhà cung cấp để lấy giá
        Item matchedItem = null;
        for (Item item : allItems) {
            if (item.getName().equals(selected.getProductName()) &&
                    item.getSupplier().equals(selected.getSupplier())) {
                matchedItem = item;
                break;
            }
        }
        if (matchedItem == null) {
            showAlert("Không tìm thấy sản phẩm tương ứng trong danh sách Item.");
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
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
    @FXML
    public void handleCancel() {
        InvoiceItem selectedItem = tableOrder.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Tìm lô hàng tương ứng để cập nhật lại soldQuantity
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Không có dòng nào được chọn");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn dòng muốn hủy trong bảng hóa đơn.");
            alert.showAndWait();
        }
    }
    @FXML
    private ObservableList<InvoiceItem> handleConfirm() {
        String id = tfOrderID.getText().trim();
        String nameCustomer = tfNameCustomer.getText().trim();
        String phoneCustomer = tfPhone.getText().trim();
        String nameEmployee = tfEmp.getText().trim();
        String date = dpOrderDate.getValue() != null ? dpOrderDate.getValue().toString() : "";

        String paymentMethod = "";
        if (radioBtnCash.isSelected()) paymentMethod = "Tiền mặt";
        else if (radioBtnCard.isSelected()) paymentMethod = "Chuyển khoản";
        else if (radioBtnWallet.isSelected()) paymentMethod = "Ví điện tử";

        // Thu thập danh sách mặt hàng và tính tổng
        List<InvoiceItem> items = getInvoiceItems();
        double total = items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();

        if (id.isEmpty() || nameCustomer.isEmpty() || phoneCustomer.isEmpty() || date.isEmpty() || paymentMethod.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập đầy đủ thông tin.");
            alert.showAndWait();
            return null;
        }

        Invoice newInvoice = new Invoice(id, date, paymentMethod, phoneCustomer, nameCustomer, nameEmployee, items);

        parentController.addNewInvoice(newInvoice);

        // đóng cửa sổ tạo hóa đơn mới
        Stage stage = (Stage) btnConfirm.getScene().getWindow();
        stage.close();

        return orderItems;
    }
    private List<InvoiceItem> getInvoiceItems() {
        return orderItems;
    }
}
