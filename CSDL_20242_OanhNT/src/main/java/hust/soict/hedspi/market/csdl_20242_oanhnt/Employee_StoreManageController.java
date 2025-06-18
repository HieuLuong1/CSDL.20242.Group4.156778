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
    @FXML private TextField selectedProductNameField;
    @FXML private TextField updateCategoryField;
    @FXML private TextField updatePriceField;
    //@FXML private ComboBox<String> creatorFilter;

    /**
     * Thiết lập employeeId: gọi từ MainController sau khi đăng nhập
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @FXML
    public void initialize() {
        // Cột bảng
        colID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));


        // Load dữ liệu
        loadProductDataFromDB();

        // Lọc tìm kiếm
        FilteredList<Item> filtered = new FilteredList<>(items, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                loadProductDataFromDB();
            } else {
                searchProducts(newVal.trim());
            }
        });
        productTable.setItems(items);

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
                        "c.category_name " +
                        "FROM products p " +
                        "JOIN categories c ON p.category_id = c.category_id "+
                        "ORDER BY p.product_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("unit"),
                        rs.getDouble("price_with_tax"),
                        rs.getInt("quantity_in_stock"),
                        rs.getString("category_name")

                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void searchProducts(String keyword) {
        items.clear();
        String sql = """
        SELECT DISTINCT p.product_id, p.product_name, p.unit, p.price_with_tax, 
                        p.quantity_in_stock, c.category_name
        FROM products p
        JOIN batch b USING (product_id)
        JOIN import_reports r USING (import_id)
        JOIN suppliers s USING (supplier_id)
        JOIN categories c ON p.category_id = c.category_id
        WHERE LOWER(p.product_name) LIKE ? OR LOWER(s.supplier_name) LIKE ?
        ORDER BY p.product_id ASC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, search);
            ps.setString(2, search);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("unit"),
                        rs.getDouble("price_with_tax"),
                        rs.getInt("quantity_in_stock"),
                        rs.getString("category_name")
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
            String sql = "DELETE FROM products WHERE product_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedItem.getProductId());
                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    items.remove(selectedItem); // Xóa khỏi danh sách hiển thị
                    selectedProductNameField.clear();
                    selectedItem = null;
                } else {
                    System.err.println("Xoá thất bại: không tìm thấy product_id.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void handleUpdateProduct() {
        if (selectedItem != null) {
            String newCatName = updateCategoryField.getText().trim();
            String newPriceStr = updatePriceField.getText().trim();
            Double newPrice = null;

            if (!newPriceStr.isEmpty()) {
                try {
                    newPrice = Double.parseDouble(newPriceStr);
                } catch (NumberFormatException e) {
                    System.err.println("Giá không hợp lệ.");
                    return;
                }
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                Integer newCatId = null;

                // Nếu có nhập category mới, tra cứu hoặc thêm mới category
                if (!newCatName.isEmpty()) {
                    // Kiểm tra xem category đã tồn tại chưa
                    String checkCatSql = "SELECT category_id FROM categories WHERE category_name = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkCatSql)) {
                        checkStmt.setString(1, newCatName);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next()) {
                            newCatId = rs.getInt("category_id");
                        } else {
                            // Nếu chưa có, thêm mới
                            String insertCatSql = "INSERT INTO categories (category_name) VALUES (?) RETURNING category_id";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertCatSql)) {
                                insertStmt.setString(1, newCatName);
                                ResultSet inserted = insertStmt.executeQuery();
                                if (inserted.next()) {
                                    newCatId = inserted.getInt("category_id");
                                }
                            }
                        }
                    }
                }

                // Update bảng products
                StringBuilder updateSql = new StringBuilder("UPDATE products SET ");
                boolean needComma = false;

                if (newPrice != null) {
                    updateSql.append("price_with_tax = ?");
                    needComma = true;
                }

                if (newCatId != null) {
                    if (needComma) updateSql.append(", ");
                    updateSql.append("category_id = ?");
                }

                updateSql.append(" WHERE product_id = ?");

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql.toString())) {
                    int idx = 1;
                    if (newPrice != null) updateStmt.setDouble(idx++, newPrice);
                    if (newCatId != null) updateStmt.setInt(idx++, newCatId);
                    updateStmt.setInt(idx, selectedItem.getProductId());
                    int updated = updateStmt.executeUpdate();

                    if (updated > 0) {
                        if (newPrice != null) selectedItem.setPrice(newPrice);
                        if (newCatName != null && !newCatName.isEmpty()) selectedItem.setCategory(newCatName);
                        productTable.refresh();
                    }
                }

                conn.commit();
                updateCategoryField.clear();
                updatePriceField.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // Các phương thức handleBatch, resetItemIdCounter giữ nguyên nếu có


    public void handleBatch(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Batch.fxml"));
            Parent root = loader.load();
            //Employee_StoreAddController addController = loader.getController();
            //addController.setStoreManageController(this);
            Stage stage = new Stage();
            stage.setTitle("Hàng mới về");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}