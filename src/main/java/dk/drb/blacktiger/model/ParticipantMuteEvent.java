package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantMuteEvent extends SparseParticipantEvent {
    
    public ParticipantMuteEvent(String roomNo, String channel) {
        super(roomNo, channel);
    }

    @Override
    public String getType() {
        return "Mute";
    }
    
}
