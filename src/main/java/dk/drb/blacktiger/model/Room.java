
package dk.drb.blacktiger.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class Room {
    private String id;
    private String displayName;
    private List<Participant> participants = new ArrayList<Participant>();
    private Contact contact;

    public Room() {
        this.contact = new Contact();
    }

    public Room(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.contact = new Contact();
    }
    
    public Room(String id, String displayName, String contactName, String contactEmail, String contactPhoneNumber, String contactComment) {
        this.id = id;
        this.displayName = displayName;
        this.contact = new Contact(contactName, contactEmail, contactPhoneNumber, contactComment);
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact == null ? new Contact() : contact;
    }

    
}
