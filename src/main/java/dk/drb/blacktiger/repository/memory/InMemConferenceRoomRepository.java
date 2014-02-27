package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author michael
 */
public class InMemConferenceRoomRepository implements ConferenceRoomRepository {

    private List<Room> rooms = new ArrayList<>();

    public InMemConferenceRoomRepository() {
        rooms.add(new Room("H45-0000", "Test Rigssal 1"));
        rooms.add(new Room("H45-0001", "Test Rigssal 2"));
        rooms.add(new Room("H45-0002", "Test Rigssal 3"));
        rooms.add(new Room("H45-0003", "Test Rigssal 4"));
        rooms.add(new Room("H45-0004", "Test Rigssal 5"));
        rooms.add(new Room("H45-0005", "Test Rigssal 6"));
        rooms.add(new Room("H45-0006", "Test Rigssal 7"));
        rooms.add(new Room("H45-0007", "Test Rigssal 8"));
        rooms.add(new Room("H45-0008", "Test Rigssal 9"));
        rooms.add(new Room("H45-0009", "Test Rigssal 10"));
    }
    
    
    @Override
    public List<Room> findAll() {
        return Collections.unmodifiableList(rooms);
    }

    @Override
    public List<Room> findAllByIds(List<String> ids) {
        List<Room> list = new ArrayList<>();
        for(Room room : rooms) {
            if(ids.contains(room.getId())) {
                list.add(room);
            }
        }
        return list;
    }

    @Override
    public Room findOne(String id) {
        for(Room room : rooms) {
            if(room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public void save(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
