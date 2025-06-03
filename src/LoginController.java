import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final Map<String, String> accountPassword = new HashMap<>();
    private final Map<String, String> accountRole = new HashMap<>();
    private final Map<String, Customer> customerAccounts = new HashMap<>();
    private final Map<String, Employee> employeeAccounts = new HashMap<>();
    private final Map<String, Admin> adminAccounts = new HashMap<>();

    @FXML
    public void initialize() {
    	accountPassword.put("customer01", "123456");
        accountRole.put("customer01", "customer");
        customerAccounts.put("customer01", new Customer("KH001", "Nguyễn Văn A", "nva@gmail.com", "0123456789"));

        accountPassword.put("employee01", "abc123");
        accountRole.put("employee01", "employee");
        employeeAccounts.put("employee01", new Employee("NV001", "Trần Thị B", "12/03/1998", "Nữ", "tranb@gmail.com", "0987654321", "Hà Nội", "123456789012"));
        
        accountPassword.put("admin01", "admin123");
        accountRole.put("admin01", "admin");
        adminAccounts.put("admin01", new Admin("AD001", "Quản lý siêu thị", "admin@gmail.com", "0123456789"));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!accountPassword.containsKey(username)) {
            errorLabel.setText("Tài khoản không tồn tại.");
            return;
        }

        if (!accountPassword.get(username).equals(password)) {
            errorLabel.setText("Sai mật khẩu.");
            return;
        }

        String role = accountRole.get(username);

        try {
            if (role.equals("customer")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("User_MainLayout.fxml"));
                Parent root = loader.load();

                User_MainController controller = loader.getController();
                controller.setCustomer(customerAccounts.get(username));

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Trang người dùng");
            }
            else if (role.equals("employee")) {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_MainLayout.fxml"));
                Parent root = loader.load();

                Employee_MainController controller = loader.getController();
                controller.setEmployee(employeeAccounts.get(username));

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Trang nhân viên");
            }
            else if (role.equals("admin")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_MainLayout.fxml"));
                Parent root = loader.load();

                Admin_MainController controller = loader.getController();
                controller.setAdmin(adminAccounts.get(username));

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Trang quản lý");
            }
        } catch (IOException e) {
            errorLabel.setText("Lỗi tải giao diện.");
            e.printStackTrace();
        }
    }
}
