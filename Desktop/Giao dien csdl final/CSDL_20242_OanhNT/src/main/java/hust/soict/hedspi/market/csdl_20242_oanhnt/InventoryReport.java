package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.time.LocalDate;
import java.util.List;

public class InventoryReport {
    private String reportId;
    private LocalDate date;
    private List<Batch> batches;
    private List<Integer> actualQuantities;

    public InventoryReport(String reportId, LocalDate date, List<Batch> batches, List<Integer> actualQuantities) {
        this.reportId = reportId;
        this.date = date;
        this.batches = batches;
        this.actualQuantities = actualQuantities;
    }

    public String getReportId() { return reportId; }
    public LocalDate getDate() { return date; }
    public List<Batch> getBatches() { return batches; }
    public List<Integer> getActualQuantities() { return actualQuantities; }

    public int getActualQuantityOfBatch(Batch batch) {
        int index = batches.indexOf(batch);
        if (index >= 0 && index < actualQuantities.size()) {
            return actualQuantities.get(index);
        }
        return 0;
    }
}