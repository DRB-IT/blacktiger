package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public abstract class SparseParticipantEvent extends ConferenceEvent {
    private final String channel;

    public SparseParticipantEvent(String roomNo, String channel) {
        super(roomNo);
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
    
}
