import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Employee_CustomerManagerController {
	@FXML private TextField searchField;
	@FXML private TableView<Customer> customerTable;
	@FXML private TableColumn<Customer, String> colCustomerId;
	@FXML private TableColumn<Customer, String> colCustomerName;
	@FXML private TableColumn<Customer, String> colCustomerPhone;
	@FXML private TableColumn<Customer, String> colCustomerEmail;

	@FXML private TextField customerIdField;
	@FXML private TextField customerNameField;
	@FXML private TextField customerPhoneField;
	@FXML private TextField customerEmailField;
	@FXML private Button updateCustomerBtn;

	private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		colCustomerId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
		colCustomerName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
		colCustomerPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone())); 
		colCustomerEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

		customerList.addAll(
				new Customer("KH001", "Nguyễn Văn A", "a@gmail.com",  "0123456789" ),
				new Customer("KH002", "Nguyễn Văn B", "b@gmail.com", "0987654321")
				);
		customerTable.setItems(customerList);
		customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Thông tin khách hàng");
				alert.setHeaderText(null);
				alert.setContentText(
						"Mã: " + newVal.getId() + "\n" +
								"Tên: " + newVal.getFullName() + "\n" +
								"SĐT: " + newVal.getPhone() + "\n" +
								"Email: " + newVal.getEmail()
						);
				alert.showAndWait();
			}
		});
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterCustomerList(newValue);
		});
		updateCustomerBtn.setOnAction(event -> {
			String name = customerNameField.getText();
			String phone = customerPhoneField.getText();
			String email = customerEmailField.getText();
			if(!name.isEmpty()) {
				String newId = "KH" + String.format("%03d", customerList.size() + 1);

				Customer newCustomer = new Customer(newId, name, email, phone);
				customerList.add(newCustomer);

				customerIdField.clear();
				customerNameField.clear();
				customerPhoneField.clear();
				customerEmailField.clear();
			}
		});
	}
	private void filterCustomerList(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			customerTable.setItems(customerList);
			return;
		}

		ObservableList<Customer> filteredList = FXCollections.observableArrayList();
		for (Customer customer : customerList) {
			if (customer.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
					customer.getPhone().contains(keyword)) {
				filteredList.add(customer);
			}
		}
		customerTable.setItems(filteredList);
	}
}
