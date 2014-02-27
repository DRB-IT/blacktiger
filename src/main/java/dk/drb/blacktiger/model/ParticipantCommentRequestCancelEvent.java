package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestCancelEvent extends ConferenceEvent {

    private String participantId;
    
    public ParticipantCommentRequestCancelEvent(String roomNo, String participantId) {
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
