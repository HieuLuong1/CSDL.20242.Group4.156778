package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class Employee_BatchController {
    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, Integer> colBatchID;
    @FXML private TableColumn<Batch, LocalDate> colDate;
    @FXML private TableColumn<Batch, LocalDate> colExp;
    @FXML private TableColumn<Batch, Integer> colQuantity;
    @FXML private TableColumn<Batch, Integer> colSale;
    @FXML private TableColumn<Batch, String> colProd;
    @FXML private TableColumn<Batch, String> colSupplier;
    @FXML private TextField searchField;
    @FXML private RadioButton radioBtnByDate;
    @FXML private RadioButton radioBtnBySupplier;

    private final ObservableList<Batch> batchList = FXCollections.observableArrayList();

    public void initialize() {
        colBatchID.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colSale.setCellValueFactory(new PropertyValueFactory<>("soldQuantity"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        loadBatchDataFromDB();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchBatch());
        radioBtnByDate.setOnAction(e -> searchBatch());
        radioBtnBySupplier.setOnAction(e -> searchBatch());
    }

    private void loadBatchDataFromDB() {
        batchList.clear();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT b.batch_id, b.import_date, b.expiration_date, b.total_quantity, " +
                         "(b.total_quantity - b.quantity_in_stock) AS sold_quantity, " +
                         "p.product_name, s.supplier_name, b.value_batch " +
                         "FROM batch b " +
                         "JOIN products p ON b.product_id = p.product_id " +
                         "JOIN suppliers s ON b.supplier_id = s.supplier_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int batchId = rs.getInt("batch_id");
                LocalDate importDate = rs.getDate("import_date").toLocalDate();
                LocalDate expDate = rs.getDate("expiration_date").toLocalDate();
                int totalQty = rs.getInt("total_quantity");
                int soldQty = rs.getInt("sold_quantity");
                String productName = rs.getString("product_name");
                String supplier = rs.getString("supplier_name");
                int value = rs.getInt("value_batch");

                batchList.add(new Batch(batchId, importDate, expDate, totalQty, soldQty, productName, supplier, value));
            }
            rs.close();
            stmt.close();
            batchTable.setItems(batchList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchBatch() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            batchTable.setItems(batchList);
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