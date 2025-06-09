package hust.soict.hedspi.market.csdl_20242_oanhnt;

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

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        // Thiết lập cell value factories
        colImport.setCellValueFactory(cell -> cell.getValue().idProperty());
        colImportDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colImportAddress.setCellValueFactory(cell -> cell.getValue().addressProperty());
        colValue.setCellValueFactory(cell -> cell.getValue().totalValueProperty().asObject());

        // SỬA LẠI phẩn colBatches:
        colBatches.setCellValueFactory(cell -> {
            // Giả sử ImportOrder có phương thức getBatches() trả về ObservableList<String>
            ObservableList<String> batches = cell.getValue().getBatches();
            // Dùng String.join để nối chuỗi
            String batchStr = String.join(", ", batches);
            return new ReadOnlyStringWrapper(batchStr);
        });

        colSupplier.setCellValueFactory(cell -> cell.getValue().supplierProperty());

        loadImportDataFromDB();
    }

    private void loadImportDataFromDB() {
        data = FXCollections.observableArrayList();
        String importSql =
                "SELECT ir.import_id, ir.import_date, ir.delivery_address, ir.total_amount, s.supplier_name " +
                        "FROM import_reports ir " +
                        "  LEFT JOIN suppliers s ON ir.supplier_id = s.supplier_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psImport = conn.prepareStatement(importSql);
             ResultSet rsImport = psImport.executeQuery()) {

            while (rsImport.next()) {
                String importId = String.format("IMP%03d", rsImport.getInt("import_id"));
                LocalDate importDate = rsImport.getDate("import_date").toLocalDate();
                String address = rsImport.getString("delivery_address");
                double totalValue = rsImport.getDouble("total_amount");
                String supplier = rsImport.getString("supplier_name");

                int idInt = rsImport.getInt("import_id");
                List<String> batchNames = fetchBatchesForImport(idInt, conn);
                ObservableList<String> batchesList = FXCollections.observableArrayList(batchNames);

                ImportOrder order = new ImportOrder(
                        importId,
                        importDate,
                        address,
                        totalValue,
                        batchesList,
                        supplier
                );
                data.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblImport.setItems(data);
    }

    private List<String> fetchBatchesForImport(int importId, Connection conn) throws SQLException {
        String batchSql = "SELECT batch_id FROM batch WHERE import_id = ?";
        try (PreparedStatement psBatch = conn.prepareStatement(batchSql)) {
            psBatch.setInt(1, importId);
            try (ResultSet rsBatch = psBatch.executeQuery()) {
                List<String> list = new ArrayList<>();
                while (rsBatch.next()) {
                    list.add("Lô" + rsBatch.getInt("batch_id"));
                }
                return list;
            }
        }
    }

    public void handleAddImport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_AddImport.fxml"));
            Parent root = loader.load();
            Admin_AddImportController controller = loader.getController();
            controller.setImportOrderList(data);
            Stage stage = new Stage();
            stage.setTitle("Thêm Đơn nhập mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}