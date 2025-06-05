import java.io.IOException;
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
    @FXML private TableColumn<Batch, String> colBatchId, colProduct, colImport, colExpiry, colQuantity;
    @FXML private Button btnCreateReport;

    private final ObservableList<Report> reportList = FXCollections.observableArrayList();
    private final ObservableList<Batch> availableBatches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colReportId.setCellValueFactory(data -> data.getValue().reportIdProperty());
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        colMethod.setCellValueFactory(data -> data.getValue().methodProperty());

        reportTable.setItems(reportList);

        colBatchId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBatchId())));
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        colImport.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImportDate().toString()));
        colExpiry.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate().toString()));
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalQuantity() - data.getValue().getSoldQuantity())));

        Batch b1 = new Batch(101, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 5, 31), 100, 90, "Sữa Vinamilk", "Vinamilk Co.", 12000);
        Batch b2 = new Batch(102, LocalDate.of(2025, 3, 20), LocalDate.of(2025, 6, 1), 80, 70, "Nước suối", "Coca", 5000);
        Batch b3 = new Batch(103, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 6, 5), 90, 85, "Mì Hảo Hảo", "Acecook", 4500);

        availableBatches.addAll(b1, b2, b3);

        reportList.add(new Report("RP001", LocalDate.of(2025, 6, 1), "Tiêu hủy", b1));
        reportList.add(new Report("RP002", LocalDate.of(2025, 6, 2), "Sử dụng làm nước rửa", b2));

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getBatch() != null) {
                batchTable.setItems(FXCollections.observableArrayList(newSel.getBatch()));
            } else {
                batchTable.setItems(FXCollections.observableArrayList());
            }
        });
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
