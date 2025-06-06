package hust.soict.hedspi.market.csdl_20242_oanhnt;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Admin_InventoryReportController {

	@FXML private TableView<InventoryReport> inventoryReportTable;
	@FXML private TableColumn<InventoryReport, String> colReportId, colReportDate;

	@FXML private TableView<Batch> batchDetailTable;
	@FXML private TableColumn<Batch, String> colBatchId, colProductName, colImportDate, colExpiryDate, colSystemQty, colActualQty;

	@FXML private Button btnCreateInventoryReport;

	private final ObservableList<InventoryReport> reportList = FXCollections.observableArrayList();
	

	@FXML
	public void initialize() {
		colReportId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportId()));
		colReportDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
		inventoryReportTable.setItems(reportList);

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

		Batch b1 = new Batch(301, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 6, 1), 100, 80, "Sữa TH", "TH Group", 12000);
		Batch b2 = new Batch(302, LocalDate.of(2025, 3, 15), LocalDate.of(2025, 5, 30), 200, 180, "Mì Hảo Hảo", "Acecook", 4000);

		reportList.add(new InventoryReport("IR001", LocalDate.of(2025, 6, 5), Arrays.asList(b1, b2), Arrays.asList(98, 195)));
		reportList.add(new InventoryReport("IR002", LocalDate.of(2025, 6, 3), Arrays.asList(b1), Arrays.asList(100)));

		inventoryReportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
			if (newSel != null) {
				batchDetailTable.setItems(FXCollections.observableArrayList(newSel.getBatches()));
			}
		});
	}
	@FXML
	private void handleOpenCreateInventoryReport() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_CreateInventoryReport.fxml"));
	        Parent root = loader.load();

	        Admin_CreateInventoryReportController controller = loader.getController();
	        controller.setReportList(reportList);

	        Stage stage = new Stage();
	        stage.setTitle("Tạo Biên Bản Kiểm Kê");
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
