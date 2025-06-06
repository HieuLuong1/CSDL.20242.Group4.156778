package hust.soict.hedspi.market.csdl_20242_oanhnt;
public class Admin {
    private String id;
    private String fullName;
    private String email;
    private String phone;

    public Admin(String id, String fullName, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
