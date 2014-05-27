package dk.drb.blacktiger.controller.rest.model;


public class SendPasswordRequest {

    private String name;
    private String phoneNumber;
    private String email;
    private String cityOfHall;
    private String phoneNumberOfHall;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCityOfHall() {
        return cityOfHall;
    }

    public void setCityOfHall(String cityOfHall) {
        this.cityOfHall = cityOfHall;
    }

    public String getPhoneNumberOfHall() {
        return phoneNumberOfHall;
    }

    public void setPhoneNumberOfHall(String phoneNumberOfHall) {
        this.phoneNumberOfHall = phoneNumberOfHall;
    }
    
}
