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

public class Employee_StoreManageController {
    private ObservableList<Item> items = FXCollections.observableArrayList();
    private Item selectedItem;
    @FXML
    private TableView<Item> productTable;
    @FXML
    private TextField searchField;
    @FXML private TableColumn<Item, Integer> colID;
    @FXML private TableColumn<Item, String> colProductName;
    @FXML private TableColumn<Item, String> colUnit;
    @FXML private TableColumn<Item, Double> colPrice;
    @FXML private TableColumn<Item, Integer> colQuantity;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private TableColumn<Item, String> colSupplier;
    @FXML
    private TextField selectedProductNameField;
    @FXML
    private TextField updateCategoryField;

    @FXML
    private TextField updatePriceField;
    @FXML
    public void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_StoreAdd.fxml"));
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

        items.addAll(
                new Item("Sữa TH True Milk", "Hộp", 20000, 100, "Đồ uống",  "TH Group"),
                new Item("Bánh Oreo", "Gói", 15000, 80, "Bánh kẹo", "Mondelez"),
                new Item("Dầu ăn Simply", "Chai", 45000, 50, "Gia vị", "Cái Lân"),
                new Item("Nước mắm Nam Ngư", "Chai", 28000, 70, "Gia vị", "Masan"),
                new Item("Coca-Cola lon", "Lon", 10000, 100, "Nước ngọt", "Coca-Cola Việt Nam")
        );

        FilteredList<Item> filteredData = new FilteredList<>(items, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
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

            // Xóa dữ liệu sau khi cập nhật
            updateCategoryField.clear();
            updatePriceField.clear();

            System.out.println("Cập nhật sản phẩm thành công!");
        } else {
            System.out.println("Chưa chọn sản phẩm để cập nhật!");
        }
    }
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
    public void resetItemIdCounter() {
        Item.resetIdCounter();
    }
}
