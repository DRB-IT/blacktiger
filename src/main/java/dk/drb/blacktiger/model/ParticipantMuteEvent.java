package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantMuteEvent extends SparseParticipantEvent {
    
    public ParticipantMuteEvent(String roomNo, String callerId) {
        super(roomNo, callerId);
    }

    @Override
    public String getType() {
        return "Mute";
    }
    
}
