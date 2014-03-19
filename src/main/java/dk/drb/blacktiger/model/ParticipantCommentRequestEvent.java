package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestEvent extends ConferenceEvent {

    private final String callerId;
    
    public ParticipantCommentRequestEvent(String roomNo, String callerId) {
        super(roomNo);
        this.callerId = callerId;
    }

    public String getCallerId() {
        return callerId;
    }

    @Override
    public String getType() {
        return "CommentRequest";
    }
    
}
