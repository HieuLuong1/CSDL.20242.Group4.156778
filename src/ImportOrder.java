import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class ImportOrder {
    private final StringProperty id;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty address;
    private final DoubleProperty totalValue;
    private final ListProperty<String> batches;
    private final StringProperty supplier;

    public ImportOrder(String id, LocalDate date, String address, double totalValue,
                       ObservableList<String> batches, String supplier) {
        this.id = new SimpleStringProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.address = new SimpleStringProperty(address);
        this.totalValue = new SimpleDoubleProperty(totalValue);
        this.batches = new SimpleListProperty<>(batches);
        this.supplier = new SimpleStringProperty(supplier);
    }

    public StringProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty addressProperty() { return address; }
    public DoubleProperty totalValueProperty() { return totalValue; }
    public ListProperty<String> batchesProperty() { return batches; }
    public StringProperty supplierProperty() { return supplier; }
}
