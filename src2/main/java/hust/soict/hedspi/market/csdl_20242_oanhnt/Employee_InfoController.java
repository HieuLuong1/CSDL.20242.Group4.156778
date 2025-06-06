package hust.soict.hedspi.market.csdl_20242_oanhnt;
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
    private Label idCardLabel;

    public void setEmployeeInfo(Employee employee) {
        fullNameLabel.setText(employee.getFullName());
        employeeIdLabel.setText(employee.getId());
        dobLabel.setText(employee.getDob());
        genderLabel.setText(employee.getGender());
        emailLabel.setText(employee.getEmail());
        phoneLabel.setText(employee.getPhone());
        addressLabel.setText(employee.getAddress());
        idCardLabel.setText(employee.getIdCard());
    }
}