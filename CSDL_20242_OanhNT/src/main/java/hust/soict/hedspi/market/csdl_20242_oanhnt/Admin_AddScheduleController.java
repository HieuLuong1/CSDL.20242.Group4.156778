package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Admin_AddScheduleController {
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private ComboBox<String> scheduleTemplateComboBox;
    @FXML private DatePicker dpFirstDate;
    @FXML private TableView<AssignedSchedule> assignedScheduleTable;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeId;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeName;
    @FXML private TableColumn<AssignedSchedule, String> colTemplate;
    @FXML private Label ca1Label;
    @FXML private Label ca2Label;
    @FXML private Label ca3Label;
    @FXML private Label ca4Label;

    private String selectedEmployeeId;

    @FXML
    public void initialize() {
        ObservableList<Integer> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) months.add(i);
        monthComboBox.setItems(months);

        int currentMonth = LocalDate.now().getMonthValue();
        monthComboBox.getSelectionModel().select(Integer.valueOf(currentMonth));

        loadEmployees();
        loadScheduleTemplates();

        colEmployeeId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeId()));
        colEmployeeName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeName()));
        colTemplate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTemplateName()));

        monthComboBox.setOnAction(a -> reload());
        employeeComboBox.setOnAction(a -> reload());

        if (selectedEmployeeId != null) applySelection();
    }

    public void setSelectedEmployeeId(String id) {
        this.selectedEmployeeId = id;
        if (employeeComboBox.getItems().size() > 0) applySelection();
    }

    private void applySelection() {
        for (String item : employeeComboBox.getItems()) {
            if (item.startsWith(selectedEmployeeId + " -")) {
                employeeComboBox.getSelectionModel().select(item);
                break;
            }
        }
        reload();
    }

    private void loadEmployees() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT employee_id, firstname || ' ' || lastname FROM employee";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getInt(1) + " - " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        employeeComboBox.setItems(list);
    }

    private void loadScheduleTemplates() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT schedule_id, start_day, end_day, start_time, end_time FROM schedule";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getInt("schedule_id") + " - T" + rs.getInt("start_day") + "-T" + rs.getInt("end_day") +
                        " (" + rs.getTime("start_time") + "-" + rs.getTime("end_time") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        scheduleTemplateComboBox.setItems(list);
    }

    @FXML
    private void handleAssignSchedule(ActionEvent e) {
        if (employeeComboBox.getValue() == null || scheduleTemplateComboBox.getValue() == null || monthComboBox.getValue() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Chọn đủ thông tin").showAndWait();
            return;
        }
        assign();
        reload();
    }

    @FXML
    private void handleLoadAssignedSchedules() {
        reload();
    }

    private void assign() {
        int emp = Integer.parseInt(employeeComboBox.getValue().split(" -")[0]);
        int sch = Integer.parseInt(scheduleTemplateComboBox.getValue().split(" -")[0]);
        int m = monthComboBox.getValue();
        if (dpFirstDate.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ngày làm việc").showAndWait();
            return;
        }
        LocalDate selectedDate = dpFirstDate.getValue();

        String sel = "SELECT start_day, end_day FROM schedule WHERE schedule_id=?";
        String ins = "INSERT INTO working(employee_id, schedule_id, work_date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sel);
             PreparedStatement pi = conn.prepareStatement(ins)) {

            ps.setInt(1, sch);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pi.setInt(1, emp);
                    pi.setInt(2, sch);
                    pi.setDate(3, Date.valueOf(selectedDate));
                    pi.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void reload() {
        if (monthComboBox.getValue() == null) return;

        int m = monthComboBox.getValue();
        int y = LocalDate.now().getYear(); // bạn có thể sửa để chọn năm nếu muốn

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end = start.plusMonths(1); // đầu tháng kế tiếp

        // Load lịch đã gán
        ObservableList<AssignedSchedule> data = FXCollections.observableArrayList();
        String q = "SELECT DISTINCT e.employee_id, e.firstname || ' ' || e.lastname AS name, " +
                "'T' || s.start_day || '-T' || s.end_day || ' (' || s.start_time || '-' || s.end_time || ')' AS tmpl " +
                "FROM working w JOIN employee e USING(employee_id) JOIN schedule s USING(schedule_id) " +
                "WHERE work_date >= ? AND work_date < ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(new AssignedSchedule(
                            rs.getString("employee_id"),
                            rs.getString("name"),
                            rs.getString("tmpl")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        assignedScheduleTable.setItems(data);

        // THỐNG KÊ CA (Ca 1 - 4)
        String caQuery = """
        SELECT schedule_id, COUNT(DISTINCT employee_id) AS soluong
        FROM schedule
        JOIN working USING (schedule_id)
        WHERE work_date >= ? AND work_date < ?
        GROUP BY schedule_id
        """;

        ca1Label.setText("Ca 1: 0");
        ca2Label.setText("Ca 2: 0");
        ca3Label.setText("Ca 3: 0");
        ca4Label.setText("Ca 4: 0");

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(caQuery)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ca = rs.getInt("schedule_id");
                    int soLuong = rs.getInt("soluong");

                    switch (ca) {
                        case 1 -> ca1Label.setText("Ca 1: " + soLuong);
                        case 2 -> ca2Label.setText("Ca 2: " + soLuong);
                        case 3 -> ca3Label.setText("Ca 3: " + soLuong);
                        case 4 -> ca4Label.setText("Ca 4: " + soLuong);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    public static class AssignedSchedule {
        private final String employeeId, employeeName, templateName;

        public AssignedSchedule(String employeeId, String employeeName, String templateName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.templateName = templateName;
        }

        public String getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getTemplateName() { return templateName; }
    }
}