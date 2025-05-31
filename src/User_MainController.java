import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class User_MainController {

    @FXML
    private Label welcomeLabel;

    private Customer customer;
    public void setCustomer(Customer customer) {
        this.customer = customer;
        welcomeLabel.setText("Xin chào, " + customer.getFullName());
    }

    @FXML
    private void handleViewPersonalInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("User_Info.fxml"));
            Parent root = loader.load();

            User_InfoController infoController = loader.getController();
            infoController.setUserInfo(customer.getFullName(), customer.getPhone(), customer.getEmail());

            Stage stage = new Stage();
            stage.setTitle("Thông tin cá nhân");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleViewPurchaseHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("User_PurchaseHistory.fxml"));
            Parent root = loader.load();

            User_PurchaseHistoryController controller = loader.getController();
            controller.loadInvoices();

            Stage stage = new Stage();
            stage.setTitle("Lịch sử mua hàng");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
