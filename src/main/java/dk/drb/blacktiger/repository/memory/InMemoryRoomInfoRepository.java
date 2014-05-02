package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ContactRepository;
import dk.drb.blacktiger.repository.RoomInfoRepository;

/**
 *
 * @author michael
 */
public class InMemoryRoomInfoRepository implements RoomInfoRepository {

    @Override
    public Room findById(String roomId) {
        return new Room();
    }

    
    
}
