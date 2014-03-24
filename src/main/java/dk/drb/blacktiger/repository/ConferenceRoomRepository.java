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
    
    Participant findByRoomNoAndCallerId(String roomNo, String callerId);
    
    void kickParticipant(String roomNo, String callerId);
   
    void muteParticipant(String roomNo, String callerId);
    void unmuteParticipant(String roomNo, String callerId);
    
    void addEventListener(ConferenceEventListener listener);
    void removeEventListener(ConferenceEventListener listener);
}
