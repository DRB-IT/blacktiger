package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantUnmuteEvent extends SparseParticipantEvent {
    
    public ParticipantUnmuteEvent(String roomNo, String callerId) {
        super(roomNo, callerId);
    }

    @Override
    public String getType() {
        return "Unmute";
    }
    
}
