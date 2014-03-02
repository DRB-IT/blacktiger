package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.text.NumberFormat;
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
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setMinimumIntegerDigits(4);
        nf.setGroupingUsed(false);
        
        for(int i=0;i<1000;i++) {
            rooms.add(new Room("H45-" + nf.format(i), "Test Rigssal " + i));
        }
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
