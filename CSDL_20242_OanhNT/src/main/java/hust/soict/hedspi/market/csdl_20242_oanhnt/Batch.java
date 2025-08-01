package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.time.LocalDate;

public class Batch {
    private int batchId;
    private LocalDate importDate;
    private LocalDate expiryDate;
    private int totalQuantity;
    private int quantityInStock;
    private String productName;
    private String supplier;
    private double cost;

    public Batch(int batchId, LocalDate importDate, LocalDate expiryDate,
                 int totalQuantity, int quantityInStock, String productName, String supplier, double cost) {
        this.batchId = batchId;
        this.importDate = importDate;
        this.expiryDate = expiryDate;
        this.totalQuantity = totalQuantity;
        this.quantityInStock = quantityInStock;
        this.productName = productName;
        this.supplier = supplier;
        this.cost = cost;
    }

    public int getBatchId() {
        return batchId;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getProductName() {
        return productName;
    }

    public String getSupplier() {
        return supplier;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}