package dk.drb.blacktiger.model;

/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> leaves a conference room.
 */
public class ConferenceLeaveEvent extends ConferenceEvent {

    public ConferenceLeaveEvent(String roomNo, String participantId) {
        super(roomNo, participantId);
    }

    

    
}
