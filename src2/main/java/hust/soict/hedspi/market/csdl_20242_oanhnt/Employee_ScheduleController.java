package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Employee_ScheduleController implements Initializable {

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

    private int employeeId;

    public void setEmployeeId(int id) {
        this.employeeId = id;
        loadFromDatabase();
        updateView();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupMonthYearSelectors();
    }

    private void setupColumns() {
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDate()));
        colShift.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShift()));
        colStartTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime()));
        colEndTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndTime()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colNote.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNote()));
        colSalary.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSalaryEarned() + " VND"));
    }

    private void setupMonthYearSelectors() {
        for (int m = 1; m <= 12; m++) {
            monthCombo.getItems().add(String.format("%02d", m));
        }
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 5; y <= currentYear + 1; y++) {
            yearCombo.getItems().add(String.valueOf(y));
        }
        monthCombo.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        yearCombo.setValue(String.valueOf(currentYear));

        monthCombo.setOnAction(e -> updateView());
        yearCombo.setOnAction(e -> updateView());
    }

    private void loadFromDatabase() {
        allRecords.clear();
        String sql = "SELECT w.work_date, s.start_day, s.end_day, s.start_time, s.end_time, w.status " +
                     "FROM working w " +
                     "JOIN schedule s ON w.schedule_id = s.schedule_id " +
                     "WHERE w.employee_id = ? " +
                     "ORDER BY w.work_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate d = rs.getDate("work_date").toLocalDate();
                    String date = d.format(dateFormatter);
                    int sd = rs.getInt("start_day");
                    int ed = rs.getInt("end_day");
                    String shift = (sd <= d.getDayOfWeek().getValue() && d.getDayOfWeek().getValue() <= ed) ? "Ca ngày" : "Ca khác";
                    String startTime = rs.getTime("start_time").toString();
                    String endTime   = rs.getTime("end_time").toString();
                    String status    = switch (rs.getString("status")) {
                        case "D" -> "Có mặt";
                        case "M" -> "Đi muộn";
                        default  -> "Vắng";
                    };
                    String note = ""; // no extra note for view-only
                    int salary = switch (status) {
                        case "Có mặt" -> 200000;
                        case "Đi muộn" -> 150000;
                        default -> 0;
                    };
                    allRecords.add(new WorkScheduleRecord(date, shift, startTime, endTime, status, note, salary));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        String sm = monthCombo.getValue();
        String sy = yearCombo.getValue();
        if (sm == null || sy == null) return;

        int month = Integer.parseInt(sm);
        int year  = Integer.parseInt(sy);
        var filtered = allRecords.stream()
                .filter(r -> {
                    LocalDate d = LocalDate.parse(r.getDate(), dateFormatter);
                    return d.getMonthValue() == month && d.getYear() == year;
                })
                .collect(Collectors.toList());

        scheduleTable.setItems(FXCollections.observableArrayList(filtered));
        titleLabel.setText("Lịch làm việc - " + sm + "/" + sy);
        updateSummary(filtered);
        updateSalary(filtered);
    }

    private void updateSummary(java.util.List<WorkScheduleRecord> records) {
        long present = records.stream().filter(r -> r.getStatus().equals("Có mặt")).count();
        long late    = records.stream().filter(r -> r.getStatus().equals("Đi muộn")).count();
        long absent  = records.stream().filter(r -> r.getStatus().equals("Vắng")).count();
        presentLabel.setText("Có mặt: " + present + " buổi");
        lateLabel   .setText("Đi muộn: " + late    + " buổi");
        absentLabel .setText("Vắng: "     + absent  + " buổi");
    }

    private void updateSalary(java.util.List<WorkScheduleRecord> records) {
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
