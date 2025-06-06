package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class Admin_ReportManageController {
    @FXML private TableView<Report> reportTable;
    @FXML private TableColumn<Report, String> colReportId;
    @FXML private TableColumn<Report, String> colDate;
    @FXML private TableColumn<Report, String> colMethod;

    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, String> colBatchId;
    @FXML private TableColumn<Batch, String> colProduct;
    @FXML private TableColumn<Batch, String> colImport;
    @FXML private TableColumn<Batch, String> colExpiry;
    @FXML private TableColumn<Batch, String> colQuantity;

    @FXML private Button btnCreateReport;

    private final ObservableList<Report> reportList = FXCollections.observableArrayList();
    private final ObservableList<Batch> availableBatches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Thiết lập cell value factory cho reportTable
        colReportId.setCellValueFactory(data -> data.getValue().reportIdProperty());
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        colMethod.setCellValueFactory(data -> data.getValue().methodProperty());
        reportTable.setItems(reportList);

        // Thiết lập cell value factory cho batchTable
        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImport.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiry.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        // Ở đây hiển thị “tồn thực tế”: totalQuantity - quantityInStock
        colQuantity.setCellValueFactory(data -> {
            int totalQty = data.getValue().getTotalQuantity();
            int qtyInStock = data.getValue().getQuantityInStock();
            return new SimpleStringProperty(String.valueOf(totalQty - qtyInStock));
        });

        // Load toàn bộ Report từ database
        loadReportsFromDB();

        // Khi chọn một Report, hiển thị Batch liên quan (ở đây mỗi Report chỉ có 1 Batch)
        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getBatch() != null) {
                // Đưa duy nhất 1 Batch đó vào batchTable
                batchTable.setItems(FXCollections.observableArrayList(newSel.getBatch()));
            } else {
                batchTable.setItems(FXCollections.observableArrayList());
            }
        });
    }

    /**
     * Phương thức này lấy dữ liệu từ bảng incident_reports, sau đó với mỗi bản ghi
     * sẽ query thêm vào bảng batch + products + import_reports + suppliers để xây dựng đối tượng Batch.
     */
    private void loadReportsFromDB() {
        String reportQuery = "SELECT incident_id, report_date, description, batch_id FROM incident_reports";
        String batchQuery =
            "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, b.quantity_in_stock, " +
            "       p.product_name, s.supplier_name, b.value_batch " +
            "FROM batch b " +
            "JOIN products p ON b.product_id = p.product_id " +
            "JOIN import_reports ir ON b.import_id = ir.import_id " +
            "JOIN suppliers s ON ir.supplier_id = s.supplier_id " +
            "WHERE b.batch_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psReport = conn.prepareStatement(reportQuery);
             ResultSet rsReport = psReport.executeQuery()) {

            while (rsReport.next()) {
                int incidentId = rsReport.getInt("incident_id");
                LocalDate reportDate = rsReport.getDate("report_date").toLocalDate();
                String method = rsReport.getString("description");
                int batchId = rsReport.getInt("batch_id");

                // Lấy chi tiết Batch tương ứng
                Batch loadedBatch = null;
                try (PreparedStatement psBatch = conn.prepareStatement(batchQuery)) {
                    psBatch.setInt(1, batchId);
                    ResultSet rsBatch = psBatch.executeQuery();
                    if (rsBatch.next()) {
                        int bId            = rsBatch.getInt("batch_id");
                        LocalDate impDate  = rsBatch.getDate("import_date").toLocalDate();
                        LocalDate expDate  = rsBatch.getDate("expiration_date").toLocalDate();
                        int totalQty       = rsBatch.getInt("total_quantity");
                        int qtyInStock     = rsBatch.getInt("quantity_in_stock");
                        String productName = rsBatch.getString("product_name");
                        String supplier    = rsBatch.getString("supplier_name");
                        int valueBatch     = rsBatch.getInt("value_batch");

                        loadedBatch = new Batch(
                            bId,
                            impDate,
                            expDate,
                            totalQty,
                            qtyInStock,
                            productName,
                            supplier,
                            valueBatch
                        );
                    }
                    rsBatch.close();
                }

                // Chỉ thêm vào reportList khi batch thực sự không null
                if (loadedBatch != null) {
                    // Report constructor: (String reportId, LocalDate date, String method, Batch batch)
                    String reportCode = "RP" + String.format("%03d", incidentId);
                    reportList.add(new Report(reportCode, reportDate, method, loadedBatch));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenCreateReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_CreateReport.fxml"));
            Parent root = loader.load();

            Admin_CreateReportController controller = loader.getController();
            controller.setReportList(reportList);
            controller.setAvailableBatches(availableBatches);

            Stage stage = new Stage();
            stage.setTitle("Tạo biên bản mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
