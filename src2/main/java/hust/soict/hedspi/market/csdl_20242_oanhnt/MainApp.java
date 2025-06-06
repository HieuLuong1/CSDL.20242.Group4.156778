package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/hust/soict/hedspi/market/csdl_20242_oanhnt/Login.fxml"));
        primaryStage.setTitle("Đăng nhập hệ thống siêu thị");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}