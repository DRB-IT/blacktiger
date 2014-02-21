
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
    private Contact contact = new Contact();

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
