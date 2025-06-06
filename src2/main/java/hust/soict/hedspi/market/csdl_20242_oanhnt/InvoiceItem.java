package hust.soict.hedspi.market.csdl_20242_oanhnt;
public class InvoiceItem {
    private String product;
    private int quantity;
    private double unitPrice;
    private int batchId;       // Mã lô
    private String productName; // Tên sản phẩm
    private double total;
    public InvoiceItem(String product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public InvoiceItem(int batchId, String product, int quantity, double unitPrice, double total) {
        this.batchId = batchId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public int getBatchId() { return batchId; }
    public String getProductName() { return productName; }
    public double getTotal() { return total; }
}