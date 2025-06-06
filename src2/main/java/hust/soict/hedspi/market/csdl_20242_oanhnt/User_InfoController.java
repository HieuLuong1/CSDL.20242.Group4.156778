package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class User_InfoController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    public void setUserInfo(String name, String phone, String email) {
        nameLabel.setText(name);
        phoneLabel.setText(phone);
        emailLabel.setText(email);
    }
}