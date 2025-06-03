import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Employee_ScheduleController {

    @FXML private Label titleLabel;
    @FXML private Label presentLabel;
    @FXML private Label lateLabel;
    @FXML private Label absentLabel;
    @FXML private Label totalSalaryLabel;

    @FXML private TableView<WorkScheduleRecord> scheduleTable;
    @FXML private TableColumn<WorkScheduleRecord, String> colDate;
    @FXML private TableColumn<WorkScheduleRecord, String> colShift;
    @FXML private TableColumn<WorkScheduleRecord, String> colStartTime;
    @FXML private TableColumn<WorkScheduleRecord, String> colEndTime;
    @FXML private TableColumn<WorkScheduleRecord, String> colStatus;
    @FXML private TableColumn<WorkScheduleRecord, String> colNote;
    @FXML private TableColumn<WorkScheduleRecord, String> colSalary;

    private final ObservableList<WorkScheduleRecord> scheduleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán cột dữ liệu
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colShift.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getShift()));
        colStartTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime()));
        colEndTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndTime()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNote()));
        colSalary.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSalaryEarned() + " VND"));

        // Dữ liệu mẫu
        scheduleList.addAll(
            new WorkScheduleRecord("01/05/2025", "Sáng", "07:00", "15:00", "Có mặt", "", 200000),
            new WorkScheduleRecord("02/05/2025", "Sáng", "07:00", "15:00", "Đi muộn", "Đến lúc 13:15", 100000),
            new WorkScheduleRecord("03/05/2025", "Sáng", "07:00", "15:00", "Vắng", "Không có lý do", 0),
            new WorkScheduleRecord("04/05/2025", "Sáng", "07:00", "15:00", "Có mặt", "", 200000)
        );

        scheduleTable.setItems(scheduleList);
        updateSummary();
        calculateTotalSalary();
    }

    private void updateSummary() {
        long present = scheduleList.stream().filter(s -> s.getStatus().equals("Có mặt")).count();
        long late = scheduleList.stream().filter(s -> s.getStatus().equals("Đi muộn")).count();
        long absent = scheduleList.stream().filter(s -> s.getStatus().equals("Vắng")).count();

        presentLabel.setText("Có mặt: " + present + " buổi");
        lateLabel.setText("Đi muộn: " + late + " buổi");
        absentLabel.setText("Vắng: " + absent + " buổi");
    }

    private void calculateTotalSalary() {
        int total = scheduleList.stream().mapToInt(WorkScheduleRecord::getSalaryEarned).sum();
        totalSalaryLabel.setText("Lương tạm tính: " + total + " VND");
    }

    @FXML
    private void handleShowDetail() {
        scheduleTable.setVisible(!scheduleTable.isVisible());
    }
}
