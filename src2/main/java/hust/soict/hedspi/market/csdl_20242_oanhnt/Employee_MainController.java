package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Employee_MainController {
    @FXML private StackPane contentArea;
    @FXML private Label welcomeLabel;

    public static int employeeID = 1;
    public static String employeeUsername = "employee01";

    @FXML
    public void initialize() {
        try {
            switch (employeeUsername) {
                case "employee01" -> employeeID = 1;
                case "employee02" -> employeeID = 2;
                case "employee03" -> employeeID = 3;
                default -> employeeID = 1;
            }

            String query = "SELECT lastname, firstname FROM employee WHERE employee_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, employeeID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    welcomeLabel.setText("Xin chào, " + rs.getString("lastname") + " " + rs.getString("firstname"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPersonalInfo() {
        try {
            String query = "SELECT * FROM employee WHERE employee_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, employeeID);
                ResultSet rs = ps.executeQuery();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Info.fxml"));
                Parent root = loader.load();

                contentArea.getChildren().setAll(root);
                Employee_InfoController infoController = loader.getController();

                if (rs.next()) {
                                        // Build Employee model and pass to controller
                    Employee emp = new Employee(
                        String.valueOf(rs.getInt("employee_id")),
                        rs.getString("firstname") + " " + rs.getString("lastname"),
                        rs.getDate("dob").toString(),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("identity_id")
                    );
                    infoController.setEmployeeInfo(emp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewSchedule() {
    	 try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Schedule.fxml"));
    	        Parent root = loader.load();

    	        // Lấy controller và truyền employeeID
    	        Employee_ScheduleController schCtrl = loader.getController();
    	        schCtrl.setEmployeeId(employeeID);

    	        contentArea.getChildren().setAll(root);
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    }

    @FXML
    private void handleManageCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_CustomersManage.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleStorage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_StoreManage.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeOrder.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_Import.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}