package hust.soict.hedspi.market.csdl_20242_oanhnt;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
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

public class Admin_SalaryManageController {
	@FXML private ComboBox<Integer> cbMonth;
	@FXML private ComboBox<Integer> cbYear;
	@FXML private Button btnFilter, btnAddNew;
	@FXML private TableView<Salary> salaryTable;
	@FXML private TableColumn<Salary, Integer> colId, colBasic, colWorkdays, colBonus, colReward, colLeave, colActual;
	@FXML private TableColumn<Salary, String> colNote;
	@FXML private TableColumn<Salary, String> colEmployeeId;
	@FXML private TableColumn<Salary, Void> colEdit;

	private final ObservableList<Salary> salaryList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		cbMonth.setItems(FXCollections.observableArrayList(getMonths()));
		cbYear.setItems(FXCollections.observableArrayList(getYears()));
		cbMonth.setValue(LocalDate.now().getMonthValue());
		cbYear.setValue(LocalDate.now().getYear());

		colId.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
		colBasic.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
		colWorkdays.setCellValueFactory(new PropertyValueFactory<>("workdays"));
		colBonus.setCellValueFactory(new PropertyValueFactory<>("bonus"));
		colReward.setCellValueFactory(new PropertyValueFactory<>("rewardPunish"));
		colLeave.setCellValueFactory(new PropertyValueFactory<>("leavePay"));
		colActual.setCellValueFactory(new PropertyValueFactory<>("actualSalary"));
		colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
		colNote.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));

		salaryTable.setItems(salaryList);

		btnFilter.setOnAction(e -> filterSalaries());
		btnAddNew.setOnAction(e -> addEmptyRow());
		salaryTable.setEditable(true);
		enableEditing();
		salaryList.addAll(
			    new Salary("101", "SAL001", 1, 2025, 6, 5000000, 22, 1000000, 0, 0, ""),
			    new Salary("102", "SAL002", 2, 2025, 6, 4500000, 20, 800000, -500000, 0, "Đi trễ 2 ngày"),
			    new Salary("103", "SAL003", 3, 2025, 4, 4800000, 21, 500000, 0, 0, ""),
			    new Salary("104", "SAL004", 4, 2025, 4, 4700000, 19, 400000, -300000, 0, "Đền bù thiệt hại"),
			    new Salary("105", "SAL005", 5, 2025, 5, 5100000, 23, 700000, 0, 0, ""),
			    new Salary("106", "SAL006", 6, 2025, 7, 5200000, 24, 900000, 0, 0, "Thưởng thêm")
			);
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

	private void filterSalaries() {
		int month = cbMonth.getValue();
		int year = cbYear.getValue();
		salaryTable.setItems(salaryList.filtered(s -> s.getMonth() == month && s.getYear() == year));
	}

	private void addEmptyRow() {
		int nextId = salaryList.size() + 1;
		Salary newSalary = new Salary(
				"", "SAL" + String.format("%03d", nextId), nextId,
				cbYear.getValue(), cbMonth.getValue(),
				0, 0, 0, 0, 0, ""
				);
		salaryList.add(newSalary);
	}

	private void enableEditing() {
		colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colId.setOnEditCommit(e -> e.getRowValue().setSalaryId(e.getNewValue()));

		colEmployeeId.setCellFactory(TextFieldTableCell.forTableColumn());
		colEmployeeId.setOnEditCommit(e -> {
			e.getRowValue().setEmployeeId(e.getNewValue());
		});

		colBasic.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colBasic.setOnEditCommit(e -> {
			e.getRowValue().setBasicSalary(e.getNewValue());
			e.getRowValue().updateActualSalary();
			salaryTable.refresh();
		});

		colWorkdays.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colWorkdays.setOnEditCommit(e -> {
			e.getRowValue().setWorkdays(e.getNewValue());
			e.getRowValue().updateActualSalary();
			salaryTable.refresh();
		});

		colBonus.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colBonus.setOnEditCommit(e -> {
			e.getRowValue().setBonus(e.getNewValue());
			e.getRowValue().updateActualSalary();
			salaryTable.refresh();
		});

		colReward.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colReward.setOnEditCommit(e -> {
			e.getRowValue().setRewardPunish(e.getNewValue());
			e.getRowValue().updateActualSalary();
			salaryTable.refresh();
		});

		colLeave.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colLeave.setOnEditCommit(e -> {
			e.getRowValue().setLeavePay(e.getNewValue());
			e.getRowValue().updateActualSalary();
			salaryTable.refresh();
		});

		colActual.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		colNote.setCellFactory(TextFieldTableCell.forTableColumn());
		colNote.setOnEditCommit(e -> e.getRowValue().setNote(e.getNewValue()));
	}

}
