import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Employee_InfoController {

	@FXML
	private Label fullNameLabel;

	@FXML
	private Label employeeIdLabel;

	@FXML
	private Label dobLabel;

	@FXML
	private Label genderLabel;

	@FXML
	private Label emailLabel;

	@FXML
	private Label phoneLabel;

	@FXML
	private Label addressLabel;

	@FXML
	private Label identityLabel;

	public void setEmployeeInfo(String lastname, String firstname, int employeeID, String dob, String gender, String email, String phone, String address, String identity) {
		fullNameLabel.setText(lastname + " " + firstname);
		employeeIdLabel.setText(String.valueOf(employeeID));
		dobLabel.setText(dob);
		genderLabel.setText(gender);
		emailLabel.setText(email);
		phoneLabel.setText(phone);
		addressLabel.setText(address);
		identityLabel.setText(identity);
	}
}