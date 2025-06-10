package hust.soict.hedspi.market.csdl_20242_oanhnt;

import javafx.beans.property.SimpleStringProperty;

public class Supplier {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty address;
    private final SimpleStringProperty representative;
    private final SimpleStringProperty phone;

    public Supplier(String id, String name, String address, String representative, String phone) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.representative = new SimpleStringProperty(representative);
        this.phone = new SimpleStringProperty(phone);
    }

    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getAddress() { return address.get(); }
    public String getRepresentative() { return representative.get(); }
    public String getPhone() { return phone.get(); }
}
