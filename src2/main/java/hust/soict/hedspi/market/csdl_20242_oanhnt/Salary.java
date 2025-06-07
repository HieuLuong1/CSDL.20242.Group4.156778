package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class Salary {
    private String employeeId;    // có thể là mã hoặc số, giữ String
    private String salaryIdStr;   // ví dụ "SAL001"
    private int salaryId;         // id trong DB (int)
    private int year;
    private int month;
    private int basicSalary;
    private int workdays;
    private int bonus;            // allowance
    private int rewardPunish;     // adjustment
    private int leavePay;
    private int actualSalary;
    private String note;

    public Salary(String employeeId, String salaryIdStr, int salaryId, int year, int month,
                  int basicSalary, int workdays, int bonus, int rewardPunish,
                  int leavePay, String note) {
        this.employeeId = employeeId;
        this.salaryIdStr = salaryIdStr;
        this.salaryId = salaryId;
        this.year = year;
        this.month = month;
        this.basicSalary = basicSalary;
        this.workdays = workdays;
        this.bonus = bonus;
        this.rewardPunish = rewardPunish;
        this.leavePay = leavePay;
        this.note = note;
        updateActualSalary();
    }

    public void updateActualSalary() {
        // Tính lương thực nhận: công thức ví dụ
        this.actualSalary = (int)(basicSalary * workdays / 26.0 + bonus + rewardPunish - leavePay);
        if (this.actualSalary < 0) this.actualSalary = 0;
    }

    // --- Getter và Setter ---
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getSalaryIdStr() { return salaryIdStr; }
    public void setSalaryIdStr(String salaryIdStr) { this.salaryIdStr = salaryIdStr; }

    public int getSalaryId() { return salaryId; }
    public void setSalaryId(int salaryId) { this.salaryId = salaryId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getBasicSalary() { return basicSalary; }
    public void setBasicSalary(int basicSalary) { this.basicSalary = basicSalary; }

    public int getWorkdays() { return workdays; }
    public void setWorkdays(int workdays) { this.workdays = workdays; }

    public int getBonus() { return bonus; }
    public void setBonus(int bonus) { this.bonus = bonus; }

    public int getRewardPunish() { return rewardPunish; }
    public void setRewardPunish(int rewardPunish) { this.rewardPunish = rewardPunish; }

    public int getLeavePay() { return leavePay; }
    public void setLeavePay(int leavePay) { this.leavePay = leavePay; }

    public int getActualSalary() { return actualSalary; }
    public void setActualSalary(int actualSalary) { this.actualSalary = actualSalary; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
