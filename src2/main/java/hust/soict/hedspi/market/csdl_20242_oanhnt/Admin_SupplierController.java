package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.io.IOException;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Controller màn hình Quản lý Supplier.
 * - Load dữ liệu từ bảng `suppliers`.
 * - Thêm mới (INSERT), Xóa (DELETE) trực tiếp lên DB.
 * - Tìm kiếm tạm thời (chỉ thay đổi TableView, không ảnh hưởng DB).
 *
 * Lưu ý: Lớp Supplier chỉ có getter, không có setter. Khi cần ADD, sẽ tạo đối tượng mới với ID do DB sinh ra.
 */
public class Admin_SupplierController {

    @FXML private TableView<Supplier> tableView;
    @FXML private TableColumn<Supplier, String> colSupID;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colAddress;
    @FXML private TableColumn<Supplier, String> colPerson;
    @FXML private TableColumn<Supplier, String> colPhone;
    @FXML private TextField tfNameSupplier; // ô tìm kiếm theo tên

    // Dùng ObservableList làm backing cho TableView
    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Thiết lập cách lấy giá trị cho từng cột
        colSupID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPerson.setCellValueFactory(new PropertyValueFactory<>("representative"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // 2. Gán dữ liệu (sẽ load từ DB)
        tableView.setItems(supplierList);

        // 3. Load dữ liệu thực từ DB
        loadSuppliersFromDB();
    }

    /**
     * Lấy toàn bộ nhà cung cấp từ DB và đưa vào supplierList.
     */
    private void loadSuppliersFromDB() {
        supplierList.clear();
        String sql = "SELECT supplier_id, supplier_name, address, representative_name, contact_phone "
                   + "FROM suppliers ORDER BY supplier_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("supplier_id"));
                String name = rs.getString("supplier_name");
                String address = rs.getString("address");
                String representative = rs.getString("representative_name");
                String phone = rs.getString("contact_phone");

                Supplier sup = new Supplier(id, name, address, representative, phone);
                supplierList.add(sup);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi nhấn nút “Thêm NCC”:
     *  - Mở form Admin_AddSup để người dùng nhập thông tin mới.
     *  - Khi click Save ở form, AddSupController sẽ gọi lại mainController.addSupplier(...)
     */
    @FXML
    private void handleAddSup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_AddSup.fxml"));
            Parent root = loader.load();
            Admin_AddSupController addController = loader.getController();
            addController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm Nhà Cung Cấp");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Được gọi từ Admin_AddSupController khi user nhấn Save.
     * @param name    tên nhà cung cấp
     * @param address địa chỉ
     * @param person  tên đại diện
     * @param phone   số điện thoại
     */
    public void addSupplierToDB(String name, String address, String person, String phone) {
        // 1. Chạy INSERT và lấy về supplier_id do DB tạo
        String insertSql = "INSERT INTO suppliers(supplier_name, address, representative_name, contact_phone) "
                         + "VALUES (?, ?, ?, ?) RETURNING supplier_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, person);
            ps.setString(4, phone);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt("supplier_id");
                // 2. Tạo đối tượng Supplier mới với ID lấy từ DB
                Supplier newSupplier = new Supplier(
                    String.valueOf(newId),
                    name,
                    address,
                    person,
                    phone
                );
                supplierList.add(newSupplier);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi nhấn nút “Xóa NCC”:
     *  - Xóa khỏi DB dựa vào selected.getId()
     *  - Nếu xóa thành công, remove khỏi supplierList.
     */
    @FXML
    private void handleRemoveSup() {
        Supplier selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String deleteSql = "DELETE FROM suppliers WHERE supplier_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {

                ps.setInt(1, Integer.parseInt(selected.getId()));
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    supplierList.remove(selected);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tìm kiếm tạm thời (chỉ filter trên supplierList, không ảnh hưởng DB).
     */
    @FXML
    private void handleSearchSupplier(KeyEvent event) {
        String keyword = tfNameSupplier.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tableView.setItems(supplierList);
        } else {
            ObservableList<Supplier> filteredList = FXCollections.observableArrayList();
            for (Supplier sup : supplierList) {
                if (sup.getName().toLowerCase().contains(keyword)) {
                    filteredList.add(sup);
                }
            }
            tableView.setItems(filteredList);
        }
    }
}
