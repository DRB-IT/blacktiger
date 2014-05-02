package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.Room;

/**
 *
 * @author michael
 */
public interface RoomInfoRepository {
    Room findById(String id);
}
