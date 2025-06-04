import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeOrderController implements Initializable {
    @FXML
    private Button btnNewOrder;

    @FXML
    private Button btnPrint;

    @FXML
    private TableView<Invoice> tableView;

    @FXML
    private TableColumn<Invoice, String> colEmp;

    @FXML
    private TableColumn<Invoice, String> colID;

    @FXML
    private TableColumn<Invoice, String> colMethod;

    @FXML
    private TableColumn<Invoice, String> colName;

    @FXML
    private TableColumn<Invoice, String> colOrderDate;

    @FXML
    private TableColumn<Invoice, String> colPhone;

    @FXML
    private TableColumn<Invoice, Double> colTotal;

    @FXML
    private Label lbSumCustomer;

    @FXML
    private TextField tfSearchField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneCustomer"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameCustomer"));
        colEmp.setCellValueFactory(new PropertyValueFactory<>("nameEmployee"));

        allInvoices = getSampleOrders(); // Lưu dữ liệu gốc
        tableView.getItems().setAll(allInvoices);
        lbSumCustomer.setText(String.valueOf(allInvoices.size()));
        tfSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchInvoiceByPhone(newValue);
        });

    }
    private List<Invoice> allInvoices; // Lưu toàn bộ dữ liệu gốc để lọc lại

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

    private List<Invoice> getSampleOrders() {
        List<Invoice> invoices = new ArrayList<>();

        List<InvoiceItem> items1 = Arrays.asList(
                new InvoiceItem("Sữa tươi", 2, 25000),
                new InvoiceItem("Bánh mì", 3, 10000)
        );
        invoices.add(new Invoice("HD001", "2025-06-01", "Tiền mặt", "0912345678", "Nguyễn Văn A", "Trần Thị B", items1));

        List<InvoiceItem> items2 = Arrays.asList(
                new InvoiceItem("Trà xanh", 1, 15000),
                new InvoiceItem("Snack", 2, 20000)
        );
        invoices.add(new Invoice("HD002", "2025-06-02", "Chuyển khoản", "0987654321", "Lê Thị C", "Ngô Văn D", items2));

        List<InvoiceItem> items3 = Arrays.asList(
                new InvoiceItem("Cà phê", 2, 30000),
                new InvoiceItem("Bánh quy", 1, 15000)
        );
        invoices.add(new Invoice("HD003", "2025-06-02", "Momo", "0909123456", "Phạm Văn E", "Nguyễn Thị F", items3));
        btnPrint.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnPrint.setDisable(newSelection == null);
        });
        return invoices;
    }


    public void handleNewOrder() {
        try {
            // TODO: Mở cửa sổ tạo hóa đơn mới
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

    public void handlePrint(){
        try{
            //TODO
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Hóa đơn đã được in thành công.");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void addNewInvoice(Invoice newInvoice) {
        allInvoices.add(newInvoice);
        tableView.getItems().add(newInvoice);
        lbSumCustomer.setText(String.valueOf(allInvoices.size()));
    }
}
