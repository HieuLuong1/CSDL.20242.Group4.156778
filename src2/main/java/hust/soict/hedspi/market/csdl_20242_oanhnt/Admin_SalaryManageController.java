package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

/**
 * Controller cho màn hình Quản lý Lương (Salary).
 * - Load dữ liệu trực tiếp từ bảng `salary`.
 * - Filter theo Tháng/Năm.
 * - Thêm bản ghi mới vào DB khi bấm Add New.
 * - Chỉnh sửa trực tiếp; mỗi lần commit sẽ UPDATE ngược về DB.
 */
public class Admin_SalaryManageController {
    @FXML private ComboBox<Integer> cbMonth;
    @FXML private ComboBox<Integer> cbYear;
    @FXML private Button btnFilter;
    @FXML private Button btnAddNew;

    @FXML private TableView<Salary> salaryTable;
    @FXML private TableColumn<Salary, Integer> colId;
    @FXML private TableColumn<Salary, String>  colEmployeeId;
    @FXML private TableColumn<Salary, Integer> colBasic;
    @FXML private TableColumn<Salary, Integer> colWorkdays;
    @FXML private TableColumn<Salary, Integer> colBonus;    // tương ứng với allowance
    @FXML private TableColumn<Salary, Integer> colReward;   // tương ứng với adjustment
    @FXML private TableColumn<Salary, Integer> colLeave;    // tương ứng với leave_pay
    @FXML private TableColumn<Salary, Integer> colActual;
    @FXML private TableColumn<Salary, String>  colNote;

    private final ObservableList<Salary> salaryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Thiết lập ComboBox Tháng/Năm
        cbMonth.setItems(FXCollections.observableArrayList(getMonths()));
        cbYear.setItems(FXCollections.observableArrayList(getYears()));
        cbMonth.setValue(LocalDate.now().getMonthValue());
        cbYear.setValue(LocalDate.now().getYear());

        // 2. Thiết lập CellValueFactory cho từng cột
        colId.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colBasic.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        colWorkdays.setCellValueFactory(new PropertyValueFactory<>("workdays"));
        colBonus.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        colReward.setCellValueFactory(new PropertyValueFactory<>("rewardPunish"));
        colLeave.setCellValueFactory(new PropertyValueFactory<>("leavePay"));
        colActual.setCellValueFactory(new PropertyValueFactory<>("actualSalary"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        salaryTable.setItems(salaryList);
        salaryTable.setEditable(true);
        enableEditing();

        // 3. Load dữ liệu từ DB
        loadSalariesFromDB();

        // 4. Gán hành động cho Filter và Add New
        btnFilter.setOnAction(e -> filterSalaries());
        btnAddNew.setOnAction(e -> addEmptyRowToDB());
    }

    private List<Integer> getMonths() {
        List<Integer> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) months.add(i);
        return months;
    }

    private List<Integer> getYears() {
        List<Integer> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) years.add(i);
        return years;
    }

    /**
     * Load tất cả bản ghi từ bảng `salary` và đổ vào salaryList.
     * Tách cột monthyear ("MM/YYYY") thành month và year.
     */
    private void loadSalariesFromDB() {
        salaryList.clear();
        String sql = ""
            + "SELECT salary_id, monthyear, basic_salary, workdays, allowance, adjustment, leave_pay, actual_salary, note, employee_id "
            + "FROM salary ORDER BY salary_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int salaryId      = rs.getInt("salary_id");
                String monthYear  = rs.getString("monthyear");   // ví dụ "6/2025"
                double basicD     = rs.getDouble("basic_salary");
                int    workdays   = rs.getInt("workdays");
                double bonusD     = rs.getDouble("allowance");
                double rewardD    = rs.getDouble("adjustment");
                double leaveD     = rs.getDouble("leave_pay");
                double actualD    = rs.getDouble("actual_salary");
                String note       = rs.getString("note");
                int    empIdInt   = rs.getInt("employee_id");

                // Tách monthYear thành month và year
                String[] parts = monthYear.split("/");
                int month = Integer.parseInt(parts[0]);
                int year  = Integer.parseInt(parts[1]);

                int basicSalary  = (int) basicD;
                int bonus        = (int) bonusD;
                int rewardPunish = (int) rewardD;
                int leavePay     = (int) leaveD;
                int actualSalary = (int) actualD;
                String employeeId = (empIdInt == 0 ? "" : String.valueOf(empIdInt));
                String salaryIdStr = "SAL" + String.format("%03d", salaryId);

                Salary s = new Salary(
                    employeeId,
                    salaryIdStr,
                    salaryId,
                    year,
                    month,
                    basicSalary,
                    workdays,
                    bonus,
                    rewardPunish,
                    leavePay,
                    note == null ? "" : note
                );
                s.setActualSalary(actualSalary);
                salaryList.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cho phép chỉnh sửa trực tiếp; mỗi khi commit, cập nhật ngược về DB.
     */
    private void enableEditing() {
        // 1. Employee ID (String)
        colEmployeeId.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmployeeId.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            String newEmp = event.getNewValue().trim();
            s.setEmployeeId(newEmp);

            if (newEmp.isEmpty()) {
                updateOneColumnInDB(s.getSalaryId(), "employee_id", null);
            } else {
                try {
                    updateOneColumnInDB(s.getSalaryId(), "employee_id", Integer.parseInt(newEmp));
                } catch (NumberFormatException ex) {
                    s.setEmployeeId("");
                }
            }
            salaryTable.refresh();
        });

        // 2. Basic Salary
        colBasic.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colBasic.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newBasic = event.getNewValue();
            s.setBasicSalary(newBasic);
            s.updateActualSalary();
            updateOneColumnInDB(s.getSalaryId(), "basic_salary", newBasic);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", s.getActualSalary());
            salaryTable.refresh();
        });

        // 3. Workdays
        colWorkdays.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colWorkdays.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newWorkdays = event.getNewValue();
            s.setWorkdays(newWorkdays);
            s.updateActualSalary();
            updateOneColumnInDB(s.getSalaryId(), "workdays", newWorkdays);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", s.getActualSalary());
            salaryTable.refresh();
        });

        // 4. Bonus (Allowance)
        colBonus.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colBonus.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newBonus = event.getNewValue();
            s.setBonus(newBonus);
            s.updateActualSalary();
            updateOneColumnInDB(s.getSalaryId(), "allowance", newBonus);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", s.getActualSalary());
            salaryTable.refresh();
        });

        // 5. Reward/Punish (Adjustment)
        colReward.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colReward.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newAdj = event.getNewValue();
            s.setRewardPunish(newAdj);
            s.updateActualSalary();
            updateOneColumnInDB(s.getSalaryId(), "adjustment", newAdj);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", s.getActualSalary());
            salaryTable.refresh();
        });

        // 6. Leave Pay
        colLeave.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colLeave.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newLeave = event.getNewValue();
            s.setLeavePay(newLeave);
            s.updateActualSalary();
            updateOneColumnInDB(s.getSalaryId(), "leave_pay", newLeave);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", s.getActualSalary());
            salaryTable.refresh();
        });

        // 7. Actual Salary (nếu cho phép edit)
        colActual.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colActual.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            int newActual = event.getNewValue();
            s.setActualSalary(newActual);
            updateOneColumnInDB(s.getSalaryId(), "actual_salary", newActual);
            salaryTable.refresh();
        });

        // 8. Note
        colNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colNote.setOnEditCommit(event -> {
            Salary s = event.getRowValue();
            String newNote = event.getNewValue();
            s.setNote(newNote);
            updateOneColumnInDB(s.getSalaryId(), "note", newNote);
            salaryTable.refresh();
        });
    }

    /**
     * Filter theo Tháng/Năm: query lại DB chỉ lấy các bản ghi matching monthyear.
     */
    private void filterSalaries() {
        int month = cbMonth.getValue();
        int year  = cbYear.getValue();
        String monthYear = month + "/" + year;

        String sql = ""
            + "SELECT salary_id, monthyear, basic_salary, workdays, allowance, adjustment, leave_pay, actual_salary, note, employee_id "
            + "FROM salary WHERE monthyear = ? ORDER BY salary_id";

        ObservableList<Salary> filtered = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, monthYear);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int   salaryId    = rs.getInt("salary_id");
                double basicD     = rs.getDouble("basic_salary");
                int    workdays   = rs.getInt("workdays");
                double bonusD     = rs.getDouble("allowance");
                double rewardD    = rs.getDouble("adjustment");
                double leaveD     = rs.getDouble("leave_pay");
                double actualD    = rs.getDouble("actual_salary");
                String note       = rs.getString("note");
                int    empIdInt   = rs.getInt("employee_id");

                int basicSalary    = (int) basicD;
                int bonus          = (int) bonusD;
                int rewardPunish   = (int) rewardD;
                int leavePay       = (int) leaveD;
                int actualSalary   = (int) actualD;
                String employeeId  = (empIdInt == 0 ? "" : String.valueOf(empIdInt));
                String salaryIdStr = "SAL" + String.format("%03d", salaryId);

                Salary s = new Salary(
                    employeeId,
                    salaryIdStr,
                    salaryId,
                    year,
                    month,
                    basicSalary,
                    workdays,
                    bonus,
                    rewardPunish,
                    leavePay,
                    note == null ? "" : note
                );
                s.setActualSalary(actualSalary);
                filtered.add(s);
            }
            rs.close();
            salaryTable.setItems(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thêm một bản ghi Salary mới với giá trị mặc định vào DB, rồi thêm vào salaryList.
     * monthyear = cbMonth/cbYear; các numeric = 0; employee_id = NULL; note = "".
     */
    private void addEmptyRowToDB() {
        int month = cbMonth.getValue();
        int year  = cbYear.getValue();
        String monthYear = month + "/" + year;

        String insertSql = ""
            + "INSERT INTO salary(monthyear, basic_salary, workdays, allowance, note, adjustment, leave_pay, actual_salary, employee_id) "
            + "VALUES (?, 0, 0, 0, '', 0, 0, 0, NULL) RETURNING salary_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setString(1, monthYear);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt("salary_id");
                String salaryIdStr = "SAL" + String.format("%03d", newId);

                Salary s = new Salary(
                    "",                 // employeeId rỗng
                    salaryIdStr,
                    newId,
                    year,
                    month,
                    0,                  // basicSalary
                    0,                  // workdays
                    0,                  // bonus
                    0,                  // rewardPunish
                    0,                  // leavePay
                    ""                  // note
                );
                s.setActualSalary(0);
                salaryList.add(s);
                salaryTable.refresh();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật một cột duy nhất trong bảng salary cho salary_id = id.
     */
    private void updateOneColumnInDB(int id, String column, Object newValue) {
        String sql = "UPDATE salary SET " + column + " = ? WHERE salary_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (newValue == null) {
                ps.setNull(1, Types.INTEGER);
            } else if (newValue instanceof Integer) {
                ps.setInt(1, (Integer) newValue);
            } else {
                ps.setString(1, newValue.toString());
            }
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
