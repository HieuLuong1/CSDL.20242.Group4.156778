package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class WorkScheduleRecord {
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String status;
    private final String note;

    public WorkScheduleRecord(String date, String startTime, String endTime, String status, String note) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
    }

    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
}
