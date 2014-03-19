package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public abstract class SparseParticipantEvent extends ConferenceEvent {
    private final String callerId;

    public SparseParticipantEvent(String roomNo, String callerId) {
        super(roomNo);
        this.callerId = callerId;
    }

    public String getCallerId() {
        return callerId;
    }
    
}
