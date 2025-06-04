import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.List;

public class Report {
	private final StringProperty reportId = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final List<Batch> batches;

    public Report(String reportId, LocalDate date, List<Batch> batches) {
        this.reportId.set(reportId);
        this.date.set(date);
        this.batches = batches;
    }

    public String getReportId() { return reportId.get(); }
    public LocalDate getDate() { return date.get(); }
    public List<Batch> getBatches() { return batches; }

    public StringProperty reportIdProperty() { return reportId; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
} 