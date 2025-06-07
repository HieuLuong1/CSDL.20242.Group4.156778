package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.io.IOException;

public class Admin_EmployeesManageController {
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> colId, colName, colGender, colDob, colEmail, colPhone, colAddress, colIdCard;
    @FXML private TextField tfSearchField;
    @FXML private TextField tfName, tfGender, tfDob, tfEmail, tfPhone, tfAddress, tfIdCard;
    @FXML private Button btnAdd, btnMark, btnAddSchedule;
    @FXML private DatePicker dpWorkDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lbPresent, lbLate, lbAbsent, lbSalary;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Thiết lập các cột của bảng
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
        colDob.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDob()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        colIdCard.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdCard()));

        // Thiết lập ComboBox trạng thái điểm danh
        cbStatus.getItems().addAll("Có mặt", "Muộn", "Vắng");
        cbStatus.setValue("Có mặt");

        // Load dữ liệu nhân viên
        loadEmployeesFromDB();

        // Thiết lập các listener và sự kiện
        btnAdd.setOnAction(e -> addEmployee());
        btnMark.setOnAction(e -> markAttendance());
        btnAddSchedule.setOnAction(this::handleSchedule);

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) updateAttendanceSummary(newSel.getId());
        });

        tfSearchField.textProperty().addListener((obs, oldText, newText) -> searchEmployee(newText));
    }

    private void loadEmployeesFromDB() {
        employeeList.clear();
        String sql = "SELECT employee_id, firstname, lastname, dob, gender, email, phone, address, identity_id FROM employee";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("employee_id"));
                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                String dob = rs.getDate("dob").toString();
                String gender = rs.getString("gender");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String idCard = rs.getString("identity_id");
                employeeList.add(new Employee(id, fullName, dob, gender, email, phone, address, idCard));
            }
            employeeTable.setItems(employeeList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        try (Connection conn = DBConnection.getConnection()) {
            String[] parts = tfName.getText().trim().split(" ", 2);
            String firstname = parts[0];
            String lastname = parts.length > 1 ? parts[1] : "";
            Date dob = Date.valueOf(tfDob.getText());
            String gender = tfGender.getText().trim();
            String email = tfEmail.getText().trim();
            String phone = tfPhone.getText().trim();
            String address = tfAddress.getText().trim();
            String idCard = tfIdCard.getText().trim();

            String sql = "INSERT INTO employee (firstname, lastname, dob, gender, email, phone, address, identity_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, firstname);
                stmt.setString(2, lastname);
                stmt.setDate(3, dob);
                stmt.setString(4, gender);
                stmt.setString(5, email);
                stmt.setString(6, phone);
                stmt.setString(7, address);
                stmt.setString(8, idCard);
                stmt.executeUpdate();
            }
            loadEmployeesFromDB();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markAttendance() {
        Employee sel = employeeTable.getSelectionModel().getSelectedItem();
        LocalDate date = dpWorkDate.getValue();
        String status = cbStatus.getValue();
        if (sel == null || date == null || status == null) return;

        String sql = "INSERT INTO working(employee_id, schedule_id, work_date, status) VALUES (?, 1, ?, ?) " +
                     "ON CONFLICT(employee_id, schedule_id, work_date) DO UPDATE SET status = EXCLUDED.status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(sel.getId()));
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, convertStatusToCode(status));
            stmt.executeUpdate();
            updateAttendanceSummary(sel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateAttendanceSummary(String empId) {
        String countSql = "SELECT COUNT(*) FROM working WHERE employee_id = ? AND status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            int present = countByStatus(ps, empId, "D");
            int late    = countByStatus(ps, empId, "M");
            int absent  = countByStatus(ps, empId, "V");
            lbPresent.setText("Có mặt: " + present);
            lbLate.setText("Đi muộn: " + late);
            lbAbsent.setText("Vắng: " + absent);
            lbSalary.setText("Lương tạm tính: " + (present * 200000 + late * 150000) + " VND");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int countByStatus(PreparedStatement ps, String empId, String code) throws SQLException {
        ps.setInt(1, Integer.parseInt(empId));
        ps.setString(2, code);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String convertStatusToCode(String status) {
        return switch (status) {
            case "Có mặt" -> "D";
            case "Muộn"   -> "M";
            default        -> "V";
        };
    }

    private void searchEmployee(String filter) {
        String f = filter.toLowerCase();
        ObservableList<Employee> filtered = employeeList.filtered(emp ->
            emp.getId().toLowerCase().contains(f) || emp.getFullName().toLowerCase().contains(f)
        );
        employeeTable.setItems(filtered);
    }

    private void clearFields() {
        tfName.clear(); tfGender.clear(); tfDob.clear();
        tfEmail.clear(); tfPhone.clear(); tfAddress.clear(); tfIdCard.clear();
    }

    @FXML
    private void handleSchedule(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_AddSchedule.fxml"));
            Parent root = loader.load();
            Admin_AddScheduleController controller = loader.getController();
            Employee sel = employeeTable.getSelectionModel().getSelectedItem();
            if (sel != null) controller.setSelectedEmployeeId(sel.getId());
            Stage stage = new Stage();
            stage.setTitle("Lịch làm việc nhân viên");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}