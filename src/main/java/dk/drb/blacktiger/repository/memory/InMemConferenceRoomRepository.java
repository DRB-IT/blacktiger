package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An in-memory implementation of ConferenceRoomRepository for use in test scenarios.
 */
public class InMemConferenceRoomRepository implements ConferenceRoomRepository {

    private List<Room> rooms = new ArrayList<>();
    private UserRepository userRepository;
    
    @PostConstruct
    protected void init() {
        for(User user : userRepository.findAll()) {
            rooms.add(new Room(user.getUsername(), "Test Kingdom Hall " + user.getUsername()));
        }
    }
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
