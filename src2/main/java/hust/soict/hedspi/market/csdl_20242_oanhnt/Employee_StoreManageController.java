package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Employee_StoreManageController {
    private ObservableList<Item> items = FXCollections.observableArrayList();
    private Item selectedItem;
    private int employeeId; // ID nhân viên hiện tại

    @FXML private TableView<Item> productTable;
    @FXML private TextField searchField;
    @FXML private TableColumn<Item, Integer> colID;
    @FXML private TableColumn<Item, String> colProductName;
    @FXML private TableColumn<Item, String> colUnit;
    @FXML private TableColumn<Item, Double> colPrice;
    @FXML private TableColumn<Item, Integer> colQuantity;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private TableColumn<Item, String> colSupplier;
    @FXML private TextField selectedProductNameField;
    @FXML private TextField updateCategoryField;
    @FXML private TextField updatePriceField;
    @FXML private ComboBox<String> creatorFilter;

    /**
     * Thiết lập employeeId: gọi từ MainController sau khi đăng nhập
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    public void initialize() {
        // Cột bảng
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // Load dữ liệu
        loadProductDataFromDB();

        // Lọc tìm kiếm
        FilteredList<Item> filtered = new FilteredList<>(items, p -> true);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            filtered.setPredicate(item -> newV == null || newV.isEmpty() ||
                item.getName().toLowerCase().contains(newV.toLowerCase()));
        });
        SortedList<Item> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sorted);

        // Chọn dòng
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedItem = n;
            if (n != null) selectedProductNameField.setText(n.getName());
        });
    }

    public void loadProductDataFromDB() {
        items.clear();
        String sql =
            "SELECT DISTINCT p.product_id, p.product_name, p.unit, p.price_with_tax, p.quantity_in_stock, " +
            "c.category_name, s.supplier_name " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "JOIN batch b ON p.product_id = b.product_id " +
            "JOIN import_reports ir ON b.import_id = ir.import_id " +
            "JOIN suppliers s ON ir.supplier_id = s.supplier_id " +
            "WHERE p.quantity_in_stock > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(
                    rs.getString("product_name"),
                    rs.getString("unit"),
                    rs.getDouble("price_with_tax"),
                    rs.getInt("quantity_in_stock"),
                    rs.getString("category_name"),
                    rs.getString("supplier_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_StoreAdd.fxml"));
            Parent root = loader.load();
            Employee_StoreAddController ctrl = loader.getController();
            ctrl.setStoreManageController(this);
            ctrl.setEmployeeId(this.employeeId);

            Stage stage = new Stage();
            stage.setTitle("Thêm sản phẩm mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRemoveProduct() {
        if (selectedItem != null) {
            items.remove(selectedItem);
            selectedProductNameField.clear();
        }
    }

    @FXML
    public void handleUpdateProduct() {
        if (selectedItem != null) {
            String newCat = updateCategoryField.getText().trim();
            String newPrice = updatePriceField.getText().trim();
            if (!newCat.isEmpty()) selectedItem.setCategory(newCat);
            if (!newPrice.isEmpty()) {
                try { selectedItem.setPrice(Double.parseDouble(newPrice)); }
                catch (NumberFormatException ignored) {}
            }
            productTable.refresh();
            updateCategoryField.clear(); updatePriceField.clear();
        }
    }

    // Các phương thức handleBatch, resetItemIdCounter giữ nguyên nếu có


    public void handleBatch() {
        // Giữ nguyên logic mở form batch
    }

    public void resetItemIdCounter() {
        Item.resetIdCounter();
    }
}