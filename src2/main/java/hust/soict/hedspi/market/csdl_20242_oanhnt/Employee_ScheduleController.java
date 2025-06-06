package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @FXML private ComboBox<String> monthCombo;
    @FXML private ComboBox<String> yearCombo;

    private final ObservableList<WorkScheduleRecord> allRecords = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupColumns();
        setupMonthYearSelectors();
        loadSampleData();
        updateView();
    }

    private void setupColumns() {
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colShift.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getShift()));
        colStartTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime()));
        colEndTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndTime()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNote()));
        colSalary.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSalaryEarned() + " VND"));
    }

    private void setupMonthYearSelectors() {
        for (int m = 1; m <= 12; m++) {
            monthCombo.getItems().add(String.format("%02d", m));
        }

        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 5; y <= currentYear + 5; y++) {
            yearCombo.getItems().add(String.valueOf(y));
        }

        monthCombo.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        yearCombo.setValue(String.valueOf(currentYear));

        monthCombo.setOnAction(e -> updateView());
        yearCombo.setOnAction(e -> updateView());
    }

    private void loadSampleData() {
        allRecords.addAll(
                new WorkScheduleRecord("01/05/2025", "Sáng", "07:00", "15:00", "Có mặt", "", 200000),
                new WorkScheduleRecord("02/05/2025", "Sáng", "07:00", "15:00", "Đi muộn", "Đến lúc 13:15", 100000),
                new WorkScheduleRecord("03/05/2025", "Sáng", "07:00", "15:00", "Vắng", "Không có lý do", 0),
                new WorkScheduleRecord("04/05/2025", "Sáng", "07:00", "15:00", "Có mặt", "", 200000),
                new WorkScheduleRecord("05/06/2025", "Chiều", "13:00", "21:00", "Có mặt", "", 220000),
                new WorkScheduleRecord("06/06/2025", "Chiều", "13:00", "21:00", "Vắng", "Có lý do", 0)
        );
    }

    private void updateView() {
        String selectedMonth = monthCombo.getValue();
        String selectedYear = yearCombo.getValue();
        if (selectedMonth == null || selectedYear == null) return;

        // Lọc lịch theo tháng/năm
        List<WorkScheduleRecord> filtered = allRecords.stream()
                .filter(record -> {
                    LocalDate date = LocalDate.parse(record.getDate(), dateFormatter);
                    return date.getMonthValue() == Integer.parseInt(selectedMonth)
                            && date.getYear() == Integer.parseInt(selectedYear);
                }).collect(Collectors.toList());

        scheduleTable.setItems(FXCollections.observableArrayList(filtered));

        titleLabel.setText("Lịch làm việc của bạn - Tháng " + selectedMonth + "/" + selectedYear);
        updateSummary(filtered);
        updateSalary(filtered);
    }

    private void updateSummary(List<WorkScheduleRecord> records) {
        long present = records.stream().filter(s -> s.getStatus().equals("Có mặt")).count();
        long late = records.stream().filter(s -> s.getStatus().equals("Đi muộn")).count();
        long absent = records.stream().filter(s -> s.getStatus().equals("Vắng")).count();

        presentLabel.setText("Có mặt: " + present + " buổi");
        lateLabel.setText("Đi muộn: " + late + " buổi");
        absentLabel.setText("Vắng: " + absent + " buổi");
    }

    private void updateSalary(List<WorkScheduleRecord> records) {
        int total = records.stream().mapToInt(WorkScheduleRecord::getSalaryEarned).sum();
        totalSalaryLabel.setText("Lương tạm tính: " + total + " VND");
    }

    @FXML
    private void handleShowDetail() {
        scheduleTable.setVisible(!scheduleTable.isVisible());
    }
    @FXML
    private void handleViewSchedule() {
        updateView();
    }
}
