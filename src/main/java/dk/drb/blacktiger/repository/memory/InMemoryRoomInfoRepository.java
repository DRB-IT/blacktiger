package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ContactRepository;
import dk.drb.blacktiger.repository.RoomInfoRepository;
import java.util.List;

/**
 *
 * @author michael
 */
public class InMemoryRoomInfoRepository implements RoomInfoRepository {

    @Override
    public Room findById(String roomId) {
        return new Room();
    }

    @Override
    public List<Room> findAllBySearchString(String search) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
