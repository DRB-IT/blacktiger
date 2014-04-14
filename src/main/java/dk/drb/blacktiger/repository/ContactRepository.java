package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.Contact;

/**
 *
 * @author michael
 */
public interface ContactRepository {
    
    public Contact findByRoomId(String roomId);
    public void save(String roomId, Contact contact);
}
