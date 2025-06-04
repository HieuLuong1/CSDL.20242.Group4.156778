package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class Employee_BatchController {
    @FXML
    private TableView<Batch> batchTable;

    @FXML
    private TableColumn<Batch, Integer> colBatchID;
    @FXML
    private TableColumn<Batch, LocalDate> colDate;
    @FXML
    private TableColumn<Batch, LocalDate> colExp;
    @FXML
    private TableColumn<Batch, Integer> colQuantity;
    @FXML
    private TableColumn<Batch, Integer> colSale;
    @FXML
    private TableColumn<Batch, String> colProd;
    @FXML
    private TableColumn<Batch, String> colSupplier;

    @FXML
    private TextField searchField;
    @FXML
    private RadioButton radioBtnByDate;
    @FXML
    private RadioButton radioBtnBySupplier;

    private final ObservableList<Batch> batchList = FXCollections.observableArrayList();

    public void initialize() {
        colBatchID.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colSale.setCellValueFactory(new PropertyValueFactory<>("soldQuantity"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));


        batchList.addAll(
                new Batch(1, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 8, 1), 100, 10, "Sữa TH True Milk", "TH Group", 10000),
                new Batch(2, LocalDate.of(2025, 5, 3), LocalDate.of(2025, 9, 1), 150, 20, "Bánh Oreo", "Mondelez", 200000),
                new Batch(3, LocalDate.of(2025, 5, 5), LocalDate.of(2025, 12, 31), 200, 50, "Coca-Cola lon", "Coca-Cola Việt Nam", 300000)
        );

        batchTable.setItems(batchList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchBatch());
        radioBtnByDate.setOnAction(e -> searchBatch());
        radioBtnBySupplier.setOnAction(e -> searchBatch());
    }
    @FXML
    private void searchBatch() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            batchTable.setItems(batchList);  // Hiện lại tất cả nếu ô tìm kiếm trống
            return;
        }

        ObservableList<Batch> filteredList = FXCollections.observableArrayList();

        for (Batch b : batchList) {
            if (radioBtnByDate.isSelected()) {

                if (b.getImportDate().toString().contains(keyword)) {
                    filteredList.add(b);
                }
            } else if (radioBtnBySupplier.isSelected()) {
                if (b.getSupplier().toLowerCase().contains(keyword)) {
                    filteredList.add(b);
                }
            }
        }

        batchTable.setItems(filteredList);
    }
}
