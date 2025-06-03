public class WorkScheduleRecord {
    private String employeeId;

    // Dùng cho từng ngày
    private String date;
    private String shift;
    private String startTime;
    private String endTime;
    private String status;   // "Có mặt", "Muộn", "Vắng"
    private String note;
    private int salaryEarned;

    // Dùng cho tổng hợp tháng
    private int present;
    private int late;
    private int absent;

    // Constructor lịch làm việc từng ngày
    public WorkScheduleRecord(String date, String shift, String startTime, String endTime, String status, String note, int salaryEarned) {
        this.date = date;
        this.shift = shift;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
        this.salaryEarned = salaryEarned;
    }

    // Constructor tổng hợp tháng
    public WorkScheduleRecord(String employeeId, int present, int late, int absent) {
        this.employeeId = employeeId;
        this.present = present;
        this.late = late;
        this.absent = absent;
        updateSalary();
    }

    // Tính lại lương
    private void updateSalary() {
        int daily = 200_000;
        int latePenalty = 50_000;
        this.salaryEarned = present * daily + late * (daily - latePenalty);
    }

    // Tăng số buổi
    public void incrementPresent() {
        this.present++;
        updateSalary();
    }

    public void incrementLate() {
        this.late++;
        updateSalary();
    }

    public void incrementAbsent() {
        this.absent++;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public String getShift() { return shift; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public int getSalaryEarned() { return salaryEarned; }

    public int getPresent() { return present; }
    public int getLate() { return late; }
    public int getAbsent() { return absent; }
}
