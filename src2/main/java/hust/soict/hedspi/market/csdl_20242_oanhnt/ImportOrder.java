package hust.soict.hedspi.market.csdl_20242_oanhnt;
import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

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
    
 // Standard getters if needed
    public ObservableList<String> getBatches() {
        return batches.get();
    }

    public String getSupplier() {
        return supplier.get();
    }
}
