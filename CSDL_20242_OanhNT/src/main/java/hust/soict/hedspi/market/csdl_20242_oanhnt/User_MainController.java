package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class User_MainController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label welcomeLabel;

    public static int customerID = 1;
    public static String currentUsername = "customer01";
    @FXML
    public void initialize() {
        try {
            switch (currentUsername) {
                case "customer01" -> customerID = 1;
                case "customer02" -> customerID = 2;
                case "customer03" -> customerID = 3;
                default -> customerID = 1;
            }
            String query = "SELECT fullname FROM customer WHERE customer_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, customerID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    welcomeLabel.setText("Xin chào, " + rs.getString("fullname"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPersonalInfo() {
        try {
            String query = "SELECT * FROM customer WHERE customer_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, customerID);
                ResultSet rs = ps.executeQuery();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("User_Info.fxml"));
                Parent root = loader.load();
                contentArea.getChildren().setAll(root);
                User_InfoController infoController = loader.getController();
                if(rs.next())infoController.setUserInfo(rs.getString("fullname"), rs.getString("phone"), rs.getString("email"));

            } catch (Exception e) {
                e.printStackTrace();
            }
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
            contentArea.getChildren().setAll(root);
            //Stage stage = new Stage();
            //stage.setTitle("Lịch sử mua hàng");
            //stage.setScene(new Scene(root));
            //stage.show();
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