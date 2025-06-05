import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class Admin_AddImportController {

    @FXML
    private Button btnAddBatch;

    @FXML
    private VBox batchContainer;

    @FXML
    private Button btnCreate;

    @FXML
    private VBox batchList;

    private ObservableList<ImportOrder> importOrderList;

    public void setImportOrderList(ObservableList<ImportOrder> list) {
        this.importOrderList = list;
    }

    @FXML
    public void initialize() {
        btnAddBatch.setOnAction(event -> addBatchFields());
    }

    @FXML private TextField txtImportId;
    @FXML private DatePicker dateImportDate;
    @FXML private TextField txtLocation;
    @FXML private TextField txtSupplier;
    @FXML private TextField txtCreator;


    private void addBatchFields() {
        HBox batchBox = new HBox(10);

        TextField txtBatchId = new TextField();
        txtBatchId.setPromptText("Mã lô");

        TextField txtExpiryDate = new TextField();
        txtExpiryDate.setPromptText("Hạn sử dụng");

        TextField txtQuantity = new TextField();
        txtQuantity.setPromptText("Số lượng");

        TextField txtProductName = new TextField();
        txtProductName.setPromptText("Tên sản phẩm");

        TextField txtCost = new TextField();
        txtCost.setPromptText("Điền giá lô");

        batchBox.getChildren().addAll(txtBatchId, txtExpiryDate, txtQuantity, txtProductName, txtCost);

        batchContainer.getChildren().add(batchBox);
    }
    @FXML
    private void handleCreateImport() {
        String id = txtImportId.getText();
        LocalDate date = dateImportDate.getValue();
        String address = txtLocation.getText();
        String supplier = txtSupplier.getText();

        ObservableList<String> batchNames = FXCollections.observableArrayList();
        double totalValue = 0;

        for (Node node : batchContainer.getChildren()) {
            if (node instanceof HBox hBox) {
                String batchName = null;
                String costStr = null;

                for (Node child : hBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        String prompt = tf.getPromptText();
                        String text = tf.getText().trim();

                        if ("Mã lô".equals(prompt)) {
                            batchName = text;
                        } else if ("Điền giá lô".equals(prompt)) {
                            costStr = text;
                        }
                    }
                }

                if (batchName != null && !batchName.isEmpty()) {
                    batchNames.add(batchName);
                }

                try {
                    double cost = costStr != null && !costStr.isEmpty() ? Double.parseDouble(costStr) : 0;
                    totalValue += cost;
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi định dạng giá lô cho batch: " + batchName);
                }
            }
        }

        System.out.println("Tổng giá trị hóa đơn nhập: " + totalValue);
        ImportOrder order = new ImportOrder(id, date, address, totalValue, batchNames, supplier);
        importOrderList.add(order);

        ((Stage) btnCreate.getScene().getWindow()).close();
    }
}
