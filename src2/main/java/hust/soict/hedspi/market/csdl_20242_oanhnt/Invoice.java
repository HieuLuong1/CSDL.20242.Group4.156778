package hust.soict.hedspi.market.csdl_20242_oanhnt;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String id;
    private String date;
    private String paymentMethod;
    private String phoneCustomer;
    private String nameCustomer;
    private String nameEmployee;
    private List<InvoiceItem> items = new ArrayList<>();;

    public Invoice(String id, String date, String paymentMethod, String phoneCustomer,
                   String nameCustomer, String nameEmployee, List<InvoiceItem> items) {
        this.id = id;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.phoneCustomer = phoneCustomer;
        this.nameCustomer = nameCustomer;
        this.nameEmployee = nameEmployee;
        this.items = items;
    }

    public Invoice(String id, String date, String paymentMethod, List<InvoiceItem> items){
        this.id = id;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }
    public String getId() { return id; }

    public String getDate() { return date; }

    public String getPaymentMethod() { return paymentMethod; }

    public String getPhoneCustomer() { return phoneCustomer; }

    public String getNameCustomer() { return nameCustomer; }

    public String getNameEmployee() { return nameEmployee; }

    public List<InvoiceItem> getItems() { return items; }

    public double getTotal() {
        if (items == null) return 0;
        return items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
    }

}
