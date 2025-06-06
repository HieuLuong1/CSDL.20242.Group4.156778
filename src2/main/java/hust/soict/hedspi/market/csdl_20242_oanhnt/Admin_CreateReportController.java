package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class Admin_CreateReportController {

    @FXML private TextField tfReportId;
    @FXML private DatePicker dpReportDate;
    @FXML private TextField tfMethod;
    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, String> colBatchId, colProduct, colImportDate, colExpiryDate, colQuantity;
    @FXML private Button btnCreate, btnCancel;

    private ObservableList<Batch> availableBatches;
    private ObservableList<Report> reportList;

    public void setReportList(ObservableList<Report> reportList) {
        this.reportList = reportList;
    }

    public void setAvailableBatches(ObservableList<Batch> availableBatches) {
        this.availableBatches = availableBatches;
        batchTable.setItems(availableBatches);
    }

    @FXML
    public void initialize() {
        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiryDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(
                data.getValue().getTotalQuantity() - data.getValue().getSoldQuantity())
        ));

        batchTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        btnCreate.setOnAction(e -> createReport());
    }

    private void createReport() {
        String reportId = tfReportId.getText();
        LocalDate date = dpReportDate.getValue();
        String method = tfMethod.getText();
        Batch selectedBatch = batchTable.getSelectionModel().getSelectedItem();

        if (reportId.isEmpty() || date == null || method.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ mã biên bản, ngày lập và nội dung xử lý.");
            return;
        }

        if (selectedBatch == null) {
            showAlert("Vui lòng chọn một lô để lập biên bản.");
            return;
        }

        Report newReport = new Report(reportId, date, method, selectedBatch);
        reportList.add(newReport);
        availableBatches.remove(selectedBatch);
        batchTable.getSelectionModel().clearSelection();
        batchTable.refresh();
        showAlert("Tạo biên bản thành công!");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
