import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Report {
    private final StringProperty reportId = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty method = new SimpleStringProperty();
    private final ObjectProperty<Batch> batch = new SimpleObjectProperty<>();

    public Report(String reportId, LocalDate date, String method, Batch batch) {
        this.reportId.set(reportId);
        this.date.set(date);
        this.method.set(method);
        this.batch.set(batch);
    }

    public String getReportId() { return reportId.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getMethod() { return method.get(); }
    public Batch getBatch() { return batch.get(); }

    public StringProperty reportIdProperty() { return reportId; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty methodProperty() { return method; }
    public ObjectProperty<Batch> batchProperty() { return batch; }
}