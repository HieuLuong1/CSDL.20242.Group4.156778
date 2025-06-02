package hust.soict.hedspi.market.csdl_20242_oanhnt;

import java.time.LocalDate;

public class Order {
    private static int orderCount = 1;

    private String id;
    private LocalDate orderDate;
    private double total;
    private String method;
    private String customerPhone;
    private String customerName;
    private String employeeName;

    public Order(LocalDate orderDate, double total, String method, String customerPhone, String customerName, String employeeName) {
        this.id = String.format("HD%03d", orderCount++);
        this.orderDate = orderDate;
        this.total = total;
        this.method = method;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.employeeName = employeeName;
    }

    public static void resetOrderCount() {
        orderCount = 1;
    }


    public String getId() { return id; }
    public LocalDate getOrderDate() { return orderDate; }
    public double getTotal() { return total; }
    public String getMethod() { return method; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerName() { return customerName; }
    public String getEmployeeName() { return employeeName; }
}
