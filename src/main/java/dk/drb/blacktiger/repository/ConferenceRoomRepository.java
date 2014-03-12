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
    
    Participant findByRoomNoAndParticipantId(String roomNo, String participantId);
    
    void kickParticipant(String roomNo, String participantId);
   
    void muteParticipant(String roomNo, String participantId);
    void unmuteParticipant(String roomNo, String participantId);
    
    void addEventListener(ConferenceEventListener listener);
    void removeEventListener(ConferenceEventListener listener);
}
