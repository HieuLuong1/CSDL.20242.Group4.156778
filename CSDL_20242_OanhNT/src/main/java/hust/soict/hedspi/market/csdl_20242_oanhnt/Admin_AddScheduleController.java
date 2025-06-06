package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Admin_AddScheduleController {

    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private ComboBox<String> scheduleTemplateComboBox;

    @FXML private TableView<AssignedSchedule> assignedScheduleTable;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeId;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeName;
    @FXML private TableColumn<AssignedSchedule, String> colTemplate;

    @FXML
    public void initialize() {
        // Load tháng
        ObservableList<Integer> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) months.add(i);
        monthComboBox.setItems(months);
        monthComboBox.getSelectionModel().selectFirst();

        // Load nhân viên (demo)
        employeeComboBox.setItems(FXCollections.observableArrayList(
                "E001 - Nguyễn Văn A",
                "E002 - Trần Thị B",
                "E003 - Lê Văn C"
        ));

        // Load mẫu lịch làm việc
        scheduleTemplateComboBox.setItems(FXCollections.observableArrayList(
                "T2-T7 (08:00 - 15:00)",
                "T2-T7 (15:00 - 22:00)",
                "T3-CN (08:00 - 15:00)",
                "T3-CN (15:00 - 22:00)"
        ));

        // Cấu hình bảng
        colEmployeeId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployeeId()));
        colEmployeeName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployeeName()));
        colTemplate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTemplateName()));
    }

    @FXML
    private void handleAssignSchedule() {
        String selectedEmployee = employeeComboBox.getValue();
        String selectedTemplate = scheduleTemplateComboBox.getValue();
        Integer selectedMonth = monthComboBox.getValue();

        if (selectedEmployee == null || selectedTemplate == null || selectedMonth == null) {
            showAlert("Vui lòng chọn đủ thông tin.");
            return;
        }

        String[] parts = selectedEmployee.split(" - ");
        String employeeId = parts[0];
        String employeeName = parts[1];

        // TODO: Ghi dữ liệu gán lịch vào cơ sở dữ liệu

        showAlert("Đã gán lịch cho " + employeeName + " trong tháng " + selectedMonth + ".");
    }

    @FXML
    private void handleLoadAssignedSchedules() {
        Integer selectedMonth = monthComboBox.getValue();
        if (selectedMonth == null) {
            showAlert("Vui lòng chọn tháng.");
            return;
        }

        // TODO: Truy vấn lịch từ DB theo tháng (ở đây demo dữ liệu)
        ObservableList<AssignedSchedule> data = FXCollections.observableArrayList(
                new AssignedSchedule("E001", "Nguyễn Văn A", "T2-T7 (08:00 - 15:00)"),
                new AssignedSchedule("E002", "Trần Thị B", "T3-CN (15:00 - 22:00)")
        );
        assignedScheduleTable.setItems(data);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class cho bảng
    public static class AssignedSchedule {
        private final String employeeId;
        private final String employeeName;
        private final String templateName;

        public AssignedSchedule(String employeeId, String employeeName, String templateName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.templateName = templateName;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }
}
