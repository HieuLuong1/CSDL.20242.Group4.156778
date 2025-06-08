package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class Item {
    private int productId;      // ID thực từ CSDL
    private String name;
    private String unit;
    private double price;
    private int quantity;
    private String category;

    public Item(int productId, String name, String unit, double price, int quantity, String category) {
        this.productId = productId;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Getter
    public int getProductId() {
        return productId;
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

    // Setter
    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
