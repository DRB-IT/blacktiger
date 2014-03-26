package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.Room;
import java.util.List;

/**
 *
 * @author michael
 */
public interface ConferenceRoomRepository {
    public List<Room> findAll();
    public List<Room> findAllByIds(List<String> ids);
    
    public Room findOne(String id);
    
    List<Participant> findByRoomNo(String roomNo);
    
    Participant findByRoomNoAndChannel(String roomNo, String channel);
    
    void kickParticipant(String roomNo, String channel);
   
    void muteParticipant(String roomNo, String channel);
    void unmuteParticipant(String roomNo, String channel);
    
    void addEventListener(ConferenceEventListener listener);
    void removeEventListener(ConferenceEventListener listener);
}
