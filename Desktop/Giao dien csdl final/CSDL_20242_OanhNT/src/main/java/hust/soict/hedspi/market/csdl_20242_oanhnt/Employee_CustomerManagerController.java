package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee_CustomerManagerController {
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerName;
    @FXML private TableColumn<Customer, String> colCustomerPhone;
    @FXML private TableColumn<Customer, String> colCustomerEmail;

    @FXML private TextField customerIdField;    // Hiện không dùng để insert, nhưng giữ để có thể show khi cần
    @FXML private TextField customerNameField;
    @FXML private TextField customerPhoneField;
    @FXML private TextField customerEmailField;
    @FXML private Button updateCustomerBtn;      // Nút “Thêm khách hàng”

    // Danh sách ObservableList để bind vào TableView
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Cấu hình các cột cho TableView
        colCustomerId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colCustomerName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        colCustomerPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colCustomerEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        // 2. Load dữ liệu khách hàng từ database
        loadCustomerDataFromDB();

        // 3. Khi chọn một dòng, hiển thị thông tin qua Alert
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin khách hàng");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Mã: " + newVal.getId() + "\n" +
                                "Tên: " + newVal.getFullName() + "\n" +
                                "SĐT: " + newVal.getPhone() + "\n" +
                                "Email: " + newVal.getEmail()
                );
                alert.showAndWait();
            }
        });

        // 4. Thiết lập filter khi nhập vào searchField
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomerList(newValue);
        });

        // 5. Bắt event cho nút “Thêm khách hàng”
        updateCustomerBtn.setOnAction(event -> handleAddCustomer());
    }

    // Hàm load toàn bộ khách hàng từ bảng 'customer' trong DB
    private void loadCustomerDataFromDB() {
        customerList.clear();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT customer_id, fullname, phone, email FROM customer ORDER BY customer_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int idInt = rs.getInt("customer_id");
                String strId = String.valueOf(idInt);
                String fullname = rs.getString("fullname");
                String phone    = rs.getString("phone");
                String email    = rs.getString("email");
                Customer c = new Customer(strId, fullname, email, phone);
                customerList.add(c);
            }
            customerTable.setItems(customerList);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi kết nối CSDL");
            alert.setHeaderText(null);
            alert.setContentText("Không thể load danh sách khách hàng:\n" + e.getMessage());
            alert.showAndWait();
        } finally {
            // KHÔNG đóng conn hoặc stmt để JVM tự giải phóng
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
    }

    // Hàm thêm khách hàng mới vào bảng 'customer'
    private void handleAddCustomer() {
        String fullname = customerNameField.getText().trim();
        String phone    = customerPhoneField.getText().trim();
        String email    = customerEmailField.getText().trim();

        if (fullname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập đầy đủ họ tên, số điện thoại và email.");
            alert.showAndWait();
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO customer (fullname, phone, email) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullname);
            stmt.setString(2, phone);
            stmt.setString(3, email);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Thêm thành công → load lại dữ liệu từ DB
                loadCustomerDataFromDB();
                // Clear form
                customerIdField.clear();
                customerNameField.clear();
                customerPhoneField.clear();
                customerEmailField.clear();

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Thành công");
                info.setHeaderText(null);
                info.setContentText("Đã thêm khách hàng thành công.");
                info.showAndWait();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Lỗi");
                err.setHeaderText(null);
                err.setContentText("Thêm khách hàng thất bại, vui lòng thử lại.");
                err.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Lỗi CSDL");
            err.setHeaderText(null);
            err.setContentText("Không thể lưu khách hàng:\n" + e.getMessage());
            err.showAndWait();
        } finally {
            // KHÔNG đóng conn hoặc stmt để giữ cho JVM tự giải phóng
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
    }

    // Hàm lọc danh sách theo từ khoá (filter)
    private void filterCustomerList(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            customerTable.setItems(customerList);
            return;
        }

        ObservableList<Customer> filteredList = FXCollections.observableArrayList();
        for (Customer customer : customerList) {
            if (customer.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                    customer.getPhone().contains(keyword)) {
                filteredList.add(customer);
            }
        }
        customerTable.setItems(filteredList);
    }
}