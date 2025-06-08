package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.*;

public class BatchWrapper {
    private final Batch batch;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty actualQuantity = new SimpleIntegerProperty();

    public BatchWrapper(Batch batch) {
        this.batch = batch;
        this.actualQuantity.set(batch.getTotalQuantity());
    }

    public Batch getBatch() {
        return batch;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public int getActualQuantity() {
        return actualQuantity.get();
    }

    public void setActualQuantity(int value) {
        actualQuantity.set(value);
    }

    public IntegerProperty actualQuantityProperty() {
        return actualQuantity;
    }
}
