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
    @FXML private Label leaveLabel;
    @FXML private Label lbTimeWork;
    @FXML private TableView<WorkScheduleRecord> scheduleTable;
    @FXML private TableColumn<WorkScheduleRecord, String> colDate;
    @FXML private TableColumn<WorkScheduleRecord, String> colStartTime;
    @FXML private TableColumn<WorkScheduleRecord, String> colEndTime;
    @FXML private TableColumn<WorkScheduleRecord, String> colStatus;

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
        colStartTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime()));
        colEndTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndTime()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
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
        String sql = "SELECT w.work_date, s.start_time, s.end_time, w.status " +
                "FROM working w " +
                "JOIN schedule s ON w.schedule_id = s.schedule_id " +
                "WHERE w.employee_id = ? " +
                "ORDER BY w.work_date";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate d = rs.getDate("work_date").toLocalDate();
                    String date = d.format(dateFormatter);

                    String startTime = rs.getTime("start_time").toString();
                    String endTime = rs.getTime("end_time").toString();

                    String rawStatus = rs.getString("status");
                    String status;
                    if (rawStatus == null) {
                        status = null;
                    } else {
                        status = switch (rawStatus) {
                            case "D" -> "Có mặt";
                            case "M" -> "Đi muộn";
                            case "V" -> "Vắng";
                            default -> "Phép";

                        };
                    }
                    System.out.println("Loaded record: date=" + date + ", status=" + status);
                    allRecords.add(new WorkScheduleRecord(date, startTime, endTime, status));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getScheduleInfoForMonthYear(int month, int year) {
        String sql = "SELECT DISTINCT s.schedule_id, s.start_day, s.end_day, s.start_time, s.end_time " +
                "FROM working w " +
                "JOIN schedule s ON w.schedule_id = s.schedule_id " +
                "WHERE w.employee_id = ? AND EXTRACT(MONTH FROM w.work_date) = ? AND EXTRACT(YEAR FROM w.work_date) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int scheduleId = rs.getInt("schedule_id");
                    int startDay = rs.getInt("start_day");
                    int endDay = rs.getInt("end_day");
                    String startTime = rs.getTime("start_time").toString();
                    String endTime = rs.getTime("end_time").toString();

                    return String.format("- Ca %d: T%d–T%d (%s–%s)",
                            scheduleId, startDay, endDay, startTime, endTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; // nếu không tìm thấy
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
        String scheduleInfo = getScheduleInfoForMonthYear(month, year);
        lbTimeWork.setText(scheduleInfo);
        updateSummary(filtered);
    }

    private void updateSummary(java.util.List<WorkScheduleRecord> records) {
        long present = records.stream().filter(r -> "Có mặt".equals(r.getStatus())).count();
        long late    = records.stream().filter(r -> "Đi muộn".equals(r.getStatus())).count();
        long absent  = records.stream().filter(r -> "Vắng".equals(r.getStatus())).count();
        long leave   = records.stream().filter(r-> "Phép".equals(r.getStatus())).count();
        presentLabel.setText("Có mặt: " + present + " buổi");
        lateLabel   .setText("Đi muộn: " + late    + " buổi");
        absentLabel .setText("Vắng: "     + absent  + " buổi");
        leaveLabel  .setText("Nghỉ phép: " + leave + " buổi");
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
