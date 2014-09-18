package dk.drb.blacktiger.controller.rest.model;


public class SendPasswordRequest {

    private String name;
    private String phoneNumber;
    private String email;
    private String cityOfHall;
    private String phoneNumberOfHall;
    private String emailSubject;
    private String emailTextManager;
    private String emailTextUser;

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

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailTextManager() {
        return emailTextManager;
    }

    public void setEmailTextManager(String emailTextManager) {
        this.emailTextManager = emailTextManager;
    }

    public String getEmailTextUser() {
        return emailTextUser;
    }

    public void setEmailTextUser(String emailTextUser) {
        this.emailTextUser = emailTextUser;
    }

    public void setPhoneNumberOfHall(String phoneNumberOfHall) {
        this.phoneNumberOfHall = phoneNumberOfHall;
    }
    
}
