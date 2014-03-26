package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantUnmuteEvent extends SparseParticipantEvent {
    
    public ParticipantUnmuteEvent(String roomNo, String channel) {
        super(roomNo, channel);
    }

    @Override
    public String getType() {
        return "Unmute";
    }
    
}
