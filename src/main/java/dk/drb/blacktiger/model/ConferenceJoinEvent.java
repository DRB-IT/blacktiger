package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> joins a conference room.
 */
public class ConferenceJoinEvent extends ConferenceEvent {

    public ConferenceJoinEvent(String roomNo, String participantId) {
        super(roomNo, participantId);
    }

    

    
}
