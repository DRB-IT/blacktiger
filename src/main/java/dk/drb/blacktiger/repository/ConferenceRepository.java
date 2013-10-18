package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import java.util.List;

/**
 *
 * @author krog
 */
public interface ConferenceRepository {
    
    List<Participant> findByRoomNo(String roomNo);
    
    Participant findByRoomNoAndParticipantId(String roomNo, String participantId);
    
    void kickParticipant(String roomNo, String participantId);
   
    void muteParticipant(String roomNo, String participantId);
    void unmuteParticipant(String roomNo, String participantId);
    
    void addEventListener(ConferenceEventListener listener);
    void removeEventListener(ConferenceEventListener listener);
    
}
