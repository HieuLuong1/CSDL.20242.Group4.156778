package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Admin_InventoryReportController {

    @FXML private TableView<InventoryReport> inventoryReportTable;
    @FXML private TableColumn<InventoryReport, String> colReportId, colReportDate;

    @FXML private TableView<Batch> batchDetailTable;
    @FXML private TableColumn<Batch, String> colBatchId, colProductName, colImportDate, colExpiryDate, colSystemQty, colActualQty;

    @FXML private Button btnCreateInventoryReport;

    private final ObservableList<InventoryReport> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupReportTable();
        setupBatchDetailTable();
        loadInventoryReportsFromDB();

        // Update batch details when a report is selected
        inventoryReportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                batchDetailTable.setItems(FXCollections.observableArrayList(newVal.getBatches()));
            } else {
                batchDetailTable.getItems().clear();
            }
        });
    }

    private void setupReportTable() {
        colReportId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportId()));
        colReportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        inventoryReportTable.setItems(reportList);
    }

    private void setupBatchDetailTable() {
        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiryDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        colSystemQty.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalQuantity())));
        colActualQty.setCellValueFactory(data -> {
            InventoryReport selected = inventoryReportTable.getSelectionModel().getSelectedItem();
            return new SimpleStringProperty(
                    selected != null ? String.valueOf(selected.getActualQuantityOfBatch(data.getValue())) : ""
            );
        });
    }

    private void loadInventoryReportsFromDB() {
        String reportQuery = "SELECT * FROM check_reports";
        String detailQuery = """
                SELECT cd.batch_id, cd.real_quantity, b.import_date, b.expiration_date, 
                       b.total_quantity, p.product_name, b.quantity_in_stock
                FROM check_details cd
                JOIN batch b ON cd.batch_id = b.batch_id
                JOIN products p ON b.product_id = p.product_id
                WHERE cd.report_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psReport = conn.prepareStatement(reportQuery);
             ResultSet rsReport = psReport.executeQuery()) {

            while (rsReport.next()) {
                int reportId = rsReport.getInt("report_id");
                LocalDate checkDate = rsReport.getDate("check_date").toLocalDate();

                List<Batch> batches = new ArrayList<>();
                List<Integer> actualQuantities = new ArrayList<>();

                try (PreparedStatement psDetail = conn.prepareStatement(detailQuery)) {
                    psDetail.setInt(1, reportId);
                    ResultSet rsDetail = psDetail.executeQuery();

                    while (rsDetail.next()) {
                        int batchId = rsDetail.getInt("batch_id");
                        int realQty = rsDetail.getInt("real_quantity");
                        LocalDate importDate = rsDetail.getDate("import_date").toLocalDate();
                        LocalDate expiryDate = rsDetail.getDate("expiration_date").toLocalDate();
                        int totalQty = rsDetail.getInt("quantity_in_stock");
                        String productName = rsDetail.getString("product_name");

                        Batch batch = new Batch(batchId, importDate, expiryDate, totalQty, 0, productName, "", 0);
                        batches.add(batch);
                        actualQuantities.add(realQty);
                    }
                }

                reportList.add(new InventoryReport("IR" + String.format("%03d", reportId), checkDate, batches, actualQuantities));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenCreateInventoryReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_CreateInventoryReport.fxml"));
            Parent root = loader.load();

            Admin_CreateInventoryReportController controller = loader.getController();
            controller.setReportList(reportList); // cho phép cập nhật lại table sau khi tạo biên bản

            Stage stage = new Stage();
            stage.setTitle("Tạo Biên Bản Kiểm Kê");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}