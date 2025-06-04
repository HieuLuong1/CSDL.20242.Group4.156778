public class Order {
    private String id;
    private String date;
    private int total;
    private String paymentMethod;
    private String phoneCustomer;
    private String nameCustomer;
    private String nameEmployee;

    public Order(String id, String date, int total, String paymentMethod, String phoneCustomer, String nameCustomer, String nameEmployee) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.phoneCustomer = phoneCustomer;
        this.nameCustomer = nameCustomer;
        this.nameEmployee = nameEmployee;
    }

    // Getters
    public String getId() { return id; }
    public String getDate() { return date; }
    public int getTotal() { return total; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPhoneCustomer() { return phoneCustomer; }
    public String getNameCustomer() { return nameCustomer; }
    public String getNameEmployee() { return nameEmployee; }
}
