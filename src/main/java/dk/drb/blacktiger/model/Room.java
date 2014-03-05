
package dk.drb.blacktiger.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author michael
 */
public class Room {
    private String id;
    private String name;
    private Contact contact;

    public Room() {
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
        this.name = displayName;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact == null ? new Contact() : contact;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
