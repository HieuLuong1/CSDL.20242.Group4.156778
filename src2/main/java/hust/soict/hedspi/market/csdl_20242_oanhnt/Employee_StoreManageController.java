package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    @FXML
    public void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_StoreAdd.fxml"));
            Parent root = loader.load();
            Employee_StoreAddController addController = loader.getController();
            addController.setStoreManageController(this);
            Stage stage = new Stage();
            stage.setTitle("Thêm sản phẩm mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void initialize() {
        Item.resetIdCounter();
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        loadProductDataFromDB();

        FilteredList<Item> filteredData = new FilteredList<>(items, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return item.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedData);

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                selectedProductNameField.setText(selectedItem.getName());
            }
        });
    }

    public void loadProductDataFromDB() {
        items.clear();
        try {
            Connection conn = DBConnection.getConnection();
            String sql =  "SELECT p.product_id, p.product_name, p.unit, p.price_with_tax, p.quantity_in_stock, " +
            	    "c.category_name, s.supplier_name " +
            	    "FROM products p " +
            	    "JOIN categories c ON p.category_id = c.category_id " +
            	    "JOIN batch b ON p.product_id = b.product_id " +
            	    "JOIN import_reports ir ON b.import_id = ir.import_id " +
            	    "JOIN suppliers s ON ir.supplier_id = s.supplier_id " +
            	    "WHERE p.quantity_in_stock > 0;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("product_name");
                String unit = rs.getString("unit");
                double price = rs.getDouble("price_with_tax");
                int quantity = rs.getInt("quantity_in_stock");
                String category = rs.getString("category_name");
                String supplier = rs.getString("supplier_name");
                items.add(new Item(name, unit, price, quantity, category, supplier));
            }
            stmt.close();
            productTable.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRemoveProduct() {
        if (selectedItem != null) {
            items.remove(selectedItem);
            productTable.getSelectionModel().clearSelection();
            selectedProductNameField.clear();
            selectedItem = null;
        } else {
            System.out.println("Chưa chọn sản phẩm để xóa!");
        }
    }

    @FXML
    private void handleUpdateProduct() {
        if (selectedItem != null) {
            String newCategory = updateCategoryField.getText().trim();
            String newPriceStr = updatePriceField.getText().trim();

            if (newCategory.isEmpty() && newPriceStr.isEmpty()) {
                System.out.println("Vui lòng nhập loại hoặc đơn giá mới để cập nhật.");
                return;
            }

            if (!newCategory.isEmpty()) {
                selectedItem.setCategory(newCategory);
            }

            if (!newPriceStr.isEmpty()) {
                try {
                    double newPrice = Double.parseDouble(newPriceStr);
                    selectedItem.setPrice(newPrice);
                } catch (NumberFormatException e) {
                    System.out.println("Đơn giá phải là số hợp lệ!");
                    return;
                }
            }

            productTable.refresh();
            updateCategoryField.clear();
            updatePriceField.clear();
            System.out.println("Cập nhật sản phẩm thành công!");
        } else {
            System.out.println("Chưa chọn sản phẩm để cập nhật!");
        }
    }

    public void handleBatch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Batch.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Hàng mới về");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetItemIdCounter() {
        Item.resetIdCounter();
    }
}