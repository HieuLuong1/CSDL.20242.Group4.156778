package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Employee_SalaryController implements Initializable {
    @FXML
    private TableView<Salary> tableView;

    @FXML
    private TableColumn<Salary, String> colID;             // Mã NV (employee_id)
    @FXML
    private TableColumn<Salary, Integer> colSalary;         // Mã lương (salary_id)
    @FXML
    private TableColumn<Salary, Integer> colBasicSalary;     // Lương cơ bản (basic_salary)
    @FXML
    private TableColumn<Salary, Integer> colBonus;           // Phụ cấp (allowance)
    @FXML
    private TableColumn<Salary, Integer> colWorkdate;       // Ngày công (workdays)
    @FXML
    private TableColumn<Salary, Integer> colRewardPunish;    // Thưởng phạt (adjustment)
    @FXML
    private TableColumn<Salary, String> colNote;            // Lí do (note)
    @FXML
    private TableColumn<Salary, Integer> colLeave;           // Tiền Phép (leave_pay)
    @FXML
    private TableColumn<Salary, Integer> colTotal;           // Tổng lương (actual_salary)

    private int filterMonth;
    private int filterYear;

    private String employeeId;

    public void setEmployeeId(String id) {
        this.employeeId = id;
    }

    // Thêm hàm này để nhận chuỗi tháng/năm dạng "5/2025" hoặc "11/2025"
    public void setFilterMonthYearFromString(String monthYearStr) {
        if (monthYearStr == null || monthYearStr.isEmpty()) return;
        String[] parts = monthYearStr.split("/");
        if (parts.length != 2) return;

        try {
            int month = Integer.parseInt(parts[0].trim());
            int year = Integer.parseInt(parts[1].trim());
            setFilterMonthYear(month, year);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Giữ nguyên method này, chỉ gọi filterSalaries()
    public void setFilterMonthYear(int month, int year) {
        this.filterMonth = month;
        this.filterYear = year;
        filterSalaries();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Liên kết cột với thuộc tính trong lớp Salary
        colID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        colBasicSalary.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        colBonus.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        colWorkdate.setCellValueFactory(new PropertyValueFactory<>("workdays"));
        colRewardPunish.setCellValueFactory(new PropertyValueFactory<>("rewardPunish"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colLeave.setCellValueFactory(new PropertyValueFactory<>("leavePay"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("actualSalary"));
    }

    private void filterSalaries() {
        if (filterMonth == 0 || filterYear == 0 || employeeId == null || employeeId.isEmpty()) return;

        // Chuẩn hóa tháng sang 2 chữ số để truy vấn đúng định dạng
        String monthYear = filterMonth + "/" + filterYear;

        String sql = "SELECT salary_id, monthyear, basic_salary, workdays, allowance, adjustment, leave_pay, actual_salary, note, employee_id "
                + "FROM salary WHERE employee_id = ? AND monthyear = ?";

        ObservableList<Salary> filtered = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(employeeId));
            ps.setString(2, monthYear);

            System.out.println("SQL query: " + sql);
            System.out.println("employeeId = " + employeeId);
            System.out.println("monthYear = " + monthYear);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String monthYearStr = rs.getString("monthyear");
                String[] parts = monthYearStr.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]);

                Salary s = new Salary(
                        rs.getString("employee_id"),
                        rs.getInt("salary_id"),
                        year,
                        month,
                        rs.getInt("basic_salary"),
                        rs.getInt("workdays"),
                        rs.getInt("allowance"),
                        rs.getInt("adjustment"),
                        rs.getInt("leave_pay"),
                        rs.getString("note"),
                        rs.getInt("actual_salary")
                );
                filtered.add(s);
            }
            tableView.setItems(filtered);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
