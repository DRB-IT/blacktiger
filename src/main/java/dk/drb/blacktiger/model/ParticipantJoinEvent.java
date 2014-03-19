package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> joins a conference room.
 */
public class ParticipantJoinEvent extends ParticipantEvent {
    
    public ParticipantJoinEvent(String roomNo, Participant participant) {
        super(roomNo, participant);
    }

    @Override
    public String getType() {
        return "Join";
    }

    
}
