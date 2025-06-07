package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.*;

public class BatchWrapper {
    private final Batch batch;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty actualQuantity = new SimpleIntegerProperty();

    // Bindings to properties in Batch for TableView usage
    private final IntegerProperty batchId;
    private final StringProperty productName;
    private final StringProperty supplier;
    private final IntegerProperty totalQuantity;

    public BatchWrapper(Batch batch) {
        this.batch = batch;
        this.actualQuantity.set(batch.getTotalQuantity());

        this.batchId = new SimpleIntegerProperty(batch.getBatchId());
        this.productName = new SimpleStringProperty(batch.getProductName());
        this.supplier = new SimpleStringProperty(batch.getSupplier());
        this.totalQuantity = new SimpleIntegerProperty(batch.getTotalQuantity());
    }

    public Batch getBatch() {
        return batch;
    }

    // --- Property for checkbox column ---
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // --- Property for editable actual quantity column ---
    public IntegerProperty actualQuantityProperty() {
        return actualQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity.get();
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity.set(actualQuantity);
    }

    // --- Expose batch info via property wrappers for TableView ---

    public IntegerProperty batchIdProperty() {
        return batchId;
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public StringProperty supplierProperty() {
        return supplier;
    }

    public IntegerProperty totalQuantityProperty() {
        return totalQuantity;
    }
}