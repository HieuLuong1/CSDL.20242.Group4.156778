import java.util.List;

public class Invoice {
    private String id;
    private String date;
    private String paymentMethod;
    private List<InvoiceItem> items;

    public Invoice(String id, String date, String paymentMethod, List<InvoiceItem> items) {
        this.id = id;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }
    public String getPaymentMethod() { return paymentMethod; }
    public String getId() { return id; }
    public String getDate() { return date; }
    public double getTotal() {
        return items.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    }
    public List<InvoiceItem> getItems() { return items; }
}
