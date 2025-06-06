package hust.soict.hedspi.market.csdl_20242_oanhnt;
import javafx.beans.property.*;

public class Salary {
	private final StringProperty employeeId = new SimpleStringProperty();
	private final StringProperty salaryIdStr = new SimpleStringProperty();
	private final IntegerProperty salaryId = new SimpleIntegerProperty();
	private final IntegerProperty year = new SimpleIntegerProperty();
	private final IntegerProperty month = new SimpleIntegerProperty();
	private final IntegerProperty basicSalary = new SimpleIntegerProperty();
	private final IntegerProperty workdays = new SimpleIntegerProperty();
	private final IntegerProperty bonus = new SimpleIntegerProperty();
	private final IntegerProperty rewardPunish = new SimpleIntegerProperty();
	private final IntegerProperty leavePay = new SimpleIntegerProperty();
	private final IntegerProperty actualSalary = new SimpleIntegerProperty();
	private final StringProperty note = new SimpleStringProperty();

	public Salary(String employeeId, String salaryIdStr, int salaryId,
	             int year, int month,
	             int basicSalary, int workdays, int bonus,
	             int rewardPunish, int leavePay, String note) {
		this.employeeId.set(employeeId);
		this.salaryIdStr.set(salaryIdStr);
		this.salaryId.set(salaryId);
		this.year.set(year);
		this.month.set(month);
		this.basicSalary.set(basicSalary);
		this.workdays.set(workdays);
		this.bonus.set(bonus);
		this.rewardPunish.set(rewardPunish);
		this.leavePay.set(leavePay);
		this.note.set(note);
		updateActualSalary();
	}

	public void updateActualSalary() {
		int result = basicSalary.get() * workdays.get() + bonus.get() + rewardPunish.get() + leavePay.get();
		this.actualSalary.set(result);
	}

	public void setBasicSalary(int value) {
		basicSalary.set(value);
		updateActualSalary();
	}
	public void setWorkdays(int value) {
		workdays.set(value);
		updateActualSalary();
	}
	public void setBonus(int value) {
		bonus.set(value);
		updateActualSalary();
	}
	public void setRewardPunish(int value) {
		rewardPunish.set(value);
		updateActualSalary();
	}
	public void setLeavePay(int value) {
		leavePay.set(value);
		updateActualSalary();
	}

	public void setEmployeeId(String value) { employeeId.set(value); }
	public void setSalaryIdStr(String value) { salaryIdStr.set(value); }
	public void setSalaryId(int value) { salaryId.set(value); }
	public void setNote(String value) { note.set(value); }
	public void setYear(int value) { year.set(value); }
	public void setMonth(int value) { month.set(value); }

	public String getEmployeeId() { return employeeId.get(); }
	public String getSalaryIdStr() { return salaryIdStr.get(); }
	public int getSalaryId() { return salaryId.get(); }
	public int getYear() { return year.get(); }
	public int getMonth() { return month.get(); }
	public int getBasicSalary() { return basicSalary.get(); }
	public int getWorkdays() { return workdays.get(); }
	public int getBonus() { return bonus.get(); }
	public int getRewardPunish() { return rewardPunish.get(); }
	public int getLeavePay() { return leavePay.get(); }
	public int getActualSalary() { return actualSalary.get(); }
	public String getNote() { return note.get(); }

	public StringProperty employeeIdProperty() { return employeeId; }
	public StringProperty salaryIdStrProperty() { return salaryIdStr; }
	public IntegerProperty salaryIdProperty() { return salaryId; }
	public IntegerProperty yearProperty() { return year; }
	public IntegerProperty monthProperty() { return month; }
	public IntegerProperty basicSalaryProperty() { return basicSalary; }
	public IntegerProperty workdaysProperty() { return workdays; }
	public IntegerProperty bonusProperty() { return bonus; }
	public IntegerProperty rewardPunishProperty() { return rewardPunish; }
	public IntegerProperty leavePayProperty() { return leavePay; }
	public IntegerProperty actualSalaryProperty() { return actualSalary; }
	public StringProperty noteProperty() { return note; }
}
