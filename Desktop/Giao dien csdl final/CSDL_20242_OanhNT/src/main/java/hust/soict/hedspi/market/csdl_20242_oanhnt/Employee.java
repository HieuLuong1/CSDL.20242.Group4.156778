package hust.soict.hedspi.market.csdl_20242_oanhnt;

public class Employee {
    private String id, fullName, dob, gender, email, phone, address, idCard;

    public Employee(String id, String fullName, String dob, String gender, String email,
                    String phone, String address, String idCard) {
        this.id = id;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.idCard = idCard;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getIdCard() { return idCard; }
    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
}