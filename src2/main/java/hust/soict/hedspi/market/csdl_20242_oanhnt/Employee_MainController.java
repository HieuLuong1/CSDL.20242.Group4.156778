package hust.soict.hedspi.market.csdl_20242_oanhnt;
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

    private Employee employee;

    public void setEmployee(Employee employee) {
        this.employee = employee;
        welcomeLabel.setText("Xin chào, " + employee.getFullName());
    }

    @FXML
    private void handleViewPersonalInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_Info.fxml"));
            Parent root = loader.load();

            Employee_InfoController controller = loader.getController();
            controller.setEmployeeInfo(employee);
            contentArea.getChildren().setAll(root);
            //Stage stage = new Stage();
            //stage.setTitle("Thông tin nhân viên");
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Login.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_Schedule.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
            //Stage stage = new Stage();
            //stage.setTitle("Lịch làm việc");
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManageCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_CustomersManage.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Employee_StoreManage.fxml"));
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

    public void handleOrder() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/EmployeeOrder.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleImport(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Admin_Import.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}