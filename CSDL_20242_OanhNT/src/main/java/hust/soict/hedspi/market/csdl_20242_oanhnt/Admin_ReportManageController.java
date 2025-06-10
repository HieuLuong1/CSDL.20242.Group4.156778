package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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
        // Bind report table columns
        colReportId.setCellValueFactory(data -> data.getValue().reportIdProperty());
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        colMethod.setCellValueFactory(data -> data.getValue().methodProperty());
        reportTable.setItems(reportList);

        // Bind batch table columns (5 columns as per FXML)
        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImport.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiry.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantityInStock())));

        // Load data
        loadReportsFromDB();
        loadAvailableBatchesFromDB();

        // Show batch details when a report is selected
        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                batchTable.setItems(FXCollections.observableArrayList(newSel.getBatch()));
            } else {
                batchTable.setItems(FXCollections.observableArrayList());
            }
        });

        btnCreateReport.setOnAction(e -> handleOpenCreateReport());
    }

    private void loadReportsFromDB() {
        String reportSql = "SELECT incident_id, report_date, description, batch_id FROM incident_reports";
        String batchSql = "SELECT batch_id, import_date, expiration_date, total_quantity, quantity_in_stock, product_id " +
                "FROM batch WHERE batch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psReport = conn.prepareStatement(reportSql);
             ResultSet rsReport = psReport.executeQuery()) {

            reportList.clear();
            while (rsReport.next()) {
                int id = rsReport.getInt("incident_id");
                LocalDate date = rsReport.getDate("report_date").toLocalDate();
                String desc = rsReport.getString("description");
                int batchId = rsReport.getInt("batch_id");

                // Load corresponding batch
                Batch batch = null;
                try (PreparedStatement psBatch = conn.prepareStatement(batchSql)) {
                    psBatch.setInt(1, batchId);
                    try (ResultSet rsBatch = psBatch.executeQuery()) {
                        if (rsBatch.next()) {
                            int total = rsBatch.getInt("total_quantity");
                            int inStock = rsBatch.getInt("quantity_in_stock");
                            batch = new Batch(
                                    rsBatch.getInt("batch_id"),
                                    rsBatch.getDate("import_date").toLocalDate(),
                                    rsBatch.getDate("expiration_date").toLocalDate(),
                                    total,
                                    total - inStock,
                                    // retrieve product name via join if needed
                                    getProductName(conn, rsBatch.getInt("product_id")),
                                    null, 0);
                        }
                    }
                }
                if (batch != null) {
                    String code = "RP" + String.format("%03d", id);
                    reportList.add(new Report(code, date, desc, batch));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableBatchesFromDB() {
        String sql = "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, b.quantity_in_stock, b.product_id " +
                "FROM batch b WHERE b.quantity_in_stock > 0 ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            availableBatches.clear();
            while (rs.next()) {
                int total = rs.getInt("total_quantity");
                int inStock = rs.getInt("quantity_in_stock");
                Batch batch = new Batch(
                        rs.getInt("batch_id"),
                        rs.getDate("import_date").toLocalDate(),
                        rs.getDate("expiration_date").toLocalDate(),
                        total,
                        inStock,
                        getProductName(conn, rs.getInt("product_id")),
                        null, 0);
                availableBatches.add(batch);
            }
            // note: batchTable not set here - it's only for create form
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getProductName(Connection conn, int productId) throws SQLException {
        String name = "";
        String sql = "SELECT product_name FROM products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) name = rs.getString(1);
            }
        }
        return name;
    }

    @FXML
    private void handleOpenCreateReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_CreateReport.fxml"));
            Parent root = loader.load();
            Admin_CreateReportController ctrl = loader.getController();
            ctrl.setReportList(reportList);
            ctrl.setAvailableBatches(availableBatches);
            Stage stage = new Stage();
            stage.setTitle("Tạo biên bản mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}