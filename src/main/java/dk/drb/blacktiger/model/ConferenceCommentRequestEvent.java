package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ConferenceCommentRequestEvent extends ConferenceEvent {

    private String participantId;
    
    public ConferenceCommentRequestEvent(String roomNo, String participantId) {
        super(roomNo);
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return participantId;
    }

    @Override
    public String getType() {
        return "CommentRequest";
    }
    
}
