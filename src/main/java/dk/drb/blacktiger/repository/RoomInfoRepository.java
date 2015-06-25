package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.Room;
import java.util.List;

/**
 *
 * @author michael
 */
public interface RoomInfoRepository {
    Room findById(String id);
    List<Room> findAllBySearchString(String search);
}
