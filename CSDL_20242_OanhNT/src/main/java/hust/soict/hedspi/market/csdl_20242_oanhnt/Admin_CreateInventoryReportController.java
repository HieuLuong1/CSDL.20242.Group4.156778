package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Admin_CreateInventoryReportController {

    @FXML private TextField tfReportId;
    @FXML private DatePicker dpReportDate;
    @FXML private TableView<BatchWrapper> batchTable;
    @FXML private TableColumn<BatchWrapper, String> colBatchId, colProduct, colImportDate, colExpiryDate;
    @FXML private TableColumn<BatchWrapper, Integer> colSystemQuantity, colActualQuantity;
    @FXML private TableColumn<BatchWrapper, Boolean> colSelect;
    @FXML private Button btnCreate, btnCancel;

    private final ObservableList<BatchWrapper> batchList = FXCollections.observableArrayList();
    private ObservableList<InventoryReport> reportList; // nhận từ bên ngoài

    public void setReportList(ObservableList<InventoryReport> reportList) {
        this.reportList = reportList;
    }

    @FXML
    public void initialize() {
        // Setup Table Columns
        colBatchId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getBatch().getBatchId())));
        colProduct.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBatch().getProductName()));
        colImportDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBatch().getImportDate().toString()));
        colExpiryDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBatch().getExpiryDate().toString()));
        colSystemQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getBatch().getTotalQuantity()).asObject());

        colActualQuantity.setCellValueFactory(cell -> cell.getValue().actualQuantityProperty().asObject());
        colActualQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colActualQuantity.setOnEditCommit(e -> {
            BatchWrapper wrapper = e.getRowValue();
            int newVal = e.getNewValue() != null && e.getNewValue() >= 0 ? e.getNewValue() : 0;
            wrapper.setActualQuantity(newVal);
        });

        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

        batchTable.setItems(batchList);
        batchTable.setEditable(true);
        colSelect.setEditable(true);
        colActualQuantity.setEditable(true);

        // Load dữ liệu batch từ DB
        loadAvailableBatchesFromDB();

        // Nút tạo báo cáo
        btnCreate.setOnAction(e -> handleCreateReport());
        // Nút huỷ
        btnCancel.setOnAction(e -> batchTable.getScene().getWindow().hide());
    }

    private void loadAvailableBatchesFromDB() {
        batchList.clear();
        String sql = "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, " +
                "b.quantity_in_stock, p.product_name, s.supplier_name, b.value_batch " +
                "FROM batch b " +
                "JOIN products p ON b.product_id = p.product_id " +
                "JOIN import_reports ir ON b.import_id = ir.import_id " +
                "JOIN suppliers s ON ir.supplier_id = s.supplier_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Batch batch = new Batch(
                        rs.getInt("batch_id"),
                        rs.getDate("import_date").toLocalDate(),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getInt("total_quantity"),
                        rs.getInt("quantity_in_stock"),
                        rs.getString("product_name"),
                        rs.getString("supplier_name"),
                        rs.getDouble("value_batch")
                );
                batchList.add(new BatchWrapper(batch));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi truy vấn", "Không tải được dữ liệu lô hàng từ CSDL.");
        }
    }

    private void handleCreateReport() {

        LocalDate date = dpReportDate.getValue();

        if (date == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng chọn ngày kiểm kê.");
            return;
        }

        // Lấy các batch đã chọn
        List<BatchWrapper> selectedWrappers = new ArrayList<>();
        for (BatchWrapper bw : batchList) {
            if (bw.isSelected()) {
                selectedWrappers.add(bw);
            }
        }

        if (selectedWrappers.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng chọn ít nhất một lô hàng.");
            return;
        }

        // Lưu báo cáo kiểm kê vào CSDL
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert vào bảng check_reports
            int generatedReportId = -1;
            String insertReportSQL = "INSERT INTO check_reports (check_date) VALUES (?) RETURNING report_id";
            try (PreparedStatement psReport = conn.prepareStatement(insertReportSQL)) {
                psReport.setDate(1, Date.valueOf(date));
                ResultSet rs = psReport.executeQuery();
                if (rs.next()) {
                    generatedReportId = rs.getInt("report_id");
                } else {
                    throw new SQLException("Không lấy được report_id được sinh tự động.");
                }
            }

            // 2. Insert chi tiết từng batch vào bảng check_details
            String insertDetailSQL = "INSERT INTO check_details (report_id, batch_id, real_quantity) VALUES (?, ?, ?)";
            try (PreparedStatement psDetail = conn.prepareStatement(insertDetailSQL)) {
                for (BatchWrapper bw : selectedWrappers) {
                    psDetail.setInt(1, generatedReportId);
                    psDetail.setInt(2, bw.getBatch().getBatchId());
                    psDetail.setInt(3, bw.getActualQuantity());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();

            // Tạo InventoryReport để cập nhật lên UI
            List<Batch> batchSelected = new ArrayList<>();
            List<Integer> actualQuantities = new ArrayList<>();
            for (BatchWrapper bw : selectedWrappers) {
                batchSelected.add(bw.getBatch());
                actualQuantities.add(bw.getActualQuantity());
            }

            InventoryReport newReport = new InventoryReport(String.valueOf(generatedReportId), date, batchSelected, actualQuantities);
            if (reportList != null) {
                reportList.add(newReport);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tạo báo cáo kiểm kê thành công!");
            batchTable.getScene().getWindow().hide();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu báo cáo kiểm kê: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}