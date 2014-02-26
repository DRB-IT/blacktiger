package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ConferenceCommentRequestCancelEvent extends ConferenceEvent {

    private String participantId;
    
    public ConferenceCommentRequestCancelEvent(String roomNo, String participantId) {
        super(roomNo);
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return participantId;
    }

    @Override
    public String getType() {
        return "CommentRequestCancel";
    }
    
}
