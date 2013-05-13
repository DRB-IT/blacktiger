package dk.drb.blacktiger.service;

/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> leaves a conference room.
 */
public class ParticipantLeaveEvent extends ParticipantEvent {

    public ParticipantLeaveEvent(String roomNo, String participantId) {
        super(roomNo, participantId);
    }

    

    
}
