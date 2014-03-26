package dk.drb.blacktiger.model;

/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> leaves a conference room.
 */
public class ParticipantLeaveEvent extends SparseParticipantEvent {
    
    public ParticipantLeaveEvent(String roomNo, String channel) {
        super(roomNo, channel);
    }

    @Override
    public String getType() {
        return "Leave";
    }

    
}
