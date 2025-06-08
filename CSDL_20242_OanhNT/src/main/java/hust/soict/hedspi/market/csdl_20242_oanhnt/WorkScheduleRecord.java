package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class WorkScheduleRecord {
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String status;

    public WorkScheduleRecord(String date, String startTime, String endTime, String status) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
}
