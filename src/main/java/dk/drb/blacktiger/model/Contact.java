package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class Contact {
    private String name;
    private String email;
    private String phoneNumber;
    private String comment;

    public Contact() {
    }

    public Contact(String name, String email, String phoneNumber, String comment) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.comment = comment;
    }
    
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getComment() {
        return comment;
    }

}
