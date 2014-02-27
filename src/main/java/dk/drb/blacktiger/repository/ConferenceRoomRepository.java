package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.Room;
import java.util.List;

/**
 *
 * @author michael
 */
public interface ConferenceRoomRepository {
    public List<Room> findAll();
    public Room findOne(String id);
    public void save(Room room);
}
