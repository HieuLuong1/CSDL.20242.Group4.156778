import java.time.LocalDate;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Admin_ImportController {
    @FXML private TableView<ImportOrder> tblImport;
    @FXML private TableColumn<ImportOrder, String> colImport;
    @FXML private TableColumn<ImportOrder, LocalDate> colImportDate;
    @FXML private TableColumn<ImportOrder, String> colImportAddress;
    @FXML private TableColumn<ImportOrder, Double> colValue;
    @FXML private TableColumn<ImportOrder, String> colBatches;
    @FXML private TableColumn<ImportOrder, String> colSupplier;
    private ObservableList<ImportOrder> data;

    @FXML
    public void initialize() {
        // Mapping các cột
        colImport.setCellValueFactory(cell -> cell.getValue().idProperty());
        colImportDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colImportAddress.setCellValueFactory(cell -> cell.getValue().addressProperty());
        colValue.setCellValueFactory(cell -> cell.getValue().totalValueProperty().asObject());
        colBatches.setCellValueFactory(cell -> {
            // Hiển thị danh sách các lô dạng: "Lô1, Lô2, ..."
            String batchStr = cell.getValue().batchesProperty().stream().collect(Collectors.joining(", "));
            return new ReadOnlyStringWrapper(batchStr);
        });
        colSupplier.setCellValueFactory(cell -> cell.getValue().supplierProperty());

        // Dữ liệu mẫu
        data = FXCollections.observableArrayList(
                new ImportOrder("IMP001", LocalDate.of(2025, 6, 1), "Kho Hà Nội",
                        5000000, FXCollections.observableArrayList("Batch A1", "Batch A2"), "Công ty ABC"),
                new ImportOrder("IMP002", LocalDate.of(2025, 6, 3), "Kho TP.HCM",
                        3200000, FXCollections.observableArrayList("Batch B1"), "Công ty XYZ"),
                new ImportOrder("IMP003", LocalDate.of(2025, 6, 4), "Kho Đà Nẵng",
                        7400000, FXCollections.observableArrayList("Batch C1", "Batch C2", "Batch C3"), "Công ty DEF")
        );
        tblImport.setItems(data);
    }
    public void handleAddImport(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_AddImport.fxml"));
            Parent root = loader.load();
            Admin_AddImportController controller = loader.getController();
            controller.setImportOrderList(data);
            Stage stage = new Stage();
            stage.setTitle("Thêm Đơn nhập mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
