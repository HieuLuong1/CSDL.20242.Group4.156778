import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

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
		colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatch().getBatchId())));
		colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getProductName()));
		colImportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getImportDate().toString()));
		colExpiryDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatch().getExpiryDate().toString()));
		colSystemQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBatch().getTotalQuantity()).asObject());

		colActualQuantity.setCellValueFactory(cell -> cell.getValue().actualQuantityProperty().asObject());
		colActualQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colActualQuantity.setOnEditCommit(e -> {
			BatchWrapper wrapper = e.getRowValue();
			wrapper.setActualQuantity(e.getNewValue());
		});

		colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
		colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

		batchTable.setItems(batchList);
		batchTable.setEditable(true);
		colSelect.setEditable(true);
		colActualQuantity.setEditable(true);

		loadAvailableBatches();

		btnCreate.setOnAction(e -> handleCreateReport());
		btnCancel.setOnAction(e -> batchTable.getScene().getWindow().hide());
	}

	private void loadAvailableBatches() {
		batchList.clear();
		List<Batch> mockData = List.of(
				new Batch(301, LocalDate.of(2025, 3, 10), LocalDate.of(2025, 6, 10), 100, 80, "Sữa Milo", "Nestle", 8000),
				new Batch(302, LocalDate.of(2025, 2, 15), LocalDate.of(2025, 5, 20), 60, 50, "Bánh Oreo", "Mondelez", 7000),
				new Batch(303, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 5, 30), 90, 90, "Trà xanh 0 độ", "URC", 6000)
				);
		for (Batch b : mockData) {
			batchList.add(new BatchWrapper(b));
		}
	}

	private void handleCreateReport() {
		String reportId = tfReportId.getText();
		LocalDate date = dpReportDate.getValue();

		if (reportId.isEmpty() || date == null) {
			showAlert("Vui lòng nhập mã biên bản và chọn ngày.");
			return;
		}

		List<Batch> selectedBatches = new ArrayList<>();
		List<Integer> actualQuantities = new ArrayList<>();

		for (BatchWrapper wrapper : batchList) {
			if (wrapper.isSelected()) {
				selectedBatches.add(wrapper.getBatch());
				actualQuantities.add(wrapper.getActualQuantity());
			}
		}

		if (selectedBatches.isEmpty()) {
			showAlert("Vui lòng chọn ít nhất một lô hàng.");
			return;
		}

		InventoryReport report = new InventoryReport(reportId, date, selectedBatches, actualQuantities);
		if (reportList != null) {
			reportList.add(report);
		}
		batchTable.getScene().getWindow().hide();
	}

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Thông báo");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
