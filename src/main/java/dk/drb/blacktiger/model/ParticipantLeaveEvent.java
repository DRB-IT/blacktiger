package dk.drb.blacktiger.model;

/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> leaves a conference room.
 */
public class ParticipantLeaveEvent extends ParticipantEvent {
    
    public ParticipantLeaveEvent(String roomNo, Participant participant) {
        super(roomNo, participant);
    }

    @Override
    public String getType() {
        return "Leave";
    }

    
}
