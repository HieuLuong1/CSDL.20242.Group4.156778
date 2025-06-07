package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.*;
import java.time.LocalDate;

public class Admin_AddScheduleController {
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private ComboBox<String> scheduleTemplateComboBox;
    @FXML private TableView<AssignedSchedule> assignedScheduleTable;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeId;
    @FXML private TableColumn<AssignedSchedule, String> colEmployeeName;
    @FXML private TableColumn<AssignedSchedule, String> colTemplate;

    private String selectedEmployeeId;

    @FXML
    public void initialize() {
        ObservableList<Integer> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) months.add(i);
        monthComboBox.setItems(months);
        monthComboBox.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        loadEmployees();
        loadScheduleTemplates();

        colEmployeeId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeId()));
        colEmployeeName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeName()));
        colTemplate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTemplateName()));

        // Khi thay đổi tháng hoặc nhân viên
        monthComboBox.setOnAction(a -> reload());
        employeeComboBox.setOnAction(a -> reload());

        if (selectedEmployeeId != null) applySelection();
    }

    public void setSelectedEmployeeId(String id) {
        this.selectedEmployeeId = id;
        if (employeeComboBox.getItems().size() > 0) applySelection();
    }

    private void applySelection() {
        for (String it : employeeComboBox.getItems()) {
            if (it.startsWith(selectedEmployeeId + " -")) {
                employeeComboBox.getSelectionModel().select(it);
                break;
            }
        }
        reload();
    }

    private void loadEmployees() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT employee_id, firstname || ' ' || lastname FROM employee";
        try (Connection c = DBConnection.getConnection();
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
        try (Connection c = DBConnection.getConnection();
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
    private void handleLoadAssignedSchedules(ActionEvent e) {
        reload();
    }

    private void assign() {
        int emp = Integer.parseInt(employeeComboBox.getValue().split(" -")[0]);
        int sch = Integer.parseInt(scheduleTemplateComboBox.getValue().split(" -")[0]);
        int m = monthComboBox.getValue();
        int y = LocalDate.now().getYear();

        String sel = "SELECT start_day, end_day FROM schedule WHERE schedule_id=?";
        String ins = "INSERT INTO working(employee_id, schedule_id, work_date, status) " +
                     "VALUES (?, ?, ?, 'D') ON CONFLICT DO NOTHING";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sel);
             PreparedStatement pi = c.prepareStatement(ins)) {

            ps.setInt(1, sch);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int sd = rs.getInt(1), ed = rs.getInt(2);
                    LocalDate st = LocalDate.of(y, m, 1);
                    LocalDate en = st.withDayOfMonth(st.lengthOfMonth());
                    for (LocalDate d = st; !d.isAfter(en); d = d.plusDays(1)) {
                        int dayOfWeek = d.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
                        if (dayOfWeek >= sd && dayOfWeek <= ed) {
                            pi.setInt(1, emp);
                            pi.setInt(2, sch);
                            pi.setDate(3, Date.valueOf(d));
                            pi.addBatch();
                        }
                    }
                    pi.executeBatch();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void reload() {
        if (employeeComboBox.getValue() == null || monthComboBox.getValue() == null) return;

        int emp = Integer.parseInt(employeeComboBox.getValue().split(" -")[0]);
        int m = monthComboBox.getValue();

        ObservableList<AssignedSchedule> data = FXCollections.observableArrayList();
        String q = "SELECT DISTINCT e.employee_id, e.firstname || ' ' || e.lastname AS name, " +
                   "'T' || s.start_day || '-T' || s.end_day || ' (' || s.start_time || '-' || s.end_time || ')' AS tmpl " +
                   "FROM working w JOIN employee e USING(employee_id) JOIN schedule s USING(schedule_id) " +
                   "WHERE EXTRACT(MONTH FROM work_date) = ? AND w.employee_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, m);
            ps.setInt(2, emp);
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