package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.time.LocalDate;

public class Batch {
    private int batchId;
    private LocalDate importDate;
    private LocalDate expiryDate;
    private int totalQuantity;
    private int soldQuantity;
    private String productName;
    private String supplier;

    public Batch(int batchId, LocalDate importDate, LocalDate expiryDate,
                 int totalQuantity, int soldQuantity, String productName, String supplier) {
        this.batchId = batchId;
        this.importDate = importDate;
        this.expiryDate = expiryDate;
        this.totalQuantity = totalQuantity;
        this.soldQuantity = soldQuantity;
        this.productName = productName;
        this.supplier = supplier;
    }

    public int getBatchId() { return batchId; }
    public LocalDate getImportDate() { return importDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getSoldQuantity() { return soldQuantity; }
    public String getProductName() { return productName; }
    public String getSupplier() { return supplier; }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}
