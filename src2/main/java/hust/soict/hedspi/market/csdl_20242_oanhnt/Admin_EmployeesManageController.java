package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

public class Admin_EmployeesManageController {
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> colId, colName, colGender, colDob, colEmail, colPhone, colAddress, colIdCard;
    @FXML private TextField tfSearchField;
    @FXML private TextField tfId, tfName, tfGender, tfDob, tfEmail, tfPhone, tfAddress, tfIdCard;
    @FXML private Button btnAdd, btnMark;
    @FXML private DatePicker dpWorkDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lbPresent, lbLate, lbAbsent, lbSalary;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
        colDob.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDob()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        colIdCard.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdCard()));

        cbStatus.getItems().addAll("Có mặt", "Muộn", "Vắng");
        cbStatus.setValue("Có mặt");

        loadEmployeesFromDB();

        btnAdd.setOnAction(e -> addEmployee());
        btnMark.setOnAction(e -> markAttendance());

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) updateAttendanceSummary(newSel.getId());
        });

        tfSearchField.textProperty().addListener((obs, oldText, newText) -> searchEmployee(newText));
    }

    private void loadEmployeesFromDB() {
        employeeList.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employee");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        try {
            String firstname = tfName.getText().trim().split(" ")[0];
            String lastname = tfName.getText().trim().replaceFirst(firstname, "").trim();
            Date dob = Date.valueOf(tfDob.getText());
            String gender = tfGender.getText().trim();
            String email = tfEmail.getText().trim();
            String phone = tfPhone.getText().trim();
            String address = tfAddress.getText().trim();
            String idCard = tfIdCard.getText().trim();

            String sql = "INSERT INTO employee (firstname, lastname, dob, email, gender, address, phone, identity_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, firstname);
                stmt.setString(2, lastname);
                stmt.setDate(3, dob);
                stmt.setString(4, email);
                stmt.setString(5, gender);
                stmt.setString(6, address);
                stmt.setString(7, phone);
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
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        LocalDate date = dpWorkDate.getValue();
        String status = cbStatus.getValue();

        if (selected == null || date == null || status == null) return;

        try (Connection conn = DBConnection.getConnection()) {
            int empId = Integer.parseInt(selected.getId());

            // Schedule ID tạm giả định là 1
            String sql = "INSERT INTO working (employee_id, schedule_id, work_date, status) " +
                         "VALUES (?, ?, ?, ?) ON CONFLICT (employee_id, schedule_id, work_date) DO UPDATE SET status = EXCLUDED.status";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            stmt.setInt(2, 1); // giả sử ca mặc định
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, convertStatusToCode(status));
            stmt.executeUpdate();

            updateAttendanceSummary(selected.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAttendanceSummary(String employeeIdStr) {
        try (Connection conn = DBConnection.getConnection()) {
            int empId = Integer.parseInt(employeeIdStr);

            int present = countStatus(conn, empId, "D");
            int late = countStatus(conn, empId, "M");
            int absent = countStatus(conn, empId, "V");

            lbPresent.setText("Có mặt: " + present);
            lbLate.setText("Đi muộn: " + late);
            lbAbsent.setText("Vắng: " + absent);

            int salary = calculateSalary(present, late);
            lbSalary.setText("Lương tạm tính: " + salary + " VND");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countStatus(Connection conn, int empId, String statusCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM working WHERE employee_id = ? AND status = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, empId);
        stmt.setString(2, statusCode);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    private int calculateSalary(int present, int late) {
        int daily = 200_000;
        int latePenalty = 50_000;
        return present * daily + late * (daily - latePenalty);
    }

    private String convertStatusToCode(String status) {
        return switch (status) {
            case "Có mặt" -> "D";
            case "Muộn" -> "M";
            case "Vắng" -> "V";
            default -> "V";
        };
    }

    private void searchEmployee(String filter) {
        String lowerCaseFilter = filter.toLowerCase();
        ObservableList<Employee> filtered = employeeList.filtered(emp ->
                emp.getId().toLowerCase().contains(lowerCaseFilter) ||
                emp.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                emp.getGender().toLowerCase().contains(lowerCaseFilter) ||
                emp.getDob().toLowerCase().contains(lowerCaseFilter) ||
                emp.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                emp.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                emp.getAddress().toLowerCase().contains(lowerCaseFilter) ||
                emp.getIdCard().toLowerCase().contains(lowerCaseFilter)
        );
        employeeTable.setItems(filtered);
    }

    private void clearFields() {
        tfId.clear(); tfName.clear(); tfGender.clear(); tfDob.clear();
        tfEmail.clear(); tfPhone.clear(); tfAddress.clear(); tfIdCard.clear();
    }
}
