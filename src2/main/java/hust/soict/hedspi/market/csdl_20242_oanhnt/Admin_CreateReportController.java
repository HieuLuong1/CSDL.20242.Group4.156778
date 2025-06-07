package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

public class Admin_CreateReportController {

    @FXML private TextField tfReportId;
    @FXML private DatePicker dpReportDate;
    @FXML private TextField tfMethod;
    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, String> colBatchId;
    @FXML private TableColumn<Batch, String> colProduct;
    @FXML private TableColumn<Batch, String> colImportDate;
    @FXML private TableColumn<Batch, String> colExpiryDate;
    @FXML private TableColumn<Batch, String> colQuantity;
    @FXML private Button btnCreate, btnCancel;

    private ObservableList<Report> reportList;
    private ObservableList<Batch> availableBatches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiryDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantityInStock())));

        batchTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnCreate.setOnAction(e -> createReport());
        btnCancel.setOnAction(e -> clearFields());
    }

    private void createReport() {
        String reportId = tfReportId.getText().trim();
        LocalDate reportDate = dpReportDate.getValue();
        String method = tfMethod.getText().trim();
        Batch selectedBatch = batchTable.getSelectionModel().getSelectedItem();

        if (reportId.isEmpty() || reportDate == null || method.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ thông tin: mã biên bản, ngày lập, nội dung xử lý.");
            return;
        }

        if (selectedBatch == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn một lô hàng để lập biên bản.");
            return;
        }

        if (isReportIdExists(reportId)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã biên bản đã tồn tại. Vui lòng nhập mã khác.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String insertSQL = "INSERT INTO incident_reports (incident_id, report_date, report_time, description, batch_id) VALUES (?, ?, current_time, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setInt(1, Integer.parseInt(reportId.replace("RP", "")));
                stmt.setDate(2, Date.valueOf(reportDate));
                stmt.setString(3, method);
                stmt.setInt(4, selectedBatch.getBatchId());
                stmt.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tạo biên bản xử lý thành công!");

            if (reportList != null) {
                String reportCode = reportId;
                reportList.add(new Report(reportCode, reportDate, method, selectedBatch));
            }
            availableBatches.remove(selectedBatch);
            batchTable.refresh();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi tạo biên bản xử lý.");
        }
    }

    private boolean isReportIdExists(String reportId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM incident_reports WHERE incident_id = ?")) {
            stmt.setInt(1, Integer.parseInt(reportId.replace("RP", "")));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void clearFields() {
        tfReportId.clear();
        tfMethod.clear();
        dpReportDate.setValue(null);
        batchTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setReportList(ObservableList<Report> reportList) {
        this.reportList = reportList;
    }

    public void setAvailableBatches(ObservableList<Batch> availableBatches) {
        this.availableBatches = availableBatches;
        if (batchTable != null) {
            batchTable.setItems(this.availableBatches);
        }
    }
}
