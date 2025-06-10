package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Employee_MainController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label welcomeLabel;

    public static int employeeID = 1;
    public static String employeeUsername = "employee01";

    @FXML
    public void initialize() {
        try {
            switch (employeeUsername) {
                case "employee01" -> employeeID = 1;
                case "employee02" -> employeeID = 2;
                case "employee03" -> employeeID = 3;
                case "employee04" -> employeeID = 4;
                case "employee05" -> employeeID = 5;
                default -> employeeID = 1;
            }
            String query = "SELECT lastname, firstname FROM employee WHERE employee_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
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
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, employeeID);
                ResultSet rs = ps.executeQuery();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Info.fxml"));
                Parent root = loader.load();
                contentArea.getChildren().setAll(root);
                Employee_InfoController infoController = loader.getController();
                if (rs.next()) {
                    infoController.setEmployeeInfo(
                            rs.getString("lastname"),
                            rs.getString("firstname"),
                            rs.getInt("employee_id"),
                            rs.getDate("dob").toString(),
                            rs.getString("gender"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("identity_id")
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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
    @FXML
    private void handleViewSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee_Schedule.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
            Employee_ScheduleController controller = loader.getController();
            controller.setEmployeeId(employeeID);
            //Stage stage = new Stage();
            //stage.setTitle("Lịch làm việc");
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSup(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_Supplier.fxml"));
            Parent root = loader.load();
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
            //Stage stage = new Stage();
            //stage.setTitle("Quản lý khách hàng");
            //stage.setScene(new Scene(root));
            //stage.show();
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
            //Stage stage = new Stage();
            //stage.setTitle("Kho và Hàng hóa");
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleOrder() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeOrder.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleImport(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_Import.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}