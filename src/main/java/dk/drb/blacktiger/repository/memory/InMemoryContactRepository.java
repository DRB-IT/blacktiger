package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.repository.ContactRepository;

/**
 *
 * @author michael
 */
public class InMemoryContactRepository implements ContactRepository {

    @Override
    public Contact findByRoomId(String roomId) {
        return new Contact("John Doe", "john@doe.dk", "+4512341234", "Doe!");
    }

    @Override
    public void save(String roomId, Contact contact) {
        
    }
    
    
}
