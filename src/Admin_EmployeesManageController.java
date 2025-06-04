import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    private final Map<String, WorkScheduleRecord> workMap = new HashMap<>();

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

        employeeList.addAll(
                new Employee("NV001", "Nguyen Van A", "01/01/1990", "Nam", "a@gmail.com", "0123456789", "Hanoi", "0011001100"),
                new Employee("NV002", "Tran Thi B", "12/03/1995", "Nu", "b@gmail.com", "0987654321", "HCM", "0022002200")
        );
        employeeTable.setItems(employeeList);

        workMap.put("NV001", new WorkScheduleRecord("NV001", 20, 3, 2));
        workMap.put("NV002", new WorkScheduleRecord("NV002", 18, 1, 3));

        cbStatus.getItems().addAll("Có mặt", "Muộn", "Vắng");
        cbStatus.setValue("Có mặt");

        btnAdd.setOnAction(e -> {
            String id = tfId.getText();
            String name = tfName.getText();
            if (id.isEmpty() || name.isEmpty()) return;
            if (employeeList.stream().anyMatch(emp -> emp.getId().equals(id))) return; // tránh trùng ID

            Employee emp = new Employee(id, name, tfDob.getText(), tfGender.getText(), tfEmail.getText(), tfPhone.getText(), tfAddress.getText(), tfIdCard.getText());
            employeeList.add(emp);
            workMap.put(id, new WorkScheduleRecord(id, 0, 0, 0));
            clearFields();
        });

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) updateAttendanceSummary(newSel.getId());
        });

        btnMark.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected == null || dpWorkDate.getValue() == null || cbStatus.getValue() == null) return;

            String id = selected.getId();
            WorkScheduleRecord record = workMap.getOrDefault(id, new WorkScheduleRecord(id, 0, 0, 0));

            switch (cbStatus.getValue()) {
                case "Có mặt" -> record.incrementPresent();
                case "Muộn" -> record.incrementLate();
                case "Vắng" -> record.incrementAbsent();
            }
            workMap.put(id, record);
            updateAttendanceSummary(id);
        });
        tfSearchField.textProperty().addListener((obs, oldText, newText) -> {
            String lowerCaseFilter = newText.toLowerCase();

            ObservableList<Employee> filteredList = employeeList.filtered(emp ->
                    emp.getId().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getGender().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getDob().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getAddress().toLowerCase().contains(lowerCaseFilter) ||
                            emp.getIdCard().toLowerCase().contains(lowerCaseFilter)
            );

            employeeTable.setItems(filteredList);
        });
    }

    private void updateAttendanceSummary(String employeeId) {
        WorkScheduleRecord record = workMap.getOrDefault(employeeId, new WorkScheduleRecord(employeeId, 0, 0, 0));
        lbPresent.setText("Có mặt: " + record.getPresent());
        lbLate.setText("Đi muộn: " + record.getLate());
        lbAbsent.setText("Vắng: " + record.getAbsent());

        int salary = calculateSalary(record);
        lbSalary.setText("Lương tạm tính: " + salary + " VND");
    }

    private int calculateSalary(WorkScheduleRecord record) {
        int daily = 200_000;
        int latePenalty = 50_000;
        return record.getPresent() * daily + record.getLate() * (daily - latePenalty);
    }

    private void clearFields() {
        tfId.clear(); tfName.clear(); tfGender.clear(); tfDob.clear();
        tfEmail.clear(); tfPhone.clear(); tfAddress.clear(); tfIdCard.clear();
    }
}