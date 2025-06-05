import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Admin_MainController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private StackPane contentArea;
    private Admin admin;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        welcomeLabel.setText("Xin chào, " + admin.getFullName());
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
    private void handleManageEmployees() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_EmployeesManage.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
            //Stage stage = new Stage();
            //stage.setTitle("Quản lý nhân viên");
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (Exception e) {
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
    private void handleReport(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_ReportManage.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSalaryManage(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_SalaryManage.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
