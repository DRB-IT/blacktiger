package dk.drb.blacktiger.model;

public class PhonebookUpdateEvent {

    private final String phoneNumber, newName;

    public PhonebookUpdateEvent(String phoneNumber, String newName) {
        this.phoneNumber = phoneNumber;
        this.newName = newName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNewName() {
        return newName;
    }

}
