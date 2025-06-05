import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Admin_CreateReportController {

	@FXML private TextField tfReportId;
	@FXML private DatePicker dpReportDate;
	@FXML private TableView<BatchWrapper> batchTable;
	@FXML private TableColumn<BatchWrapper, String> colBatchId, colProduct, colImportDate, colExpiryDate, colQuantity;
	@FXML private TableColumn<BatchWrapper, Boolean> colSelect;
	@FXML private Button btnCreate, btnCancel;

	private final ObservableList<BatchWrapper> batchList = FXCollections.observableArrayList();
	private ObservableList<Report> reportList;

	public void setReportList(ObservableList<Report> reportList) {
		this.reportList = reportList;
	}
	@FXML
	public void initialize() {
		colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatch().getBatchId())));
		colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getProductName()));
		colImportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getImportDate().toString()));
		colExpiryDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getExpiryDate().toString()));
		colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(
				data.getValue().getBatch().getTotalQuantity() - data.getValue().getBatch().getSoldQuantity())
				));

		colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
		colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

		loadExpiredBatches();

		batchTable.setItems(batchList);
		btnCreate.setOnAction(e -> createReport());
		batchTable.setEditable(true);
		colSelect.setEditable(true);

	}

	private void loadExpiredBatches() {
		LocalDate today = LocalDate.now();
		List<Batch> allBatches = List.of(
				new Batch(201, LocalDate.of(2025, 3, 10), LocalDate.of(2025, 5, 10), 100, 80, "Nước suối Lavie", "Lavie", 3000),
				new Batch(202, LocalDate.of(2025, 2, 20), LocalDate.of(2025, 4, 15), 50, 30, "Bánh Custas", "Orion", 6000),
				new Batch(203, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 8, 1), 70, 10, "Snack Oishi", "Oishi", 5000)
				);

		for (Batch batch : allBatches) {
			if (batch.getExpiryDate().isBefore(today)) {
				batchList.add(new BatchWrapper(batch));
			}
		}
	}

	private void createReport() {
		String reportId = tfReportId.getText();
		LocalDate date = dpReportDate.getValue();

		if (reportId.isEmpty() || date == null) {
			showAlert("Vui lòng nhập đầy đủ mã biên bản và ngày lập.");
			return;
		}

		List<Batch> selectedBatches = new ArrayList<>();
		for (BatchWrapper wrapper : batchList) {
			if (wrapper.isSelected()) {
				selectedBatches.add(wrapper.getBatch());
			}
		}

		if (selectedBatches.isEmpty()) {
			showAlert("Chưa chọn lô nào để tạo biên bản.");
			return;
		}

		Report newReport = new Report(reportId, date, selectedBatches);
		reportList.add(newReport);
		batchList.removeIf(wrapper -> wrapper.isSelected());
		batchTable.refresh(); 
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Thông báo");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
