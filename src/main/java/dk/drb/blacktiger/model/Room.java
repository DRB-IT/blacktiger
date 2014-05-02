
package dk.drb.blacktiger.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author michael
 */
public class Room {
    private String id;
    private String name;
    private Contact contact;
    private String postalCode;
    private String city;
    private String hallNumber;
    private String phoneNumber;
    private String countryCallingCode;
    
    public Room() {
        this.contact = new Contact();
    }

    public Room(String id) {
        this.id = id;
        this.contact = new Contact();
    }
    
    public Room(String id, String displayName) {
        this.id = id;
        this.name = displayName;
        this.contact = new Contact();
    }
    
    public Room(String id, String displayName, String contactName, String contactEmail, String contactPhoneNumber, String contactComment) {
        this.id = id;
        this.name = displayName;
        this.contact = new Contact(contactName, contactEmail, contactPhoneNumber, contactComment);
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String displayName) {
        this.name = displayName == null ? null : displayName.trim();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact == null ? new Contact() : contact;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }
    
    public void mergeIn(Room room) {
        if(room.getCity() != null) {
            this.city = room.getCity();
        }
        
        if(room.getCountryCallingCode() != null) {
            this.countryCallingCode = room.getCountryCallingCode();
        }
        
        if(room.getHallNumber() != null) {
            this.hallNumber = room.getHallNumber();
        }
        
        if(room.getName() != null) {
            this.name = room.getName();
        }
        
        if(room.getPhoneNumber() != null) {
            this.phoneNumber = room.getPhoneNumber();
        }
        
        if(room.getPostalCode() != null) {
            this.postalCode = room.getPostalCode();
        }
        
        if(room.getContact() != null) {
            this.contact = room.getContact();
        }
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
