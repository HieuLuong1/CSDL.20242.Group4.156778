package hust.soict.hedspi.market.csdl_20242_oanhnt;
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

public class Admin_SupplierController {

    @FXML
    private TableView<Supplier> tableView;
    @FXML
    private TableColumn<Supplier, String> colSupID;
    @FXML
    private TableColumn<Supplier, String> colName;
    @FXML
    private TableColumn<Supplier, String> colAddress;
    @FXML
    private TableColumn<Supplier, String> colPerson;
    @FXML
    private TableColumn<Supplier, String> colPhone;

    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSupID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPerson.setCellValueFactory(new PropertyValueFactory<>("representative"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        supplierList.addAll(
                new Supplier("NCC01", "Công ty ABC", "Hà Nội", "Nguyễn Văn A", "0901234567"),
                new Supplier("NCC02", "Công ty XYZ", "TP.HCM", "Trần Thị B", "0987654321"),
                new Supplier("NCC03", "Công ty Minh Long", "Đà Nẵng", "Phạm Văn C", "0911122233")
        );

        tableView.setItems(supplierList);
    }
    public void addSupplier(Supplier supplier) {
        supplierList.add(supplier);
        tableView.refresh();
    }

    @FXML
    private void handleAddSup() {
        // TODO: thêm chức năng thêm nhà cung cấp mới
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_AddSup.fxml"));
            Parent root = loader.load();
            Admin_AddSupController addController = loader.getController();
            addController.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Thêm NCC");
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveSup() {
        Supplier selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            supplierList.remove(selected);
        }
    }
    @FXML
    private TextField tfNameSupplier;
    @FXML
    private void handleSearchSupplier(KeyEvent event) {
        String keyword = tfNameSupplier.getText().toLowerCase();

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
