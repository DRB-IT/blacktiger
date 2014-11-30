package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> has been changed, fx. new name.
 */
public class ParticipantChangeEvent extends ParticipantEvent {
    
    public ParticipantChangeEvent(String roomNo, Participant participant) {
        super(roomNo, participant);
    }

    @Override
    public String getType() {
        return "Change";
    }

    
}
