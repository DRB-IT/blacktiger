package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> joins a conference room.
 */
public class ParticipantJoinEvent extends ParticipantEvent {

    public ParticipantJoinEvent(String roomNo, String participantId) {
        super(roomNo, participantId);
    }

    

    
}
