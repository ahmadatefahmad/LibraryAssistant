package classes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Member{
    private final SimpleIntegerProperty memberID;
    private final SimpleStringProperty fName;
    private final SimpleStringProperty lName;
    private final SimpleStringProperty address;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty email;
    private final SimpleIntegerProperty rented;

    public Member(int memberID, String fName, String lName, String address, String phone, String email, int rented){
        this.memberID = new SimpleIntegerProperty(memberID);
        this.fName = new SimpleStringProperty(fName);
        this.lName = new SimpleStringProperty(lName);
        this.address = new SimpleStringProperty(address);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.rented = new SimpleIntegerProperty(rented);
    }

    public int getMemberID() {
        return memberID.get();
    }

    public String getFName() {
        return fName.get();
    }

    public String getLName() {
        return lName.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getEmail() {
        return email.get();
    }

    public int getRented() {
        return rented.get();
    }
}
