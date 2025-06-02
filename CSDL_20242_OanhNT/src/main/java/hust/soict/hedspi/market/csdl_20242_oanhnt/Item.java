package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class Item {
    private String name;
    private String unit;
    private double price;
    private int quantity;
    private String category;
    private String supplier;
    private int id;
    private static int idCounter = 1;
    public Item(String name, String unit, double price, int quantity, String category, String supplier) {
        this.id = idCounter++;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.supplier = supplier;
    }
    public String getName() {
        return name;
    }
    public String getUnit() {
        return unit;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getCategory() {
        return category;
    }
    public String getSupplier() {
        return supplier;
    }
    public int getId() {
        return id;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public static void resetIdCounter() {
        idCounter = 1;
    }
}
